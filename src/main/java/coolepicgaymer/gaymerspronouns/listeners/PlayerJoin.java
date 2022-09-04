package coolepicgaymer.gaymerspronouns.listeners;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.managers.DisplayManager;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import coolepicgaymer.gaymerspronouns.managers.UserManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    DisplayManager displayManager;
    UserManager userManager;

    public PlayerJoin(GaymersPronouns plugin) {
        displayManager = plugin.getDisplayManager();
        userManager = plugin.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        displayManager.updateDisplay(e.getPlayer());

        if (userManager.hasPronouns(e.getPlayer().getUniqueId().toString())) {
            if (userManager.getFluidReminders(e.getPlayer().getUniqueId().toString())) {
                sendReminder(e.getPlayer(), "fluid-reminder");
            }
        } else {
            if (!userManager.getOptOutReminders(e.getPlayer().getUniqueId().toString())) {
                sendReminder(e.getPlayer(), "no-pronouns-reminder");
            }
        }
    }

    private void sendReminder(Player player, String message) {
        TextComponent msg = new TextComponent(MessageManager.getMessage(message, userManager.getDisplayUserPronouns(player.getUniqueId().toString())));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageManager.getMessage("change-pronouns-hover"))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pronouns"));
        player.spigot().sendMessage(msg);
    }

}
