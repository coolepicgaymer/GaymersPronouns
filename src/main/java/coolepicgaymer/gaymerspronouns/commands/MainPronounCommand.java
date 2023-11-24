package coolepicgaymer.gaymerspronouns.commands;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.guis.PronounsMenu;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainPronounCommand implements CommandExecutor {

    GaymersPronouns plugin;

    public MainPronounCommand(GaymersPronouns plugin) {
        this.plugin = plugin;
        plugin.getCommand("pronouns").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player arg = Bukkit.getPlayer(args[0]);
            if (arg == null) {
                sender.sendMessage(MessageManager.getMessage("player-not-online", args[0]));
            } else {
                sender.sendMessage(MessageManager.getMessage("pronouns-other", arg.getName(), GaymersPronouns.getUserManager().getDisplayUserPronouns(arg.getUniqueId())));
            }
            return false;
        }
        ((Player) sender).openInventory(PronounsMenu.getInventory((Player) sender));
        return false;
    }
}
