package coolepicgaymer.gaymerspronouns.guis;

import coolepicgaymer.gaymerspronouns.managers.PronounManager;
import coolepicgaymer.gaymerspronouns.utilities.GPUtils;
import coolepicgaymer.gaymerspronouns.GaymersPronouns;
import coolepicgaymer.gaymerspronouns.managers.MessageManager;
import coolepicgaymer.gaymerspronouns.managers.UserManager;
import coolepicgaymer.gaymerspronouns.utilities.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationMenu {

    private final GaymersPronouns plugin;
    private final UserManager um;
    private FileConfiguration config;
    private final PronounManager pronounManager;

    public ConfigurationMenu(GaymersPronouns plugin) {
        this.plugin = plugin;
        um = GaymersPronouns.getUserManager();
        pronounManager = GaymersPronouns.getPronounManager();
    }

    public void reload() {
        config = plugin.getConfig();
    }

    public Inventory getInventory(Player player) {

        Inventory inv = Bukkit.createInventory(null, 18, MessageManager.getMessage("configuration.gui.name"));

        inv.setItem(10, InventoryUtils.getItem(new ItemStack(Material.NAME_TAG), MessageManager.getMessage("configuration.gui.pronouns.enter"), MessageManager.getMessages("configuration.gui.pronouns.enter-lore", formatNumber(config.getInt("max-pronouns")), pronounManager.getDefaultPronoun("subjective"), pronounManager.getDefaultPronoun("objective"), pronounManager.getDefaultPronoun("possessive"), pronounManager.getDefaultPronoun("reflexive"), pronounManager.getDefaultPronoun("verb"), convertBooleanToText(config.getBoolean("prompt-on-first-join")), convertBooleanToText(config.getBoolean("default-no-pronouns-reminder")))));

        inv.setItem(6, InventoryUtils.getItem(new ItemStack(Material.OAK_SIGN), MessageManager.getMessage("configuration.gui.display.enter"), MessageManager.getMessages("configuration.gui.display.enter-lore")));
        updateDisplaySettings(inv, player);

        if (GPUtils.isPapiInstalled()) {
            inv.setItem(12, InventoryUtils.getItem(new ItemStack(Material.WRITABLE_BOOK), MessageManager.getMessage("configuration.gui.placeholderapi.installed"), MessageManager.getMessages("configuration.gui.placeholderapi.installed-lore")));
            updatePapiSettings(inv);
        } else {
            inv.setItem(12, InventoryUtils.getItem(new ItemStack(Material.WRITABLE_BOOK), MessageManager.getMessage("configuration.gui.placeholderapi.not-installed"), MessageManager.getMessages("configuration.gui.placeholderapi.not-installed-lore")));
        }



        /**
         *  pronoun settings
         *   - max pronouns, click to cycle through 1-9 and no max
         *   - default pronouns
         *   - pronoun list
         *   - special pronoun list
         *   - reminder settings
         *   - enable prompt on first join
         *
         *  instructions to add more w/ commands
         *
         *  locale (+ resetting pronouns and messages)
         *  reset config
         *  reset pronouns
         */

        return inv;
    }

    private String formatNumber(int number) {
        if (number <= 0) return "Unlimited";
        else return number + "";
    }

    private void updateDisplaySettings(Inventory inv, Player player) {
        List<String> enabledDisplays = (List<String>) config.getList("display", new ArrayList<>());
        inv.setItem(14, InventoryUtils.getItem(new ItemStack(convertBooleanToDye(enabledDisplays.contains("chatformat"))), MessageManager.getMessage("configuration.gui.display.chatformat", convertPredictiveBooleanToText(!enabledDisplays.contains("chatformat"))), MessageManager.getMessages("configuration.gui.display.chatformat-lore", convertBooleanToText(enabledDisplays.contains("chatformat")), GPUtils.replaceVariables(player, config.getString("display-format.chat-format").replace("{MESSAGE}", MessageManager.getMessage("configuration.misc.generic-message"))), convertPredictiveBooleanToText(!enabledDisplays.contains("chatformat")))));
        inv.setItem(15, InventoryUtils.getItem(new ItemStack(convertBooleanToDye(enabledDisplays.contains("chathover"))), MessageManager.getMessage("configuration.gui.display.chathover", convertPredictiveBooleanToText(!enabledDisplays.contains("chathover"))), MessageManager.getMessages("configuration.gui.display.chathover-lore", convertBooleanToText(enabledDisplays.contains("chathover")), GPUtils.replaceVariables(player, String.join("\n", (List<String>) config.getList("display-format.chat-hover"))), convertPredictiveBooleanToText(!enabledDisplays.contains("chathover")))));
        inv.setItem(16, InventoryUtils.getItem(new ItemStack(convertBooleanToDye(enabledDisplays.contains("tablist"))), MessageManager.getMessage("configuration.gui.display.tablist", convertPredictiveBooleanToText(!enabledDisplays.contains("tablist"))), MessageManager.getMessages("configuration.gui.display.tablist-lore", convertBooleanToText(enabledDisplays.contains("tablist")), GPUtils.replaceVariables(player, config.getString("display-format.tab-list")), convertPredictiveBooleanToText(!enabledDisplays.contains("tablist")))));
    }

    private void updatePapiSettings(Inventory inv) {
        boolean bool;
        inv.setItem(3, InventoryUtils.getItem(new ItemStack(convertBooleanToDye((bool = config.getBoolean("random-priority-in-placeholders")))), MessageManager.getMessage("configuration.gui.placeholderapi.randomize-priority", convertPredictiveBooleanToText(!bool)), MessageManager.getMessages("configuration.gui.placeholderapi.randomize-priority-lore", getBooleanAsString(bool), convertPredictiveBooleanToText(!bool))));
    }

    public void onClick(InventoryClickEvent e) {
        if (e.getSlot() == 3) {
            InventoryUtils.playSound(e.getWhoClicked(), true);
            e.getWhoClicked().sendMessage(MessageManager.getMessage("configuration.toggles.randomize-pronouns", getBooleanAsString(toggleBoolean("random-priority-in-placeholders", e.getWhoClicked().getName()))), MessageManager.getMessage("configuration.toggles.requires-reload"));
            updatePapiSettings(e.getInventory());
        } else if (e.getSlot() == 12) {
            InventoryUtils.playSound(e.getWhoClicked(), false);
            e.getWhoClicked().closeInventory();
            for (String s : MessageManager.getMessages("configuration.instructions.papi")) e.getWhoClicked().sendMessage(s);
            if (!GPUtils.isPapiInstalled()) for (String s : MessageManager.getMessages("configuration.instructions.papi-not-installed")) e.getWhoClicked().sendMessage(s);
        } else if (e.getSlot() == 14) {
            InventoryUtils.playSound(e.getWhoClicked(), true);
            if (e.isRightClick()) {
                e.getWhoClicked().closeInventory();
                for (String s : MessageManager.getMessages("configuration.instructions.chatformat")) e.getWhoClicked().sendMessage(s);
                return;
            }
            else {
                e.getWhoClicked().sendMessage(MessageManager.getMessage("configuration.toggles.chatformat", getBooleanAsString(toggleDisplayMethod("chatformat", e.getWhoClicked().getName()))), MessageManager.getMessage("configuration.toggles.requires-reload"));
                updateDisplaySettings(e.getInventory(), (Player) e.getWhoClicked());
            }
        } else if (e.getSlot() == 15) {
            InventoryUtils.playSound(e.getWhoClicked(), true);
            if (e.isRightClick()) {
                e.getWhoClicked().closeInventory();
                for (String s : MessageManager.getMessages("configuration.instructions.chathover")) e.getWhoClicked().sendMessage(s);
                return;
            }
            else {
                e.getWhoClicked().sendMessage(MessageManager.getMessage("configuration.toggles.chathover", getBooleanAsString(toggleDisplayMethod("chathover", e.getWhoClicked().getName()))), MessageManager.getMessage("configuration.toggles.requires-reload"));
                updateDisplaySettings(e.getInventory(), (Player) e.getWhoClicked());
            }
        } else if (e.getSlot() == 16) {
            InventoryUtils.playSound(e.getWhoClicked(), true);
            if (e.isRightClick()) {
                e.getWhoClicked().closeInventory();
                for (String s : MessageManager.getMessages("configuration.instructions.tablist")) e.getWhoClicked().sendMessage(s);
                return;
            }
            else {
                e.getWhoClicked().sendMessage(MessageManager.getMessage("configuration.toggles.tablist", getBooleanAsString(toggleDisplayMethod("tablist", e.getWhoClicked().getName()))), MessageManager.getMessage("configuration.toggles.requires-reload"));
                updateDisplaySettings(e.getInventory(), (Player) e.getWhoClicked());
            }
        }
    }

    private String getBooleanAsString(boolean bool) {
        if (bool) return MessageManager.getMessage("misc.enabled");
        else return MessageManager.getMessage("misc.disabled");
    }

    private boolean toggleDisplayMethod(String type, String username) {
        boolean state = true;
        List<String> enabledDisplays = (List<String>) config.getList("display", new ArrayList<>());
        final String before = enabledDisplays.toString();
        if (enabledDisplays.contains(type)) {
            if (enabledDisplays.size() <= 1) enabledDisplays = new ArrayList<>();
            else enabledDisplays.remove(type);
            state = false;
        } else enabledDisplays.add(type);
        plugin.getLogger().info(MessageManager.getMessage("console.changed-config", "display", username, before, enabledDisplays.toString()));
        plugin.getConfig().set("display", enabledDisplays);
        plugin.saveConfig();
        config = plugin.getConfig();
        return state;
    }

    private boolean toggleBoolean(String path, String username) {
        boolean state = config.getBoolean(path);
        plugin.getLogger().info(MessageManager.getMessage("console.changed-config", path, username, state + "", !state + ""));
        plugin.getConfig().set(path, !state);
        plugin.saveConfig();
        config = plugin.getConfig();
        return !state;
    }

    private String convertBooleanToText(boolean bool) {
        if (bool) return MessageManager.getMessage("configuration.misc.true");
        else return MessageManager.getMessage("configuration.misc.false");
    }

    private String convertPredictiveBooleanToText(boolean bool) {
        if (bool) return MessageManager.getMessage("misc.enable");
        else return MessageManager.getMessage("misc.disable");
    }

    private Material convertBooleanToDye(boolean bool) {
        if (bool) return Material.LIME_DYE;
        else return Material.GRAY_DYE;
    }

}
