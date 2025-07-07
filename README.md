# 🎁 Redeemify

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Paper](https://img.shields.io/badge/Paper-1.21.4-brightgreen.svg)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)

A powerful and flexible Paper plugin for creating and managing redeemable codes with customizable rewards. Perfect for giveaways, events, promotions, and player engagement!

## ✨ Features

### 🔑 **Flexible Code System**
- **Unlimited codes** with custom names and rewards
- **Expiration dates** for time-limited promotions
- **Usage limits** (per-player and global)
- **Command-based rewards** with placeholder support

### 📊 **Smart Management**
- **Per-player restrictions** to prevent abuse
- **Usage tracking** and statistics
- **Automatic data persistence** with async saving
- **Hot-reload** configuration without restart

### 🛡️ **Robust & Secure**
- **Permission-based** access control
- **Error handling** and validation
- **Performance optimized** with caching
- **Thread-safe** operations

### 🎯 **Easy Administration**
- **Simple YAML configuration**
- **Comprehensive statistics**
- **Admin commands** for management
- **Detailed logging** and debug options

## 🚀 Quick Start

### Installation
1. Download the latest release from [Releases](https://github.com/Mxykl/Redeemify/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure your codes in `plugins/Redeemify/codes.yml`

### Basic Usage

**For Players:**
```
/redeem <code>
```

**For Administrators:**
```
/redeemify reload    # Reload configuration
/redeemify info      # Show plugin information
/redeemify stats     # Display usage statistics
```

## 📝 Configuration

### Creating Codes (`codes.yml`)

```yaml
codes:
  welcome-gift:
    expires: "2025-12-31 23:59"  # Date format: yyyy-MM-dd HH:mm
    max-uses: 100                # Maximum total uses (-1 = unlimited)
    per-player: true             # Each player can only use once
    commands:
      - "eco give %player% 1000"
      - "give %player% diamond 5"
      - "tell %player% Welcome to our server!"
      
  unlimited-daily:
    expires: "-1"                # Never expires
    max-uses: -1                 # Unlimited uses
    per-player: false            # Players can use multiple times
    commands:
      - "eco give %player% 100"
```

### Available Placeholders
- `%player%` - Player's username
- `%code%` - The redeemed code name

### Message Customization (`messages.yml`)

Fully customizable messages with color code support:

```yaml
messages:
  code-redeemed: "&aSuccessfully redeemed code: &e%code%&a!"
  invalid-code: "&cInvalid code! Please check your spelling."
  already-redeemed: "&cYou have already redeemed this code!"
  # ... and many more
```

## 🔧 Commands & Permissions

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/redeem <code>` | Redeem a code for rewards | `redeemify.redeem` |
| `/redeemify reload` | Reload plugin configuration | `redeemify.admin` |
| `/redeemify info` | Show plugin information | `redeemify.admin` |
| `/redeemify stats` | Display usage statistics | `redeemify.admin` |

### Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `redeemify.redeem` | Allows players to redeem codes | `true` |
| `redeemify.admin` | Access to admin commands | `op` |
| `redeemify.*` | All permissions | `op` |

## 📊 Statistics & Tracking

Redeemify automatically tracks:
- **Total redemptions** across all codes
- **Unique players** who have redeemed codes
- **Individual code usage** counts
- **Per-player redemption history**

Access statistics with `/redeemify stats` or check the data files in the plugin folder.

## 🔄 Data Management

### Automatic Saving
- **Async data persistence** for optimal performance
- **Auto-save intervals** (configurable)
- **Graceful shutdown** data protection

### Data Files
- `playerdata.yml` - Player redemption history
- `usagedata.yml` - Code usage statistics
- `codes.yml` - Code definitions
- `config.yml` - Plugin settings
- `messages.yml` - Customizable messages

## 🛠️ Advanced Configuration

### Performance Settings (`config.yml`)

```yaml
settings:
  debug: false
  auto-save-interval: 5  # minutes
  
performance:
  cache-size: 1000
  async-save: true
```

### Integration Examples

**Economy Integration:**
```yaml
commands:
  - "eco give %player% 1000"
  - "eco take %player% 500"
```

**Permission Integration:**
```yaml
commands:
  - "lp user %player% parent add premium 30d"
  - "lp user %player% permission set some.permission true"
```

**Custom Commands:**
```yaml
commands:
  - "kit give starter %player%"
  - "tp %player% spawn"
  - "broadcast %player% just redeemed a special code!"
```

## 🔧 Requirements

- **Server:** Paper 1.21.4+ (or compatible forks)
- **Java:** 21 or higher
- **Dependencies:** None (standalone plugin)

## 🤝 Support & Contributing

### Getting Help
- 🐛 **Bug Reports:** [GitHub Issues](https://github.com/Mxykl/Redeemify/issues)
- 💡 **Feature Requests:** [GitHub Discussions](https://github.com/Mxykl/Redeemify/discussions)
- 📖 **Documentation:** [Wiki](https://github.com/Mxykl/Redeemify/wiki)

### Contributing
Contributions are welcome! Please read our [Contributing Guidelines](CONTRIBUTING.md) before submitting pull requests.

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built for the **Paper** Minecraft server platform
- Inspired by the community's need for flexible code redemption systems
- Thanks to all contributors and users who provide feedback

---

**Made with ❤️ by [Michael Brauer (Mxykl)](https://github.com/Mxykl)**

*If you find this plugin useful, please consider giving it a ⭐ on GitHub!*