package coolepicgaymer.gaymerspronouns.listeners;

import coolepicgaymer.gaymerspronouns.utilities.GPUtils;
import coolepicgaymer.gaymerspronouns.GaymersPronouns;
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
    }

    public void reload() {
        chatFormat = plugin.getConfig().getStringList("display").contains("chatformat");
        chatHover = plugin.getConfig().getStringList("display").contains("chathover");
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        if (chatFormat) e.setFormat(ChatColor.translateAlternateColorCodes('&', GPUtils.replaceVariables(e.getPlayer(), plugin.getConfig().getString("display-format.chat-format").replace("{MESSAGE}", e.getMessage()))));
        if (chatHover) {
            e.setCancelled(true);
            TextComponent message = new TextComponent(e.getFormat());
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.translateAlternateColorCodes('&', GPUtils.replaceVariables(e.getPlayer(), String.join("\n", plugin.getConfig().getStringList("display-format.chat-hover")))))));
            Bukkit.spigot().broadcast(message);
        }
    }

}
