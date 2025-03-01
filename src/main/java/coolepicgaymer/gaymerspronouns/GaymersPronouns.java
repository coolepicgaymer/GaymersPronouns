package coolepicgaymer.gaymerspronouns;

import coolepicgaymer.gaymerspronouns.commands.AdminCommand;
import coolepicgaymer.gaymerspronouns.commands.MainPronounCommand;
import coolepicgaymer.gaymerspronouns.guis.ConfigurationMenu;
import coolepicgaymer.gaymerspronouns.guis.PronounsMenu;
import coolepicgaymer.gaymerspronouns.listeners.InventoryEvents;
import coolepicgaymer.gaymerspronouns.listeners.PlayerChat;
import coolepicgaymer.gaymerspronouns.listeners.PlayerJoin;
import coolepicgaymer.gaymerspronouns.managers.*;
import coolepicgaymer.gaymerspronouns.utilities.GPUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GaymersPronouns extends JavaPlugin {

    private static UserManager userManager;
    private static PronounManager pronounManager;
    private static ConfigManager configManager;
    private static DisplayManager displayManager;
    private static MessageManager messageManager;
    private ConfigurationMenu configMenu;
    private PlaceholderManager placeholderManager;
    private DatabaseManager databaseManager;

    private static PlayerChat playerChat;
    private static AdminCommand adminCommand;
    private PlayerJoin playerJoin;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        pronounManager = new PronounManager(this);
        userManager = new UserManager(this);
        configMenu = new ConfigurationMenu(this);
        messageManager = new MessageManager(this);
        displayManager = new DisplayManager(this);

        Bukkit.getPluginManager().registerEvents((playerJoin = new PlayerJoin(this)), this);
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(configMenu), this);
        Bukkit.getPluginManager().registerEvents((playerChat = new PlayerChat(this)), this);

        GPUtils.reload();

        new MainPronounCommand(this);
        adminCommand = new AdminCommand(this, configMenu);

        if (GPUtils.isPapiInstalled()) (placeholderManager = new PlaceholderManager(this)).register();

        reload();
    }



    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.close();
    }

    /**
     * Reloads the config, messages and all pronouns (not users.yml).
     */
    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        configManager.reload();

        pronounManager.reload();

        messageManager.reloadMessages();

        if (getConfig().getBoolean("database.use")) databaseManager = new DatabaseManager(this, getConfig().getString("database.url"), getConfig().getString("database.user"), getConfig().getString("database.password"));

        playerChat.reload();
        playerJoin.reload();
        userManager.reload();
        displayManager.reload();
        configMenu.reload();

        adminCommand.reload();

        if (GPUtils.isPapiInstalled()) placeholderManager.reload();

        PronounsMenu.reload(getConfig().getBoolean("show-tutorial-item"), getConfig().getBoolean("log-pronoun-changes"), getLogger());
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

    public DatabaseManager getDatabaseManager() { return databaseManager; }

}
