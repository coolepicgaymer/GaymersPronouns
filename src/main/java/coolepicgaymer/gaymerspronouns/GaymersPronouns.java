package coolepicgaymer.gaymerspronouns;

import coolepicgaymer.gaymerspronouns.commands.MainPronounCommand;
import coolepicgaymer.gaymerspronouns.listeners.InventoryEvents;
import coolepicgaymer.gaymerspronouns.listeners.PlayerChat;
import coolepicgaymer.gaymerspronouns.listeners.PlayerJoin;
import coolepicgaymer.gaymerspronouns.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GaymersPronouns extends JavaPlugin {

    private static UserManager userManager;
    private static PronounManager pronounManager;
    private static ConfigManager configManager;
    private static DisplayManager displayManager;
    private static MessageManager messageManager;

    @Override
    public void onEnable() {
        boolean papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        configManager = new ConfigManager(this);
        pronounManager = new PronounManager(this);
        userManager = new UserManager(this);
        messageManager = new MessageManager(this);
        displayManager = new DisplayManager(this, papi);
        if (papi) new PlaceholderManager(this).register();

        Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChat(this), this);

        new MainPronounCommand(this);

        reload();
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Reloads the config and all pronouns (not users.yml).
     */
    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        pronounManager.reloadDefaults();
        pronounManager.reloadPronouns();

        messageManager.reloadMessages();
    }

    public static UserManager getUserManager() {
        return userManager;
    }

    public static DisplayManager getDisplayManager() {
        return displayManager;
    }

    public static PronounManager getPronounManager() {
        return pronounManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static String getPronouns(String uuid) {
        return userManager.getDisplayUserPronouns(uuid);
    }

}
