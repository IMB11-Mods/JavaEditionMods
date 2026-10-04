# Changelog

## Unreleased

## [2.5.2+lts] - 2026-09-09

### Added

- Added a way to disable item sounds individually.

### Changed

- Refreshed the Simplified Chinese localization.

### Fixed

- Prevented a NeoForge crash caused by other mods initializing blocks prematurely.

## [2.5.1+edge] - 2026-06-29

**Pre-release.**

### Fixed

- Restored customization of the swoosh sound.

## [2.5.1+lts] - 2026-06-29

### Fixed

- Restored customization of the swoosh sound.

## [2.5.0+lts] - 2026-05-27

### Added

- Introduced a volume setting that applies to the whole mod.
- Added audio feedback when scrolling through Bundles.

### Changed

- UI sounds no longer display subtitles.
- Assigned the mod's sounds to the UI sound source.

### Fixed

- Made the option for hiding the menu work correctly.

## [2.4.25+lts] - 2026-05-03

### Added

- Ported this release to NeoForge.

### Removed

- Eliminated the use of Suppliers.

## [2.4.24+lts] - 2026-03-24

### Added

- Added Minecraft 26.1 compatibility.
- Introduced caching for dynamic item sounds.

### Changed

- Item-specific dynamic sound definitions now take precedence over tags.
- Switched to Fabric's client tag API.

### Removed

- Removed unusual sound types.

### Fixed

- Cleaned up missing sounds.

## [2.4.23+edge] - 2026-02-19

### Added

- Added a configuration toggle for dynamic item sounds.

### Changed

- Combined the two typing-sound configurations.

### Fixed

- Improved handling of invalid regular expressions.
- Restricted hotbar-change audio to actual slot changes.

## [2.4.22+edge] - 2025-12-12

### Added

- Added compatibility with the spawn eggs in Minecraft 1.21.11.

### Fixed

- Prevented tags from loading prematurely.

## [2.4.22+lts] - 2025-12-12

### Fixed

- Prevented tags from loading prematurely.

## [2.4.21+lts] - 2025-12-09

### Fixed

- Corrected Fabric remapping problems.

## [2.4.21+edge] - 2025-12-09

### Added

- Added Minecraft 1.21.11 compatibility.

## [2.4.20+lts] - 2025-12-09

### Fixed

- Restored typing audio in EMI and extended it to additional vanilla and modded edit boxes.
- Corrected Fabric remapping problems.

## [2.4.18+lts] - 2025-12-05

### Added

- Added audio feedback for typing in EMI.

### Fixed

- Prevented a crash when the mod is run on a NeoForge server.

## [2.4.20+edge] - 2025-12-05

### Fixed

- Made copper tools use their copper sound effects.

## [2.4.19+edge] - 2025-11-13

### Added

- Added a cooldown and subtitle for sword-swoosh audio.

### Fixed

- Stopped other players' swings from triggering the sword-swoosh sound.
- Resolved a startup crash on NeoForge for Minecraft 1.21.1.

## [2.4.17+lts] - 2025-11-13

### Added

- Added a cooldown and subtitle for sword-swoosh audio.

### Fixed

- Stopped other players' swings from triggering the sword-swoosh sound.
- Resolved a startup crash on NeoForge for Minecraft 1.21.1.

## [2.4.18+edge] - 2025-11-13

### Added

- Introduced sword-swoosh sound effects.

### Changed

- Adjusted the configuration interface to more closely match YACL.

### Fixed

