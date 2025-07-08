package de.michaelbrauer.redeemify.commands;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class LanguageCommand implements CommandExecutor {
    
    private final Redeemify plugin;

    public LanguageCommand(Redeemify plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("messages.player-only"));
            return true;
        }

        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();

        if (args.length == 0) {
            // Show current language and available languages
            String currentLang = plugin.getLanguageManager().getPlayerLanguage(playerUUID);
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.current-language", playerUUID)
                    .replace("%language%", currentLang));
            
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.available-languages", playerUUID));
            Map<String, String> languages = plugin.getLanguageManager().getAvailableLanguages();
            for (Map.Entry<String, String> entry : languages.entrySet()) {
                player.sendMessage("§7- §e" + entry.getKey() + " §7(" + entry.getValue() + ")");
            }
            
            return true;
        }

        String newLanguage = args[0];
        Map<String, String> availableLanguages = plugin.getLanguageManager().getAvailableLanguages();
        
        if (!availableLanguages.containsKey(newLanguage)) {
            player.sendMessage(plugin.getLanguageManager().getMessage("messages.invalid-language", playerUUID)
                    .replace("%language%", newLanguage));
            return true;
        }

        plugin.getLanguageManager().setPlayerLanguage(playerUUID, newLanguage);
        player.sendMessage(plugin.getLanguageManager().getMessage("messages.language-changed", playerUUID)
                .replace("%language%", newLanguage));

        return true;
    }
}