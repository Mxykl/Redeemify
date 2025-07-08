package de.michaelbrauer.redeemify.integrations;

import de.michaelbrauer.redeemify.Redeemify;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultIntegration {
    
    private final Redeemify plugin;
    private Economy economy;
    private boolean enabled = false;

    public VaultIntegration(Redeemify plugin) {
        this.plugin = plugin;
        setupEconomy();
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().info("Vault not found - economy features disabled");
            return;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy plugin found - economy features disabled");
            return;
        }

        economy = rsp.getProvider();
        enabled = true;
        plugin.getLogger().info("Vault economy integration enabled");
    }

    public boolean isEnabled() {
        return enabled && economy != null;
    }

    public boolean giveEconomy(Player player, double amount) {
        if (!isEnabled()) return false;
        
        try {
            return economy.depositPlayer(player, amount).transactionSuccess();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to give economy to " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    public boolean takeEconomy(Player player, double amount) {
        if (!isEnabled()) return false;
        
        try {
            return economy.withdrawPlayer(player, amount).transactionSuccess();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to take economy from " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }

    public double getBalance(Player player) {
        if (!isEnabled()) return 0;
        
        try {
            return economy.getBalance(player);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get balance for " + player.getName() + ": " + e.getMessage());
            return 0;
        }
    }

    public String format(double amount) {
        if (!isEnabled()) return String.valueOf(amount);
        
        try {
            return economy.format(amount);
        } catch (Exception e) {
            return String.valueOf(amount);
        }
    }
}