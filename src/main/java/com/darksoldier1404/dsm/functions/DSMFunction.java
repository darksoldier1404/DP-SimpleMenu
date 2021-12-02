package com.darksoldier1404.dsm.functions;

import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import com.darksoldier1404.duc.utils.ConfigUtils;
import com.darksoldier1404.duc.utils.NBT;
import com.darksoldier1404.duc.utils.Tuple;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("all")
public class DSMFunction {
    private static final SimpleMenu plugin = SimpleMenu.getInstance();
    private static final String prefix = plugin.prefix;

    public static boolean isValid(String name, Player p) {
        if(!plugin.menus.containsKey(name)) {
            p.sendMessage(prefix + name + " 메뉴는 존재하지 않습니다.");
            return false;
        }
        return true;
    }

    public static void openMenu(Player p,String name) {
        if(!isValid(name, p)) return;

    }

    public static void setTitle(Player p, String name) {
        if(!isValid(name, p)) return;
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    plugin.menus.get(name).set("Menu.TITLE", text);
                    player.sendMessage(prefix + name + "타이틀이 설정되었습니다. : " + ChatColor.translateAlternateColorCodes('&', text));
                    ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text(name)
                .itemLeft(new ItemStack(Material.PAPER))
                .itemRight(null)
                .title(name + " 메뉴 타이틀 설정")
                .plugin(plugin)
                .open(p);
    }

    public static Inventory getMenuInventory(String name) {
        YamlConfiguration data = plugin.menus.get(name);
        String rows = data.getString("Menu.ROWS");
        String title = data.getString("Menu.TITLE");
        Inventory inv = Bukkit.createInventory(null, Integer.parseInt(rows) * 9, ChatColor.translateAlternateColorCodes('&', title));

        data.getConfigurationSection("Menu.ITEMS").getKeys(false).forEach(key -> {
            inv.setItem(Integer.parseInt(key), data.getItemStack("Menu.ITEMS." + key));

        });
        return inv;
    }

    public static void createMenu(Player p, String name, String rows) {
        if(isValid(name, p)) return;
        YamlConfiguration data = new YamlConfiguration();
        data.set("Menu.NAME", name);
        data.set("Menu.ROWS", rows);
        data.set("Menu.TITLE", name + " 메뉴");
        plugin.menus.put(name, data);
        ConfigUtils.saveCustomData(plugin, data, name, "menus");
        p.sendMessage(prefix + name + " 메뉴가 생성되었습니다.");
    }

    public static void deleteMenu(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.menus.remove(name);
    }

    public static void openItemSettingGUI(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.ITEMS));
        p.openInventory(getMenuInventory(name));
    }

    public static void openCommandSettingGUI(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.COMMANDS));
        p.openInventory(getMenuInventory(name));
    }

    public static void openPriceSettingGUI(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.PRICES));
        p.openInventory(getMenuInventory(name));
    }

    public static void saveItemSetting(Player p, String name, Inventory inv) {
        if(!isValid(name, p)) return;
        YamlConfiguration data = plugin.menus.get(name);
        for(int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(item == null || item.getType() == Material.AIR) continue;
            data.set("Menu.ITEMS." + i, item);
        }
        ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
        p.sendMessage(prefix + name + " 메뉴의 아이템 설정이 저장되었습니다.");
    }

    public static void openCommandSettingGUI(Player p, String name, ItemStack item, int slot) {
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    setCommand(item, text);
                    player.sendMessage(prefix + name + " 메뉴 " + slot + "슬롯의 커맨드가 설정되었습니다. : " + text);
                    plugin.menus.get(name).set("Menu.ITEMS." + slot, item);
                    ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> openCommandSettingGUI(p, name), 5L);
                    return AnvilGUI.Response.close();
                })
                .preventClose()
                .text(name)
                .itemLeft(new ItemStack(Material.COMMAND_BLOCK))
                .itemRight(null)
                .title(name + " 메뉴 " + slot + "슬롯의 커맨드 설정")
                .plugin(plugin)
                .open(p);
    }

    public static void setCommand(ItemStack item, String command) {
        item = NBT.setStringTag(item, "dsm.command", command);
    }
}
