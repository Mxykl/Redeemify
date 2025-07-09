package de.michaelbrauer.redeemify.gui.guis;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class AdminGui extends BaseGui {

    public AdminGui(Redeemify plugin, Player player) {
        super(plugin, player, 45, "gui.admin.title");
    }

    @Override
    protected void setupGui() {
        // Statistics
        inventory.setItem(10, createItem(
                Material.MAP,
                "gui.admin.statistics",
                "gui.admin.statistics-lore"
        ));

        // Code Management
        inventory.setItem(12, createItem(
                Material.WRITABLE_BOOK,
                "gui.admin.code-management",
                "gui.admin.code-management-lore"
        ));

        // Player Management
        inventory.setItem(14, createItem(
                Material.PLAYER_HEAD,
                "gui.admin.player-management",
                "gui.admin.player-management-lore"
        ));

        // Periodic Codes
        inventory.setItem(16, createItem(
                Material.CLOCK,
                "gui.admin.periodic-codes",
                "gui.admin.periodic-codes-lore"
        ));

        // Reload Configuration
        inventory.setItem(28, createItem(
                Material.REDSTONE,
                "gui.admin.reload",
                "gui.admin.reload-lore"
        ));

        // Error Logs
        inventory.setItem(30, createItem(
                Material.PAPER,
                "gui.admin.error-logs",
                "gui.admin.error-logs-lore"
        ));

        // Backup Management
        inventory.setItem(32, createItem(
                Material.CHEST,
                "gui.admin.backup",
                "gui.admin.backup-lore"
        ));

        // Plugin Info
        inventory.setItem(34, createItem(
                Material.BOOK,
                "gui.admin.plugin-info",
                "gui.admin.plugin-info-lore"
        ));

        // Close button
        inventory.setItem(44, createItem(
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
            case 10: // Statistics
                playSound("UI_BUTTON_CLICK");
                showStatistics();
                break;
                
            case 12: // Code Management
                playSound("UI_BUTTON_CLICK");
                showCodeManagement();
                break;
                
            case 14: // Player Management
                playSound("UI_BUTTON_CLICK");
                showPlayerManagement();
                break;
                
            case 16: // Periodic Codes
                playSound("UI_BUTTON_CLICK");
                showPeriodicCodes();
                break;
                
            case 28: // Reload
                playSound("UI_BUTTON_CLICK");
                reloadConfiguration();
                break;
                
            case 30: // Error Logs
                playSound("UI_BUTTON_CLICK");
                showErrorLogs();
                break;
                
            case 32: // Backup
                playSound("UI_BUTTON_CLICK");
                showBackupManagement();
                break;
                
            case 34: // Plugin Info
                playSound("UI_BUTTON_CLICK");
                showPluginInfo();
                break;
                
            case 44: // Close
                playSound("UI_BUTTON_CLICK");
                close();
                break;
        }
    }

    private void showStatistics() {
        String stats = String.format(
                plugin.getLanguageManager().getMessage("gui.admin.stats-display", player.getUniqueId().toString()),
                plugin.getDataManager().getTotalRedemptions(),
                plugin.getDataManager().getUniquePlayersCount(),
                plugin.getCodeManager().getCodes().size(),
                plugin.getPeriodicCodeManager().getPeriodicCodes().size()
        );
        player.sendMessage(stats);
    }

    private void showCodeManagement() {
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.admin.code-management-info", player.getUniqueId().toString()));
    }

    private void showPlayerManagement() {
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.admin.player-management-info", player.getUniqueId().toString()));
    }

    private void showPeriodicCodes() {
        int periodicCount = plugin.getPeriodicCodeManager().getPeriodicCodes().size();
        String message = plugin.getLanguageManager().getMessage("gui.admin.periodic-info", player.getUniqueId().toString())
                .replace("%count%", String.valueOf(periodicCount));
        player.sendMessage(message);
    }

    private void reloadConfiguration() {
        plugin.reload();
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.admin.reload-success", player.getUniqueId().toString()));
    }

    private void showErrorLogs() {
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.admin.error-logs-info", player.getUniqueId().toString()));
    }

    private void showBackupManagement() {
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.admin.backup-info", player.getUniqueId().toString()));
    }

    private void showPluginInfo() {
        String info = String.format(
                plugin.getLanguageManager().getMessage("gui.admin.plugin-info-display", player.getUniqueId().toString()),
                plugin.getDescription().getVersion(),
                plugin.getDescription().getAuthors().toString()
        );
        player.sendMessage(info);
    }
}