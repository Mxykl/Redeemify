package de.michaelbrauer.redeemify.config;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;

public class ConfigManager {
    
    private final Redeemify plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration codes;
    
    private File configFile;
    private File messagesFile;
    private File codesFile;

    public ConfigManager(Redeemify plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        createConfigFiles();
        loadConfigFiles();
    }

    private void createConfigFiles() {
        // Create plugin data folder
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // Create config files
        configFile = new File(plugin.getDataFolder(), "config.yml");
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        codesFile = new File(plugin.getDataFolder(), "codes.yml");

        // Copy default files if they don't exist
        createFileFromResource(configFile, "config.yml");
        createFileFromResource(messagesFile, "messages.yml");
        createFileFromResource(codesFile, "codes.yml");
    }

    private void createFileFromResource(File file, String resourceName) {
        if (!file.exists()) {
            try {
                InputStream inputStream = plugin.getResource(resourceName);
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                    inputStream.close();
                } else {
                    file.createNewFile();
                    plugin.getLogger().warning("Could not find default " + resourceName + " in resources. Created empty file.");
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create " + resourceName, e);
            }
        }
    }

    private void loadConfigFiles() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            codes = YamlConfiguration.loadConfiguration(codesFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load configuration files", e);
        }
    }

    public void saveCodesConfig() {
        try {
            codes.save(codesFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save codes.yml", e);
        }
    }

    // Getters
    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getCodes() {
        return codes;
    }

    public String getMessage(String key) {
        return messages.getString(key, "Message not found: " + key)
                .replace("&", "§");
    }

    public String getMessage(String key, String placeholder, String value) {
        return getMessage(key).replace(placeholder, value);
    }
}