package com.darksoldier1404.dsm.events;

import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import com.darksoldier1404.dsm.functions.DSMFunction;
import com.darksoldier1404.duc.utils.ConfigUtils;
import com.darksoldier1404.duc.utils.NBT;
import com.darksoldier1404.duc.utils.Quadruple;
import com.darksoldier1404.duc.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

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
                    DSMFunction.setCommand(p, plugin.currentMenuSettings.get(p.getUniqueId()).getA(), e.getCurrentItem(), e.getSlot());
                }
            }
        } else {
            if (e.getCurrentItem() == null) return;
            String title = e.getView().getTitle();
            plugin.menus.keySet().forEach(menu -> {
                if (title.contains(menu) && title.contains("메뉴")) {
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

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        if (plugin.currentMenuSettings.containsKey(e.getPlayer().getUniqueId())) {
            Quadruple<String, MenuSettingType, ItemStack, Integer> t = plugin.currentMenuSettings.get(e.getPlayer().getUniqueId());
            if (t.getB() == MenuSettingType.INSERT) {
                e.setCancelled(true);
                String command = e.getMessage();
                String name = t.getA();
                ItemStack item = t.getC();
                int slot = t.getD();
                plugin.menus.get(name).set("Menu.ITEMS." + slot, DSMFunction.setCommand(item, command));
                e.getPlayer().sendMessage(prefix + name + " 메뉴 " + slot + "슬롯의 커맨드가 설정되었습니다. : " + command);
                ConfigUtils.saveCustomData(plugin, plugin.menus.get(name), name, "menus");
                Bukkit.getScheduler().runTaskLater(plugin, () -> DSMFunction.openCommandSettingGUI(e.getPlayer(), name), 5L);
                plugin.currentMenuSettings.remove(e.getPlayer().getUniqueId());
            }
        }
    }
}