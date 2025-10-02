# Vim On IntelliJ

A plugin that bridges Neovim and IntelliJ IDEA, allowing you to leverage the power of both editors simultaneously. When IdeaVim isn't enough and you need the full power of Neovim alongside IntelliJ's excellent Kotlin LSP and IDE features, this plugin creates a seamless workflow between the two.

## Features

- **Bidirectional File Synchronization**: Files opened in IntelliJ automatically open in Neovim and vice versa
- **Cursor Position Sync**: Cursor movements in IntelliJ are reflected in Neovim
- **Auto-reload**: File changes in IntelliJ trigger automatic buffer reloads in Neovim
- **Socket-based Communication**: Uses Neovim's RPC API over TCP socket (port 6666)

## Prerequisites

- IntelliJ IDEA 2022.1 or newer (build 221+)
- Neovim with socket support
- A companion Neovim plugin (requires `intellij-on-vim` Lua module)

## Building the Plugin

### Build the Plugin Artifact

To build the plugin distribution:

```bash
./gradlew buildPlugin
```

The plugin artifact will be generated in:
```
build/distributions/vim-on-intellij-<version>.zip
```

### Publish to JetBrains Marketplace (Optional)

To publish the plugin to the JetBrains Plugin Marketplace:

```bash
./gradlew publishPlugin
```

**Note**: You need to set the `PUBLISH_TOKEN` environment variable with your JetBrains Marketplace token before publishing.

## Installation

### Method 1: Install from Disk (Recommended for Development)

1. Build the plugin using the command above
2. Open IntelliJ IDEA
3. Go to **Settings/Preferences** → **Plugins**
4. Click the gear icon ⚙️ in the top-right corner
5. Select **Install Plugin from Disk...**
6. Navigate to `build/distributions/` in your project directory
7. Select the `vim-on-intellij-<version>.zip` file
8. Click **OK**
9. Restart IntelliJ IDEA when prompted

### Method 2: Install from JetBrains Marketplace

Once published, you can install directly from the marketplace:

1. Open IntelliJ IDEA
2. Go to **Settings/Preferences** → **Plugins**
3. Click on the **Marketplace** tab
4. Search for "VimOnIntellij"
5. Click **Install**
6. Restart IntelliJ IDEA

## Configuration

### Neovim Setup

1. Start Neovim with a socket listener on port 6666:

```bash
nvim --listen localhost:6666
```

Or add this to your Neovim configuration to always start with socket support:

```lua
-- In your init.lua or init.vim
vim.fn.serverstart('localhost:6666')
```

2. Install the companion Neovim plugin that provides the `intellij-on-vim` module (required for proper integration)

### IntelliJ Setup

Config IntelliJ to work on single tab (neovim buffer can only sync with one tab on IntelliJ)
- Press Cmd + ,
- In the Settings/Preferences dialog, go to Editor | General | Editor Tabs.
- Tab placement: None
- Tab limit: 1

Once the plugin is installed, it will automatically:
- Connect to Neovim on `localhost:6666`
- Listen for file open events
- Sync cursor positions
- Handle file changes

## How It Works

The plugin establishes a bidirectional communication channel with Neovim:

1. **IntelliJ → Neovim**:
   - When you open a file in IntelliJ, it opens in Neovim
   - When you move the cursor in IntelliJ, Neovim's cursor follows
   - When you save a file in IntelliJ, Neovim reloads the buffer

2. **Neovim → IntelliJ**:
   - When you switch buffers in Neovim (`BufEnter`), IntelliJ opens the corresponding file
   - Cursor position requests from Neovim sync the editor position in IntelliJ

## Development

### Project Structure

```
vim-on-intellij/
├── src/main/kotlin/com/longcb/vimonintellij/
│   ├── VimOnIntellijService.kt          # Main service
│   ├── ProjectOpenStartupActivity.kt    # Startup hook
│   ├── intellij/                        # IntelliJ event listeners
│   │   └── listeners/
│   │       ├── FileEventsListener.kt    # File open/close events
│   │       ├── CaretEventsListener.kt   # Cursor movement events
│   │       └── VirtualFileEventsListener.kt  # File change events
│   └── neovim/                          # Neovim RPC API
│       ├── NeovimApi.kt                 # Main API client
│       ├── SocketConnection.kt          # TCP socket connection
│       ├── notificationhandler/         # Handlers for Neovim notifications
│       └── requesthandler/              # Handlers for Neovim requests
└── build.gradle.kts                     # Build configuration
```

### Running in Development Mode

```bash
./gradlew runIde
```

This will start a new IntelliJ IDEA instance with the plugin installed for testing.

### Running Tests

```bash
./gradlew test
```

## Troubleshooting

### Plugin won't connect to Neovim

- Ensure Neovim is running with socket listener on port 6666
- Check that no firewall is blocking localhost connections
- Verify the `intellij-on-vim` Lua module is installed in Neovim

### Files not syncing

- Check IntelliJ's Event Log for error messages
- Verify the Neovim socket is accessible: `netstat -an | grep 6666`
- Restart both IntelliJ and Neovim

### Compatibility Issues

If you see "plugin is not compatible with the current version of the IDE":

1. Update the `untilBuild` version in `build.gradle.kts`:
   ```kotlin
   patchPluginXml {
       sinceBuild.set("221")
       untilBuild.set("252.*")  // Update this to your IDE version
   }
   ```
2. Rebuild the plugin: `./gradlew clean buildPlugin`
3. Reinstall from the new artifact

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

See [LICENSE.txt](LICENSE.txt) for details.

## Author

**LongCB**
- Email: support@longcb.me
- Website: https://www.longcb.me
