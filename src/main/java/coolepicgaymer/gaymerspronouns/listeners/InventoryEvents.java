package coolepicgaymer.gaymerspronouns.listeners;

import coolepicgaymer.gaymerspronouns.guis.ConfigurationMenu;
import coolepicgaymer.gaymerspronouns.guis.PronounsMenu;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryEvents implements Listener {

    private final ConfigurationMenu configMenu;

    public InventoryEvents(ConfigurationMenu configMenu) {
        this.configMenu = configMenu;
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null || e.getCurrentItem() == null) return;
        String title = e.getView().getTitle();
        if (title.equals(MessageManager.getMessage("pronouns-menu"))) {
            e.setCancelled(true);
            if (e.getClickedInventory().getType() == InventoryType.CHEST) PronounsMenu.onClick(e);
        }
        else if (title.equals(MessageManager.getMessage("configuration.gui.name"))) {
            e.setCancelled(true);
            if (e.getClickedInventory().getType() == InventoryType.CHEST) configMenu.onClick(e);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals(MessageManager.getMessage("pronouns-menu"))) {
            PronounsMenu.close(e.getPlayer());
        }
    }

}
