package me.tomsuppn.antiXrayer.Vanish;

import me.tomsuppn.antiXrayer.AntiXrayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AxViewCommand implements CommandExecutor {
    private final AntiXrayer plugin;

    public AxViewCommand(AntiXrayer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l反矿透 §4§l▶ §6§l" + "只有玩家能使用这个命令");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§c§l反矿透 §4§l▶ §6§l" + "用法: /axview <玩家>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]); // 确保名字完全正确
        if (target == null) {
            sender.sendMessage("§c§l反矿透 §4§l▶ §6§l" + args[0] + " 当前不在线");
            return true;
        }

        Player player = (Player) sender;

        // 先传送
        player.teleport(target.getLocation());
        // 再执行 axv
        Bukkit.dispatchCommand(player, "axv");

        sender.sendMessage("§c§l反矿透 §4§l▶ §6§l已传送至嫌疑人 §e§l" + target.getName() + " §6§l并启用审查模式");
        return true;
    }
}
