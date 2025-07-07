# 🔧 Redeemify Compatibility Guide

## 📋 Supported Versions

### Current Release (v1.0.0)
- **Minecraft:** 1.21.0 - 1.21.6+
- **Server:** Paper 1.21.4+ (recommended)
- **Java:** 21+

### Legacy Support
For older versions, you can modify the plugin:

## 🔄 Version Adaptations

### For Minecraft 1.20.x
1. **Change API version** in `plugin.yml`:
   ```yaml
   api-version: '1.20'
   ```

2. **Update Paper dependency** in `pom.xml`:
   ```xml
   <dependency>
       <groupId>io.papermc.paper</groupId>
       <artifactId>paper-api</artifactId>
       <version>1.20.6-R0.1-SNAPSHOT</version>
       <scope>provided</scope>
   </dependency>
   ```

3. **Adjust Java version** (optional):
   ```xml
   <maven.compiler.source>17</maven.compiler.source>
   <maven.compiler.target>17</maven.compiler.target>
   ```

### For Spigot Servers
The plugin works on Spigot 1.21.x but with reduced performance:

**Limitations on Spigot:**
- Less optimized async operations
- Missing Paper-specific performance improvements
- Reduced thread safety optimizations

**Recommended for Spigot:**
- Use Paper instead for better performance
- If Spigot is required, monitor server performance

### For Older Java Versions
- **Java 17:** Compatible with 1.20.x versions
- **Java 11:** Requires code modifications (not recommended)
- **Java 8:** Not supported (modern features required)

## 🛠️ Server Software Compatibility

| Software | 1.21.x | 1.20.x | Notes |
|----------|--------|--------|-------|
| **Paper** | ✅ Full | ✅ Full | Recommended |
| **Purpur** | ✅ Full | ✅ Full | Paper-based |
| **Pufferfish** | ✅ Full | ✅ Full | Paper-based |
| **Spigot** | ⚠️ Limited | ⚠️ Limited | Reduced performance |
| **CraftBukkit** | ❌ No | ❌ No | Missing APIs |
| **Bukkit** | ❌ No | ❌ No | Too old |

## 🔧 Manual Adaptations

### For Custom Server Software
If you're using a custom server implementation:

1. **Check API compatibility** with Paper/Spigot
2. **Test async operations** thoroughly
3. **Monitor performance** under load
4. **Verify command execution** works correctly

### For Modded Servers
- **Forge:** Not compatible (different plugin system)
- **Fabric:** Not compatible (different plugin system)
- **Hybrid servers:** May work if they support Bukkit plugins

## 📊 Performance Considerations

### Recommended Setup
- **Paper 1.21.4+** with **Java 21**
- **8GB+ RAM** for large servers
- **SSD storage** for faster data access

### Minimum Requirements
- **Spigot 1.21.0** with **Java 21**
- **4GB+ RAM**
- **Regular HDD** storage

## 🚀 Migration Guide

### From Older Versions
1. **Backup** your current plugin data
2. **Update** server software to supported version
3. **Install** new plugin version
4. **Test** functionality thoroughly
5. **Restore** data if needed

### Version-Specific Notes
- **1.20.x → 1.21.x:** Direct upgrade possible
- **1.19.x → 1.21.x:** May require data migration
- **1.18.x and older:** Not recommended

## 🔍 Testing Compatibility

Before deploying on production:

1. **Test on staging server** first
2. **Verify all commands** work correctly
3. **Check data persistence** after restart
4. **Monitor performance** metrics
5. **Test with your other plugins**

## 📞 Support

If you encounter compatibility issues:

1. **Check this guide** first
2. **Review server logs** for errors
3. **Test with minimal plugin setup**
4. **Report issues** on GitHub with:
   - Server software and version
   - Java version
   - Plugin version
   - Error logs
   - Steps to reproduce

---

**Note:** While the plugin may work on other configurations, only the officially supported versions receive full testing and support.