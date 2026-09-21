# veyron

Ghost-client base for Minecraft 1.21.11 (Fabric + Yarn). No modules included.

## Setup (IntelliJ IDEA)

1. Install JDK 21 and set it as Project SDK.
2. Open this folder in IDEA, trust the project, wait for Gradle sync (bundled Gradle is fine).
3. Run `genSources`, then `runClient`.
4. Drop your `bold.otf` (SF Pro Display Bold) into
   `src/main/resources/assets/veyron/textures/font/bold.otf`
   or the GUI falls back to the default font.

## Usage

- Right Shift — open/close ClickGui (works in game and in MainMenu).
- Account swapping: install
  [IAS 9.0.7+1.21.11-fabric](https://modrinth.com/mod/in-game-account-switcher)
  into your `mods` folder (for dev runs: `run/mods/`).
  The built-in Accounts tab in ClickGui handles offline nicknames.
- Panels: drag header, `v` collapses, LMB toggles module, RMB sets bind (ESC clears, MMB resets).
- Bottom dock: Accounts (add/delete/offline login) | Theme (accent color) | Search.
- Add your modules in `ModuleManager.load()`.
