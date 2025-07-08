package de.michaelbrauer.redeemify;

import de.michaelbrauer.redeemify.commands.RedeemCommand;
import de.michaelbrauer.redeemify.commands.RedeemifyCommand;
import de.michaelbrauer.redeemify.commands.GuiCommand;
import de.michaelbrauer.redeemify.commands.LanguageCommand;
import de.michaelbrauer.redeemify.config.ConfigManager;
import de.michaelbrauer.redeemify.data.DataManager;
import de.michaelbrauer.redeemify.gui.GuiManager;
import de.michaelbrauer.redeemify.integrations.PlaceholderAPIIntegration;
import de.michaelbrauer.redeemify.integrations.VaultIntegration;
import de.michaelbrauer.redeemify.integrations.LuckPermsIntegration;
import de.michaelbrauer.redeemify.managers.CodeManager;
import de.michaelbrauer.redeemify.managers.LanguageManager;
import de.michaelbrauer.redeemify.managers.PeriodicCodeManager;
import de.michaelbrauer.redeemify.managers.ErrorManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Redeemify extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private DataManager dataManager;
    private CodeManager codeManager;
    private LanguageManager languageManager;
    private PeriodicCodeManager periodicCodeManager;
    private ErrorManager errorManager;
    private GuiManager guiManager;
    
    // Integrations
    private VaultIntegration vaultIntegration;
    private LuckPermsIntegration luckPermsIntegration;
    private PlaceholderAPIIntegration placeholderAPIIntegration;

    @Override
    public void onEnable() {
        try {
            // Initialize core managers first
            this.errorManager = new ErrorManager(this);
            this.configManager = new ConfigManager(this);
            this.languageManager = new LanguageManager(this);
            this.dataManager = new DataManager(this);

            // Load configurations and data
            configManager.loadConfigs();
            languageManager.loadLanguages();
            dataManager.loadData();

            // Initialize managers that depend on configs
            this.codeManager = new CodeManager(this);
            this.periodicCodeManager = new PeriodicCodeManager(this);
            this.guiManager = new GuiManager(this);

            // Initialize integrations
            setupIntegrations();

            // Register commands and events
            registerCommands();
            registerEvents();

            // Create initial backup
            errorManager.createBackup();

            getLogger().info("Redeemify has been enabled successfully!");
            getLogger().info("Plugin developed by Michael Brauer (Mxykl)");
            getLogger().info("Loaded features: GUI, Multi-Language, Periodic Codes, Integrations");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to enable Redeemify", e);
            if (errorManager != null) {
                errorManager.logError("PluginStartup", "Failed to enable plugin", e);
            }
        }
    }

    private void setupIntegrations() {
        // Vault Integration
        this.vaultIntegration = new VaultIntegration(this);
        
        // LuckPerms Integration
        this.luckPermsIntegration = new LuckPermsIntegration(this);
        
        // PlaceholderAPI Integration
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            this.placeholderAPIIntegration = new PlaceholderAPIIntegration(this);
            placeholderAPIIntegration.register();
            getLogger().info("PlaceholderAPI integration enabled");
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        try {
            // Shutdown periodic code manager
            if (periodicCodeManager != null) {
                periodicCodeManager.shutdown();
            }
            
            // Close all GUIs
            if (guiManager != null) {
                guiManager.closeAllGuis();
            }
            
            // Save all data
            if (dataManager != null) {
                dataManager.saveData();
            }
            
            if (languageManager != null) {
                languageManager.savePlayerLanguages();
            }
            
            // Create final backup
            if (errorManager != null) {
                errorManager.createBackup();
            }
            
            getLogger().info("Redeemify has been disabled. Data saved successfully!");
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Error during plugin shutdown", e);
        }
    }

    private void registerCommands() {
        try {
            getCommand("redeem").setExecutor(new RedeemCommand(this));
            getCommand("redeemify").setExecutor(new RedeemifyCommand(this));
            getCommand("redeemgui").setExecutor(new GuiCommand(this));
            getCommand("language").setExecutor(new LanguageCommand(this));
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to register commands", e);
            errorManager.logError("CommandRegistration", "Failed to register commands", e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Auto-detect and set player language
        languageManager.autoSetPlayerLanguage(player);
        
        // Send welcome message if configured
        if (configManager.getConfig().getBoolean("welcome-message.enabled", false)) {
            String message = languageManager.getMessage("messages.welcome", player.getUniqueId().toString())
                    .replace("%player%", player.getName());
            player.sendMessage(message);
        }
    }

    public void reload() {
        errorManager.attemptRecovery("ConfigReload", () -> {
            configManager.loadConfigs();
            languageManager.loadLanguages();
            dataManager.loadData();
            codeManager.loadCodes();
            periodicCodeManager.loadPeriodicCodes();
            getLogger().info("Redeemify configuration reloaded!");
        });
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

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public PeriodicCodeManager getPeriodicCodeManager() {
        return periodicCodeManager;
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public VaultIntegration getVaultIntegration() {
        return vaultIntegration;
    }

    public LuckPermsIntegration getLuckPermsIntegration() {
        return luckPermsIntegration;
    }

    public PlaceholderAPIIntegration getPlaceholderAPIIntegration() {
        return placeholderAPIIntegration;
    }
}