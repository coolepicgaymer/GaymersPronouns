package coolepicgaymer.gaymerspronouns.commands;

import coolepicgaymer.gaymerspronouns.managers.*;
import coolepicgaymer.gaymerspronouns.types.GPPlayer;
import coolepicgaymer.gaymerspronouns.types.PronounSet;
import coolepicgaymer.gaymerspronouns.utilities.GPUtils;
import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.guis.ConfigurationMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final GaymersPronouns plugin;
    private final ConfigurationMenu configMenu;

    private boolean configurable;
    private boolean logChanges;
    private boolean devMode;
    private boolean useDatabase;

    private final String[] configure = {"format", "papi", "configure", "set", "pronouns"};
    private final String[] assign = {"assign", "unassign"};
    private final String[] formats = {"chat", "hover", "tablist"};
    private final String[] variables = {"{USERNAME}", "{DISPLAYNAME}", "{PRONOUNS}"};
    private final String[] locales = {"en", "da"};
    private final String[] configValues = {"language", "default-pronouns", "max-pronouns", "log-pronoun-changes", "random-priority-in-placeholders", "prompt-on-first-join", "default-no-pronouns-reminder", "show-tutorial-item", "update-tablist-periodically", "tablist-update-delay"};
    private List<String> pronounAliases;

    private final UserManager um;
    private final PronounManager pm;
    private DatabaseManager db;

    private HashMap<String, Integer> aliases;

    public AdminCommand(GaymersPronouns plugin, ConfigurationMenu configMenu) {
        this.plugin = plugin;
        this.configMenu = configMenu;
        plugin.getCommand("gaymerspronouns").setExecutor(this);

        this.um = GaymersPronouns.getUserManager();
        this.pm = GaymersPronouns.getPronounManager();
    }

    public void reload() {
        configurable = !plugin.getConfig().getBoolean("disable-in-game-configuration");
        logChanges = plugin.getConfig().getBoolean("log-pronoun-changes");

        devMode = plugin.getConfig().getBoolean("developer-mode", false);

        reloadAliases();

        useDatabase = plugin.getConfig().getBoolean("database.use");
        this.db = plugin.getDatabaseManager();
    }

    private void reloadAliases() {
        aliases = new HashMap<>();
        pronounAliases = new ArrayList<>();

        HashMap<Integer, PronounSet> sets;
        for (int id : (sets = pm.getPronounSets()).keySet()) {
            PronounSet set = sets.get(id);
            aliases.put(id + "", id);
            aliases.put(set.getDisplay().toLowerCase(), id);
            aliases.put(set.getDominant().toLowerCase(), id);
            aliases.put(set.getSubjective().toLowerCase(), id);
            aliases.put(set.getObjective().toLowerCase(), id);
            aliases.put(set.getPossessive().toLowerCase(), id);
            aliases.put(set.getReflexive().toLowerCase(), id);

            pronounAliases.add(set.getDisplay());
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gaymerspronouns.admin")) {
            sender.sendMessage(MessageManager.getMessage("configuration.no-permission"));
            return false;
        }
        if (configurable) {
            // TODO: add something i actually don't remember what it was but do something probably
        }
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "assign":
                    if (sender.hasPermission("gaymerspronouns.assign")) {
                        Player player;
                        if (args.length < 2) sender.sendMessage(MessageManager.getMessage("assignments.name-required"));
                        else if ((player = Bukkit.getPlayerExact(args[1])) == null) sender.sendMessage(MessageManager.getMessage("player-not-online"));
                        else attemptAssignPronouns(sender, player, args);
                        return false;
                    }
                    break;
                case "unassign":
                    if (sender.hasPermission("gaymerspronouns.assign")) {
                        Player player;
                        if (args.length < 2) sender.sendMessage(MessageManager.getMessage("assignments.name-required"));
                        else if ((player = Bukkit.getPlayerExact(args[1])) == null) sender.sendMessage(MessageManager.getMessage("player-not-online"));
                        else if (um.hasPronouns(player.getUniqueId())) {
                            sender.sendMessage(MessageManager.getMessage("assignments.unassigned", player.getName(), um.getDisplayUserPronouns(player.getUniqueId())));
                            if (logChanges) plugin.getLogger().info((MessageManager.getMessage("console.unassigned", sender.getName(), player.getName(), um.getDisplayUserPronouns(player.getUniqueId()))));
                            um.setUserPronouns(player.getUniqueId(), new ArrayList<>());
                        } else sender.sendMessage(MessageManager.getMessage("assignments.no-assigned-pronouns", player.getName()));
                        return false;
                    }
                    break;
                case "reload":
                    if (sender.hasPermission("gaymerspronouns.reload")) {
                        long time = System.currentTimeMillis();
                        plugin.reload();
                        if (sender instanceof Player) plugin.getLogger().info(MessageManager.getMessage("console.reload", sender.getName(), System.currentTimeMillis()-time + ""));
                        sender.sendMessage(MessageManager.getMessage("configuration.reload", System.currentTimeMillis()-time + ""));
                        return false;
                    }
                    break;
                case "configure":
                case "config":
                case "menu":
                    if (devMode && configurable && sender.hasPermission("gaymerspronouns.configure")) {
                        if (!(sender instanceof Player)) sender.sendMessage(MessageManager.getMessage("console.players-only"));
                        else ((Player) sender).openInventory(configMenu.getInventory((Player) sender));
                        return false;
                    }
                    break;
                case "set":
                    if (devMode && configurable && sender.hasPermission("gaymerspronouns.configure")) {
                        if (args.length < 3) {
                            if (args.length == 2) sender.sendMessage(MessageManager.getMessage("configuration.other.invalid-value"));
                            else sender.sendMessage(MessageManager.getMessage("configuration.other.no-value"));
                            return false;
                        }

                        switch (args[1].toLowerCase()) {
                            case "language":
                            case "locale":
                                attemptChangeLanguage(sender, args[2].toLowerCase());
                                break;
                            case "default-pronouns":
                            case "max-pronouns":
                                attemptChangeNumber(sender, args[1].toLowerCase(), args[2].toLowerCase());
                                break;
                            case "tablist-update-delay":
                                attemptChangeNumber(sender, "display-format." + args[1].toLowerCase(), args[2].toLowerCase());
                                break;
                            case "update-tablist-periodically":
                                attemptChangeBoolean(sender, "display-format." + args[1].toLowerCase(), args[2].toLowerCase());
                                break;
                            default:
                                attemptChangeBoolean(sender, args[1].toLowerCase(), args[2].toLowerCase());
                                break;
                        }
                        return false;
                    }
                    break;
                case "pronoun":
                case "pronouns":
                    if (devMode && configurable && sender.hasPermission("gaymerspronouns.configure")) {
                        sender.sendMessage("This is meant to do something...");
                        return false;
                    }
                    break;
                case "format":
                    if (devMode && configurable && sender.hasPermission("gaymerspronouns.configure")) {
                        if (args.length > 1) {
                            switch (args[1].toLowerCase()) {
                                case "chat":
                                    if (args.length > 2) attemptChangeChatFormat(sender, args);
                                    else {
                                        for (String s : MessageManager.getMessages("configuration.instructions.chatformat", PronounManager.noPronouns)) sender.sendMessage(s);
                                        if (!plugin.getConfig().getList("display", new ArrayList<>()).contains("chatformat")) sender.sendMessage(MessageManager.getMessage("configuration.display-changes.format-disabled"));
                                    }
                                    return false;
                                case "hover":
                                    if (args.length > 2) attemptChangeChatHover(sender, args);
                                    else {
                                        for (String s : MessageManager.getMessages("configuration.instructions.chathover", PronounManager.noPronouns)) sender.sendMessage(s);
                                        if (!plugin.getConfig().getList("display", new ArrayList<>()).contains("chathover")) sender.sendMessage(MessageManager.getMessage("configuration.display-changes.format-disabled"));
                                    }
                                    return false;
                                case "tablist":
                                case "tab":
                                    if (args.length > 2) attemptChangeTablist(sender, args);
                                    else {
                                        for (String s : MessageManager.getMessages("configuration.instructions.tablist", PronounManager.noPronouns)) sender.sendMessage(s);
                                        if (!plugin.getConfig().getList("display", new ArrayList<>()).contains("tablist")) sender.sendMessage(MessageManager.getMessage("configuration.display-changes.format-disabled"));
                                    }
                                    return false;
                            }
                        }
                        for (String s : MessageManager.getMessagesRepeat("configuration.instructions.format-editor", label)) sender.sendMessage(s);
                        return false;
                    }
                    break;
                case "papi":
                case "placeholder":
                case "placeholderapi":
                    if (sender.hasPermission("gaymerspronouns.configure")) {
                        for (String s : MessageManager.getMessages("configuration.instructions.papi")) sender.sendMessage(s);
                        if (!GPUtils.isPapiInstalled()) for (String s : MessageManager.getMessages("configuration.instructions.papi-not-installed")) sender.sendMessage(s);
                        return false;
                    }
                    break;
                case "migratedata":
                    if (sender.hasPermission("gaymerspronouns.database")) {
                        if (!useDatabase) sender.sendMessage(MessageManager.getMessage("configuration.database.migration.not-enabled", label));
                        else if (args.length > 1 && args[1].equalsIgnoreCase("confirm")){
                            sender.sendMessage(MessageManager.getMessage("configuration.database.migration.start"));
                            plugin.getLogger().info(MessageManager.getMessage("console.sql.migration-started", sender.getName()));
                            if (attemptMigratePlayerDataToDatabase()) {
                                plugin.reload();
                            } else {
                                sender.sendMessage(MessageManager.getMessage("configuration.database.migration.error"));
                                plugin.getLogger().info(MessageManager.getMessage("console.sql.migration-error", sender.getName()));
                            }
                            sender.sendMessage(MessageManager.getMessage("configuration.database.migration.success"));
                            plugin.getLogger().info(MessageManager.getMessage("console.sql.migration-success", sender.getName()));
                        } else {
                            sender.sendMessage(MessageManager.getMessage("configuration.database.migration.confirm", label));
                        }
                        return false;
                    }
                    break;
            }
        }
        sender.sendMessage(MessageManager.getMessage("configuration.help"));
        if (sender.hasPermission("gaymerspronouns.assign")) for (String s : MessageManager.getMessagesRepeat("configuration.help-assign", label)) sender.sendMessage(s);
        if (sender.hasPermission("gaymerspronouns.reload")) sender.sendMessage(MessageManager.getMessage("configuration.help-reload", label));
        if (devMode && configurable && sender.hasPermission("gaymerspronouns.configure")) for (String s : MessageManager.getMessagesRepeat("configuration.help-configure", label)) sender.sendMessage(s);
        if (sender.hasPermission("gaymerspronouns.database") && useDatabase) for (String s : MessageManager.getMessagesRepeat("configuration.help-database", label)) sender.sendMessage(s);
        return false;
    }

    private boolean attemptMigratePlayerDataToDatabase() {
        List<GPPlayer> players = new ArrayList<>();

        File folder = new File(plugin.getDataFolder() + "/users/");

        if (folder.isDirectory()) for (File f : folder.listFiles()) {
            players.add(um.getOfflineLocalGPPlayer(f.getName().substring(0, 36)));
        }

        for (GPPlayer player : players) {
            if (db.createFullPlayerProfile(player.getUuid(), player) == null) return false;
            if (!db.updatePlayerPronounsEntry(player)) return false;
        }

        return true;
    }

    private void attemptChangeLanguage(CommandSender sender, String lang) {
        for (String s : locales) {
            if (lang.equals(s)) {
                plugin.getLogger().info(MessageManager.getMessage("console.changed-config", "locale", sender.getName(), plugin.getConfig().getString("locale").toUpperCase(), lang.toUpperCase()));
                sender.sendMessage(MessageManager.getMessage("configuration.other.changed-value", "locale", plugin.getConfig().getString("locale").toUpperCase(), lang.toUpperCase()));
                plugin.getConfig().set("locale", lang.toUpperCase());
                plugin.saveConfig();

                ConfigManager configManager = GaymersPronouns.getConfigManager();
                configManager.reload();

                configManager.regenerateCustomConfig("messages.yml");
                configManager.regenerateCustomConfig("pronouns.yml");

                plugin.reload();

                plugin.getLogger().info(MessageManager.getMessage("console.language-files-reset"));
                sender.sendMessage(MessageManager.getMessage("configuration.other.language-files-reset"));
                return;
            }
        }
        sender.sendMessage(MessageManager.getMessage("configuration.other.not-a-language"));
    }

    private void attemptChangeNumber(CommandSender sender, String path, String value) {
        int i;
        try {
            i = Integer.parseInt(value);
        } catch(NumberFormatException e) {
            sender.sendMessage(MessageManager.getMessage("configuration.other.not-a-number"));
            return;
        }

        plugin.getLogger().info(MessageManager.getMessage("console.changed-config", path, sender.getName(), plugin.getConfig().getString(path), value));
        sender.sendMessage(MessageManager.getMessage("configuration.other.changed-value", path, plugin.getConfig().getString(path), value));
        plugin.getConfig().set(path, i);
        plugin.saveConfig();
    }

    private void attemptChangeBoolean(CommandSender sender, String path, String value) {
        if (!(plugin.getConfig().isSet(path) && plugin.getConfig().get(path) instanceof Boolean)) {
            sender.sendMessage(MessageManager.getMessage("configuration.other.invalid-value"));
        }

        boolean bool;

        switch (value) {
            case "true":
            case "yes":
                bool = true;
                break;
            case "false":
            case "no":
                bool = false;
                break;
            default:
                sender.sendMessage(MessageManager.getMessage("configuration.other.not-a-boolean"));
                return;
        }

        plugin.getLogger().info(MessageManager.getMessage("console.changed-config", path, sender.getName(), plugin.getConfig().getString(path), value));
        sender.sendMessage(MessageManager.getMessage("configuration.other.changed-value", path, plugin.getConfig().getString(path), value));
        plugin.getConfig().set(path, bool);
        plugin.saveConfig();
    }

    private void attemptAssignPronouns(CommandSender sender, Player player, String[] args) {
        List<Integer> newPronouns = new ArrayList<>();

        for (int i = 2; i < args.length; i++) {
            int id;
            if (aliases.containsKey(args[i].toLowerCase())) {
                if (!newPronouns.contains((id = aliases.get(args[i].toLowerCase())))) newPronouns.add(id);
            } else {
                sender.sendMessage(MessageManager.getMessage("assignments.unrecognized-pronouns", args[i]));
                return;
            }
        }

        if (um.hasPronouns(player.getUniqueId())) {
            sender.sendMessage(MessageManager.getMessage("assignments.assigned-changed", um.getDisplayFromList(newPronouns), player.getName(), um.getDisplayUserPronouns(player.getUniqueId())));
            if (logChanges) plugin.getLogger().info((MessageManager.getMessage("console.assigned-changed", sender.getName(), um.getDisplayFromList(newPronouns), player.getName(), um.getDisplayUserPronouns(player.getUniqueId()))));
        }
        else {
            sender.sendMessage(MessageManager.getMessage("assignments.assigned-new", um.getDisplayFromList(newPronouns), player.getName()));
            if (logChanges) plugin.getLogger().info((MessageManager.getMessage("console.assigned-new", sender.getName(), um.getDisplayFromList(newPronouns), player.getName(), um.getDisplayUserPronouns(player.getUniqueId()))));
        }

        um.setUserPronouns(player.getUniqueId(), newPronouns);
    }

    private String joinArguments(String[] args, int offset) {
        StringBuilder s = new StringBuilder();
        for (int i = offset; i < args.length; i++) {
            s.append(" ").append(args[i]);
        }
        return s.substring(1);
    }

    private void attemptChangeChatFormat(CommandSender sender, String[] args) {
        String format = joinArguments(args, 2);
        if (format.contains("{USERNAME}") || format.contains("{DISPLAYNAME}")) {
            if (!format.contains("{MESSAGE}")) {
                if (!format.substring(format.length()-3).startsWith(" &")) format += " ";
                format += "{MESSAGE}";
            }
            if (sender instanceof Player) for (String s : MessageManager.getMessages("configuration.display-changes.chatformat", GPUtils.replaceVariables((Player) sender, format.replace("{MESSAGE}", MessageManager.getMessage("configuration.misc.generic-message"))))) sender.sendMessage(s);
            else for (String s : MessageManager.getMessages("configuration.display-changes.chatformat", MessageManager.getMessage("configuration.display-changes.example-unavailable"))) sender.sendMessage(s);
            if (!plugin.getConfig().getList("display", new ArrayList<>()).contains("chatformat")) sender.sendMessage(MessageManager.getMessage("configuration.display-changes.format-disabled"));
            setFormat("chat-format", format, sender.getName());
        } else {
            sender.sendMessage(MessageManager.getMessage("configuration.display-changes.must-contain-username"));
        }
    }

    private void attemptChangeChatHover(CommandSender sender, String[] args) {
        String format = joinArguments(args, 2);
        if (sender instanceof Player) for (String s : MessageManager.getMessages("configuration.display-changes.chathover", GPUtils.replaceVariables((Player) sender, format.replaceAll("\\n", "\n")))) sender.sendMessage(s);
        else for (String s : MessageManager.getMessages("configuration.display-changes.chathover", MessageManager.getMessage("configuration..example-unavailable"))) sender.sendMessage(s);
        if (!plugin.getConfig().getList("display", new ArrayList<>()).contains("chathover")) sender.sendMessage(MessageManager.getMessage("configuration.display-changes.format-disabled"));
        setFormat("chat-hover", format.split("\\n"), sender.getName());
    }

    private void attemptChangeTablist(CommandSender sender, String[] args) {
        String format = joinArguments(args, 2);
        if (format.contains("{USERNAME}") || format.contains("{DISPLAYNAME}")) {
            if (sender instanceof Player) for (String s : MessageManager.getMessages("configuration.display-changes.tablist", GPUtils.replaceVariables((Player) sender, format))) sender.sendMessage(s);
            else for (String s : MessageManager.getMessages("configuration.display-changes.tablist", MessageManager.getMessage("configuration.display-changes.example-unavailable"))) sender.sendMessage(s);
            if (plugin.getConfig().getList("display", new ArrayList<>()).contains("tablist")) sender.sendMessage(MessageManager.getMessage("configuration.toggles.requires-reload"));
            else sender.sendMessage(MessageManager.getMessage("configuration.display-changes.format-disabled"));
            setFormat("tab-list", format, sender.getName());
        } else {
            sender.sendMessage(MessageManager.getMessage("configuration.display-changes.must-contain-username"));
        }
    }

    private void setFormat(String type, Object format, String username) {
        plugin.getLogger().info(MessageManager.getMessage("console.changed-config", "display-format." + type, username, plugin.getConfig().get("display-format." + type).toString(), format.toString()));
        plugin.getConfig().set("display-format." + type, format);
        plugin.saveConfig();
        configMenu.reload();
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> availableArguments = new ArrayList<>();
            if (devMode && configurable && sender.hasPermission("gaymerspronouns.configure")) {
                for (String s : configure) if (s.startsWith(args[0].toLowerCase())) availableArguments.add(s);
            }
            if (sender.hasPermission("gaymerspronouns.assign")) {
                for (String s : assign) if (s.startsWith(args[0].toLowerCase())) availableArguments.add(s);
            }
            if (sender.hasPermission("gaymerspronouns.reload")) {
                if ("reload".startsWith(args[0].toLowerCase())) availableArguments.add("reload");
            }
            if (sender.hasPermission("gaymerspronouns.database") && useDatabase) {
                if ("migratedata".startsWith(args[0].toLowerCase())) availableArguments.add("migratedata");
            }
            return availableArguments;
        } else if (args.length > 1) {
            if (args.length > 2 && args[0].equalsIgnoreCase("assign")) {
                if (sender.hasPermission("gaymerspronouns.assign")) {
                    List<String> availableArguments = new ArrayList<>();
                    for (String s : pronounAliases) if (s.toLowerCase().contains(args[args.length - 1].toLowerCase())) availableArguments.add(s);
                    return availableArguments;
                }
            } else if (devMode) {
                if (args[0].equalsIgnoreCase("format")) {
                    if (configurable && sender.hasPermission("gaymerspronouns.configure")) {
                        List<String> availableArguments = new ArrayList<>();
                        if (args.length == 2) {
                            for (String s : formats) if (s.startsWith(args[1].toLowerCase())) availableArguments.add(s);
                        } else {
                            for (String s : variables) if (s.contains(args[args.length - 1].toUpperCase())) availableArguments.add(s);
                            if (args[1].equalsIgnoreCase("chat")) if ("{MESSAGE}".contains(args[args.length-1].toUpperCase())) availableArguments.add("{MESSAGE}");
                        }
                        return availableArguments;
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (configurable && sender.hasPermission("gaymerspronouns.configure")) {
                        if (args.length == 2) {
                            List<String> availableArguments = new ArrayList<>();
                            for (String s : configValues) if (s.toLowerCase().contains(args[args.length - 1].toLowerCase())) availableArguments.add(s);
                            return availableArguments;
                        } else if (args.length == 3) {
                            List<String> availableArguments = new ArrayList<>();
                            switch (args[1].toLowerCase()) {
                                case "language":
                                case "locale":
                                    for (String s : locales) if (s.toLowerCase().startsWith(args[2].toLowerCase())) availableArguments.add(s);
                                    break;
                                case "tablist-update-delay":
                                    String s1;
                                    if ((s1 = plugin.getConfig().getString("display-format." + args[1].toLowerCase())).startsWith(args[2]))availableArguments.add(s1);
                                    break;
                                case "default-pronouns":
                                case "max-pronouns":
                                    String s2;
                                    if ((s2 = plugin.getConfig().getString(args[1].toLowerCase())).startsWith(args[2])) availableArguments.add(s2);
                                    break;
                                default:
                                    if ("true".startsWith(args[2].toLowerCase())) availableArguments.add("true");
                                    if ("false".startsWith(args[2].toLowerCase())) availableArguments.add("false");
                                    break;
                            }
                            return availableArguments;
                        }
                    }
                }
            }
        }
        return null;
    }
}
