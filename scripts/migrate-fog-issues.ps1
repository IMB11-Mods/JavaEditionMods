param(
    [switch]$Apply,
    [ValidatePattern('^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$')]
    [string]$Source = 'IMB11/Fog',
    [string]$Checkpoint = (Join-Path $PSScriptRoot '../.imports/fog-issue-migration.json'),
    [ValidateRange(1, 8)]
    [int]$Concurrency = 4,
    [ValidateRange(750, 60000)]
    [int]$WriteIntervalMs = 800
)

$ErrorActionPreference = 'Stop'
$PSNativeCommandUseErrorActionPreference = $false
$OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$Destination = 'IMB11-Mods/JavaEditionMods'
$ProjectLabel = 'project:fog'
$TypeLabels = [ordered]@{
    Bug = @('bug')
    Feature = @('feature request', 'enhancement', 'feature')
    Task = @('task')
}
$ExcludedLabels = @('bug', 'feature request', 'enhancement', 'feature', 'task', 'duplicate')
$RequestState = [hashtable]::Synchronized(@{
    NextWriteAt = [DateTime]::MinValue
    CooldownUntil = [DateTime]::MinValue
    Stopping = $false
})

function Wait-RequestSlot {
    param([switch]$Write, [switch]$Cleanup)

    if ($Write -and -not $Apply) { throw 'GitHub changes require -Apply.' }
    while ($true) {
        [System.Threading.Monitor]::Enter($RequestState.SyncRoot)
        try {
            if ($RequestState.Stopping -and -not $Cleanup) {
                throw [System.OperationCanceledException]::new('Migration stopped after a fatal API error.')
            }
            $now = [DateTime]::UtcNow
            $availableAt = $RequestState.CooldownUntil
            if ($Write -and $RequestState.NextWriteAt -gt $availableAt) { $availableAt = $RequestState.NextWriteAt }
            $waitMs = ($availableAt - $now).TotalMilliseconds
            if ($waitMs -le 0) {
                if ($Write) { $RequestState.NextWriteAt = $now.AddMilliseconds($WriteIntervalMs) }
                return
            }
        } finally {
            [System.Threading.Monitor]::Exit($RequestState.SyncRoot)
        }
        Start-Sleep -Milliseconds ([int][Math]::Min(1000, [Math]::Ceiling($waitMs)))
    }
}

function New-GitHubRequestError {
    param([string]$Response)

    $status = [regex]::Match($Response, '(?im)^HTTP/\S+\s+(?<code>\d+)')
    $statusCode = if ($status.Success) { [int]$status.Groups['code'].Value } else { 0 }
    $bodyMatch = [regex]::Match($Response, '(?ms)^[ \t]*(?<json>[\{\[].*)\z')
    $body = $null
    if ($bodyMatch.Success) {
        try { $body = $bodyMatch.Groups['json'].Value | ConvertFrom-Json -AsHashtable -Depth 100 } catch { }
    }
    $messages = @()
    if ($body -is [System.Collections.IDictionary]) {
        if ($body.errors) { $messages = @($body.errors | ForEach-Object { if ($_.message) { $_.message } else { [string]$_ } }) }
        if (-not $messages.Count -and $body.message) { $messages = @($body.message) }
    }
    if (-not $messages.Count) {
        $messages = @([regex]::Matches($Response, '(?im)^gh:\s*(.+)$') | ForEach-Object { $_.Groups[1].Value.Trim() })
    }
    if (-not $messages.Count) {
        $messages = if ($statusCode) { @("HTTP $statusCode") } else { @(($Response.Trim() -split '\r?\n')[-1]) }
    }
    $errorTypes = @($body.errors | ForEach-Object { $_.type })
    $exception = [System.InvalidOperationException]::new("GitHub: $($messages -join '; ')")
    $exception.Data['StopMigration'] = (
        $statusCode -in @(401, 403, 429) -or
        @($errorTypes | Where-Object { $_ -in @('FORBIDDEN', 'UNAUTHORIZED', 'RATE_LIMITED') }).Count -gt 0
    )
    return $exception
}

