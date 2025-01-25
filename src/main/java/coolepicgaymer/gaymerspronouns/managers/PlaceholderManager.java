package coolepicgaymer.gaymerspronouns.managers;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager extends PlaceholderExpansion {

    GaymersPronouns plugin;
    UserManager userManager;

    boolean random;

    public PlaceholderManager(GaymersPronouns plugin) {
        this.plugin = plugin;
        this.userManager = GaymersPronouns.getUserManager();
    }

    public void reload() {
        random = plugin.getConfig().getBoolean("random-priority-in-placeholders");
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "GaymersPronouns";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "coolepicgaymer";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("pronouns")) {
            return userManager.getDisplayUserPronouns(player.getUniqueId());
        }
        else if (params.equalsIgnoreCase("subjective")) {
            if (!params.startsWith("S")) return (userManager.getUserPronoun(player.getUniqueId(), params, random)).toLowerCase();
            return (userManager.getUserPronoun(player.getUniqueId(), params, random));
        }
        else if (params.equalsIgnoreCase("objective")) {
            if (!params.startsWith("O")) return (userManager.getUserPronoun(player.getUniqueId(), params, random)).toLowerCase();
            return (userManager.getUserPronoun(player.getUniqueId(), params, random));
        }
        else if (params.equalsIgnoreCase("possessive")) {
            if (!params.startsWith("P")) return (userManager.getUserPronoun(player.getUniqueId(), params, random)).toLowerCase();
            return (userManager.getUserPronoun(player.getUniqueId(), params, random));
        }
        else if (params.equalsIgnoreCase("reflexive")) {
            if (!params.startsWith("R")) return (userManager.getUserPronoun(player.getUniqueId(), params, random)).toLowerCase();
            return (userManager.getUserPronoun(player.getUniqueId(), params, random));
        }
        else if (params.equalsIgnoreCase("verb")) {
            if (!params.startsWith("V")) return (userManager.getUserPronoun(player.getUniqueId(), params, random)).toLowerCase();
            return (userManager.getUserPronoun(player.getUniqueId(), params, random));
        }
        return null;
    }
}
