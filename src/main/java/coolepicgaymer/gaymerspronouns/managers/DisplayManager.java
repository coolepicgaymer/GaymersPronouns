package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DisplayManager {

    GaymersPronouns plugin;
    static UserManager userManager;
    boolean papi;

    boolean tablist;
    boolean nametag;
    boolean undernametag;

    public DisplayManager(GaymersPronouns plugin, boolean papi) {
        this.plugin = plugin;
        userManager = GaymersPronouns.getUserManager();
        this.papi = papi;

        tablist = plugin.getConfig().getStringList("display").contains("tablist");
        nametag = plugin.getConfig().getStringList("display").contains("nametag");
        undernametag = plugin.getConfig().getStringList("display").contains("undernametag");
    }



    public void updateDisplay(Player player) {
        if (tablist) {
            player.setPlayerListName(replaceVariables(player, plugin.getConfig().getString("display-format.tab-list")));
        }
    }

    public static String replaceVariables(Player player, String string) {
        return ChatColor.translateAlternateColorCodes('&', string.replace("{DISPLAYNAME}", player.getDisplayName()).replace("{USERNAME}", player.getName()).replace("{PRONOUNS}", userManager.getDisplayUserPronouns(player.getUniqueId().toString())));
    }

}
