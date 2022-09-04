package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlaceholderManager extends PlaceholderExpansion {

    GaymersPronouns plugin;

    public PlaceholderManager(GaymersPronouns plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "GaymersPronouns";
    }

    @Override
    public String getAuthor() {
        return "coolepicgaymer";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("pronouns")){
            return plugin.getPronouns(player.getUniqueId().toString());
        }
        return null;
    }
}
