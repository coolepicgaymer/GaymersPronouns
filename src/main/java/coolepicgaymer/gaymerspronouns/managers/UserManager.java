package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.types.GPPlayer;
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

    private HashMap<UUID, GPPlayer> players;


    public UserManager(GaymersPronouns plugin) {
        this.plugin = plugin;
        pronounManager = GaymersPronouns.getPronounManager();
    }

    public void reload() {
        defaultReminders = !plugin.getConfig().getBoolean("default-no-pronouns-reminder");
        useDatabase = plugin.getConfig().getBoolean("database.use");

        db = plugin.getDatabaseManager();

        this.players = new HashMap<>();
        for(Player p : plugin.getServer().getOnlinePlayers()) loadPlayer(p.getUniqueId());
    }

    public void loadPlayer(UUID uuid) {
        if (useDatabase) {
            GPPlayer defaults = new GPPlayer(uuid.toString(), new ArrayList<>(), defaultReminders, false);
            GPPlayer player = db.createFullPlayerProfile(uuid.toString(), defaults);

            if (player != null) players.put(uuid, player);
            else {
                players.put(uuid, defaults);
                plugin.getLogger().warning(MessageManager.getMessage("console.sql.player-load-error", uuid.toString()));
            }
        } else {
            List<Integer> pronouns;
            boolean optOutReminders;
            boolean fluidReminders;

            YamlConfiguration config = getUserFile(uuid);

            if (!config.isSet("pronouns")) pronouns = new ArrayList<>();
            else pronouns = config.getIntegerList("pronouns");
            optOutReminders = config.getBoolean("opt-out-of-reminders", defaultReminders);
            fluidReminders = config.getBoolean("fluid-reminders");

            players.put(uuid, new GPPlayer(uuid.toString(), pronouns, optOutReminders, fluidReminders));
        }
    }

    public void unloadPlayer(UUID uuid) {
        players.remove(uuid);
    }

    public GPPlayer getPlayer(UUID uuid) {
        if (!players.containsKey(uuid)) loadPlayer(uuid);
        return players.get(uuid);
    }



    public void setOptOutReminders(UUID uuid, boolean value) {
        getPlayer(uuid).setOptOutReminders(value);

        if (useDatabase) db.updatePlayerOptionsEntry(getPlayer(uuid));
        else setSingleValue(uuid, "opt-out-of-reminders", value);
    }



    public boolean getOptOutReminders(UUID uuid) {
        return getPlayer(uuid).isOptOutReminders();
    }



    public void setFluidReminders(UUID uuid, boolean value) {
        getPlayer(uuid).setFluidReminders(value);

        if (useDatabase) db.updatePlayerOptionsEntry(getPlayer(uuid));
        else setSingleValue(uuid, "fluid-reminders", value);
    }



    public boolean getFluidReminders(UUID uuid) {
        return getPlayer(uuid).isFluidReminders();
    }



    public boolean hasPronouns(UUID uuid) {
        return (getPlayer(uuid).getPronouns().size() > 0);
    }



    public void setUserPronouns(UUID uuid, int... pronouns) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int id : pronouns) ids.add(id);
        setUserPronouns(uuid, ids);
    }



    public void setUserPronouns(UUID uuid, List<Integer> pronouns) {
        getPlayer(uuid).setPronouns(pronouns);

        if (useDatabase) db.updatePlayerPronounsEntry(getPlayer(uuid));
        else setSingleValue(uuid, "pronouns", pronouns);

        GaymersPronouns.getDisplayManager().updateDisplay(uuid);
    }



    private void setSingleValue(UUID uuid, String key, Object value) {
        YamlConfiguration config = getUserFile(uuid);
        config.set(key, value);
        setUserFile(uuid, config);
    }



    public List<Integer> getUserPronouns(UUID uuid) {
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
            StringBuilder result = new StringBuilder();
            for (int id : pronouns) {
                result.append("/").append(pronounManager.getPronounSets().get(id).getDominant());
            }
            return result.substring(1);
        } else {
            return PronounManager.noPronouns;
        }
    }

    public GPPlayer getOfflineLocalGPPlayer(String uuid) {
        YamlConfiguration user = getUserFile(uuid);
        List<Integer> pronouns = user.getIntegerList("pronouns");
        if (pronouns == null) pronouns = new ArrayList<>();

        return new GPPlayer(uuid, pronouns, user.getBoolean("opt-out-of-reminders", defaultReminders), user.getBoolean("fluid-reminders", false));
    }

    private YamlConfiguration getUserFile(String uuid) {
        File file = new File(plugin.getDataFolder() + "/users/" + uuid + ".yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    private YamlConfiguration getUserFile(UUID uuid) {
        return getUserFile(uuid.toString());
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
