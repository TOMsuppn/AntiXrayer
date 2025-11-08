package me.tomsuppn.antiXrayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class DIAMOND_ORE implements Listener {

    AntiXrayer plugin = AntiXrayer.getInstance();
    XrayerIndexManager xrayerIndex= plugin.getXrayerIndexManager();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        boolean breakingAlarms = plugin.getConfig().getBoolean("settings.breaking-alarms", true);

        if (!breakingAlarms) {
            return;
        }

        Player player = event.getPlayer();
        int index = xrayerIndex.getXrayerIndex(player);


        if (player.hasPermission("antixrayer.bypass")) {
            return;
        }
        if (event.getBlock().getType() == Material.DIAMOND_ORE) {

            // 调用全局的 XrayerIndexManager 增加数值
            AntiXrayer plugin = (AntiXrayer) Bukkit.getPluginManager().getPlugin("AntiXrayer");
            if (plugin != null) {
                plugin.getXrayerIndexManager().addXrayerIndex(player, 1);
            }

            // 给 OP 发送提示
            if (index > 0) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("antixrayer.alarm")) {
                        Component msg = Component.text("§c§l反矿透 §4§l▶ §e§l" + player.getName() + " §6§l挖掘了 钻石矿石 " + "§8(dex " + index + ")")
                                //.clickEvent(ClickEvent.runCommand("/playerinfomenu " + player.getName()))
                                .clickEvent(ClickEvent.runCommand("/axview " + player.getName()))


                                .hoverEvent(HoverEvent.showText(Component.text("§6§l开关审查模式")));
                        //.hoverEvent(HoverEvent.showText(Component.text("§f§l点击打开 §e§l" + player.getName() + " §f§l的操作菜单")));

                        onlinePlayer.sendMessage(msg);
                    }
                }
            }
        }
    }
}
