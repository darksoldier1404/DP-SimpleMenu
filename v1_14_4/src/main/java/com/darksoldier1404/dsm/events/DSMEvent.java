package com.darksoldier1404.dsm.events;

import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import com.darksoldier1404.dsm.functions.DSMFunction;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

@SuppressWarnings("all")
public class DSMEvent implements Listener {
    private final SimpleMenu plugin = SimpleMenu.getInstance();
    private final String prefix = plugin.prefix;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (plugin.currentMenuSettings.containsKey(p.getUniqueId())) {
            if (plugin.currentMenuSettings.get(p.getUniqueId()).getB() == MenuSettingType.ITEMS) {
                DSMFunction.saveItemSetting(p, plugin.currentMenuSettings.get(p.getUniqueId()).getA(), e.getView().getTopInventory());
                plugin.currentMenuSettings.remove(p.getUniqueId());
            } else if (plugin.currentMenuSettings.get(p.getUniqueId()).getB() == MenuSettingType.COMMANDS) {
                plugin.currentMenuSettings.remove(p.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (plugin.currentMenuSettings.containsKey(p.getUniqueId())) {
            if (e.getCurrentItem() != null) {
                if (plugin.currentMenuSettings.get(p.getUniqueId()).getB() == MenuSettingType.COMMANDS) {
                    e.setCancelled(true);
                    DSMFunction.openCommandSettingGUI(p, plugin.currentMenuSettings.get(p.getUniqueId()).getA(), e.getCurrentItem(), e.getSlot());
                }
            }
        } else {
            if (e.getCurrentItem() == null) return;
            String title = ChatColor.stripColor(e.getView().getTitle());
            plugin.menus.keySet().forEach(menu -> {
                if (title.contains("메뉴")) {
                    e.setCancelled(true);
                    if (NBT.hasTagKey(e.getCurrentItem(), "dsm.command")) {
                        String command = NBT.getStringTag(e.getCurrentItem(), "dsm.command");
                        p.performCommand(command);
                        p.closeInventory();
                    }
                }
            });
        }
    }
}