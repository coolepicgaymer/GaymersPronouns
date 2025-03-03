package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.types.GPPlayer;
import coolepicgaymer.gaymerspronouns.types.PronounSet;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class UserManager {

    private final GaymersPronouns plugin;

    private final PronounManager pronounManager;
    private DatabaseManager db;

    private boolean defaultReminders;
    private boolean useDatabase;

    private boolean individualColors;
    private String separatorColor;

    private HashMap<String, GPPlayer> players;


    public UserManager(GaymersPronouns plugin) {
        this.plugin = plugin;
        pronounManager = GaymersPronouns.getPronounManager();
    }

    public void reload() {
        defaultReminders = !plugin.getConfig().getBoolean("default-no-pronouns-reminder");
        useDatabase = plugin.getConfig().getBoolean("database.use");

        individualColors = plugin.getConfig().getBoolean("use-individual-colors");
        separatorColor = plugin.getConfig().getString("individual-separator-color", "&7");

        db = plugin.getDatabaseManager();

        this.players = new HashMap<>();
        for(Player p : plugin.getServer().getOnlinePlayers()) loadPlayer(p);
    }

    public GPPlayer getDefaultGPPlayer(String uuid, String username) {
        return new GPPlayer(uuid, username.toLowerCase(), new ArrayList<>(), defaultReminders, false);
    }

    public void loadPlayer(Player p) {
        if (useDatabase) {
            GPPlayer defaults = getDefaultGPPlayer(p.getUniqueId().toString(), p.getName().toLowerCase());
            GPPlayer player = db.createFullPlayerProfile(p.getUniqueId().toString(), defaults);

            if (player != null) players.put(p.getUniqueId().toString(), player);
            else {
                players.put(p.getUniqueId().toString(), defaults);
                plugin.getLogger().warning(MessageManager.getMessage("console.sql.player-load-error", p.getUniqueId().toString()));
            }
        } else {
            List<Integer> pronouns;
            boolean optOutReminders;
            boolean fluidReminders;

            YamlConfiguration config = getUserFile(p.getUniqueId().toString());

            if (!config.isSet("pronouns")) pronouns = new ArrayList<>();
            else pronouns = config.getIntegerList("pronouns");
            optOutReminders = config.getBoolean("opt-out-of-reminders", defaultReminders);
            fluidReminders = config.getBoolean("fluid-reminders");

            players.put(p.getUniqueId().toString(), new GPPlayer(p.getUniqueId().toString(), p.getName().toLowerCase(), pronouns, optOutReminders, fluidReminders));
        }
    }

    public void unloadPlayer(String uuid) {
        players.remove(uuid);
    }

    public GPPlayer getPlayer(String uuid) {
        return players.get(uuid);
    }



    public void setOptOutReminders(String uuid, boolean value) {
        getPlayer(uuid).setOptOutReminders(value);

        if (useDatabase) db.updatePlayerOptionsEntry(getPlayer(uuid));
        else setSingleValue(uuid, "opt-out-of-reminders", value);
    }



    public boolean getOptOutReminders(String uuid) {
        return getPlayer(uuid).isOptOutReminders();
    }



    public void setFluidReminders(String uuid, boolean value) {
        getPlayer(uuid).setFluidReminders(value);

        if (useDatabase) db.updatePlayerOptionsEntry(getPlayer(uuid));
        else setSingleValue(uuid, "fluid-reminders", value);
    }



    public boolean getFluidReminders(String uuid) {
        return getPlayer(uuid).isFluidReminders();
    }



    public void setUsername(String uuid, String username) {
        getPlayer(uuid).setUsername(username.toLowerCase());

        if (useDatabase) db.updateUsername(uuid, username);
        else setSingleValue(uuid, "username", username.toLowerCase());
    }



    public String getUsername(String uuid) {
        return getPlayer(uuid).getUsername();
    }




    public boolean hasPronouns(String uuid) {
        return (getPlayer(uuid).getPronouns().size() > 0);
    }



    public void setUserPronouns(String uuid, int... pronouns) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int id : pronouns) ids.add(id);
        setUserPronouns(uuid, ids);
    }



    public void setUserPronouns(String uuid, List<Integer> pronouns) {
        getPlayer(uuid).setPronouns(pronouns);

        if (useDatabase) db.updatePlayerPronounsEntry(getPlayer(uuid));
        else setSingleValue(uuid, "pronouns", pronouns);

        GaymersPronouns.getDisplayManager().updateDisplay(uuid);
    }



    private void setSingleValue(String uuid, String key, Object value) {
        YamlConfiguration config = getUserFile(uuid);
        config.set(key, value);
        setUserFile(uuid, config);
    }



    public List<Integer> getUserPronouns(String uuid) {
        return getPlayer(uuid).getPronouns();
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
    public String getUserPronoun(String uuid, String type, boolean random) {
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

    public String getDisplayUserPronouns(String uuid) {
        return getDisplayFromList(getUserPronouns(uuid));
    }

    public String getDisplayFromList(List<Integer> pronouns) {
        if (pronouns.size() == 1) {
            PronounSet set = pronounManager.getPronounSets().get(pronouns.get(0));

            if (individualColors) return ChatColor.translateAlternateColorCodes('&', set.getColor() + set.getDisplay());
            return set.getDisplay();
        } else if (pronouns.size() > 1) {
            StringBuilder result = new StringBuilder();
            for (int id : pronouns) {
                PronounSet set = pronounManager.getPronounSets().get(id);
                if (individualColors) result.append(separatorColor).append("/").append(set.getColor()).append(set.getDominant());
                else result.append("/").append(set.getDominant());
            }

            if (individualColors) return ChatColor.translateAlternateColorCodes('&', result.substring(separatorColor.length() + 1));
            return result.substring(1);
        } else {
            return PronounManager.noPronouns;
        }
    }

    public GPPlayer getOfflineLocalGPPlayer(String uuid) {
        YamlConfiguration user = getUserFile(uuid);
        List<Integer> pronouns = user.getIntegerList("pronouns");
        if (pronouns == null) pronouns = new ArrayList<>();

        return new GPPlayer(uuid, user.getString("username"), pronouns, user.getBoolean("opt-out-of-reminders", defaultReminders), user.getBoolean("fluid-reminders", false));
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
