package coolepicgaymer.gaymerspronouns.listeners;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.guis.PronounsMenu;
import coolepicgaymer.gaymerspronouns.managers.DisplayManager;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import coolepicgaymer.gaymerspronouns.managers.UserManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    DisplayManager displayManager;
    UserManager userManager;
    GaymersPronouns plugin;

    boolean update;
    int delay;

    boolean popup;

    public PlayerJoin(GaymersPronouns plugin) {
        displayManager = plugin.getDisplayManager();
        userManager = plugin.getUserManager();
        this.plugin = plugin;
    }

    public void reload() {
        update = plugin.getConfig().getBoolean("display-format.update-tablist-periodically");
        delay = plugin.getConfig().getInt("display-format.tablist-update-delay");

        popup = plugin.getConfig().getBoolean("prompt-on-first-join", false);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        displayManager.updateDisplay(e.getPlayer());
        if (update) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                displayManager.updateDisplay(e.getPlayer());
            }, delay);
        }

        if (popup && !e.getPlayer().hasPlayedBefore()) {
            e.getPlayer().openInventory(PronounsMenu.getInventory(e.getPlayer()));
        } else if (userManager.hasPronouns(e.getPlayer().getUniqueId())) {
            if (userManager.getFluidReminders(e.getPlayer().getUniqueId())) {
                sendReminder(e.getPlayer(), "fluid-reminder");
            }
        } else {
            if (!userManager.getOptOutReminders(e.getPlayer().getUniqueId())) {
                sendReminder(e.getPlayer(), "no-pronouns-reminder");
            }
        }
    }

    private void sendReminder(Player player, String message) {
        TextComponent msg = new TextComponent(MessageManager.getMessage(message, userManager.getDisplayUserPronouns(player.getUniqueId())));
        msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(MessageManager.getMessage("change-pronouns-hover"))));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pronouns"));
        player.spigot().sendMessage(msg);
    }

}
