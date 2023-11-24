package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.utilities.GPUtils;
import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DisplayManager {

    GaymersPronouns plugin;
    static UserManager userManager;
    boolean papi;

    boolean tablist;

    public DisplayManager(GaymersPronouns plugin) {
        this.plugin = plugin;
        userManager = GaymersPronouns.getUserManager();
    }

    public void reload() {
        this.papi = GPUtils.isPapiInstalled();
        tablist = plugin.getConfig().getStringList("display").contains("tablist");
        updateAllDisplays();
    }



    public void updateDisplay(Player player) {
        if (tablist) {
            if (papi) player.setPlayerListName(PlaceholderAPI.setPlaceholders(player, GPUtils.replaceVariables(player, plugin.getConfig().getString("display-format.tab-list"))));
            else player.setPlayerListName(GPUtils.replaceVariables(player, plugin.getConfig().getString("display-format.tab-list")));
        }
    }

    public void updateAllDisplays() {
        if (tablist) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateDisplay(player);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setPlayerListName(null);
            }
        }
    }

}
