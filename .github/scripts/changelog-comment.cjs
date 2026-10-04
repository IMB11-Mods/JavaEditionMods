module.exports = async ({ github, context }) => {
  let pull = context.payload.pull_request;
  if (context.eventName === 'issue_comment') {
    if (!context.payload.issue.pull_request || context.payload.comment.body.trim() !== '/changelog') return;
    const { data: permission } = await github.rest.repos.getCollaboratorPermissionLevel({
      ...context.repo,
      username: context.payload.comment.user.login
    });
    if (!['admin', 'maintain', 'write'].includes(permission.permission)) return;
    const { data } = await github.rest.pulls.get({
      ...context.repo,
      pull_number: context.payload.issue.number
    });
    pull = data;
  }
  if (pull.base.ref !== 'main') return;
  const issue_number = pull.number;
  const repo = { ...context.repo, issue_number };
  const comments = await github.paginate(github.rest.issues.listComments, repo);
  for (const project of ['sounds', 'mru']) {
    if (!pull.labels.some(label => label.name === `project:${project}`)) continue;
    const marker = `[release-changelog-${project}]: #`;
    if (comments.some(comment => comment.user.type === 'Bot' && comment.body.split(/\r?\n/).includes(marker))) continue;
    await github.rest.issues.createComment({
      ...repo,
      body: `### Added\n\n- TODO\n\n### Changed\n\n### Deprecated\n\n### Removed\n\n### Fixed\n\n### Security\n\n${marker}\n`
    });
  }
};
