package de.michaelbrauer.redeemify.gui;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public abstract class BaseGui {
    
    protected final Redeemify plugin;
    protected final Player player;
    protected final Inventory inventory;
    protected final int size;
    protected final String title;

    public BaseGui(Redeemify plugin, Player player, int size, String title) {
        this.plugin = plugin;
        this.player = player;
        this.size = size;
        this.title = plugin.getLanguageManager().getMessage(title, player.getUniqueId().toString());
        this.inventory = Bukkit.createInventory(null, size, this.title);
        
        setupGui();
    }

    protected abstract void setupGui();

    public abstract void handleClick(int slot, ClickType clickType);

    public void open() {
        player.openInventory(inventory);
    }

    public void close() {
        player.closeInventory();
    }

    public void onClose() {
        // Override in subclasses if needed
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(plugin.getLanguageManager().getMessage(name, player.getUniqueId().toString()));
            
            if (lore.length > 0) {
                List<String> loreList = Arrays.stream(lore)
                        .map(line -> plugin.getLanguageManager().getMessage(line, player.getUniqueId().toString()))
                        .toList();
                meta.setLore(loreList);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    protected ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(plugin.getLanguageManager().getMessage(name, player.getUniqueId().toString()));
            
            if (lore != null && !lore.isEmpty()) {
                List<String> translatedLore = lore.stream()
                        .map(line -> plugin.getLanguageManager().getMessage(line, player.getUniqueId().toString()))
                        .toList();
                meta.setLore(translatedLore);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    protected void fillEmptySlots(Material material) {
        ItemStack filler = new ItemStack(material);
        ItemMeta meta = filler.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            filler.setItemMeta(meta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    protected void playSound(String soundName) {
        try {
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            // Sound not found, ignore
        }
    }
}