function Invoke-Gh {
    param([string[]]$Arguments, [string]$Body, [switch]$Write, [switch]$Cleanup)

    for ($attempt = 0; $attempt -lt 4; $attempt++) {
        Wait-RequestSlot -Write:$Write -Cleanup:$Cleanup
        if ($Body) {
            $result = $Body | & gh @Arguments 2>&1
        } else {
            $result = & gh @Arguments 2>&1
        }
        $exitCode = $LASTEXITCODE
        $text = $result -join [Environment]::NewLine
        if ($exitCode -eq 0) { return $text }
        if ($attempt -eq 3 -or $text -notmatch '(?i)rate.limit|abuse detection|HTTP(?:/[0-9.]+)?\s+429\b') {
            throw (New-GitHubRequestError -Response $text)
        }
        $waitSeconds = 60 * [Math]::Pow(2, $attempt)
        $retryAfter = [regex]::Match($text, '(?im)^retry-after:\s*(\d+)')
        if ($retryAfter.Success) { $waitSeconds = [Math]::Max($waitSeconds, [double]$retryAfter.Groups[1].Value) }
        $remaining = [regex]::Match($text, '(?im)^x-ratelimit-remaining:\s*0\s*$')
        $reset = [regex]::Match($text, '(?im)^x-ratelimit-reset:\s*(\d+)')
        if ($remaining.Success -and $reset.Success) {
            $resetAt = [DateTimeOffset]::FromUnixTimeSeconds([long]$reset.Groups[1].Value).UtcDateTime
            $waitSeconds = [Math]::Max($waitSeconds, ($resetAt - [DateTime]::UtcNow).TotalSeconds + 1)
        }
        [System.Threading.Monitor]::Enter($RequestState.SyncRoot)
        try {
            $until = [DateTime]::UtcNow.AddSeconds($waitSeconds)
            if ($until -gt $RequestState.CooldownUntil) { $RequestState.CooldownUntil = $until }
        } finally {
            [System.Threading.Monitor]::Exit($RequestState.SyncRoot)
        }
        Write-Host "GitHub rate limit: pausing all workers for at least $([Math]::Ceiling($waitSeconds)) seconds."
    }
}

function Invoke-GitHubApi {
    param([string]$Endpoint, [string]$Method = 'GET', [hashtable]$Payload, [switch]$Cleanup)

    $arguments = @(
        'api', '--hostname', 'github.com', '--include', '--method', $Method, $Endpoint,
        '-H', 'Accept: application/vnd.github+json',
        '-H', 'X-GitHub-Api-Version: 2022-11-28'
    )
    $body = ''
    if ($null -ne $Payload) {
        $arguments += @('--input', '-')
        $body = ConvertTo-Json -InputObject $Payload -Depth 100 -Compress
    }
    $response = Invoke-Gh -Arguments $arguments -Body $body -Write:($Method -ne 'GET') -Cleanup:$Cleanup
    if ($response -match '(?im)^HTTP/\S+\s+204\b') { return $null }
    $bodyMatch = [regex]::Match($response, '(?ms)^[ \t]*(?<json>[\{\[].*)\z')
    if (-not $bodyMatch.Success) { throw "GitHub response has no JSON body: $Endpoint" }
    $parsed = $bodyMatch.Groups['json'].Value | ConvertFrom-Json -AsHashtable -Depth 100 -NoEnumerate
    if ($parsed -is [System.Collections.IDictionary] -and $parsed.errors) {
        throw (New-GitHubRequestError -Response $response)
    }
    return ,$parsed
}

function Get-ApiPages {
    param([string]$Endpoint)

    $separator = if ($Endpoint.Contains('?')) { '&' } else { '?' }
    for ($page = 1; ; $page++) {
        $batch = Invoke-GitHubApi "$Endpoint${separator}per_page=100&page=$page"
        foreach ($item in $batch) { $item }
        if ($batch.Count -lt 100) { break }
    }
}

