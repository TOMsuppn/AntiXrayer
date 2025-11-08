package me.tomsuppn.antiXrayer.Vanish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l反矿透 §4§l▶ §6§l" + "只有玩家能使用这个命令");
            return true;
        }

        Player player = (Player) sender;

        if (VanishManager.isVanished(player)) {
            VanishManager.unvanish(player);
        } else {
            VanishManager.vanish(player);
        }
        return true;
    }
}