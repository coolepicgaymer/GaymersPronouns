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
        configManager.saveDefaultCustomConfig("messages.yml");
        messages = configManager.reloadCustomConfig("messages.yml");
    }

    public static String getMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', messages.getString(message));
    }

    public static String getMessage(String message, String... replace) {
        String msg = messages.getString(message);
        for (String s : replace) {
            msg = msg.replaceFirst("%s", s);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> getMessages(String message) {
        List<String> msg = new ArrayList<>();
        for (String s : messages.getStringList(message)) {
            msg.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return msg;
    }

    public static List<String> getMessages(String message, String... replace) {
        List<String> msg = new ArrayList<>();
        int i = 0;
        for (String s : messages.getStringList(message)) {
            if (i < replace.length && s.contains("%s")) {
                s = s.replaceFirst("%s", replace[i]);
                i++;
            }
            msg.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return msg;
    }

}
