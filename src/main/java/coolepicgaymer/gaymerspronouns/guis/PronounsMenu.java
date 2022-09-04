package coolepicgaymer.gaymerspronouns.guis;

import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import coolepicgaymer.gaymerspronouns.managers.PronounManager;
import coolepicgaymer.gaymerspronouns.managers.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PronounsMenu {

    private static UserManager userManager = GaymersPronouns.getUserManager();
    private static PronounManager pronounManager = GaymersPronouns.getPronounManager();
    private static HashMap<Player, List<Integer>> picked = new HashMap<>();
    private static HashMap<Player, Integer> playerPage = new HashMap<>();
    private static HashMap<Player, Boolean> multiple = new HashMap<>();

    public static Inventory getInventory(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, MessageManager.getMessage("pronouns-menu"));
        playerPage.put(player, 1);
        multiple.put(player, false);

        for (int i = 41; i <= 43; i++) inv.setItem(i, InventoryUtils.getItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), MessageManager.getMessage("cancel-name"), MessageManager.getMessages("cancel-lore")));

        setMultiplePronounStatus(inv, 0);

        formatGroup1(player, inv, 1, false);
        formatGroup2(player, inv, false);

        inv.setItem(29, InventoryUtils.getItem(new ItemStack(Material.BOOK), MessageManager.getMessage("info-item-name"), MessageManager.getMessages("info-item-lore")));

        if (!userManager.getFluidReminders(player.getUniqueId().toString())) inv.setItem(44, InventoryUtils.getItem(new ItemStack(Material.GRAY_DYE), MessageManager.getMessage("pronoun-reminder-name"), MessageManager.getMessages("pronoun-reminder-lore", "enable")));
        else inv.setItem(44, InventoryUtils.getItem(new ItemStack(Material.LIME_DYE), MessageManager.getMessage("pronoun-reminder-name"), MessageManager.getMessages("pronoun-reminder-lore", "disable")));

        return inv;
    }

    private static final int[] slots = {1, 2, 3, 10, 11, 12, 19, 20, 21};

    private static void formatGroup1(Player player, Inventory inv, int page, boolean multiple) {
        playerPage.put(player, page);
        int max = pronounManager.getGroup1().size();
        boolean prev;
        if (prev = (page > 1)) inv.setItem(28, InventoryUtils.getItem(new ItemStack(Material.ARROW), MessageManager.getMessage("previous-page-name")));
        else inv.setItem(28, new ItemStack(Material.AIR));
        if (page*9 < max) inv.setItem(30, InventoryUtils.getItem(new ItemStack(Material.PAPER), MessageManager.getMessage("next-page-name")));
        else if (prev) {
            inv.setItem(28, InventoryUtils.getItem(new ItemStack(Material.GRAY_DYE), "§cPick your pronouns above"));
            inv.setItem(30, InventoryUtils.getItem(new ItemStack(Material.GRAY_DYE), "§cPick your pronouns above"));
        }
        else inv.setItem(30, new ItemStack(Material.AIR));
        for (int i : slots) {
            inv.setItem(i, new ItemStack(Material.AIR));
        }
        for (int i = (page-1)*9; i < page*9; i++) {
            if (i >= max) break;
            int id = pronounManager.getGroup1().get(i);
            ItemStack stack = new ItemStack(Material.NAME_TAG);
            List<Integer> picks;
            if (multiple && (picks = picked.get(player)).contains(id)) {
                stack = new ItemStack(Material.EMERALD_BLOCK, picks.indexOf(id)+1);
            }
            inv.setItem(slots[i-((page-1)*9)], InventoryUtils.getItem(stack, "§c" + pronounManager.getPronounSets().get(pronounManager.getGroup1().get(i)).getDisplay()));
        }
    }

    private static void formatGroup2(Player player, Inventory inv, boolean multiple) {
        for (int i = 0; i < 9; i++) {
            if (i >= pronounManager.getGroup2().size()) break;
            int id = pronounManager.getGroup2().get(i);
            ItemStack stack = new ItemStack(Material.RABBIT_FOOT);
            List<Integer> picks;
            if (multiple && (picks = picked.get(player)).contains(id)) {
                stack = new ItemStack(Material.EMERALD_BLOCK, picks.indexOf(id)+1);
            }
            inv.setItem(slots[i]+4, InventoryUtils.getItem(stack, "§c" + pronounManager.getPronounSets().get(id).getDisplay()));
        }
    }

    public static void onClick(InventoryClickEvent e) {
        if (e.getSlot() == 44) {
            if (!userManager.getFluidReminders(e.getWhoClicked().getUniqueId().toString())) {
                e.getClickedInventory().setItem(44, InventoryUtils.getItem(new ItemStack(Material.LIME_DYE), MessageManager.getMessage("pronoun-reminder-name"), MessageManager.getMessages("pronoun-reminder-lore", "disable")));
                userManager.setFluidReminders(e.getWhoClicked().getUniqueId().toString(), true);
                InventoryUtils.playSound(e.getWhoClicked(), true);
                e.getWhoClicked().sendMessage(MessageManager.getMessage("toggle-fluid-reminders", "enabled"));
            } else {
                e.getClickedInventory().setItem(44, InventoryUtils.getItem(new ItemStack(Material.GRAY_DYE), MessageManager.getMessage("pronoun-reminder-name"), MessageManager.getMessages("pronoun-reminder-lore", "enable")));
                userManager.setFluidReminders(e.getWhoClicked().getUniqueId().toString(), false);
                InventoryUtils.playSound(e.getWhoClicked(), true);
                e.getWhoClicked().sendMessage(MessageManager.getMessage("toggle-fluid-reminders", "disabled"));
            }
        } else if (e.getSlot() <= 43 && e.getSlot() >= 41) {
            InventoryUtils.playSound(e.getWhoClicked(), false);
            e.getWhoClicked().closeInventory();
        } else if (e.getSlot() <= 40 && e.getSlot() >= 36) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)) return;
            if (multiple.get(e.getWhoClicked())) {
                if (picked.get(e.getWhoClicked()).size() == 0) InventoryUtils.playSound(e.getWhoClicked(), false);
                else {
                    InventoryUtils.playSound(e.getWhoClicked(), true);
                    userManager.setUserPronouns(e.getWhoClicked().getUniqueId().toString(), picked.get(e.getWhoClicked()));
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(MessageManager.getMessage("pronouns-set", userManager.getDisplayUserPronouns(e.getWhoClicked().getUniqueId().toString())));
                }
            } else {
                InventoryUtils.playSound(e.getWhoClicked(), true);
                setMultiplePronounStatus(e.getClickedInventory(), 1);
                startPickMultiple((Player) e.getWhoClicked());
            }
        } else if (e.getSlot() == 28 && (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR))) {
            InventoryUtils.playSound(e.getWhoClicked(), true);
            formatGroup1((Player) e.getWhoClicked(), e.getClickedInventory(), playerPage.get(e.getWhoClicked())-1, multiple.get(e.getWhoClicked()));
        } else if (e.getSlot() == 30 && (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR))) {
            InventoryUtils.playSound(e.getWhoClicked(), true);
            formatGroup1((Player) e.getWhoClicked(), e.getClickedInventory(), playerPage.get(e.getWhoClicked())+1, multiple.get(e.getWhoClicked()));
        } else if (Arrays.stream(slots).boxed().collect(Collectors.toList()).contains(e.getSlot())) {
            if (multiple.get(e.getWhoClicked())) {
                int total;
                setMultiplePronounStatus(e.getClickedInventory(), (total = togglePick(e.getWhoClicked(), getClickedGroup1(e.getWhoClicked(), e.getSlot()))+1));
                if (total == -1) {
                    e.getWhoClicked().sendMessage(MessageManager.getMessage("max-pronouns-reached", "" + userManager.maxPronouns()));
                    InventoryUtils.playSound(e.getWhoClicked(), false);
                    return;
                }
                InventoryUtils.playSound(e.getWhoClicked(), true);
                formatGroup1((Player) e.getWhoClicked(), e.getClickedInventory(), playerPage.get(e.getWhoClicked()), true);
            } else {
                InventoryUtils.playSound(e.getWhoClicked(), true);
                userManager.setUserPronouns(e.getWhoClicked().getUniqueId().toString(), getClickedGroup1(e.getWhoClicked(), e.getSlot()));
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(MessageManager.getMessage("pronouns-set", userManager.getDisplayUserPronouns(e.getWhoClicked().getUniqueId().toString())));
            }
        } else if (Arrays.stream(slots).boxed().collect(Collectors.toList()).contains(e.getSlot()-4)) {
            if (multiple.get(e.getWhoClicked())) {
                int total;
                setMultiplePronounStatus(e.getClickedInventory(), (total = togglePick(e.getWhoClicked(), getClickedGroup2(e.getWhoClicked(), e.getSlot()))+1));
                if (total == -1) {
                    e.getWhoClicked().sendMessage(MessageManager.getMessage("max-pronouns-reached", "" + userManager.maxPronouns()));
                    InventoryUtils.playSound(e.getWhoClicked(), false);
                    return;
                }
                InventoryUtils.playSound(e.getWhoClicked(), true);
                formatGroup2((Player) e.getWhoClicked(), e.getClickedInventory(), true);
            } else {
                InventoryUtils.playSound(e.getWhoClicked(), true);
                userManager.setUserPronouns(e.getWhoClicked().getUniqueId().toString(), getClickedGroup2(e.getWhoClicked(), e.getSlot()));
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().sendMessage(MessageManager.getMessage("pronouns-set", userManager.getDisplayUserPronouns(e.getWhoClicked().getUniqueId().toString())));
            }
        }
    }

    private static int getClickedGroup1(HumanEntity player, int slot) {
        return pronounManager.getGroup1().get(((playerPage.get(player)-1)*9 + Arrays.stream(slots).boxed().collect(Collectors.toList()).indexOf(slot)));
    }

    private static int getClickedGroup2(HumanEntity player, int slot) {
        return pronounManager.getGroup2().get(((playerPage.get(player)-1)*9 + Arrays.stream(slots).boxed().collect(Collectors.toList()).indexOf(slot-4)));
    }

    private static int togglePick(HumanEntity player, int id) {
        List<Integer> current = picked.get(player);
        if (current.contains(id)) {
            if (current.size() > 1) current.remove((Integer) id);
            else current = new ArrayList<>();
        } else {
            if (current.size() >= userManager.maxPronouns()) return -2;
            current.add(id);
        }
        picked.put((Player) player, current);
        return current.size();
    }

    private static void setMultiplePronounStatus(Inventory inv, int status) {
        if (status == 0) for (int i = 36; i <= 40; i++) inv.setItem(i, InventoryUtils.getItem(new ItemStack(Material.CYAN_STAINED_GLASS_PANE), MessageManager.getMessage("initialize-multiple-name"), MessageManager.getMessages("initialize-multiple-lore")));
        else if (status == 1) for (int i = 36; i <= 40; i++) inv.setItem(i, InventoryUtils.getItem(new ItemStack(Material.RED_STAINED_GLASS_PANE), MessageManager.getMessage("not-enough-multiple-name"), MessageManager.getMessages("not-enough-multiple-lore")));
        else if (status == 2) for (int i = 36; i <= 40; i++) inv.setItem(i, InventoryUtils.getItem(new ItemStack(Material.YELLOW_STAINED_GLASS_PANE), MessageManager.getMessage("barely-enough-multiple-name"), MessageManager.getMessages("barely-enough-multiple-lore")));
        else if (status >= 3 || status <= -1) for (int i = 36; i <= 40; i++) inv.setItem(i, InventoryUtils.getItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE), MessageManager.getMessage("confirm-multiple-name"), MessageManager.getMessages("confirm-multiple-lore")));
    }

    private static void startPickMultiple(Player player) {
        picked.put(player, new ArrayList<>());
        multiple.put(player, true);
    }

    public static void close(HumanEntity player) {
        if (picked.size() > 1) picked.remove(player);
        else picked = new HashMap<>();

        if (multiple.size() > 1) multiple.remove(player);
        else multiple = new HashMap<>();

        if (playerPage.size() > 1) playerPage.remove(player);
        else playerPage = new HashMap<>();
    }
}
