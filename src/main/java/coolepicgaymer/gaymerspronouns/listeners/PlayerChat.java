package coolepicgaymer.gaymerspronouns.listeners;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.managers.DisplayManager;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    GaymersPronouns plugin;
    boolean chatFormat;
    boolean chatHover;

    public PlayerChat(GaymersPronouns plugin) {
        this.plugin = plugin;
        chatFormat = plugin.getConfig().getStringList("display").contains("chatformat");
        chatHover = plugin.getConfig().getStringList("display").contains("chathover");
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        String pronouns = GaymersPronouns.getUserManager().getDisplayUserPronouns(e.getPlayer().getUniqueId().toString());
        if (chatFormat) e.setFormat(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(e.getPlayer(), plugin.getConfig().getString("display-format.chat-format").replace("{PRONOUNS}", pronouns).replace("{DISPLAYNAME}", e.getPlayer().getDisplayName()).replace("{USERNAME}", e.getPlayer().getName()).replace("{MESSAGE}", e.getMessage()))));
        if (chatHover) {
            e.setCancelled(true);
            TextComponent message = new TextComponent(e.getFormat());
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(e.getPlayer(), DisplayManager.replaceVariables(e.getPlayer(), String.join("\n", plugin.getConfig().getStringList("display-format.chat-hover"))))))));
            Bukkit.spigot().broadcast(message);
        }
    }

}
