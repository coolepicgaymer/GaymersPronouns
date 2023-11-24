package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.types.PronounSet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;

public class PronounManager {

    GaymersPronouns plugin;
    FileConfiguration config;
    ConfigurationSection section;

    ConfigManager configManager;

    HashMap<String, String> defaults;
    HashMap<Integer, PronounSet> pronouns;

    ArrayList<Integer> group1;
    ArrayList<Integer> group2;

    public static String noPronouns;

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
     * Reloads files associated with pronouns as well as defaults and configured pronouns.
     */
    public void reload() {
        configManager.saveDefaultCustomConfig("pronouns.yml", false);

        config = configManager.reloadCustomConfig("pronouns.yml");
        section = config.getConfigurationSection("pronouns");

        noPronouns = config.getString("undefined-pronouns");

        reloadDefaults();
        reloadPronouns();
    }


    /**
     * Reloads pronouns.
     */
    private void reloadPronouns() {
        pronouns = new HashMap<>();
        for (String key : section.getKeys(false)) {
            int id;
            try {
                id = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Pronoun ID is not a number: " + key + ". Ignoring it for now...");
                continue;
            }
            if (section.isSet(key + ".display")) {
                pronouns.put(id, new PronounSet(section.getString(key + ".display"), section.getString(key + ".dominant", null), section.getString(key + ".subjective", getDefaultPronoun("subjective")), section.getString(key + ".objective", getDefaultPronoun("objective")), section.getString(key + ".possessive", getDefaultPronoun("possessive")), section.getString(key + ".reflexive", getDefaultPronoun("reflexive")), section.getString(key + ".verb", getDefaultPronoun("verb")), section.getBoolean(key + ".hidden", false)));
            } else {
                plugin.getLogger().warning("Pronoun " + key + " is invalid. Ignoring it for now...");
                continue;
            }
        }

        group1 = new ArrayList<>();
        group2 = new ArrayList<>();
        for (int i : pronouns.keySet()) {
            if (pronouns.get(i).isHidden()) continue;
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



    private void reloadDefaults() {
        defaults = new HashMap<>();
        int id = plugin.getConfig().getInt("default-pronouns");
        defaults.put("subjective", section.getString(id + ".subjective"));
        defaults.put("objective", section.getString(id + ".objective"));
        defaults.put("possessive", section.getString(id + ".possessive"));
        defaults.put("reflexive", section.getString(id + ".reflexive"));
        defaults.put("verb", section.getString(id + ".verb"));
    }



    /**
     * Gives you a default pronoun of a specific type.
     *
     * @param type can be "subjective", "objective", "possessive", "reflexive", or "verb".
     * @return the default pronoun of the specified type.
     * @throws IllegalArgumentException when the type is not one of the three.
     */
    public String getDefaultPronoun(String type) throws IllegalArgumentException {
        switch(type) {
            case "subjective":
            case "objective":
            case "possessive":
            case "reflexive":
            case "verb":
                return defaults.get(type);
            default:
                throw new IllegalArgumentException(type);
        }
    }

}
