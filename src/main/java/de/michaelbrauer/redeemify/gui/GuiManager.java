package de.michaelbrauer.redeemify.gui;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.gui.guis.AdminGui;
import de.michaelbrauer.redeemify.gui.guis.PlayerGui;
import de.michaelbrauer.redeemify.gui.guis.RedeemGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {
    
    private final Redeemify plugin;
    private final Map<UUID, BaseGui> openGuis = new HashMap<>();

    public GuiManager(Redeemify plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openPlayerGui(Player player) {
        PlayerGui gui = new PlayerGui(plugin, player);
        openGuis.put(player.getUniqueId(), gui);
        gui.open();
    }

    public void openAdminGui(Player player) {
        AdminGui gui = new AdminGui(plugin, player);
        openGuis.put(player.getUniqueId(), gui);
        gui.open();
    }

    public void openRedeemGui(Player player) {
        RedeemGui gui = new RedeemGui(plugin, player);
        openGuis.put(player.getUniqueId(), gui);
        gui.open();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        BaseGui gui = openGuis.get(player.getUniqueId());
        
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            event.setCancelled(true);
            gui.handleClick(event.getSlot(), event.getClick());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player player = (Player) event.getPlayer();
        BaseGui gui = openGuis.get(player.getUniqueId());
        
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            gui.onClose();
            openGuis.remove(player.getUniqueId());
        }
    }

    public void closeAllGuis() {
        openGuis.values().forEach(BaseGui::close);
        openGuis.clear();
    }
}