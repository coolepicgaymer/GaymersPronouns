package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ConfigManager {

    GaymersPronouns plugin;

    public ConfigManager(GaymersPronouns plugin) {
        this.plugin = plugin;
    }

    /**
     * Saves a default custom config or fails silently if it already exists.
     *
     * @param config the name of the custom config file.
     * @return the custom config.
     */
    public FileConfiguration saveDefaultCustomConfig(String config) {
        File configFile = new File(plugin.getDataFolder(), config);
        if (!configFile.exists()) {
            plugin.saveResource(config, false);
        }
        return YamlConfiguration.loadConfiguration(configFile);
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
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(plugin.getResource(config), "UTF8");
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                customConfig.setDefaults(defConfig);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return customConfig;
    }

}
