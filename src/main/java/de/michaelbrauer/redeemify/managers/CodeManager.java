package de.michaelbrauer.redeemify.managers;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.models.RedeemCode;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.Duration;
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

    public void createPeriodicCode(String codeName, String duration, int maxUses, boolean perPlayer, List<String> commands) {
        RedeemCode code = new RedeemCode();
        code.setCode(codeName);
        
        // Parse duration and set expiration
        if (!"-1".equals(duration)) {
            LocalDateTime expires = LocalDateTime.now().plus(parseDuration(duration));
            code.setExpires(expires);
        }
        
        code.setMaxUses(maxUses);
        code.setPerPlayer(perPlayer);
        code.setCommands(commands);
        
        codes.put(codeName.toLowerCase(), code);
        plugin.getLogger().info("Created periodic code: " + codeName);
    }

    private Duration parseDuration(String durationStr) {
        // Simple duration parser (e.g., "24h", "30m", "7d")
        try {
            if (durationStr.endsWith("h")) {
                return Duration.ofHours(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
            } else if (durationStr.endsWith("m")) {
                return Duration.ofMinutes(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
            } else if (durationStr.endsWith("d")) {
                return Duration.ofDays(Long.parseLong(durationStr.substring(0, durationStr.length() - 1)));
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid duration format: " + durationStr);
        }
        return Duration.ofHours(24); // Default to 24 hours
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
        executeCommands(player, redeemCode.getCommands(), code);

        // Update data
        if (redeemCode.isPerPlayer()) {
            plugin.getDataManager().addPlayerRedeemed(playerUUID, code);
        }
        plugin.getDataManager().incrementCodeUsage(code);

        // Save data asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.getDataManager().saveData());

        return new RedeemResult(true, plugin.getConfigManager().getMessage("messages.code-redeemed", "%code%", codeInput));
    }

    private void executeCommands(Player player, List<String> commands, String code) {
        for (String command : commands) {
            String processedCommand = processCommand(player, command, code);
            
            // Execute command on next tick to ensure proper execution
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    if (processedCommand.startsWith("economy:")) {
                        handleEconomyCommand(player, processedCommand);
                    } else if (processedCommand.startsWith("permission:")) {
                        handlePermissionCommand(player, processedCommand);
                    } else if (processedCommand.startsWith("group:")) {
                        handleGroupCommand(player, processedCommand);
                    } else {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to execute command: " + processedCommand + " - " + e.getMessage());
                    plugin.getErrorManager().logError("CommandExecution", "Failed to execute command: " + processedCommand, e);
                }
            });
        }
    }

    private String processCommand(Player player, String command, String code) {
        String processed = command
                .replace("%player%", player.getName())
                .replace("%code%", code);
        
        // PlaceholderAPI integration
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            processed = PlaceholderAPI.setPlaceholders(player, processed);
        }
        
        return processed;
    }

    private void handleEconomyCommand(Player player, String command) {
        // Format: "economy: 1000" or "economy: -500"
        try {
            String amountStr = command.substring(9).trim();
            double amount = Double.parseDouble(amountStr);
            
            if (amount > 0) {
                plugin.getVaultIntegration().giveEconomy(player, amount);
            } else {
                plugin.getVaultIntegration().takeEconomy(player, Math.abs(amount));
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Invalid economy amount: " + command);
        }
    }

    private void handlePermissionCommand(Player player, String command) {
        // Format: "permission: some.permission" or "permission: some.permission.30d"
        String permissionData = command.substring(11).trim();
        String[] parts = permissionData.split("\\.");
        
        if (parts.length >= 2) {
            String permission = permissionData;
            Duration duration = null;
            
            // Check if last part is a duration
            String lastPart = parts[parts.length - 1];
            if (lastPart.matches("\\d+[dhm]")) {
                duration = parseDuration(lastPart);
                permission = permissionData.substring(0, permissionData.lastIndexOf("." + lastPart));
            }
            
            plugin.getLuckPermsIntegration().addPermission(player, permission, duration);
        }
    }

    private void handleGroupCommand(Player player, String command) {
        // Format: "group: vip" or "group: vip.30d"
        String groupData = command.substring(6).trim();
        String[] parts = groupData.split("\\.");
        
        String group = parts[0];
        Duration duration = null;
        
        if (parts.length > 1 && parts[1].matches("\\d+[dhm]")) {
            duration = parseDuration(parts[1]);
        }
        
        plugin.getLuckPermsIntegration().addGroup(player, group, duration);
    }

    public int getAvailableCodesCount() {
        return (int) codes.values().stream()
                .filter(code -> !code.isExpired())
                .count();
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