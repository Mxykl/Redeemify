# 🎁 Redeemify - Flexible Code Redemption System

Transform your server engagement with **Redeemify** - the ultimate solution for creating and managing redeemable codes with customizable rewards!

## 🌟 Why Choose Redeemify?

Perfect for **giveaways**, **events**, **promotions**, and **player retention**! Whether you're running a small community server or a large network, Redeemify scales with your needs.

### ✨ Key Features

🔑 **Smart Code Management**
- Create unlimited codes with custom names
- Set expiration dates for time-limited offers
- Control usage limits (per-player and global)
- Execute any server commands as rewards

📊 **Advanced Tracking**
- Real-time usage statistics
- Per-player redemption history
- Prevent code abuse with built-in restrictions
- Comprehensive admin tools

🛡️ **Enterprise-Grade Reliability**
- Async data saving for optimal performance
- Thread-safe operations
- Automatic error handling
- Hot-reload configuration

🎨 **Fully Customizable**
- Personalize all messages with color codes
- Flexible reward system using commands
- Configurable permissions
- Extensive placeholder support

## 🚀 Quick Setup

1. **Install** the plugin in your `plugins` folder
2. **Restart** your server
3. **Configure** codes in `plugins/Redeemify/codes.yml`
4. **Start** engaging your players!

## 💡 Usage Examples

### For Players
```
/redeem WELCOME2024
```

### For Administrators
```
/redeemify stats    # View redemption statistics
/redeemify reload   # Reload configuration
/redeemify info     # Plugin information
```

## 🔧 Code Configuration Made Simple

```yaml
codes:
  # Welcome gift for new players
  WELCOME2024:
    expires: "2025-12-31 23:59"
    max-uses: 1000
    per-player: true
    commands:
      - "eco give %player% 5000"
      - "kit give starter %player%"
      - "tell %player% Welcome to our amazing server!"
      
  # Daily reward (unlimited uses)
  DAILY100:
    expires: "-1"
    max-uses: -1
    per-player: false
    commands:
      - "eco give %player% 100"
      
  # VIP promotion (limited time)
  VIP30DAYS:
    expires: "2025-06-30 23:59"
    max-uses: 50
    per-player: true
    commands:
      - "lp user %player% parent add vip 30d"
      - "eco give %player% 10000"
      - "broadcast %player% just became VIP!"
```

## 🎯 Perfect For

- **🎉 Server Events** - Special event codes with time limits
- **🎁 Giveaways** - YouTube/Discord promotion codes
- **👥 Community Building** - Welcome gifts for new players
- **💎 VIP Promotions** - Exclusive membership offers
- **🏆 Competitions** - Reward codes for contest winners
- **📱 Social Media** - Cross-platform engagement campaigns

## 🔐 Permissions & Security

| Permission | Description | Default |
|------------|-------------|---------|
| `redeemify.redeem` | Redeem codes | All players |
| `redeemify.admin` | Admin commands | Operators only |

Built-in protection against:
- ✅ Code abuse and exploitation
- ✅ Duplicate redemptions
- ✅ Expired code usage
- ✅ Unauthorized access

## 📊 Comprehensive Statistics

Track your success with detailed analytics:
- Total redemptions across all codes
- Unique players engaged
- Individual code performance
- Usage trends and patterns

## 🔄 Integration Ready

Works seamlessly with popular plugins:

**💰 Economy Plugins**
- EssentialsX Economy
- Vault-compatible plugins
- Custom economy systems

**🛡️ Permission Plugins**
- LuckPerms
- PermissionsEx
- GroupManager

**🎒 Other Plugins**
- Essentials Kits
- Custom commands
- Any Bukkit/Spigot plugin

## ⚡ Performance Optimized

- **Async operations** prevent server lag
- **Smart caching** for frequently accessed data
- **Minimal resource usage**
- **Optimized for large player bases**

## 🛠️ Technical Details

**Requirements:**
- Paper 1.21.4+ (or compatible forks)
- Java 21+
- No additional dependencies

**Features:**
- Thread-safe data handling
- Automatic backup and recovery
- Hot-reload configuration
- Comprehensive error logging

## 🤝 Community & Support

Join our growing community of server administrators who trust Redeemify for their code redemption needs!

**Need Help?**
- 📖 Comprehensive documentation
- 🐛 Active bug tracking
- 💡 Feature request system
- 🤝 Community support

## 🏆 Why Server Owners Love Redeemify

> *"Redeemify transformed our player engagement. We've seen a 40% increase in player retention since implementing welcome codes!"* - Server Owner

> *"The flexibility is incredible. We use it for everything from daily rewards to special event promotions."* - Community Manager

> *"Setup was incredibly easy, and the performance impact is virtually zero. Highly recommended!"* - Network Administrator

## 📈 Boost Your Server Today

Ready to enhance your player experience? Download Redeemify now and start creating engaging code campaigns that keep players coming back!

**Perfect for servers of all sizes** - from small communities to large networks.

---

**🔗 Links:**
- [GitHub Repository](https://github.com/Mxykl/Redeemify)
- [Documentation](https://github.com/Mxykl/Redeemify/wiki)
- [Issue Tracker](https://github.com/Mxykl/Redeemify/issues)

**Made with ❤️ by Michael Brauer (Mxykl)**

*Transform your server engagement today with Redeemify!*