- Corrected stripped mangrove sounds.
- Corrected deprecated tags.
- Resolved left-ear-only audio with Sound Physics Perfected installed, thanks to [@FalseMSP](https://github.com/FalseMSP).

## [2.4.16+lts] - 2025-11-13

### Added

- Introduced sword-swoosh sound effects.

### Changed

- Adjusted the configuration interface to more closely match YACL.

### Fixed

- Corrected stripped mangrove sounds.
- Corrected deprecated tags.
- Resolved left-ear-only audio with Sound Physics Perfected installed, thanks to [@FalseMSP](https://github.com/FalseMSP).

## [2.4.16+edge] - 2025-09-27

### Fixed

- Restored normal volume when Sound Physics Perfected is absent.

## [2.4.15+lts] - 2025-09-27

### Fixed

- Restored normal volume when Sound Physics Perfected is absent.

## [2.4.15+edge] - 2025-09-25

**Pre-release.**

### Added

- Integrated support for Sound Physics Perfected.

### Fixed

- Prevented a crash while accessing the contributor API.

## [2.4.14+lts] - 2025-09-25

### Added

- Integrated support for Sound Physics Perfected.

### Fixed

- Prevented crashes on NeoForge dedicated servers.
- Prevented a crash while accessing the contributor API.

## [2.4.13.1+lts] - 2025-07-23

### Fixed

- Corrected version constraints.

## [2.4.14.1+edge] - 2025-07-23

**Pre-release.**

### Fixed

- Corrected a version constraint.

## [2.4.14+edge] - 2025-07-23

**Pre-release.**

Fabric release for Minecraft 1.21.6–1.21.8.

### Added

- Extended support to Minecraft 1.21.7 and 1.21.8.

### Fixed

- Removed blur artifacts left behind after viewing the configuration screen.

## [2.4.13+lts] - 2025-07-23

Long-term support (LTS) release for Minecraft 1.21–1.21.1 on Fabric and NeoForge. The release notes do not list individual changes.

## [2.4.12] - 2025-06-29

**Upgrade note:** The upstream release recommends backing up the configuration before moving to Minecraft 1.21.6.

### Added

- Added Minecraft 1.21.6 support on Fabric and NeoForge.

### Removed

- Ended Minecraft 1.21.3 support on both loaders.
- Ended NeoForge support for Minecraft 1.21.4 and 1.21.5.

### Fixed

- Restored potion audio on Fabric and on NeoForge for Minecraft 1.21.5.

## [2.4.11] - 2025-04-09

### Fixed

- Corrected resource reloads on NeoForge for Minecraft 1.21.5.
- Fixed mixins failing to apply for some NeoForge users on Minecraft 1.21.4 and 1.21.5, which left the mod nonfunctional.
- Resolved a loading crash on NeoForge for Minecraft 1.21.5.

## [2.4.10] - 2025-04-06

### Fixed

- Prevented intermittent crashes when entering a singleplayer world on NeoForge for Minecraft 1.21.5.
- Stopped unrelated inventory interactions from triggering hotbar-scroll audio in some Minecraft 1.21.5 scenarios.

## [2.4.9] - 2025-04-01

### Added

- Added Minecraft 1.21.5 compatibility.

### Fixed

- Prevented launch crashes when other mods access the configuration too early.
- Prevented flower-removal crashes in plant pots on Minecraft versions earlier than 1.21.3.
- Resolved a rare NeoForge 1.21.4 launch crash involving edits to the chat mention list.
- Restored support for custom sound events registered by resource packs.

## [2.4.8] - 2025-03-18

### Added

- Added an opt-in console diagnostic for failures to retrieve sound types; it is disabled by default.

### Changed

- Refreshed the Russian localization, thanks to [@mpustovoi](https://github.com/mpustovoi).

### Fixed

- Reduced excessive logging caused by mods registering blocks through sound types.
- Prevented NeoForge startup crashes with Raise Sound Limits Simplified installed.

## [2.4.7] - 2025-03-15

### Fixed

- Prevented startup crashes with Remove Sound Limit Simplified installed.

## [2.4.6] - 2025-03-15

### Added

- Added a recommendation to install [Raise Sound Limits Simplified](https://modrinth.com/mod/rsls).

### Changed

- Plant pots now play dynamic audio from the flower item when flowers are inserted or removed.

### Removed

- Deleted legacy convention tags that were no longer needed after Minecraft 1.20.x support ended, reducing the mod's size.

### Fixed

- Prevented crashes when opening mod-specific options.
- Corrected translations for the "Hide Button In Sound Mixer Menu" setting.
- Made sound events supplied by non-mod resource packs available in the configuration screen and playable by Sounds.

## [2.4.5] - 2025-03-01

**Dependency change:** Install YACL separately; this release no longer bundles it.

### Changed

- Updated NeoForge to address Minecraft 1.21.4 crashes.
- Replaced configuration-button background images with icons to improve launch time and configuration-screen performance.

### Removed

- Removed the bundled YACL dependency.

## [2.4.4] - 2025-02-07

### Fixed

- Corrected the order of sounds triggered by hotbar key presses.

## [2.4.3] - 2025-02-07

### Fixed

- Restored hotbar keybinds on Minecraft versions earlier than 1.21.3.

## [2.4.2] - 2025-02-07

### Fixed

- Addressed Fabric problems when opening the creative inventory.

## [2.4.1] - 2025-02-07

### Fixed

- Resolved mixin issues by enforcing the required Fabric Loader version, 0.16.10.

## [2.4.0] - 2025-02-06

**Dependency change:** Update M.R.U to version 1.0.8.

### Added

- Added per-block exclusions for custom block audio under World → Blocks.
- Introduced sound-definition replacement events for mod developers to support custom NBT/component-based items.

### Changed

- Refreshed the Simplified Chinese localization, thanks to [@Chiloven945](https://github.com/Chiloven945).
- Migrated the build system to address NeoForge issues.
- Updated the required M.R.U version to 1.0.8.

### Removed

- Removed the included Architectury API dependency.

### Fixed

- Restored sound events for hotbar keybinds.
- Stopped the scroll sound from playing when the creative inventory opens.

## [2.3.2] - 2025-01-01

### Changed

- Moved the Ko-Fi supporter-list request off the main thread for a small performance improvement.

### Fixed

- Prevented text from becoming blurry after using the configuration screen on Minecraft 1.21.3 and later.

## [2.3.1] - 2024-12-31

### Fixed

- Corrected NeoForge dependency problems.

## [2.3.0] - 2024-12-15

### Added

- Added Minecraft 1.21.4 compatibility.

### Removed

- Ended Minecraft 1.20.1 support.

## [2.2.1] - 2024-11-27

### Changed

- Updated the bundled Architectury API.

### Fixed

- Addressed crashes caused by Architectury API.
- Corrected internal build scripts for the NeoForge Minecraft 1.21.3 release.

## [2.2.0] - 2024-10-26

**Migration required:** Move any modified files from `config/sounds/dynamic_sounds` into a resource pack. The removed packing system no longer loads that directory.

### Added

- Added [Trash Slot](https://modrinth.com/mod/trashslot/versions) compatibility.
- Introduced dedicated paper sounds, thanks to [@Fydar](https://github.com/Fydar).
- Added Minecraft 1.21.2 and 1.21.3 compatibility.

### Removed

- Removed the packing system to address a range of bugs.

### Fixed

- Restored placement and breaking audio for glass variants, including stained glass blocks and panes.
- Corrected server-side loading behavior on Forge and NeoForge.

## [2.1.0] - 2024-09-20

**Troubleshooting note:** Upstream recommends deleting `.minecraft/config/sounds/dynamic_sounds` and restarting if resource-reload problems persist, then reporting unresolved problems on GitHub.

### Added

- Added a `mod_utils.json` setting to show or hide the Sounds button in Minecraft's volume options.

### Changed

- Replaced resource-loading synchronizers to address resource-reload problems.
- Made item-drop audio respect the existing "Item Sound Cooldown" setting instead of a hardcoded cooldown.

## [2.0.4] - 2024-08-19

### Fixed

- Addressed an unspecified Forge issue.

## [2.0.3] - 2024-08-18

### Fixed

- Addressed mixin problems affecting Forge and NeoForge.

## [2.0.2] - 2024-08-17

### Fixed

- Reduced excessive logging in some situations ([#101](https://github.com/IMB11-Mods/Sounds/issues/101)).

## [2.0.1] - 2024-08-16

### Fixed

- Corrected the MixinExtras dependency packaged in the Forge JAR.

## [2.0.0] - 2024-08-16

**Support change:** Upstream recommends Minecraft 1.20.1 or 1.21 for continued updates after support for 1.20.4 and 1.20.6 ends.

### Added

- Added NeoForge support for Minecraft 1.21 and Forge support for Minecraft 1.20.1.
- Added dynamic audio for Minecraft 1.21 items; meats, fish, crop foods, and bowl foods; bottles of enchanting; all boat and chest-boat variants; all minecart variants; turtle armor; and scute variants, including armadillo scutes.

### Changed

- Externalized the default resource pack so its dynamic sound definitions can be edited in `.minecraft/config/sounds/dynamic_sounds`.

### Removed

- Ended Minecraft 1.20.4 and 1.20.6 support.

### Fixed

- Restored dynamic audio for middle-clicking and hotbar-key interactions with items in creative inventory tabs.
- Corrected the swoosh sound when rapidly moving items out of a crafting table's output slot.

## [1.1.5] - 2024-08-11

### Added

- Added Minecraft 1.21.1 compatibility.

### Changed

- Adjusted the supported MRU dependency range.

## [1.1.4] - 2024-08-08

### Added

- Added Simplified Chinese localization from [@Chiloven945](https://github.com/Chiloven945) and Russian localization from [@mpustovoi](https://github.com/mpustovoi).
- Added configurable item-sound cooldowns.

### Deprecated

- Announced the final update for Minecraft 1.20.4 and 1.20.6, with a recommendation to move to 1.20.1 or 1.21 for future updates.

### Fixed

- Prevented excessive sound playback when Inventive Inventory sorts items.
- Reduced excessive audio when using Chat Patches.
- Prevented repeated audio when transferring many items between inventories, including shulker boxes, through the new cooldown settings.

## [1.1.3] - 2024-07-28

### Fixed

- Corrected the `TagPair` implementation to improve mod compatibility without a breaking change.

## [1.1.2] - 2024-07-28

### Changed

- Improved block-sound performance.

## [1.1.1] - 2024-07-19

### Fixed

- Prevented a null-handler crash when `ConfigGroup.getHandler()` returns null before `ConfigClassHandler.instance()` is called.
- Repaired the Discord invitation link.

## [1.1.0] - 2024-07-14

### Added

- Added Simplified Chinese language support.

### Changed

- Moved World Config block sounds (`TagPair` definitions) from the configuration screen into JSON resource-pack loading, allowing custom definitions for modded blocks. See the [Sounds documentation](https://docs.imb11.dev/sounds).

### Fixed

- Stopped repeated typing audio while holding Shift in an anvil.

[Full changelog](https://github.com/IMB11/Sounds/compare/1.0.1...1.1.0)

## [1.0.1] - 2024-06-29

### Fixed

- Resolved mixin conflicts with MidnightLib and DoAPI that could crash the game.

## [1.0.0] - 2024-06-28

### Added

- Added Minecraft 1.21 compatibility.
- Introduced more than 56 block sounds sourced from Zapsplat, bringing the feature set in line with QualitySounds.
- Added an incompatibility screen explaining the overlap and conflict with QualitySounds.
- Added a configuration-screen splash thanking Ko-Fi supporters.
- Introduced `TagPair` classes for mods to provide custom block-sound configurations.

### Changed

- Simplified the configuration interface, replacing the Ko-Fi and Discord grid widgets with buttons beside Done.
- Cleaned up internal implementations to reduce hard-to-reproduce bugs.

### Fixed

- Restored distinct ender pearl audio when its option is enabled.
- Stopped item-drop audio from repeating when opening an inventory with Inventorio.
- Restored the configuration-screen background so the previous screen does not show through.
- Replaced crashes from invalid configured sound-event IDs with a toast prompting users to correct the broken sound.
- Cleaned up mixin targets, particularly for leashes and cakes, to reduce changes needed between Minecraft versions.

## [0.9.0] - 2024-06-14

**Custom sounds:** Keep the resource pack providing a custom sound enabled. Reset custom sound events in the configuration interface before disabling that pack.

### Added

- Made individual sound events editable in the configuration interface without manually changing JSON.
- Added sound-event previews to the configuration interface.

### Changed

- Replaced fishing-rod audio with the quieter Water Drip Splash event.
- Replaced anvil placement audio with the less intense Entity Fall On Block event.

## [0.8.1] - 2024-05-27

### Fixed

- Restored dynamic sound definitions missing from JAR resources.
- Ensured access wideners are applied on Minecraft 1.20.6.

## [0.8.0] - 2024-05-26

### Added

- Added Minecraft 1.20.6 compatibility.
- Added [CarryOn](https://modrinth.com/mod/carry-on) compatibility through the "Ignore Silenced Status Effects" setting.
- Added cake-eating audio.
- Added an ender pearl sound-variety setting that substitutes randomized chorus-fruit teleport audio for the usual impact teleport sound.
- Added a setting to ignore clicks on empty inventory slots, also covering the hotbar item-drop logic.

### Removed

- Ended Minecraft 1.20.2 support.
- Removed dynamic eating audio because the implementation did not work.

### Fixed

- Made dynamic audio consistent between Minecraft versions running the same Sounds release.
- Attempted to resolve macOS launch problems; upstream explicitly noted that this fix was unverified.

## [0.7.0] - 2024-04-07

**Configuration note:** Upstream instructs users to delete their existing configuration files to apply the revised default sounds.

### Added

- Added controller-mod compatibility and narrator support to the configuration screen.
- Introduced an invalid-action sound for disallowed inventory operations.
- Added audio when filling a flower pot with a plant.

### Changed

- Adjusted numerous default sounds to match their ExtraSounds counterparts.
- Made the creative-inventory Shift-delete action use a fizzing sound.
- Lowered typing pitch and volume, matching inventory typing to chat typing.
- Made mention notifications clearer and quieter.
- Made potion audio less intrusive.

## [0.6.2] - 2024-03-11

### Fixed

- Corrected YACL not being applied properly.

## [0.6.1] - 2024-03-11

### Fixed

- Addressed a YACL issue.

## [0.6.0] - 2024-03-10

**Breaking configuration change:** Existing configurations are incompatible. Back up any configuration files you have customized before upgrading.

### Added

- Added automatic EMI support.
- Introduced action sounds for Frost Walker ice formation, snapping leads, and eating sounds that vary with hunger.

### Changed

- Reorganized the configuration interface.

### Fixed

- Reduced excessive memory use.
- Corrected inventory-drag audio on LAN servers.

[Full changelog](https://github.com/IMB11/Sounds/compare/0.5.0...0.6.0)

## [0.5.0] - 2024-01-20

### Added

- Added cooldown settings for chat audio to reduce repeated sounds on busy servers; typing audio is excluded.

### Changed

- Refactored item-sound logic, contributed by [@MUKSC](https://github.com/MUKSC).
- Migrated to [stonecutter-kt](https://github.com/kikugie/stonecutter-kt) by [@kikugie](https://github.com/kikugie).

### Fixed

- Corrected behavior when `enableDynamicSounds = false`, contributed by [@MUKSC](https://github.com/MUKSC).
- Prevented a `NullPointerException` with bow-pull audio disabled, contributed by [@MUKSC](https://github.com/MUKSC).
- Stopped duplicated audio after opening a world to LAN, contributed by [@MUKSC](https://github.com/MUKSC).
- Stopped repeated Shift-key audio in EMI, contributed by [@MUKSC](https://github.com/MUKSC).
- Made `BlockItem` audio respect JSON sound definitions and their priorities.

[Full changelog](https://github.com/IMB11/Sounds/compare/0.4.0...0.5.0)

## [0.4.0] - 2023-12-12

### Added

- Added Minecraft 1.20.4 compatibility.
- Added a Sounds configuration button to the sound-options menu.
- Introduced interaction audio for repeaters, comparators, jukeboxes, daylight sensors, and furnace minecarts.
- Added audio for gaining and losing both positive and negative status effects.
- Added [EMI](https://modrinth.com/mod/emi) support.
- Introduced dynamic audio for spawn eggs and ordinary eggs, the Netherite Upgrade Trim, Disc Fragment 5, brushes, dragon's breath, and all enchanted books.
- Added optional `volume` and `pitch` fields to sound-definition files, overriding defaults for the specified items.
- Added an option to ignore system messages when playing chat audio.
- Added a toggle to disable audio for empty hotbar slots.
- Introduced inventory typing audio for anvil text fields and the creative-inventory search box.

### Changed

- Renamed the project from Sonance to Sounds.
- Split configuration screens into UI Sounds, Gameplay Sounds, and Mod Sounds.
- Made chat mentions require an `@` before the player's username by default; the requirement can be disabled in the configuration.

[Full changelog](https://github.com/IMB11/Sounds/compare/0.3.0...0.4.0)

## [0.3.0] - 2023-11-12

No release notes were published for this Sonance release.

## [0.2.1+1.20] - 2023-11-08

Sonance release for Minecraft 1.20–1.20.1. Upstream provides only the comparison link below, with no written change details.

[Full changelog](https://github.com/mineblock11/Sonance/compare/0.2.0+1.20...0.2.1+1.20)

## [0.2.1+1.20.2] - 2023-11-08

Sonance release for Minecraft 1.20.2. Upstream provides only the comparison link below, with no written change details.

[Full changelog](https://github.com/mineblock11/Sonance/compare/0.2.0+1.20.2...0.2.1+1.20.2)

## [0.2.0+1.20.2] - 2023-11-07

Sonance release for Minecraft 1.20.2. Its GitHub notes link to the [shared Sonance 0.2.0 changelog on Modrinth](https://modrinth.com/mod/sounds/version/eM5D6BNh); those changes are included below.

### Added

- Added item audio for dry and wet mob loot; armor and armor materials; shiny, dirty, and ingot metals; pottery sherds; smithing and banner templates; brewing items; bones; fireworks; and tools such as bows, crossbows, and fishing rods.
- Introduced screen audio for hoppers, droppers, and dispensers.
- Enabled resource packs to customize dynamic sound events through `sonance/<type>` definitions, allowing servers to override players' sounds for custom assets and bringing Sonance to feature parity with ExtraSounds. Definitions specify a `soundEvent` identifier and a `keys` list of item identifiers or `#`-prefixed tags. See the [upstream item-definition examples](https://github.com/mineblock11/Sonance/tree/master/src/main/generated/assets/minecraft/sonance/items).

### Changed

- Revised screen audio, particularly for beacons and smithing tables.
- Adopted MRU's YACL helper methods.

### Fixed

- Prevented crashes caused by certain screens.

## [0.2.0+1.20] - 2023-11-07

Sonance release for Minecraft 1.20–1.20.1.

### Added

- Added item audio for dry and wet mob loot; armor and armor materials; shiny, dirty, and ingot metals; pottery sherds; smithing and banner templates; brewing items; bones; fireworks; and tools such as bows, crossbows, and fishing rods.
- Introduced screen audio for hoppers, droppers, and dispensers.
- Enabled resource packs to customize dynamic sound events through `sonance/<type>` definitions, allowing servers to override players' sounds for custom assets and bringing Sonance to feature parity with ExtraSounds. Definitions specify a `soundEvent` identifier and a `keys` list of item identifiers or `#`-prefixed tags. See the [upstream item-definition examples](https://github.com/mineblock11/Sonance/tree/master/src/main/generated/assets/minecraft/sonance/items).

### Changed

- Revised screen audio, particularly for beacons and smithing tables.
- Adopted MRU's YACL helper methods.

### Fixed

- Prevented crashes caused by certain screens.

[Full changelog](https://github.com/mineblock11/Sonance/compare/0.1.0+1.20...0.2.0+1.20)

## [0.1.0+1.20.2] - 2023-10-26

### Added

- Ported Sonance to Minecraft 1.20.2.

[Full changelog](https://github.com/mineblock11/Sonance/compare/0.1.0+1.20...0.1.0+1.20.2)

## [0.1.0+1.20] - 2023-10-21

### Added

- Published the initial Sonance release for Minecraft 1.20–1.20.1.

[Full changelog](https://github.com/mineblock11/Sonance/commits/0.1.0+1.20)

[2.5.2+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.5.2%2Blts
[2.5.1+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.5.1%2Bedge
[2.5.1+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.5.1%2Blts
[2.5.0+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.5.0%2Blts
[2.4.25+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.25%2Blts
[2.4.24+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.24%2Blts
[2.4.23+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.23%2Bedge
[2.4.22+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.22%2Bedge
[2.4.22+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.22%2Blts
[2.4.21+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.21%2Blts
[2.4.21+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.21%2Bedge
[2.4.20+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.20%2Blts
[2.4.18+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.18%2Blts
[2.4.20+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.20%2Bedge
[2.4.19+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.19%2Bedge
[2.4.17+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.17%2Blts
[2.4.18+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.18%2Bedge
[2.4.16+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.16%2Blts
[2.4.16+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.16%2Bedge
[2.4.15+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.15%2Blts
[2.4.15+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.15%2Bedge
[2.4.14+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.14%2Blts
[2.4.13.1+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.13.1%2Blts
[2.4.14.1+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.14.1%2Bedge
[2.4.14+edge]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.14%2Bedge
[2.4.13+lts]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.13%2Blts
[2.4.12]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.12
[2.4.11]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.11
[2.4.10]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.10
[2.4.9]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.9
[2.4.8]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.8
[2.4.7]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.7
[2.4.6]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.6
[2.4.5]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.5
[2.4.4]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.4
[2.4.3]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.3
[2.4.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.2
[2.4.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.1
[2.4.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.4.0
[2.3.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.3.2
[2.3.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.3.1
[2.3.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.3.0
[2.2.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.2.1
[2.2.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.2.0
[2.1.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.1.0
[2.0.4]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.0.4
[2.0.3]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.0.3
[2.0.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.0.2
[2.0.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.0.1
[2.0.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/2.0.0
[1.1.5]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.1.5
[1.1.4]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.1.4
[1.1.3]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.1.3
[1.1.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.1.2
[1.1.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.1.1
[1.1.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.1.0
[1.0.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.0.1
[1.0.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/1.0.0
[0.9.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.9.0
[0.8.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.8.1
[0.8.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.8.0
[0.7.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.7.0
[0.6.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.6.2
[0.6.1]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.6.1
[0.6.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.6.0
[0.5.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.5.0
[0.4.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.4.0
[0.3.0]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.3.0
[0.2.1+1.20]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.2.1%2B1.20
[0.2.1+1.20.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.2.1%2B1.20.2
[0.2.0+1.20.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.2.0%2B1.20.2
[0.2.0+1.20]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.2.0%2B1.20
[0.1.0+1.20.2]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.1.0%2B1.20.2
[0.1.0+1.20]: https://github.com/IMB11-Mods/Sounds/releases/tag/0.1.0%2B1.20

