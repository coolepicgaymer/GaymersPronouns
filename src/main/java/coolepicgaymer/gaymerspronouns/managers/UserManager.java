package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class UserManager {

    GaymersPronouns plugin;

    ConfigManager configManager;
    PronounManager pronounManager;


    public UserManager(GaymersPronouns plugin) {
        this.plugin = plugin;
        configManager = plugin.getConfigManager();
        pronounManager = plugin.getPronounManager();
    }



    public void setOptOutReminders(String uuid, boolean value) {
        setSingleValue(uuid, "opt-out-of-reminders", value);
    }



    public boolean getOptOutReminders(String uuid) {
        return getUserFile(uuid).getBoolean("opt-out-of-reminders");
    }



    public void setFluidReminders(String uuid, boolean value) {
        setSingleValue(uuid, "fluid-reminders", value);
    }



    public boolean getFluidReminders(String uuid) {
        return getUserFile(uuid).getBoolean("fluid-reminders");
    }



    public boolean hasPronouns(String uuid) {
        YamlConfiguration config = getUserFile(uuid);
        if (!config.isSet("pronouns") || config.getList("pronouns").size() <= 0) return false;
        else return true;
    }



    public void setUserPronouns(String uuid, int... pronouns) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int id : pronouns) ids.add(id);
        setUserPronouns(uuid, ids);
    }



    public void setUserPronouns(String uuid, List<Integer> pronouns) {
        setSingleValue(uuid, "pronouns", pronouns);
        GaymersPronouns.getDisplayManager().updateDisplay(Bukkit.getPlayer(UUID.fromString(uuid)));
    }



    private void setSingleValue(String uuid, String key, Object value) {
        YamlConfiguration config = getUserFile(uuid);
        config.set(key, value);
        setUserFile(uuid, config);
    }



    public List<Integer> getUserPronouns(String uuid) {
        YamlConfiguration config = getUserFile(uuid);

        if (!config.isSet("pronouns")) return new ArrayList<>();
        return config.getIntegerList("pronouns");
    }

    public int maxPronouns() {
        return plugin.getConfig().getInt("max-pronouns");
    }



    public String getDisplayUserPronouns(String uuid) {
        List<Integer> pronouns = getUserPronouns(uuid);
        if (pronouns.size() == 1) {
            return pronounManager.getPronounSets().get(pronouns.get(0)).getDisplay();
        } else if (pronouns.size() > 1) {
            String result = "";
            for (int id : pronouns) {
                result += "/" + pronounManager.getPronounSets().get(id).getSecondaryDisplay();
            }
            return result.substring(1);
        } else {
            return "Not set";
        }
    }



    private YamlConfiguration getUserFile(String uuid) {
        File file = new File(plugin.getDataFolder() + "/users/" + uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private void setUserFile(String uuid, YamlConfiguration config) {
        File file = new File(plugin.getDataFolder() + "/users/" + uuid + ".yml");
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Problem saving " + file + ": " + e);
        }
    }
}
