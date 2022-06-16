package com.darksoldier1404.dsm;

import com.darksoldier1404.dppc.DPPCore;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.Quadruple;
import com.darksoldier1404.dsm.commands.DSMCommand;
import com.darksoldier1404.dsm.enums.MenuSettingType;
import com.darksoldier1404.dsm.events.DSMEvent;
import com.darksoldier1404.dsm.functions.DSMFunction;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class SimpleMenu extends JavaPlugin {
    public DPPCore core;
    private static SimpleMenu plugin;
    public YamlConfiguration config;
    public String prefix;
    public Map<String, YamlConfiguration> menus = new HashMap<>();
    public Map<UUID, Quadruple<String, MenuSettingType, ItemStack, Integer>> currentMenuSettings = new HashMap<>();

    public static SimpleMenu getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Plugin pl = getServer().getPluginManager().getPlugin("DPP-Core");
        if (pl == null) {
            getLogger().warning("DPP-Core 플러그인이 설치되어있지 않습니다.");
            getLogger().warning("DP-SimplePrefix 플러그인을 비활성화 합니다.");
            plugin.setEnabled(false);
            return;
        }
        core = (DPPCore) pl;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.Prefix"));
        DSMFunction.loadAllMenus();
        plugin.getServer().getPluginManager().registerEvents(new DSMEvent(), plugin);
        getCommand("dsm").setExecutor(new DSMCommand());
    }

    @Override
    public void onDisable() {
        menus.forEach((s, data) -> {
            ConfigUtils.saveCustomData(plugin, data, s, "menus");
        });
    }
}
