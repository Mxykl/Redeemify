package de.michaelbrauer.redeemify.commands;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RedeemifyCommand implements CommandExecutor {
    
    private final Redeemify plugin;

    public RedeemifyCommand(Redeemify plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("redeemify.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("messages.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(plugin.getConfigManager().getMessage("messages.config-reloaded"));
                break;
                
            case "info":
                sendInfoMessage(sender);
                break;
                
            case "stats":
                sendStatsMessage(sender);
                break;
                
            default:
                sendHelpMessage(sender);
                break;
        }

        return true;
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§6=== Redeemify Commands ===");
        sender.sendMessage("§e/redeemify reload §7- Reload configuration");
        sender.sendMessage("§e/redeemify info §7- Show plugin information");
        sender.sendMessage("§e/redeemify stats §7- Show usage statistics");
    }

    private void sendInfoMessage(CommandSender sender) {
        sender.sendMessage("§6=== Redeemify Information ===");
        sender.sendMessage("§eVersion: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("§eAuthor: §fMichael Brauer (Mxykl)");
        sender.sendMessage("§eLoaded Codes: §f" + plugin.getCodeManager().getCodes().size());
        sender.sendMessage("§eDescription: §f" + plugin.getDescription().getDescription());
    }

    private void sendStatsMessage(CommandSender sender) {
        sender.sendMessage("§6=== Redeemify Statistics ===");
        sender.sendMessage("§eTotal Redemptions: §f" + plugin.getDataManager().getTotalRedemptions());
        sender.sendMessage("§eUnique Players: §f" + plugin.getDataManager().getUniquePlayersCount());
        sender.sendMessage("§eActive Codes: §f" + plugin.getCodeManager().getCodes().size());
        sender.sendMessage("§eRedeemed Codes: §f" + plugin.getDataManager().getAllRedeemedCodes().size());
    }
}