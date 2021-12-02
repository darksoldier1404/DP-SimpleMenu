package com.darksoldier1404.dsm;

import com.darksoldier1404.dsm.enums.MenuSettingType;
import com.darksoldier1404.duc.UniversalCore;
import com.darksoldier1404.duc.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class SimpleMenu extends JavaPlugin {
    public UniversalCore core;
    private static SimpleMenu plugin;
    public YamlConfiguration config;
    public String prefix;
    public Map<String, YamlConfiguration> menus = new HashMap<>();
    public Map<UUID, MenuSettingType> currentMenuSettings = new HashMap<>();

    public static SimpleMenu getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        Plugin pl = getServer().getPluginManager().getPlugin("DP-UniversalCore");
        if (pl == null) {
            getLogger().warning("DP-UniversalCore 플러그인이 설치되어있지 않습니다.");
            getLogger().warning("DP-SimplePrefix 플러그인을 비활성화 합니다.");
            plugin.setEnabled(false);
            return;
        }
        core = (UniversalCore) pl;
        config = ConfigUtils.loadDefaultPluginConfig(plugin);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.prefix"));

    }

    @Override
    public void onDisable() {
    }
}
