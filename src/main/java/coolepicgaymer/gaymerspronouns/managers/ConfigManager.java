package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    GaymersPronouns plugin;
    private String locale;

    public ConfigManager(GaymersPronouns plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        locale = plugin.getConfig().getString("locale", "en").toLowerCase();
    }

    /**
     * Saves a default custom config or fails silently if it already exists.
     *
     * @param config the name of the custom config file.
     * @return the custom config.
     */
    public FileConfiguration saveDefaultCustomConfig(String config, boolean overwrite) {
        File configFile = new File(plugin.getDataFolder(), config);
        YamlConfiguration customConfig = null;
        Reader defConfigStream;
        if (!configFile.exists() || overwrite) {
            try {
                try {
                    defConfigStream = new InputStreamReader(plugin.getResource("locales/" + locale + "/" + config), StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid locale in config.yml: " + locale);
                    defConfigStream = new InputStreamReader(plugin.getResource("locales/en/" + config), StandardCharsets.UTF_8);
                }
                customConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                customConfig.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return customConfig;
    }



    /**
     * Reloads a default custom config.
     *
     * @param config the name of the custom config file.
     * @return the custom config.
     */
    public FileConfiguration reloadCustomConfig(String config) {
        File configFile = new File(plugin.getDataFolder(), config);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(configFile);
        Reader defConfigStream;
        YamlConfiguration defConfig;
        try {
            defConfigStream = new InputStreamReader(plugin.getResource("locales/" + locale + "/" + config), StandardCharsets.UTF_8);
            defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            defConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("locales/en/" + config), StandardCharsets.UTF_8)));
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid locale in config.yml: " + locale);
            defConfigStream = new InputStreamReader(plugin.getResource("locales/en/" + config), StandardCharsets.UTF_8);
            defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        }

        customConfig.setDefaults(defConfig);
        return customConfig;
    }

    /**
     * Renames and regenerates a default custom config.
     *
     * @param config the name of the custom config file.
     * @return the custom config.
     */
    public FileConfiguration regenerateCustomConfig(String config) {
        File configFile = new File(plugin.getDataFolder(), config);
        File dir = new File(plugin.getDataFolder() + "/old");
        if (configFile.exists()) {
            if (!dir.isDirectory()) dir.mkdirs();
            configFile.renameTo(new File(plugin.getDataFolder() + "/old", config.substring(0, config.length()-4) + "." + System.currentTimeMillis() + ".yml"));
        }
        return saveDefaultCustomConfig(config, false);
    }

}
