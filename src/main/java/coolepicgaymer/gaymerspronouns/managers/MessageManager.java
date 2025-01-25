package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MessageManager {

    private final ConfigManager configManager;
    private static FileConfiguration messages;

    Logger logger;

    public MessageManager(GaymersPronouns plugin) {
        logger = plugin.getLogger();

        configManager = GaymersPronouns.getConfigManager();
    }

    public void reloadMessages() {
        configManager.saveDefaultCustomConfig("messages.yml", false);
        messages = configManager.reloadCustomConfig("messages.yml");

        int version = 3;                                                                                                                 // <-- CURRENT CONFIG VERSION
        if (!messages.isSet("version") || !messages.isInt("version") || messages.getInt("version") != version) {
            messages = configManager.regenerateCustomConfig("messages.yml");

            logger.info(getMessage("console.invalid-file", "messages.yml"));
        }
    }

    public static String getMessage(String message) {
        if (!messages.isSet(message)) return message;
        return ChatColor.translateAlternateColorCodes('&', messages.getString(message, message));
    }

    public static String getMessage(String message, String... replace) {
        if (!messages.isSet(message)) return message;
        String msg = messages.getString(message);
        for (int i = 0; i < replace.length; i++) {
            msg = msg.replaceFirst("\\{" + i + "}", replace[i]);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> getMessages(String message) {
        List<String> msg = new ArrayList<>();
        if (!messages.isSet(message)) {
            msg.add(message);
            return msg;
        }
        for (String s : messages.getStringList(message)) {
            msg.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return msg;
    }

    public static List<String> getMessages(String message, String... replace) {
        List<String> msg = new ArrayList<>();
        if (!messages.isSet(message)) {
            msg.add(message);
            return msg;
        }
        int i = 0;
        for (String s : messages.getStringList(message)) {
            if (i < replace.length && s.contains("{" + i + "}")) {
                while (i < replace.length && s.contains("{" + i + "}")) {
                    if (replace[i].contains("\n")) {
                        String[] rep = replace[i].split("\n");
                        msg.add(ChatColor.translateAlternateColorCodes('&', s.replaceFirst("\\{" + i + "}", rep[0])));
                        for (int i2 = 1; i2 < rep.length; i2++) {
                            msg.add(ChatColor.translateAlternateColorCodes('&', rep[i2]));
                        }
                    } else s = s.replaceFirst("\\{" + i + "}", replace[i]);
                    i++;
                }
            }
            msg.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return msg;
    }

    public static List<String> getMessagesRepeat(String message, String replace) {
        List<String> msg = new ArrayList<>();
        if (!messages.isSet(message)) {
            msg.add(message);
            return msg;
        }

        for (String s : messages.getStringList(message)) {
            if (s.contains("{0}")) {
                while (s.contains("{0}")) {
                    s = s.replaceFirst("\\{0}", replace);
                }
            }
            msg.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return msg;
    }

}
