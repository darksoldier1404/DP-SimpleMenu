package com.darksoldier1404.dsm.functions;

import com.darksoldier1404.dppc.utils.ColorUtils;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Tuple;
import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("all")
public class DSMFunction {
    private static final SimpleMenu plugin = SimpleMenu.getInstance();
    private static final String prefix = plugin.prefix;

    public static boolean isValid(String name) {
        if (!plugin.menus.containsKey(name)) {
            return false;
        }
        return true;
    }

    public static void openMenu(Player p, String name) {
        if (!isValid(name)) return;
        p.openInventory(getMenuInventory(name));
    }

    public static void setTitle(Player p, String name) {
        if (!isValid(name)) return;
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
        title = ColorUtils.applyColor(title);
        Inventory inv = Bukkit.createInventory(null, Integer.parseInt(rows) * 9, title + " 메뉴");
        if (data.get("Menu.ITEMS") != null) {
            data.getConfigurationSection("Menu.ITEMS").getKeys(false).forEach(key -> {
                inv.setItem(Integer.parseInt(key), data.getItemStack("Menu.ITEMS." + key));
            });
        }
        return inv;
    }

    public static void createMenu(Player p, String name, String rows) {
        YamlConfiguration data = new YamlConfiguration();
        data.set("Menu.NAME", name);
        data.set("Menu.ROWS", rows);
        data.set("Menu.TITLE", name);
        plugin.menus.put(name, data);
        ConfigUtils.saveCustomData(plugin, data, name, "menus");
        p.sendMessage(prefix + name + " 메뉴가 생성되었습니다.");
    }

    public static void deleteMenu(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(prefix + "존재하지 않는 메뉴입니다.");
            return;
        }
        plugin.menus.remove(name);
        p.sendMessage(prefix + name + " 메뉴가 삭제되었습니다.");
    }

    public static void openItemSettingGUI(Player p, String name) { // 1
        if (!isValid(name)) {
            p.sendMessage(prefix + "존재하지 않는 메뉴입니다.");
            return;
        }
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.ITEMS));
        p.openInventory(getMenuInventory(name));
    }

    public static void openCommandSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(prefix + "존재하지 않는 메뉴입니다.");
            return;
        }
        p.openInventory(getMenuInventory(name));
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.COMMANDS));
    }

    public static void openSoundSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(prefix + "존재하지 않는 메뉴입니다.");
            return;
        }
        p.openInventory(getMenuInventory(name));
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.SOUND));
    }

    public static void openPriceSettingGUI(Player p, String name) {
        if (!isValid(name)) {
            p.sendMessage(prefix + "존재하지 않는 메뉴입니다.");
            return;
        }
        plugin.currentMenuSettings.put(p.getUniqueId(), new Tuple<>(name, MenuSettingType.PRICES));
        p.openInventory(getMenuInventory(name));
    }

    public static void saveItemSetting(Player p, String name, Inventory inv) {
        if (!isValid(name)) return;
        YamlConfiguration data = plugin.menus.get(name);
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                data.set("Menu.ITEMS." + i, null);
            } else {
                data.set("Menu.ITEMS." + i, item);
            }
        }
        ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
        p.sendMessage(prefix + name + " 메뉴의 아이템 설정이 저장되었습니다.");
    }

    public static void openCommandSettingGUI(Player p, String name, ItemStack item, int slot) {
        String command = "명령어를 설정해주세요.";
        if (NBT.hasTagKey(item, "dsm.command")) {
            command = NBT.getStringTag(item, "dsm.command");
        }
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    plugin.menus.get(name).set("Menu.ITEMS." + slot, setCommand(item, text));
                    player.sendMessage(prefix + name + " 메뉴 " + slot + "슬롯의 커맨드가 설정되었습니다. : " + text);
                    ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> openCommandSettingGUI(p, name), 5L);
                    return AnvilGUI.Response.close();
                })
                .text(command)
                .itemLeft(new ItemStack(Material.COMMAND_BLOCK))
                .itemRight(null)
                .title(name + " 메뉴 커맨드 설정")
                .plugin(plugin)
                .open(p);
    }

    public static void openSoundSettingGUI(Player p, String name, ItemStack item, int slot) {
        String sound = "사용하지 않음";
        if (NBT.hasTagKey(item, "dsm.sound")) {
            sound = NBT.getStringTag(item, "dsm.sound");
        }
        new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    plugin.menus.get(name).set("Menu.ITEMS." + slot, setSound(item, text));
                    player.sendMessage(prefix + name + " 메뉴 " + slot + "슬롯의 클릭 사운드가 설정되었습니다. : " + text);
                    ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
                    Bukkit.getScheduler().runTaskLater(plugin, () -> openSoundSettingGUI(p, name), 5L);
                    return AnvilGUI.Response.close();
                })
                .text(sound)
                .itemLeft(new ItemStack(Material.NOTE_BLOCK))
                .itemRight(null)
                .title(name + " 메뉴 클릭 사운드 설정")
                .plugin(plugin)
                .open(p);
    }

    public static ItemStack setCommand(ItemStack item, String command) {
        return NBT.setStringTag(item, "dsm.command", command);
    }

    public static ItemStack setSound(ItemStack item, String sound) {
        return NBT.setStringTag(item, "dsm.sound", sound);
    }


    public static void loadAllMenus() {
        List<YamlConfiguration> menus = ConfigUtils.loadCustomDataList(plugin, "menus");
        menus.forEach(menu -> {
            plugin.menus.put(menu.getString("Menu.NAME"), menu);
        });
    }
}
