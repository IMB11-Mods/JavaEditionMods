# IMB11's Monorepo

Monorepo for all my Java Edition mods!

## Development

| Command                                                     | What it does                                     |
|-------------------------------------------------------------|--------------------------------------------------|
| `./gradlew buildAll`                                        | Build all projects for all loaders               |
| `./gradlew buildFabric`                                     | Build all projects for Fabric                    |
| `./gradlew buildNeoForge`                                   | Build all projects for NeoForge                  |
| `./gradlew collectArtifacts`                                | Build all and collect the files in `build/artifacts` |
| `./gradlew :<mod>:<game-version>-<loader>:runClient`        | Launch a mod                                     |

## Publishing

Push a tag matching the mod's version e.g `mru/v1.0.41` or `sounds/v2.5.2+lts`.
