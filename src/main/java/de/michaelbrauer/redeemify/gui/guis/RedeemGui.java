package de.michaelbrauer.redeemify.gui.guis;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.gui.BaseGui;
import de.michaelbrauer.redeemify.managers.CodeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class RedeemGui extends BaseGui {

    private String currentCode = "";

    public RedeemGui(Redeemify plugin, Player player) {
        super(plugin, player, 27, "gui.redeem.title");
    }

    @Override
    protected void setupGui() {
        // Code input display
        inventory.setItem(13, createItem(
                Material.PAPER,
                "gui.redeem.code-input",
                "gui.redeem.code-input-lore"
        ));

        // Confirm button
        inventory.setItem(11, createItem(
                Material.GREEN_CONCRETE,
                "gui.redeem.confirm",
                "gui.redeem.confirm-lore"
        ));

        // Clear button
        inventory.setItem(15, createItem(
                Material.RED_CONCRETE,
                "gui.redeem.clear",
                "gui.redeem.clear-lore"
        ));

        // Back button
        inventory.setItem(18, createItem(
                Material.ARROW,
                "gui.common.back",
                "gui.common.back-lore"
        ));

        // Close button
        inventory.setItem(26, createItem(
                Material.BARRIER,
                "gui.common.close",
                "gui.common.close-lore"
        ));

        // Instructions
        inventory.setItem(22, createItem(
                Material.BOOK,
                "gui.redeem.instructions",
                "gui.redeem.instructions-lore"
        ));

        // Fill empty slots
        fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE);
    }

    @Override
    public void handleClick(int slot, ClickType clickType) {
        switch (slot) {
            case 11: // Confirm
                playSound("UI_BUTTON_CLICK");
                redeemCode();
                break;
                
            case 13: // Code input (open chat input)
                playSound("UI_BUTTON_CLICK");
                requestCodeInput();
                break;
                
            case 15: // Clear
                playSound("UI_BUTTON_CLICK");
                clearCode();
                break;
                
            case 18: // Back
                playSound("UI_BUTTON_CLICK");
                plugin.getGuiManager().openPlayerGui(player);
                break;
                
            case 22: // Instructions
                playSound("UI_BUTTON_CLICK");
                showInstructions();
                break;
                
            case 26: // Close
                playSound("UI_BUTTON_CLICK");
                close();
                break;
        }
    }

    private void redeemCode() {
        if (currentCode.isEmpty()) {
            player.sendMessage(plugin.getLanguageManager().getMessage("gui.redeem.no-code", player.getUniqueId().toString()));
            return;
        }

        CodeManager.RedeemResult result = plugin.getCodeManager().redeemCode(player, currentCode);
        player.sendMessage(result.getMessage());
        
        if (result.isSuccess()) {
            playSound("ENTITY_PLAYER_LEVELUP");
            close();
        } else {
            playSound("ENTITY_VILLAGER_NO");
        }
    }

    private void requestCodeInput() {
        close();
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.redeem.enter-code", player.getUniqueId().toString()));
        
        // In a real implementation, you would use a chat input system
        // For now, we'll just show a message
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.redeem.use-command", player.getUniqueId().toString()));
    }

    private void clearCode() {
        currentCode = "";
        updateCodeDisplay();
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.redeem.code-cleared", player.getUniqueId().toString()));
    }

    private void updateCodeDisplay() {
        String displayCode = currentCode.isEmpty() ? 
                plugin.getLanguageManager().getMessage("gui.redeem.no-code-entered", player.getUniqueId().toString()) : 
                currentCode;
        
        inventory.setItem(13, createItem(
                Material.PAPER,
                "gui.redeem.current-code",
                "§7" + displayCode
        ));
    }

    private void showInstructions() {
        player.sendMessage(plugin.getLanguageManager().getMessage("gui.redeem.instructions-text", player.getUniqueId().toString()));
    }

    public void setCode(String code) {
        this.currentCode = code;
        updateCodeDisplay();
    }
}