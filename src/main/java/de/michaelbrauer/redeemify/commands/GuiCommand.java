package de.michaelbrauer.redeemify.commands;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiCommand implements CommandExecutor {
    
    private final Redeemify plugin;

    public GuiCommand(Redeemify plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("messages.player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("admin")) {
            if (!player.hasPermission("redeemify.admin")) {
                player.sendMessage(plugin.getLanguageManager().getMessage("messages.no-permission", player.getUniqueId().toString()));
                return true;
            }
            plugin.getGuiManager().openAdminGui(player);
        } else {
            if (!player.hasPermission("redeemify.gui")) {
                player.sendMessage(plugin.getLanguageManager().getMessage("messages.no-permission", player.getUniqueId().toString()));
                return true;
            }
            plugin.getGuiManager().openPlayerGui(player);
        }

        return true;
    }
}