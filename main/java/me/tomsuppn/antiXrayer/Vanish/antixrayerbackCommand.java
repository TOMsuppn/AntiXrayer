package me.tomsuppn.antiXrayer.Vanish;

import me.tomsuppn.antiXrayer.AntiXrayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class antixrayerbackCommand implements Listener, CommandExecutor {

    private final Map<UUID, Location> lastLocation = new HashMap<>();
    private final AntiXrayer plugin;

    public antixrayerbackCommand(AntiXrayer plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("axb").setExecutor(this);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        // 记录传送前的位置
        lastLocation.put(player.getUniqueId(), event.getFrom());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c§l反矿透 §4§l▶ §6§l" + "只有玩家能使用这个命令");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        Location loc = lastLocation.get(uuid);
        if (loc == null) {
            player.sendMessage("§c§l反矿透 §4§l▶ §6§l" + "§c你还没有传送过，无法回到上一次传送位置！尝试输入 /axv 退出隐身");
            return true;
        }

        // 传送玩家
        player.teleport(loc);
        player.sendMessage("§c§l反矿透 §4§l▶ §6§l" + "已退出审查模式");

        // 执行后台命令 axv
        Bukkit.dispatchCommand(player, "axv");

        return true;
    }
}