function Get-DestinationIssue {
    param([string]$Url, [switch]$Cleanup)

    $pattern = '^https://github\.com/' + [regex]::Escape($Destination) + '/issues/(?<number>[1-9]\d*)/?$'
    $match = [regex]::Match($Url.Trim(), $pattern, [System.Text.RegularExpressions.RegexOptions]::IgnoreCase)
    if (-not $match.Success) { throw "Invalid destination issue URL: $Url" }
    $number = $match.Groups['number'].Value
    return Invoke-GitHubApi "repos/$Destination/issues/$number" -Cleanup:$Cleanup
}

function Move-Issue {
    param([string]$IssueId, [string]$RepositoryId)

    $query = @'
mutation TransferIssue($issueId: ID!, $repositoryId: ID!) {
  transferIssue(input: {issueId: $issueId, repositoryId: $repositoryId, createLabelsIfMissing: false}) {
    issue {
      number
      url
      repository { nameWithOwner }
      labels(first: 100) {
        nodes { name }
        pageInfo { hasNextPage }
      }
    }
  }
}
'@
    $response = Invoke-GitHubApi 'graphql' 'POST' @{
        query = $query
        variables = @{ issueId = $IssueId; repositoryId = $RepositoryId }
    }
    $moved = $response.data.transferIssue.issue
    if (-not $moved.url -or -not $moved.number) { throw 'Transfer response did not include the destination issue.' }
    return $moved
}

function Get-IssueSnapshot {
    param([System.Collections.IDictionary]$Issue)

    return [ordered]@{
        Number = $Issue.number
        Url = $Issue.html_url
        Title = $Issue.title
        State = $Issue.state
        StateReason = $Issue.state_reason
        Type = $Issue.type.name
        Labels = @($Issue.labels | Sort-Object { $_.name } | ForEach-Object {
            [ordered]@{ name = $_.name; color = $_.color; description = $_.description }
        })
        Milestone = if ($Issue.milestone) {
            [ordered]@{ title = $Issue.milestone.title; due_on = $Issue.milestone.due_on }
        } else { $null }
    }
}

function Get-IssueType {
    param([System.Collections.IDictionary]$Issue)

    $names = @($Issue.Labels | ForEach-Object { $_.name })
    foreach ($type in $TypeLabels.Keys) {
        if ($TypeLabels[$type] | Where-Object { $names -contains $_ }) { return $type }
    }
    return $Issue.Type
}

function Get-FinalLabelNames {
    param([string[]]$Names)

    @($Names) + $ProjectLabel | Where-Object { $ExcludedLabels -notcontains $_ } | Sort-Object -Unique
}

function Restore-IssueLock {
    param([System.Collections.IDictionary]$Issue, [System.Collections.IDictionary]$LockState, [switch]$Cleanup)

    $repository = if ($Issue.repository_url -eq "https://api.github.com/repos/$Source") {
        $Source
    } elseif ($Issue.repository_url -eq "https://api.github.com/repos/$Destination") {
        $Destination
    } else { throw 'Refusing to restore a lock in an unexpected repository.' }
    $payload = @{}
    if ($LockState.Reason) { $payload.lock_reason = $LockState.Reason }
    $null = Invoke-GitHubApi "repos/$repository/issues/$($Issue.number)/lock" 'PUT' $payload -Cleanup:$Cleanup
}

