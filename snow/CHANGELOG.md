# Changelog

## [3.0.0] - 2026-11-01

### Added

- Snow Under Trees (Fabric) is now Snow. The mod id is now `snow`. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Snowlogging: snow can cover small plants in the `snow:snowloggable` block tag, and breaking the snow leaves the plant alone. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Snowy leaves: leaves turn white while it snows in cold biomes. Client-side only, works with Sodium and Iris. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Better snow generation: snow generates in several layers that follow the terrain, keeps layering up while it snows, and each biome has its own depth through the `snow:snow_profile` datapack registry. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Gradual melting, melting in warm biomes, and shovel harvesting of snow layers. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- NeoForge support! ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Serene Seasons support: snowfall, snowlogging, melting and snowy leaves follow the seasonal temperature, and Serene Seasons' own melting removes one layer at a time when gradual melting is on. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Server-synced config: the server controls the settings on multiplayer and only op players can change them in game. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))

### Changed

- Snow under trees now works in every biome that is cold enough to snow at ground level, instead of a configured biome list. Modded biomes no longer need to be added by hand. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Snow under trees uses the vanilla snowfall tick instead of its own chunk scheduler, so the "Snow Update Limit" option is gone. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Snow under trees keeps falling under trees whose canopy already has snow on top. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- The config moved from `snowundertrees.config.json` to `snow.json`, with new option names. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))

### Removed

- The "Supported Biomes", "Snow Update Limit", "Respect Season Mods" and "Melt Snow Seasonally" options. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))
- Support for Minecraft versions before 26.2. ([#357](https://github.com/IMB11-Mods/JavaEditionMods/pull/357))

## [2.7.6] - 2026-03-24

### Added

- Ports to 26.1 and 26.3.

## [2.7.5] - 2025-12-09

### Added

- Port to 1.21.11.

## [2.7.4] - 2025-10-07

### Fixed

- Snow not melting unless it is raining with Serene Seasons installed.

## [2.7.3] - 2025-09-30

### Added

- Port to 1.21.9.

## [2.7.2] - 2025-09-25

### Added

- Support for 1.21.8.

### Removed

- Support for 1.21.3 and 1.21.5. The only supported older version is 1.21.1.

## [2.7.1] - 2025-04-03

### Added

- Support for 1.21.5.
- German translations, thanks to Lucanoria.

## [2.7.0] - 2025-02-15

### Added

- Support for [Snowbound](https://modrinth.com/mod/snow_bound)'s Boreal Forest.

### Changed

- Updated Russian translations, thanks to achookh.
- Updated YACL.

## [2.6.0] - 2024-12-17

### Added

- Support for 1.21.2, 1.21.3 and 1.21.4.

### Removed

- Support for 1.20.1, 1.20.4 and 1.20.6.

## [2.5.4] - 2024-11-24

### Fixed

- Random freezes and crashes in singleplayer and on dedicated servers caused by the Serene Seasons compatibility.

## [2.5.3] - 2024-08-23

### Fixed

- Log spam when Serene Seasons is installed.
- Deadlock issues.

## [2.5.2] - 2024-08-19

### Changed

- Updated to MRU 1.0.0.

### Fixed

- Some configuration inconsistencies.

## [2.5.1] - 2024-08-13

### Added

- Russian, Chinese and French translations.

### Changed

- Snow is placed and melted based on temperature when Serene Seasons is installed.

### Fixed

- Crash with C2ME when Serene Seasons is installed.
- The config screen sometimes not opening from Mod Menu.

## [2.5.0] - 2024-08-11

### Added

- Support for 1.21.1.
- Support for [Serene Seasons](https://modrinth.com/mod/serene-seasons) on 1.20.4 and newer, with the "Respect Season Mods" and "Melt Snow Seasonally" options.
- Support for [Sensible](https://modrinth.com/datapack/sensible-biomes)'s Snowy Woodland.
- Support for [Austin's Biomes Expansion](https://modrinth.com/datapack/austins-biomes-expansion)'s Snowy Dark Forest, Snowy Forest, Snowy Forest Strand, Snowy Stony Shore and Snowy Strand.

## [2.4.0] - 2024-06-23

### Added

- Support for 1.21.
- Support for [William Wythers' Expanded Ecosphere](https://modrinth.com/mod/expanded-ecosphere)'s Snowy Bog, Snowy Canyon, Snowy Fen, Snowy Peaks, Snowy Thermal Taiga, Snowy Tundra, Crimson Tundra, Deep Snowy Taiga, Highlands, Icy Crags, Icy Shore, Icy Volcano and Ice Cap.

### Removed

- Support for 1.20.2.

## [2.3.0] - 2024-05-18

### Added

- Support for 1.20.6.
- Support for [Terrestria](https://modrinth.com/mod/terrestria)'s Snowy Hemlock Forest, Snowy Hemlock Clearing, Caldera and Caldera Foothills.
- Support for [Wilder Wild](https://modrinth.com/mod/wilder-wild)'s Frozen Caves, Snowy Dying Forest, Snowy Dying Mixed Forest and Snowy Old Growth Pine Taiga.
- Support for [Promenade](https://modrinth.com/mod/promenade)'s Glacarian Taiga.

## [2.2.0] - 2024-02-19

### Added

- Support for Traverse's Snowy Coniferous Forest, Snowy Coniferous Wooded Hills and Snowy High Coniferous Forest.
- Support for Woods And Mires' Snowy Fell and Snowy Pine Forest.
- Support for Regions Unexplored's Cold Deciduous Forest, Cold Boreal Taiga, Icy Heights, Spires, Frozen Pine Taiga and Frozen Tundra.

### Changed

- Migrated to the Stonecutter build system, so updates ship for every 1.20 version at once.
- New biome support needs the "Supported Biomes" list to be reset from the config screen.

## [2.1.0] - 2023-12-15

### Added

- Support for 1.20.4.
- Support for [Terralith](https://modrinth.com/mod/terralith), [Biomes O' Plenty](https://modrinth.com/mod/biomes-o-plenty) and [Oh The Biomes You'll Go](https://modrinth.com/mod/biomesyougo).

## [2.0.0] - 2023-11-03

### Changed

- The config screen now uses YACL.
- The config moved from `snowundertrees.json5` to `snowundertrees.config.json`. Old configs are migrated automatically.
- Restructured the mod's classes to be easier to maintain.

## [1.1.1] - 2023-10-14

### Changed

- Renamed the `filteredBiomes` config option to `supportedBiomes`.
- Confirmed working on 1.20.2.

## [1.1.0] - 2023-03-21

### Changed

- Replaced Cloth Config with owo-config. Old config files are no longer valid.
- Ported to 1.19.4 and later 1.20.

## [1.0.0] - 2023-01-08

### Added

- Initial release for 1.18.2, 1.19.2 and 1.19.3.
