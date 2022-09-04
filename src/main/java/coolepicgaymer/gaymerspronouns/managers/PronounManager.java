package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.types.PronounSet;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public class PronounManager {

    GaymersPronouns plugin;
    FileConfiguration config;

    ConfigManager configManager;

    HashMap<String, String> defaults;
    HashMap<Integer, PronounSet> pronouns;

    ArrayList<Integer> group1;
    ArrayList<Integer> group2;

    public PronounManager(GaymersPronouns plugin) {
        defaults = new HashMap<>();
        pronouns = new HashMap<>();
        configManager = plugin.getConfigManager();
        this.plugin = plugin;
    }



    public HashMap<Integer, PronounSet> getPronounSets() {
        return pronouns;
    }



    /**
     * Reloads pronouns.
     */
    public void reloadPronouns() {
        configManager.saveDefaultCustomConfig("pronouns.yml");
        config = configManager.reloadCustomConfig("pronouns.yml");

        pronouns = new HashMap<>();
        for (String key : config.getKeys(false)) {
            int id;
            try {
                id = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Pronoun ID is not a number: " + key + ". Ignoring it for now...");
                continue;
            }
            if (config.isSet(key + ".subjective")) {
                pronouns.put(id, new PronounSet(config.getString(key + ".display"), config.getString(key + ".subjective"), config.getString(key + ".subjective"), config.getString(key + ".objective"), config.getString(key + ".possessiveadj")));
            } else {
                pronouns.put(id, new PronounSet(config.getString(key + ".display")));
            }
        }

        group1 = new ArrayList<>();
        group2 = new ArrayList<>();
        for (int i : pronouns.keySet()) {
            if (i > 0) group1.add(i);
            else group2.add(i);
        }
    }

    public ArrayList<Integer> getGroup1() {
        return group1;
    }

    public ArrayList<Integer> getGroup2() {
        return group2;
    }



    /**
     * Get the pronouns.yml.
     * @return the pronouns.yml as a FileConfiguration.
     */
    private FileConfiguration getPronounsConfig() {
        if (config == null) {
            reloadPronouns();
        }
        return config;
    }



    public void reloadDefaults() {
        defaults = new HashMap<>();
        defaults.put("subjective", plugin.getConfig().getString("default-pronouns.subjective"));
        defaults.put("objective", plugin.getConfig().getString("default-pronouns.objective"));
        defaults.put("possessiveadj", plugin.getConfig().getString("default-pronouns.possessiveadj"));
    }



    /**
     * Gives you a default pronoun of a specific type.
     *
     * @param type can be "subjective", "objective", or "possessiveadj".
     * @return the default pronoun of the specified type.
     * @throws IllegalArgumentException when the type is not one of the three.
     */
    public String getDefaultPronoun(String type) throws IllegalArgumentException {
        switch(type) {
            case "subjective":
            case "objective":
            case "possessiveadj":
                return defaults.get(type);
            default:
                throw new IllegalArgumentException(type);
        }
    }

}
