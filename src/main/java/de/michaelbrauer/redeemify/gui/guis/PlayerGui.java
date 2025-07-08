package de.michaelbrauer.redeemify.gui.guis;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class PlayerGui extends BaseGui {

    public PlayerGui(Redeemify plugin, Player player) {
        super(plugin, player, 27, "gui.player.title");
    }

    @Override
    protected void setupGui() {
        // Redeem Code button
        inventory.setItem(11, createItem(
                Material.EMERALD,
                "gui.player.redeem-code",
                "gui.player.redeem-code-lore"
        ));

        // My History button
        inventory.setItem(13, createItem(
                Material.BOOK,
                "gui.player.my-history",
                "gui.player.my-history-lore"
        ));

        // Available Codes button (if enabled)
        if (plugin.getConfigManager().getConfig().getBoolean("gui.show-available-codes", false)) {
            inventory.setItem(15, createItem(
                    Material.CHEST,
                    "gui.player.available-codes",
                    "gui.player.available-codes-lore"
            ));
        }

        // Language Selection
        inventory.setItem(22, createItem(
                Material.GLOBE_BANNER_PATTERN,
                "gui.player.language",
                "gui.player.language-lore"
        ));

        // Close button
        inventory.setItem(26, createItem(
                Material.BARRIER,
                "gui.common.close",
                "gui.common.close-lore"
        ));

        // Fill empty slots
        fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE);
    }

    @Override
    public void handleClick(int slot, ClickType clickType) {
        switch (slot) {
            case 11: // Redeem Code
                playSound("UI_BUTTON_CLICK");
                plugin.getGuiManager().openRedeemGui(player);
                break;
                
            case 13: // My History
                playSound("UI_BUTTON_CLICK");
                showPlayerHistory();
                break;
                
            case 15: // Available Codes
                if (plugin.getConfigManager().getConfig().getBoolean("gui.show-available-codes", false)) {
                    playSound("UI_BUTTON_CLICK");
                    showAvailableCodes();
                }
                break;
                
            case 22: // Language Selection
                playSound("UI_BUTTON_CLICK");
                showLanguageSelection();
                break;
                
            case 26: // Close
                playSound("UI_BUTTON_CLICK");
                close();
                break;
        }
    }

    private void showPlayerHistory() {
        String playerUUID = player.getUniqueId().toString();
        int redeemedCount = plugin.getDataManager().getPlayerRedeemedCount(playerUUID);
        String lastCode = plugin.getDataManager().getLastRedeemedCode(playerUUID);
        
        String message = plugin.getLanguageManager().getMessage("gui.player.history-info", playerUUID)
                .replace("%count%", String.valueOf(redeemedCount))
                .replace("%last_code%", lastCode != null ? lastCode : "None");
        
        player.sendMessage(message);
    }

    private void showAvailableCodes() {
        int availableCodes = plugin.getCodeManager().getAvailableCodesCount();
        String message = plugin.getLanguageManager().getMessage("gui.player.available-info", player.getUniqueId().toString())
                .replace("%count%", String.valueOf(availableCodes));
        
        player.sendMessage(message);
    }

    private void showLanguageSelection() {
        String currentLang = plugin.getLanguageManager().getPlayerLanguage(player.getUniqueId().toString());
        String message = plugin.getLanguageManager().getMessage("gui.player.current-language", player.getUniqueId().toString())
                .replace("%language%", currentLang);
        
        player.sendMessage(message);
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.player.language-help", player.getUniqueId().toString()));
    }
}