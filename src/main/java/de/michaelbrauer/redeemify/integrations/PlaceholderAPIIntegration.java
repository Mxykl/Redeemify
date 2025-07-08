package de.michaelbrauer.redeemify.integrations;

import de.michaelbrauer.redeemify.Redeemify;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPIIntegration extends PlaceholderExpansion {
    
    private final Redeemify plugin;

    public PlaceholderAPIIntegration(Redeemify plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "redeemify";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) {
            return "";
        }

        String playerUUID = player.getUniqueId().toString();

        switch (params.toLowerCase()) {
            case "codes_redeemed":
                return String.valueOf(plugin.getDataManager().getPlayerRedeemedCount(playerUUID));
            
            case "last_code":
                return plugin.getDataManager().getLastRedeemedCode(playerUUID);
            
            case "total_redemptions":
                return String.valueOf(plugin.getDataManager().getTotalRedemptions());
            
            case "available_codes":
                return String.valueOf(plugin.getCodeManager().getAvailableCodesCount());
            
            case "player_language":
                return plugin.getLanguageManager().getPlayerLanguage(playerUUID);
            
            default:
                return null;
        }
    }
}