package de.michaelbrauer.redeemify.managers;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.models.RedeemCode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeManager {
    
    private final Redeemify plugin;
    private final Map<String, RedeemCode> codes = new HashMap<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CodeManager(Redeemify plugin) {
        this.plugin = plugin;
        loadCodes();
    }

    public void loadCodes() {
        codes.clear();
        ConfigurationSection codesSection = plugin.getConfigManager().getCodes().getConfigurationSection("codes");
        
        if (codesSection == null) {
            plugin.getLogger().warning("No codes section found in codes.yml");
            return;
        }

        for (String codeKey : codesSection.getKeys(false)) {
            try {
                ConfigurationSection codeSection = codesSection.getConfigurationSection(codeKey);
                if (codeSection == null) continue;

                RedeemCode code = new RedeemCode();
                code.setCode(codeKey);
                
                // Parse expiration date
                String expiresStr = codeSection.getString("expires", "-1");
                if (!"-1".equals(expiresStr)) {
                    try {
                        LocalDateTime expires = LocalDateTime.parse(expiresStr, dateFormatter);
                        code.setExpires(expires);
                    } catch (DateTimeParseException e) {
                        plugin.getLogger().warning("Invalid date format for code " + codeKey + ": " + expiresStr);
                        continue;
                    }
                }

                code.setMaxUses(codeSection.getInt("max-uses", -1));
                code.setPerPlayer(codeSection.getBoolean("per-player", true));
                code.setCommands(codeSection.getStringList("commands"));

                codes.put(codeKey.toLowerCase(), code);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load code " + codeKey + ": " + e.getMessage());
            }
        }

        plugin.getLogger().info("Loaded " + codes.size() + " codes from configuration");
    }

    public RedeemResult redeemCode(Player player, String codeInput) {
        String code = codeInput.toLowerCase();
        RedeemCode redeemCode = codes.get(code);

        if (redeemCode == null) {
            return new RedeemResult(false, plugin.getConfigManager().getMessage("messages.invalid-code"));
        }

        // Check if code has expired
        if (redeemCode.getExpires() != null && LocalDateTime.now().isAfter(redeemCode.getExpires())) {
            return new RedeemResult(false, plugin.getConfigManager().getMessage("messages.code-expired"));
        }

        String playerUUID = player.getUniqueId().toString();

        // Check per-player restriction
        if (redeemCode.isPerPlayer() && plugin.getDataManager().hasPlayerRedeemed(playerUUID, code)) {
            return new RedeemResult(false, plugin.getConfigManager().getMessage("messages.already-redeemed"));
        }

        // Check max uses
        if (redeemCode.getMaxUses() > 0) {
            int currentUses = plugin.getDataManager().getCodeUsage(code);
            if (currentUses >= redeemCode.getMaxUses()) {
                return new RedeemResult(false, plugin.getConfigManager().getMessage("messages.max-uses-reached"));
            }
        }

        // Execute commands
        executeCommands(player, redeemCode.getCommands());

        // Update data
        if (redeemCode.isPerPlayer()) {
            plugin.getDataManager().addPlayerRedeemed(playerUUID, code);
        }
        plugin.getDataManager().incrementCodeUsage(code);

        // Save data asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDataManager().saveData());

        return new RedeemResult(true, plugin.getConfigManager().getMessage("messages.code-redeemed", "%code%", codeInput));
    }

    private void executeCommands(Player player, List<String> commands) {
        for (String command : commands) {
            String processedCommand = command.replace("%player%", player.getName());
            
            // Execute command on next tick to ensure proper execution
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to execute command: " + processedCommand + " - " + e.getMessage());
                }
            });
        }
    }

    public Map<String, RedeemCode> getCodes() {
        return new HashMap<>(codes);
    }

    public static class RedeemResult {
        private final boolean success;
        private final String message;

        public RedeemResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}