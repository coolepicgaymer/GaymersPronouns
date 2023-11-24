package coolepicgaymer.gaymerspronouns.utilities;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {

    public static ItemStack getItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getItem(Material material, ItemStack original, int amount, String... lore) {
        ItemStack item = original.clone();
        item.setAmount(amount);
        item.setType(material);
        return getItem(item, item.getItemMeta().getDisplayName(), lore);
    }

    public static ItemStack getItem(ItemStack item, String name, String... lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> lores = new ArrayList<>();

        for (String s : lore) {
            lores.add(s);
        }
        return getItem(item, name, lores);
    }

    public static ItemStack getItem(ItemStack item, String name, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static void playSound(HumanEntity player, boolean success) {
        if (success) ((Player) player).playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
        else ((Player) player).playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 10, 1);
    }

}
