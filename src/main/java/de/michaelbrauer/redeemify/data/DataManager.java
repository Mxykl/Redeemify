package de.michaelbrauer.redeemify.data;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class DataManager {
    
    private final Redeemify plugin;
    private FileConfiguration playerData;
    private FileConfiguration usageData;
    private File playerDataFile;
    private File usageDataFile;

    // In-memory storage for faster access
    private final Map<String, Set<String>> playerRedeemed = new HashMap<>(); // player -> codes
    private final Map<String, Integer> codeUsages = new HashMap<>(); // code -> usage count

    public DataManager(Redeemify plugin) {
        this.plugin = plugin;
    }

    public void loadData() {
        createDataFiles();
        loadDataFiles();
        loadInMemoryData();
    }

    private void createDataFiles() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        usageDataFile = new File(plugin.getDataFolder(), "usagedata.yml");

        try {
            if (!playerDataFile.exists()) {
                playerDataFile.createNewFile();
            }
            if (!usageDataFile.exists()) {
                usageDataFile.createNewFile();
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create data files", e);
        }
    }

    private void loadDataFiles() {
        try {
            playerData = YamlConfiguration.loadConfiguration(playerDataFile);
            usageData = YamlConfiguration.loadConfiguration(usageDataFile);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load data files", e);
        }
    }

    private void loadInMemoryData() {
        // Load player redeemed codes
        playerRedeemed.clear();
        if (playerData != null && playerData.getConfigurationSection("players") != null) {
            for (String playerUUID : playerData.getConfigurationSection("players").getKeys(false)) {
                List<String> codes = playerData.getStringList("players." + playerUUID + ".redeemed");
                playerRedeemed.put(playerUUID, new HashSet<>(codes));
            }
        }

        // Load code usage counts
        codeUsages.clear();
        if (usageData != null && usageData.getConfigurationSection("codes") != null) {
            for (String code : usageData.getConfigurationSection("codes").getKeys(false)) {
                int usage = usageData.getInt("codes." + code + ".uses", 0);
                codeUsages.put(code, usage);
            }
        }
    }

    public void saveData() {
        if (playerData != null && usageData != null) {
            savePlayerData();
            saveUsageData();
        }
    }

    private void savePlayerData() {
        try {
            // Clear existing data
            playerData.set("players", null);
            
            // Save current data
            for (Map.Entry<String, Set<String>> entry : playerRedeemed.entrySet()) {
                String playerUUID = entry.getKey();
                List<String> codes = new ArrayList<>(entry.getValue());
                playerData.set("players." + playerUUID + ".redeemed", codes);
            }
            
            playerData.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player data", e);
        }
    }

    private void saveUsageData() {
        try {
            // Clear existing data
            usageData.set("codes", null);
            
            // Save current data
            for (Map.Entry<String, Integer> entry : codeUsages.entrySet()) {
                String code = entry.getKey();
                int usage = entry.getValue();
                usageData.set("codes." + code + ".uses", usage);
            }
            
            usageData.save(usageDataFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save usage data", e);
        }
    }

    // Player data methods
    public boolean hasPlayerRedeemed(String playerUUID, String code) {
        return playerRedeemed.getOrDefault(playerUUID, new HashSet<>()).contains(code);
    }

    public void addPlayerRedeemed(String playerUUID, String code) {
        playerRedeemed.computeIfAbsent(playerUUID, k -> new HashSet<>()).add(code);
    }

    // Code usage methods
    public int getCodeUsage(String code) {
        return codeUsages.getOrDefault(code, 0);
    }

    public void incrementCodeUsage(String code) {
        codeUsages.put(code, getCodeUsage(code) + 1);
    }

    // Statistics methods
    public int getTotalRedemptions() {
        return codeUsages.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getUniquePlayersCount() {
        return playerRedeemed.size();
    }

    public Set<String> getAllRedeemedCodes() {
        return new HashSet<>(codeUsages.keySet());
    }

    // Enhanced methods for new features
    public int getPlayerRedeemedCount(String playerUUID) {
        return playerRedeemed.getOrDefault(playerUUID, new HashSet<>()).size();
    }

    public String getLastRedeemedCode(String playerUUID) {
        Set<String> codes = playerRedeemed.get(playerUUID);
        if (codes == null || codes.isEmpty()) return null;
        
        // Return the last code (this is simplified - in production, store timestamps)
        return codes.iterator().next();
    }

    public void resetPeriodicCodeData(String periodicCodeName) {
        // Reset player redemptions for periodic codes
        for (Set<String> codes : playerRedeemed.values()) {
            codes.removeIf(code -> code.startsWith(periodicCodeName));
        }
        
        // Reset usage data for periodic codes
        codeUsages.entrySet().removeIf(entry -> entry.getKey().startsWith(periodicCodeName));
    }

    public Map<String, Set<String>> getAllPlayerRedemptions() {
        return new HashMap<>(playerRedeemed);
    }

    public Map<String, Integer> getAllCodeUsages() {
        return new HashMap<>(codeUsages);
    }
}