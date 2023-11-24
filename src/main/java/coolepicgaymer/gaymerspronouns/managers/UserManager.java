package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class UserManager {

    GaymersPronouns plugin;

    ConfigManager configManager;
    PronounManager pronounManager;

    private boolean defaultReminders;


    public UserManager(GaymersPronouns plugin) {
        this.plugin = plugin;
        configManager = plugin.getConfigManager();
        pronounManager = plugin.getPronounManager();
    }

    public void reload() {
        defaultReminders = !plugin.getConfig().getBoolean("default-no-pronouns-reminder");
    }



    public void setOptOutReminders(UUID uuid, boolean value) {
        setSingleValue(uuid, "opt-out-of-reminders", value);
    }



    public boolean getOptOutReminders(UUID uuid) {
        return getUserFile(uuid).getBoolean("opt-out-of-reminders", defaultReminders);
    }



    public void setFluidReminders(UUID uuid, boolean value) {
        setSingleValue(uuid, "fluid-reminders", value);
    }



    public boolean getFluidReminders(UUID uuid) {
        return getUserFile(uuid).getBoolean("fluid-reminders");
    }



    public boolean hasPronouns(UUID uuid) {
        YamlConfiguration config = getUserFile(uuid);
        if (!config.isSet("pronouns") || config.getList("pronouns").size() <= 0) return false;
        else return true;
    }



    public void setUserPronouns(UUID uuid, int... pronouns) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int id : pronouns) ids.add(id);
        setUserPronouns(uuid, ids);
    }



    public void setUserPronouns(UUID uuid, List<Integer> pronouns) {
        setSingleValue(uuid, "pronouns", pronouns);
        GaymersPronouns.getDisplayManager().updateDisplay(Bukkit.getPlayer(uuid));
    }



    private void setSingleValue(UUID uuid, String key, Object value) {
        YamlConfiguration config = getUserFile(uuid);
        config.set(key, value);
        setUserFile(uuid, config);
    }



    public List<Integer> getUserPronouns(UUID uuid) {
        YamlConfiguration config = getUserFile(uuid);

        if (!config.isSet("pronouns")) return new ArrayList<>();
        return config.getIntegerList("pronouns");
    }

    public int maxPronouns() {
        return plugin.getConfig().getInt("max-pronouns");
    }

    /**
     * Gives you a specific type of pronoun for a player.
     *
     * @param uuid the UUID of the player you want the pronoun for.
     * @param type can be "subjective", "objective", "possessive", or "reflexive".
     * @param random whether you want to randomly pick a set or just pick the player's first priority.
     * @return the pronoun of the specified type.
     * @throws IllegalArgumentException when the type is not one of the three.
     */
    public String getUserPronoun(UUID uuid, String type, boolean random) {
        List<Integer> sets = getUserPronouns(uuid);

        int set;
        if (sets.size() <= 0) {
            return pronounManager.getDefaultPronoun(type.toLowerCase());
        } else if (!random) {
            set = sets.get(0);
        } else {
            set = sets.get(new Random(System.currentTimeMillis()/1000).nextInt(sets.size()));
        }

        switch (type.toLowerCase()) {
            case "subjective":
                return pronounManager.getPronounSets().get(set).getSubjective();
            case "objective":
                return pronounManager.getPronounSets().get(set).getObjective();
            case "possessive":
                return pronounManager.getPronounSets().get(set).getPossessive();
            case "reflexive":
                return pronounManager.getPronounSets().get(set).getReflexive();
            case "verb":
                return pronounManager.getPronounSets().get(set).getVerb();
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getDisplayUserPronouns(UUID uuid) {
        return getDisplayFromList(getUserPronouns(uuid));
    }

    public String getDisplayFromList(List<Integer> pronouns) {
        if (pronouns.size() == 1) {
            return pronounManager.getPronounSets().get(pronouns.get(0)).getDisplay();
        } else if (pronouns.size() > 1) {
            String result = "";
            for (int id : pronouns) {
                result += "/" + pronounManager.getPronounSets().get(id).getDominant();
            }
            return result.substring(1);
        } else {
            return PronounManager.noPronouns;
        }
    }



    private YamlConfiguration getUserFile(UUID uuid) {
        File file = new File(plugin.getDataFolder() + "/users/" + uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private void setUserFile(UUID uuid, YamlConfiguration config) {
        File file = new File(plugin.getDataFolder() + "/users/" + uuid + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Problem saving " + file + ": " + e);
        }
    }
}
