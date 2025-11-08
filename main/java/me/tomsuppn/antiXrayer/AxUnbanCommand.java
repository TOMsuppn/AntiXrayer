package me.tomsuppn.antiXrayer;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AxUnbanCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§c§l反矿透 §4§l▶ " + "§6§l用法: /" + label + " <玩家>");
            return true;
        }

        String targetName = args[0];
        if (Bukkit.getServer().getBanList(BanList.Type.NAME).isBanned(targetName)) {
            Bukkit.getServer().getBanList(BanList.Type.NAME).pardon(targetName);
            sender.sendMessage("§c§l反矿透 §4§l▶ §e§l" + targetName + " §6§l已被解封");
        } else {
            sender.sendMessage("§c§l反矿透 §4§l▶ §e§l" + targetName + " §6§l当前未被封禁");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            for (BanEntry ban : Bukkit.getServer().getBanList(BanList.Type.NAME).getBanEntries()) {
                String name = ban.getTarget();
                if (name != null && name.toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(name);
                }
            }
        }
        return suggestions;
    }
}
