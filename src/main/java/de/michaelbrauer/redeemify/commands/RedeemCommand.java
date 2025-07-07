package de.michaelbrauer.redeemify.commands;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.managers.CodeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedeemCommand implements CommandExecutor {
    
    private final Redeemify plugin;

    public RedeemCommand(Redeemify plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("messages.player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("redeemify.redeem")) {
            player.sendMessage(plugin.getConfigManager().getMessage("messages.no-permission"));
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(plugin.getConfigManager().getMessage("messages.usage-redeem"));
            return true;
        }

        String code = args[0];
        CodeManager.RedeemResult result = plugin.getCodeManager().redeemCode(player, code);
        
        player.sendMessage(result.getMessage());
        
        return true;
    }
}