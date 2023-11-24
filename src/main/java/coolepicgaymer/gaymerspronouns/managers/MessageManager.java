package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private ConfigManager configManager;
    private static FileConfiguration messages;

    public MessageManager(GaymersPronouns plugin) {
        configManager = plugin.getConfigManager();
    }

    public void reloadMessages() {
        configManager.saveDefaultCustomConfig("messages.yml", false);
        messages = configManager.reloadCustomConfig("messages.yml");
    }

    public static String getMessage(String message) {
        if (!messages.isSet(message)) return message;
        return ChatColor.translateAlternateColorCodes('&', messages.getString(message));
    }

    public static String getMessage(String message, String... replace) {
        if (!messages.isSet(message)) return message;
        String msg = messages.getString(message);
        for (String s : replace) {
            msg = msg.replaceFirst("%s", s);
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
            if (i < replace.length && s.contains("%s")) {
                while (i < replace.length && s.contains("%s")) {
                    if (replace[i].contains("\n")) {
                        String[] rep = replace[i].split("\n");
                        msg.add(ChatColor.translateAlternateColorCodes('&', s.replaceFirst("%s", rep[0])));
                        for (int i2 = 1; i2 < rep.length; i2++) {
                            msg.add(ChatColor.translateAlternateColorCodes('&', rep[i2]));
                        }
                    } else s = s.replaceFirst("%s", replace[i]);
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
            if (s.contains("%s")) {
                while (s.contains("%s")) {
                    s = s.replaceFirst("%s", replace);
                }
            }
            msg.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return msg;
    }

}
