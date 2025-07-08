package de.michaelbrauer.redeemify.managers;

import de.michaelbrauer.redeemify.Redeemify;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LanguageManager {
    
    private final Redeemify plugin;
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private final Map<String, String> playerLanguages = new ConcurrentHashMap<>();
    private String defaultLanguage = "en_US";
    private File languagesFolder;

    public LanguageManager(Redeemify plugin) {
        this.plugin = plugin;
        this.languagesFolder = new File(plugin.getDataFolder(), "languages");
        loadLanguages();
    }

    public void loadLanguages() {
        createLanguagesFolder();
        loadLanguageFiles();
        loadPlayerLanguages();
    }

    private void createLanguagesFolder() {
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }

        // Create default language files
        String[] defaultLanguages = {"en_US", "de_DE", "es_ES", "fr_FR", "pt_BR"};
        
        for (String lang : defaultLanguages) {
            File langFile = new File(languagesFolder, lang + ".yml");
            if (!langFile.exists()) {
                createLanguageFile(langFile, lang);
            }
        }
    }

    private void createLanguageFile(File file, String language) {
        try {
            InputStream inputStream = plugin.getResource("languages/" + language + ".yml");
            if (inputStream != null) {
                Files.copy(inputStream, file.toPath());
                inputStream.close();
            } else {
                // Create basic language file if resource doesn't exist
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                createDefaultMessages(config, language);
                config.save(file);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create language file: " + language, e);
        }
    }

    private void createDefaultMessages(FileConfiguration config, String language) {
        // Add default messages based on language
        switch (language) {
            case "de_DE":
                config.set("messages.code-redeemed", "&aCode erfolgreich eingelöst: &e%code%&a!");
                config.set("messages.invalid-code", "&cUngültiger Code! Bitte überprüfe die Schreibweise.");
                config.set("messages.already-redeemed", "&cDu hast diesen Code bereits eingelöst!");
                break;
            case "es_ES":
                config.set("messages.code-redeemed", "&a¡Código canjeado exitosamente: &e%code%&a!");
                config.set("messages.invalid-code", "&c¡Código inválido! Por favor verifica la ortografía.");
                config.set("messages.already-redeemed", "&c¡Ya has canjeado este código!");
                break;
            case "fr_FR":
                config.set("messages.code-redeemed", "&aCode échangé avec succès: &e%code%&a!");
                config.set("messages.invalid-code", "&cCode invalide! Veuillez vérifier l'orthographe.");
                config.set("messages.already-redeemed", "&cVous avez déjà échangé ce code!");
                break;
            case "pt_BR":
                config.set("messages.code-redeemed", "&aCódigo resgatado com sucesso: &e%code%&a!");
                config.set("messages.invalid-code", "&cCódigo inválido! Por favor verifique a ortografia.");
                config.set("messages.already-redeemed", "&cVocê já resgatou este código!");
                break;
            default: // en_US
                config.set("messages.code-redeemed", "&aSuccessfully redeemed code: &e%code%&a!");
                config.set("messages.invalid-code", "&cInvalid code! Please check your spelling.");
                config.set("messages.already-redeemed", "&cYou have already redeemed this code!");
                break;
        }
    }

    private void loadLanguageFiles() {
        languages.clear();
        
        File[] files = languagesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String langCode = file.getName().replace(".yml", "");
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                languages.put(langCode, config);
                plugin.getLogger().info("Loaded language: " + langCode);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load language file: " + langCode + " - " + e.getMessage());
            }
        }

        if (languages.isEmpty()) {
            plugin.getLogger().warning("No language files loaded! Creating default English file.");
            createLanguageFile(new File(languagesFolder, "en_US.yml"), "en_US");
            loadLanguageFiles(); // Retry
        }
    }

    private void loadPlayerLanguages() {
        // Load player language preferences from data file
        File playerLangFile = new File(plugin.getDataFolder(), "player-languages.yml");
        if (playerLangFile.exists()) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(playerLangFile);
                for (String playerUUID : config.getKeys(false)) {
                    String language = config.getString(playerUUID);
                    if (language != null && languages.containsKey(language)) {
                        playerLanguages.put(playerUUID, language);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load player languages: " + e.getMessage());
            }
        }
    }

    public void savePlayerLanguages() {
        File playerLangFile = new File(plugin.getDataFolder(), "player-languages.yml");
        try {
            FileConfiguration config = new YamlConfiguration();
            for (Map.Entry<String, String> entry : playerLanguages.entrySet()) {
                config.set(entry.getKey(), entry.getValue());
            }
            config.save(playerLangFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save player languages", e);
        }
    }

    public String getMessage(String key, String playerUUID) {
        String language = getPlayerLanguage(playerUUID);
        FileConfiguration langConfig = languages.get(language);
        
        if (langConfig == null) {
            langConfig = languages.get(defaultLanguage);
        }
        
        if (langConfig == null && !languages.isEmpty()) {
            langConfig = languages.values().iterator().next();
        }
        
        if (langConfig != null) {
            String message = langConfig.getString(key);
            if (message != null) {
                return message.replace("&", "§");
            }
        }
        
        // Fallback to key if message not found
        return "§c[Missing: " + key + "]";
    }

    public String getMessage(String key) {
        return getMessage(key, null);
    }

    public String getPlayerLanguage(String playerUUID) {
        return playerLanguages.getOrDefault(playerUUID, defaultLanguage);
    }

    public void setPlayerLanguage(String playerUUID, String language) {
        if (languages.containsKey(language)) {
            playerLanguages.put(playerUUID, language);
            savePlayerLanguages();
        }
    }

    public void setPlayerLanguage(Player player, String language) {
        setPlayerLanguage(player.getUniqueId().toString(), language);
    }

    public String detectPlayerLanguage(Player player) {
        // Try to detect language from player's client locale
        String locale = player.getLocale();
        if (locale != null) {
            // Convert locale format (en_us) to our format (en_US)
            String[] parts = locale.split("_");
            if (parts.length == 2) {
                String langCode = parts[0].toLowerCase() + "_" + parts[1].toUpperCase();
                if (languages.containsKey(langCode)) {
                    return langCode;
                }
            }
        }
        return defaultLanguage;
    }

    public void autoSetPlayerLanguage(Player player) {
        String playerUUID = player.getUniqueId().toString();
        if (!playerLanguages.containsKey(playerUUID)) {
            String detectedLang = detectPlayerLanguage(player);
            setPlayerLanguage(playerUUID, detectedLang);
        }
    }

    public Map<String, String> getAvailableLanguages() {
        Map<String, String> result = new HashMap<>();
        for (String langCode : languages.keySet()) {
            FileConfiguration config = languages.get(langCode);
            String displayName = config.getString("language.display-name", langCode);
            result.put(langCode, displayName);
        }
        return result;
    }
}