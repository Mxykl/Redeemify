package de.michaelbrauer.redeemify.managers;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class ErrorManager {
    
    private final Redeemify plugin;
    private final ConcurrentLinkedQueue<ErrorEntry> errorQueue = new ConcurrentLinkedQueue<>();
    private final File errorLogFile;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean autoBackupEnabled;
    private int maxBackups;
    private int retryAttempts;

    public ErrorManager(Redeemify plugin) {
        this.plugin = plugin;
        this.errorLogFile = new File(plugin.getDataFolder(), "error-log.yml");
        loadErrorConfig();
        startErrorProcessor();
    }

    private void loadErrorConfig() {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        autoBackupEnabled = config.getBoolean("error-handling.auto-backup", true);
        maxBackups = config.getInt("error-handling.max-backups", 10);
        retryAttempts = config.getInt("error-handling.retry-attempts", 3);
    }

    public void logError(String source, String message, Throwable throwable) {
        ErrorEntry entry = new ErrorEntry(
                LocalDateTime.now(),
                source,
                message,
                throwable != null ? throwable.getMessage() : null,
                throwable != null ? getStackTrace(throwable) : null
        );
        
        errorQueue.offer(entry);
        
        // Log to console as well
        if (throwable != null) {
            plugin.getLogger().log(Level.SEVERE, "[" + source + "] " + message, throwable);
        } else {
            plugin.getLogger().severe("[" + source + "] " + message);
        }
        
        // Notify admins if configured
        if (plugin.getConfigManager().getConfig().getBoolean("error-handling.notify-admins", true)) {
            notifyAdmins(source, message);
        }
    }

    public void logError(String source, String message) {
        logError(source, message, null);
    }

    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private void startErrorProcessor() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            processErrorQueue();
        }, 20L, 100L); // Process every 5 seconds
    }

    private void processErrorQueue() {
        if (errorQueue.isEmpty()) return;
        
        try {
            FileConfiguration errorLog;
            if (errorLogFile.exists()) {
                errorLog = YamlConfiguration.loadConfiguration(errorLogFile);
            } else {
                errorLog = new YamlConfiguration();
            }
            
            List<ErrorEntry> processedErrors = new ArrayList<>();
            ErrorEntry entry;
            
            while ((entry = errorQueue.poll()) != null) {
                String timestamp = entry.timestamp.format(dateFormatter);
                String path = "errors." + timestamp.replace(" ", "_").replace(":", "-");
                
                errorLog.set(path + ".source", entry.source);
                errorLog.set(path + ".message", entry.message);
                errorLog.set(path + ".exception", entry.exceptionMessage);
                errorLog.set(path + ".stacktrace", entry.stackTrace);
                
                processedErrors.add(entry);
            }
            
            if (!processedErrors.isEmpty()) {
                errorLog.save(errorLogFile);
            }
            
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save error log", e);
        }
    }

    private void notifyAdmins(String source, String message) {
        String notification = plugin.getLanguageManager().getMessage("messages.admin-error-notification")
                .replace("%source%", source)
                .replace("%message%", message);
        
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("redeemify.admin"))
                .forEach(player -> player.sendMessage(notification));
    }

    public boolean createBackup() {
        if (!autoBackupEnabled) return false;
        
        try {
            File backupFolder = new File(plugin.getDataFolder(), "backups");
            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File backupFile = new File(backupFolder, "backup_" + timestamp + ".zip");
            
            // Create backup (simplified - in production use proper zip library)
            // For now, just copy important files
            copyDataFiles(backupFolder, timestamp);
            
            // Clean old backups
            cleanOldBackups(backupFolder);
            
            plugin.getLogger().info("Backup created: " + backupFile.getName());
            return true;
            
        } catch (Exception e) {
            logError("BackupManager", "Failed to create backup", e);
            return false;
        }
    }

    private void copyDataFiles(File backupFolder, String timestamp) throws IOException {
        File[] dataFiles = {
                new File(plugin.getDataFolder(), "playerdata.yml"),
                new File(plugin.getDataFolder(), "usagedata.yml"),
                new File(plugin.getDataFolder(), "codes.yml"),
                new File(plugin.getDataFolder(), "config.yml")
        };
        
        for (File file : dataFiles) {
            if (file.exists()) {
                File backupFile = new File(backupFolder, timestamp + "_" + file.getName());
                java.nio.file.Files.copy(file.toPath(), backupFile.toPath());
            }
        }
    }

    private void cleanOldBackups(File backupFolder) {
        File[] backups = backupFolder.listFiles((dir, name) -> name.startsWith("backup_") || name.contains("_"));
        if (backups == null || backups.length <= maxBackups) return;
        
        // Sort by last modified and delete oldest
        java.util.Arrays.sort(backups, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
        
        for (int i = 0; i < backups.length - maxBackups; i++) {
            if (backups[i].delete()) {
                plugin.getLogger().info("Deleted old backup: " + backups[i].getName());
            }
        }
    }

    public boolean attemptRecovery(String operation, Runnable recoveryAction) {
        for (int attempt = 1; attempt <= retryAttempts; attempt++) {
            try {
                recoveryAction.run();
                if (attempt > 1) {
                    plugin.getLogger().info("Recovery successful for " + operation + " after " + attempt + " attempts");
                }
                return true;
            } catch (Exception e) {
                logError("RecoveryManager", "Recovery attempt " + attempt + " failed for " + operation, e);
                
                if (attempt < retryAttempts) {
                    try {
                        Thread.sleep(1000 * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        logError("RecoveryManager", "All recovery attempts failed for " + operation);
        return false;
    }

    public List<ErrorEntry> getRecentErrors(int limit) {
        try {
            if (!errorLogFile.exists()) return new ArrayList<>();
            
            FileConfiguration errorLog = YamlConfiguration.loadConfiguration(errorLogFile);
            List<ErrorEntry> errors = new ArrayList<>();
            
            if (errorLog.getConfigurationSection("errors") != null) {
                for (String key : errorLog.getConfigurationSection("errors").getKeys(false)) {
                    String path = "errors." + key;
                    ErrorEntry entry = new ErrorEntry(
                            LocalDateTime.parse(key.replace("_", " ").replace("-", ":"), dateFormatter),
                            errorLog.getString(path + ".source"),
                            errorLog.getString(path + ".message"),
                            errorLog.getString(path + ".exception"),
                            errorLog.getString(path + ".stacktrace")
                    );
                    errors.add(entry);
                }
            }
            
            // Sort by timestamp (newest first) and limit
            errors.sort((a, b) -> b.timestamp.compareTo(a.timestamp));
            return errors.subList(0, Math.min(limit, errors.size()));
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Failed to load error log", e);
            return new ArrayList<>();
        }
    }

    public static class ErrorEntry {
        public final LocalDateTime timestamp;
        public final String source;
        public final String message;
        public final String exceptionMessage;
        public final String stackTrace;

        public ErrorEntry(LocalDateTime timestamp, String source, String message, String exceptionMessage, String stackTrace) {
            this.timestamp = timestamp;
            this.source = source;
            this.message = message;
            this.exceptionMessage = exceptionMessage;
            this.stackTrace = stackTrace;
        }
    }
}