function Invoke-IssueMigration {
    param([System.Collections.IDictionary]$Work)

    $original = $Work.Original
    $current = $Work.Current
    $timer = [System.Diagnostics.Stopwatch]::StartNew()
    $restorePending = [bool]$Work.LockState.Locked
    $moved = $null
    Write-Host "Processing source #$($original.Number)..."
    try {
        if ($current.repository_url -eq "https://api.github.com/repos/$Source") {
            if ($current.locked) {
                Write-Host "Temporarily unlocking source #$($original.Number) for transfer."
                $null = Invoke-GitHubApi "repos/$Source/issues/$($original.Number)/lock" 'DELETE'
            }
            $moved = Move-Issue -IssueId $current.node_id -RepositoryId $DestinationId
            [pscustomobject]@{ Kind = 'Transferred'; Key = $Work.Key; Url = $moved.url }
            $current = if ($moved.labels.pageInfo.hasNextPage) {
                Get-DestinationIssue $moved.url
            } else {
                @{
                    number = $moved.number
                    html_url = $moved.url
                    repository_url = "https://api.github.com/repos/$($moved.repository.nameWithOwner)"
                    labels = @($moved.labels.nodes)
                }
            }
        }
        if ($current.repository_url -ne "https://api.github.com/repos/$Destination") {
            throw "Source #$($original.Number) did not resolve to the destination repository."
        }
        if ($Work.DestinationUrl -ne $current.html_url -and $Work.Current.repository_url -ne "https://api.github.com/repos/$Source") {
            [pscustomobject]@{ Kind = 'Transferred'; Key = $Work.Key; Url = $current.html_url }
        }
        if ($restorePending) {
            if (-not $current.locked -or $current.active_lock_reason -ne $Work.LockState.Reason) {
                Restore-IssueLock -Issue $current -LockState $Work.LockState
            }
            $restorePending = $false
        }
        $type = Get-IssueType $original
        $candidateLabels = @($original.Labels | ForEach-Object { $_.name }) + @($current.labels | ForEach-Object { $_.name })
        $expectedLabels = @(
            Get-FinalLabelNames -Names $candidateLabels | ForEach-Object {
                if ($LabelMap.ContainsKey($_)) { $LabelMap[$_].name } else { $_ }
            }
        )
        $payload = @{ labels = $expectedLabels }
        if ($type) { $payload.type = $IssueTypes[$type] }
        $updated = Invoke-GitHubApi "repos/$Destination/issues/$($current.number)" 'PATCH' $payload
        $actualLabels = @($updated.labels | ForEach-Object { $_.name })
        if (@($expectedLabels | Where-Object { $actualLabels -notcontains $_ }).Count -or
            @($actualLabels | Where-Object { $expectedLabels -notcontains $_ }).Count) {
            throw "Labels were not applied to $($updated.html_url)."
        }
        if ($type -and $updated.type.name -ne $IssueTypes[$type]) { throw "Issue type was not applied to $($updated.html_url)." }
        if ($updated.state -ne $original.State -or $updated.state_reason -ne $original.StateReason) {
            throw "State changed on $($updated.html_url); inspect before resuming."
        }
        if ($Work.LockState.Locked -and (-not $updated.locked -or $updated.active_lock_reason -ne $Work.LockState.Reason)) {
            $restorePending = $true
            throw "Original conversation lock was not restored on $($updated.html_url)."
        }
    } catch {
        $migrationFailure = $_
        if ($restorePending) {
            try {
                $latest = if ($moved.url) {
                    Get-DestinationIssue $moved.url -Cleanup
                } elseif ($current.repository_url -eq "https://api.github.com/repos/$Destination") {
                    Get-DestinationIssue $current.html_url -Cleanup
                } else {
                    Invoke-GitHubApi "repos/$Source/issues/$($original.Number)" -Cleanup
                }
                if ($latest.repository_url -eq "https://api.github.com/repos/$Destination") {
                    [pscustomobject]@{ Kind = 'Transferred'; Key = $Work.Key; Url = $latest.html_url }
                }
                Restore-IssueLock -Issue $latest -LockState $Work.LockState -Cleanup
            } catch {
                $exception = [System.InvalidOperationException]::new(
                    "$($migrationFailure.Exception.Message) Lock restoration also failed: $($_.Exception.Message). Resume with the same checkpoint to restore it."
                )
                $exception.Data['StopMigration'] = $true
                throw $exception
            }
        }
        throw $migrationFailure
    }
    [pscustomobject]@{
        Kind = 'Completed'; Key = $Work.Key; Url = $updated.html_url
        Seconds = [Math]::Round($timer.Elapsed.TotalSeconds, 1)
    }
}

