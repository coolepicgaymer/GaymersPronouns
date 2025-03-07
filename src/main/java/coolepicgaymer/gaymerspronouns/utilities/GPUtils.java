package coolepicgaymer.gaymerspronouns.utilities;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.managers.UserManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GPUtils {

    private static boolean papi;
    private static UserManager userManager;

    public static void reload() {
        papi = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        userManager = GaymersPronouns.getUserManager();
    }

    public static String replaceVariables(Player player, String string) {
        String s = ChatColor.translateAlternateColorCodes('&', string.replace("{DISPLAYNAME}", player.getDisplayName()).replace("{USERNAME}", player.getName()).replace("{PRONOUNS}", userManager.getDisplayUserPronouns(player.getUniqueId().toString())));
        if (papi) return PlaceholderAPI.setPlaceholders(player, s);
        else return s;
    }

    public static boolean isPapiInstalled() {
        return papi;
    }

}
