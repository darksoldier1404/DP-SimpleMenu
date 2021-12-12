package com.darksoldier1404.dsm.commands;

import com.darksoldier1404.dsm.SimpleMenu;
import com.darksoldier1404.dsm.functions.DSMFunction;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
public class DSMCommand implements CommandExecutor, TabCompleter {
    private final SimpleMenu plugin = SimpleMenu.getInstance();
    private final String prefix = plugin.prefix;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + "플레이어만 사용 가능합니다.");
            return false;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(prefix + "/dsm open <name> - 해당 메뉴를 엽니다.");
            p.sendMessage(prefix + "/dsm list - 모든 메뉴 목록을 표시합니다.");
            if (p.isOp()) {
                p.sendMessage(prefix + "/dsm create <name> <rows> - 메뉴를 생성합니다. (1~6 rows)");
                p.sendMessage(prefix + "/dsm title <name> - 해당 메뉴의 타이틀을 설정합니다.");
                p.sendMessage(prefix + "/dsm items <name> - 메뉴 아이템 설정 GUI를 엽니다.");
                p.sendMessage(prefix + "/dsm cmds <name> - 메뉴 커맨드 설정 GUI를 엽니다.");
                p.sendMessage(prefix + "/dsm price <name> - 메뉴 커맨드 사용 가격 설정 GUI를 엽니다.");
                p.sendMessage(prefix + "/dsm delete <name> - 메뉴를 삭제합니다.");
            }
            return false;
        }
        if (args[0].equals("open")) {
            if (args.length == 1) {
                p.sendMessage(prefix + "메뉴를 열기 위해서는 메뉴 이름을 입력해야 합니다.");
                return false;
            }
            if (!plugin.menus.containsKey(args[1])) {
                p.sendMessage(prefix + "해당 메뉴는 존재하지 않습니다.");
                return false;
            }
            DSMFunction.openMenu(p, args[1]);
            return false;
        }
        if (args[0].equals("list")) {
            p.sendMessage(prefix + "<<< 메뉴 목록 >>>");
            plugin.menus.keySet().forEach(s -> p.sendMessage(prefix + s));
            return false;
        }
        if (p.isOp()) {
            if (args[0].equals("create")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "메뉴를 생성하기 위해서는 메뉴 이름을 입력해야 합니다.");
                    return false;
                }
                if (args.length == 2) {
                    p.sendMessage(prefix + "메뉴를 생성하기 위해서는 메뉴 행의 수를 입력해야 합니다.");
                    return false;
                }
                if (plugin.menus.containsKey(args[1])) {
                    p.sendMessage(prefix + "해당 메뉴는 이미 존재합니다.");
                    return false;
                }
                DSMFunction.createMenu(p, args[1], args[2]);
                return false;
            }
            if (args[0].equals("title")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "메뉴 타이틀을 설정하기 위해서는 메뉴 이름을 입력해야 합니다.");
                    return false;
                }
                if (!plugin.menus.containsKey(args[1])) {
                    p.sendMessage(prefix + "해당 메뉴는 존재하지 않습니다.");
                    return false;
                }
                DSMFunction.setTitle(p, args[1]);
            }
            if (args[0].equals("items")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "메뉴 이름을 입력해주세요.");
                    return false;
                }
                if (!plugin.menus.containsKey(args[1])) {
                    p.sendMessage(prefix + "해당 메뉴는 존재하지 않습니다.");
                    return false;
                }
                DSMFunction.openItemSettingGUI(p, args[1]);
                return false;
            }
            if (args[0].equals("cmds")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "메뉴 이름을 입력해주세요.");
                    return false;

                }
                if (!plugin.menus.containsKey(args[1])) {
                    p.sendMessage(prefix + "해당 메뉴는 존재하지 않습니다.");
                    return false;
                }
                DSMFunction.openCommandSettingGUI(p, args[1]);
                return false;
            }
            if (args[0].equals("price")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "메뉴 이름을 입력해주세요.");
                    return false;
                }
                if (!plugin.menus.containsKey(args[1])) {
                    p.sendMessage(prefix + "해당 메뉴는 존재하지 않습니다.");
                    return false;
                }
                DSMFunction.openPriceSettingGUI(p, args[1]);
                return false;
            }
            if (args[0].equals("delete")) {
                if (args.length == 1) {
                    p.sendMessage(prefix + "메뉴 이름을 입력해주세요.");
                    return false;
                }
                if (!plugin.menus.containsKey(args[1])) {
                    p.sendMessage(prefix + "해당 메뉴는 존재하지 않습니다.");
                    return false;
                }
                DSMFunction.deleteMenu(p, args[1]);
                return false;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                return Arrays.asList("open", "list", "create", "items", "cmds", "price", "delete");
            }
            return Arrays.asList("open", "list");
        }
        if (args.length == 2) {
            if (!args[0].equals("list")) {
                return plugin.menus.keySet().stream().collect(Collectors.toList());
            }
        }
        if (args.length == 3) {
            if (args[0].equals("create")) {
                return Arrays.asList("1", "2", "3", "4", "5", "6");
            }
        }
        return null;
    }
}
