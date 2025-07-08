package de.michaelbrauer.redeemify.managers;

import de.michaelbrauer.redeemify.Redeemify;
import de.michaelbrauer.redeemify.models.PeriodicCode;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PeriodicCodeManager {
    
    private final Redeemify plugin;
    private final Map<String, PeriodicCode> periodicCodes = new ConcurrentHashMap<>();
    private final Map<String, BukkitTask> scheduledTasks = new HashMap<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public PeriodicCodeManager(Redeemify plugin) {
        this.plugin = plugin;
        loadPeriodicCodes();
    }

    public void loadPeriodicCodes() {
        // Cancel existing tasks
        scheduledTasks.values().forEach(BukkitTask::cancel);
        scheduledTasks.clear();
        periodicCodes.clear();

        ConfigurationSection section = plugin.getConfigManager().getConfig().getConfigurationSection("periodic-codes");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            try {
                ConfigurationSection codeSection = section.getConfigurationSection(key);
                if (codeSection == null) continue;

                PeriodicCode periodicCode = new PeriodicCode();
                periodicCode.setName(key);
                periodicCode.setPattern(codeSection.getString("pattern", key + "_%date%"));
                periodicCode.setSchedule(codeSection.getString("schedule", "0 0 * * *"));
                periodicCode.setDuration(codeSection.getString("duration", "24h"));
                periodicCode.setMaxUses(codeSection.getInt("max-uses", -1));
                periodicCode.setPerPlayer(codeSection.getBoolean("per-player", true));
                periodicCode.setAutoReset(codeSection.getBoolean("auto-reset", false));
                periodicCode.setCommands(codeSection.getStringList("commands"));
                periodicCode.setEnabled(codeSection.getBoolean("enabled", true));

                periodicCodes.put(key, periodicCode);
                
                if (periodicCode.isEnabled()) {
                    schedulePeriodicCode(periodicCode);
                }

                plugin.getLogger().info("Loaded periodic code: " + key);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load periodic code " + key + ": " + e.getMessage());
            }
        }
    }

    private void schedulePeriodicCode(PeriodicCode periodicCode) {
        // Parse cron-like schedule and convert to Bukkit scheduler
        long interval = parseCronToTicks(periodicCode.getSchedule());
        
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            generateAndActivateCode(periodicCode);
        }, 0L, interval);
        
        scheduledTasks.put(periodicCode.getName(), task);
    }

    private long parseCronToTicks(String schedule) {
        // Simple cron parser - for production, use a proper cron library
        // Format: "minute hour day month dayOfWeek"
        // For now, default to daily (24 hours = 1728000 ticks)
        return 1728000L; // 24 hours in ticks
    }

    private void generateAndActivateCode(PeriodicCode periodicCode) {
        try {
            String codeName = generateCodeName(periodicCode.getPattern());
            
            // Create the actual code
            plugin.getCodeManager().createPeriodicCode(
                codeName,
                periodicCode.getDuration(),
                periodicCode.getMaxUses(),
                periodicCode.isPerPlayer(),
                periodicCode.getCommands()
            );

            // Reset player data if auto-reset is enabled
            if (periodicCode.isAutoReset()) {
                plugin.getDataManager().resetPeriodicCodeData(periodicCode.getName());
            }

            // Notify players if configured
            if (plugin.getConfigManager().getConfig().getBoolean("periodic-codes.notify-players", true)) {
                String message = plugin.getLanguageManager().getMessage("messages.periodic-code-available")
                        .replace("%code%", codeName);
                
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("redeemify.notifications")) {
                        player.sendMessage(message);
                    }
                });
            }

            plugin.getLogger().info("Generated periodic code: " + codeName);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to generate periodic code for " + periodicCode.getName() + ": " + e.getMessage());
        }
    }

    private String generateCodeName(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        return pattern
                .replace("%date%", now.format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .replace("%time%", now.format(DateTimeFormatter.ofPattern("HHmm")))
                .replace("%week%", String.valueOf(now.getDayOfYear() / 7))
                .replace("%month%", now.format(DateTimeFormatter.ofPattern("MM")))
                .replace("%year%", now.format(DateTimeFormatter.ofPattern("yyyy")));
    }

    public void shutdown() {
        scheduledTasks.values().forEach(BukkitTask::cancel);
        scheduledTasks.clear();
    }

    public Map<String, PeriodicCode> getPeriodicCodes() {
        return new HashMap<>(periodicCodes);
    }
}