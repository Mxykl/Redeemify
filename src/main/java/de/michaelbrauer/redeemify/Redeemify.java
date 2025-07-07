package de.michaelbrauer.redeemify;

import de.michaelbrauer.redeemify.commands.RedeemCommand;
import de.michaelbrauer.redeemify.commands.RedeemifyCommand;
import de.michaelbrauer.redeemify.config.ConfigManager;
import de.michaelbrauer.redeemify.data.DataManager;
import de.michaelbrauer.redeemify.managers.CodeManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Redeemify extends JavaPlugin {

    private ConfigManager configManager;
    private DataManager dataManager;
    private CodeManager codeManager;

    @Override
    public void onEnable() {
        // Initialize managers in correct order
        this.configManager = new ConfigManager(this);
        this.dataManager = new DataManager(this);

        // Load configurations first
        configManager.loadConfigs();
        
        // Load data
        dataManager.loadData();

        // Initialize CodeManager AFTER configs are loaded
        this.codeManager = new CodeManager(this);

        // Register commands
        registerCommands();

        getLogger().info("Redeemify has been enabled successfully!");
        getLogger().info("Plugin developed by Michael Brauer (Mxykl)");
    }

    @Override
    public void onDisable() {
        // Save data before shutdown
        if (dataManager != null) {
            dataManager.saveData();
        }
        
        getLogger().info("Redeemify has been disabled. Data saved successfully!");
    }

    private void registerCommands() {
        try {
            getCommand("redeem").setExecutor(new RedeemCommand(this));
            getCommand("redeemify").setExecutor(new RedeemifyCommand(this));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register commands", e);
        }
    }

    public void reload() {
        configManager.loadConfigs();
        dataManager.loadData();
        codeManager.loadCodes();
        getLogger().info("Redeemify configuration reloaded!");
    }

    // Getters
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CodeManager getCodeManager() {
        return codeManager;
    }
}