function Save-Checkpoint {
    $directory = Split-Path -Parent $Checkpoint
    [void][System.IO.Directory]::CreateDirectory($directory)
    $json = ConvertTo-Json -InputObject $script:Progress -Depth 100
    [System.IO.File]::WriteAllText("$Checkpoint.tmp", $json, [System.Text.UTF8Encoding]::new($false))
    [System.IO.File]::Move("$Checkpoint.tmp", $Checkpoint, $true)
}

try {
    if ($PSVersionTable.PSVersion -lt [version]'7.3') { throw 'Run this script with PowerShell 7.3 or newer (pwsh).' }
    if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
        throw 'Install GitHub CLI and authenticate with gh auth login.'
    }
    $Checkpoint = [System.IO.Path]::GetFullPath($Checkpoint)
    $sourceRepo = Invoke-GitHubApi "repos/$Source"
    $destinationRepo = Invoke-GitHubApi "repos/$Destination"
    foreach ($repo in @($sourceRepo, $destinationRepo)) {
        if ($repo.archived -or -not $repo.has_issues -or -not $repo.permissions.push) {
            throw "$($repo.full_name) must have issues enabled, be unarchived, and grant you write access."
        }
    }
    $Source = $sourceRepo.full_name
    $Destination = $destinationRepo.full_name
    if ($sourceRepo.owner.id -ne $destinationRepo.owner.id) {
        throw "GitHub issue transfers require the same owner. Transfer the $Source repository to $($destinationRepo.owner.login) first, then rerun this script. Repository redirects are followed automatically."
    }
    if ($sourceRepo.private -and -not $destinationRepo.private) {
        throw 'GitHub cannot transfer private issues into a public repository.'
    }
    $types = @{}
    foreach ($type in (Invoke-GitHubApi "orgs/$($destinationRepo.owner.login)/issue-types")) {
        if ($type.is_enabled) { $types[$type.name] = $type.name }
    }
    $identity = @{
        Version = 1; Source = $Source; Destination = $Destination
        SourceId = $sourceRepo.id; DestinationId = $destinationRepo.id
    }
    if (Test-Path -LiteralPath $Checkpoint) {
        $script:Progress = Get-Content -LiteralPath $Checkpoint -Raw | ConvertFrom-Json -AsHashtable -Depth 100
        foreach ($key in $identity.Keys) {
            if ($script:Progress[$key] -ne $identity[$key]) { throw 'Checkpoint belongs to a different migration.' }
        }
        if ($script:Progress.Issues -isnot [System.Collections.IDictionary]) { throw 'Invalid checkpoint issue list.' }
    } else {
        $script:Progress = $identity
        $script:Progress.Issues = @{}
    }
    $sourceIssues = @(Get-ApiPages "repos/$Source/issues?state=all&sort=created&direction=asc")
    $sourceByNumber = @{}
    foreach ($issue in $sourceIssues) {
        if ($issue.pull_request) { continue }
        $key = [string]$issue.number
        $sourceByNumber[$key] = $issue
        if (-not $script:Progress.Issues.Contains($key)) {
            $script:Progress.Issues[$key] = @{ Original = Get-IssueSnapshot $issue; Completed = $false }
        }
    }
    $pending = @($script:Progress.Issues.Values | Where-Object { -not $_.Completed } | Sort-Object { $_.Original.Number })
    $labels = @{}
    foreach ($label in (Get-ApiPages "repos/$Destination/labels")) { $labels[$label.name] = $label }
    $requiredLabels = @{}
    $requiredLabels[$ProjectLabel] = @{ name = $ProjectLabel; color = '1D76DB'; description = 'Issues for Fog' }
    foreach ($entry in $pending) {
        $original = $entry.Original
        $type = Get-IssueType $original
        if ($type -and -not $types.ContainsKey($type)) { throw "Enable the '$type' issue type before migrating." }
        $retainedNames = @(Get-FinalLabelNames -Names @($original.Labels | ForEach-Object { $_.name }))
        foreach ($label in $original.Labels) {
            if ($retainedNames -notcontains $label.name) { continue }
            if (-not $requiredLabels.ContainsKey($label.name)) {
                $requiredLabels[$label.name] = @{
                    name = $label.name; color = $label.color; description = [string]$label.description
                }
            }
        }
    }
    $mode = if ($Apply) { 'APPLY' } else { 'PREVIEW' }
    Write-Host "${mode}: $Source -> $Destination ($($pending.Count) pending issues)"
    Write-Host 'Omit type labels and duplicate; keep other labels and add project:fog.'
    Write-Host 'Bug takes precedence over Feature, then Task.'
    foreach ($entry in $pending) {
        $original = $entry.Original
        $type = Get-IssueType $original
        $names = @(Get-FinalLabelNames -Names @($original.Labels | ForEach-Object { $_.name }))
        if (-not $type) { $type = '(untyped)' }
        Write-Host "#$($original.Number) [$($original.State)] type=$type labels=$($names -join ', ')"
    }
    $missingLabels = @($requiredLabels.Values | Where-Object { -not $labels.ContainsKey($_.name) })
    foreach ($label in $missingLabels) { Write-Host "Create destination label: $($label.name)" }
    if (-not $pending.Count) { Write-Host 'No pending issues.'; return }
    if (-not $Apply) {
        Write-Host 'Preview only. No GitHub changes or checkpoint writes. Add -Apply to migrate.'
        return
    }
    Write-Host 'Fetching destination issues and preparing all migration requests...'
    $destinationByUrl = @{}
    foreach ($issue in (Get-ApiPages "repos/$Destination/issues?state=all&sort=created&direction=asc")) {
        if (-not $issue.pull_request) { $destinationByUrl[$issue.html_url] = $issue }
    }
    $workItems = [System.Collections.Generic.List[object]]::new()
    foreach ($entry in $pending) {
        $original = $entry.Original
        $key = [string]$original.Number
        $current = if ($entry.DestinationUrl) {
            if ($destinationByUrl.ContainsKey($entry.DestinationUrl)) {
                $destinationByUrl[$entry.DestinationUrl]
            } else {
                Get-DestinationIssue $entry.DestinationUrl
            }
        } elseif ($sourceByNumber.ContainsKey($key)) {
            $sourceByNumber[$key]
        } else {
            Invoke-GitHubApi "repos/$Source/issues/$key"
        }
        if ($current.pull_request) { throw "Source #$key unexpectedly resolves to a pull request." }
        if ($current.repository_url -eq "https://api.github.com/repos/$Source") {
            $fresh = ConvertTo-Json -InputObject (Get-IssueSnapshot $current) -Depth 100 -Compress
            $saved = ConvertTo-Json -InputObject $original -Depth 100 -Compress
            if ($fresh -cne $saved) {
                throw "Source #$key changed. Review and remove only this untransferred checkpoint entry, then rerun."
            }
        } elseif ($current.repository_url -ne "https://api.github.com/repos/$Destination") {
            throw "Source #$key belongs to an unexpected repository."
        }
        if (-not $entry.LockState -or $current.locked) {
            $entry.LockState = @{ Locked = [bool]$current.locked; Reason = $current.active_lock_reason }
        }
        $workItems.Add(@{
            Key = $key; Original = $original; Current = $current; DestinationUrl = $entry.DestinationUrl
            LockState = $entry.LockState
        })
    }
    Save-Checkpoint
    foreach ($label in $missingLabels) {
        $created = Invoke-GitHubApi "repos/$Destination/labels" 'POST' $label
        $labels[$created.name] = $created
    }
    $workerFunctions = [ordered]@{}
    foreach ($name in @(
        'Wait-RequestSlot', 'New-GitHubRequestError', 'Invoke-Gh', 'Invoke-GitHubApi', 'Get-DestinationIssue',
        'Move-Issue', 'Get-IssueType', 'Get-FinalLabelNames', 'Restore-IssueLock', 'Invoke-IssueMigration'
    )) {
        $workerFunctions[$name] = (Get-Item "Function:$name").Definition
    }
    $destinationId = $destinationRepo.node_id
    $failures = [System.Collections.Generic.List[string]]::new()
    $completedThisRun = 0
    Write-Host "Running $Concurrency parallel workers with a shared ${WriteIntervalMs}ms write interval."
    $lockedCount = @($workItems | Where-Object { $_.LockState.Locked }).Count
    if ($lockedCount) { Write-Host "$lockedCount locked issue(s) will have their original lock restored after transfer." }
    $workItems | ForEach-Object -Parallel {
        $work = $_
        $ErrorActionPreference = 'Stop'
        $PSNativeCommandUseErrorActionPreference = $false
        $OutputEncoding = [System.Text.UTF8Encoding]::new($false)
        $Apply = [bool]$using:Apply
        $Source = $using:Source
        $Destination = $using:Destination
        $DestinationId = $using:destinationId
        $ProjectLabel = $using:ProjectLabel
        $TypeLabels = $using:TypeLabels
        $ExcludedLabels = $using:ExcludedLabels
        $LabelMap = $using:labels
        $IssueTypes = $using:types
        $RequestState = $using:RequestState
        $WriteIntervalMs = $using:WriteIntervalMs
        $definitions = $using:workerFunctions
        if ($RequestState.Stopping) { return }
        try {
            foreach ($definition in $definitions.GetEnumerator()) {
                Set-Item -Path "Function:$($definition.Key)" -Value ([scriptblock]::Create($definition.Value))
            }
            Invoke-IssueMigration -Work $work
        } catch {
            if ($_.Exception.Data['StopMigration']) { $RequestState.Stopping = $true }
            if ($_.Exception -isnot [System.OperationCanceledException]) {
                [pscustomobject]@{ Kind = 'Failed'; Key = $work.Key; Message = $_.Exception.Message }
            }
        }
    } -ThrottleLimit $Concurrency | ForEach-Object {
        $migrationEvent = $_
        $entry = $script:Progress.Issues[$migrationEvent.Key]
        try {
            switch ($migrationEvent.Kind) {
                'Transferred' {
                    $entry.DestinationUrl = $migrationEvent.Url
                    Save-Checkpoint
                }
                'Completed' {
                    $entry.DestinationUrl = $migrationEvent.Url
                    $entry.Completed = $true
                    Save-Checkpoint
                    $completedThisRun++
                    Write-Host "[$completedThisRun/$($pending.Count)] $($entry.Original.Url) -> $($migrationEvent.Url) ($($migrationEvent.Seconds)s)"
                }
                'Failed' {
                    $failures.Add("Source #$($migrationEvent.Key): $($migrationEvent.Message)")
                    Write-Host $failures[$failures.Count - 1] -ForegroundColor Red
                }
            }
        } catch {
            $RequestState.Stopping = $true
            throw
        }
    }
    if ($failures.Count) { throw "$($failures.Count) worker(s) stopped. $($failures[0])" }
    $remaining = @(Get-ApiPages "repos/$Source/issues?state=all" | Where-Object { -not $_.pull_request })
    if ($remaining.Count) { throw "$($remaining.Count) issues remain. Rerun with the same checkpoint to include them." }
    Write-Host "Migration complete. Progress and old/new URLs: $Checkpoint"
} catch {
    Write-Host "Stopped: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host 'If -Apply was used, keep the checkpoint and rerun with it after resolving the error.'
    exit 1
}
