package com.darksoldier1404.dsm.events;

import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import com.darksoldier1404.dsm.functions.DSMFunction;
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
            if (plugin.currentMenuSettings.get(p.getUniqueId()).b() == MenuSettingType.ITEMS) {
                DSMFunction.saveItemSetting(p, plugin.currentMenuSettings.get(p.getUniqueId()).a(), e.getView().getTopInventory());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) return;
        if (plugin.currentMenuSettings.containsKey(p.getUniqueId())) {
            if (e.getCurrentItem() != null) {
                if (plugin.currentMenuSettings.get(p.getUniqueId()).b() == MenuSettingType.ITEMS) {
                    DSMFunction.openCommandSettingGUI(p, plugin.currentMenuSettings.get(p.getUniqueId()).a(), e.getCurrentItem(), e.getSlot());
                    e.setCancelled(true);
                }
            }
        }
    }
}
