package com.darksoldier1404.dsm.functions;

import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

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

    public static void createMenu(Player p, String name, String rows) {
        if(isValid(name, p)) return;
        YamlConfiguration data = new YamlConfiguration();
        data.set("Menu.NAME", name);
        data.set("Menu.ROWS", rows);

        plugin.menus.put(name, data);
    }

    public static void deleteMenu(Player p, String name) {
        if(!isValid(name, p)) return;

    }

    public static void openItemSettingGUI(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.currentMenuSettings.put(p.getUniqueId(), MenuSettingType.ITEMS);
    }

    public static void openCommandSettingGUI(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.currentMenuSettings.put(p.getUniqueId(), MenuSettingType.COMMANDS);
    }

    public static void openPriceSettingGUI(Player p, String name) {
        if(!isValid(name, p)) return;
        plugin.currentMenuSettings.put(p.getUniqueId(), MenuSettingType.PRICES);

    }
}
