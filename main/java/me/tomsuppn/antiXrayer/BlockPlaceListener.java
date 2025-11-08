package me.tomsuppn.antiXrayer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

public class BlockPlaceListener implements Listener {

    private final AntiXrayer plugin;

    public BlockPlaceListener(AntiXrayer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        // 检查放置的方块是不是钻石矿石
        if (event.getBlockPlaced().getType() == Material.DIAMOND_ORE) {
            plugin.getXrayerIndexManager().reduceXrayerIndex(player, 1);
        }

        if (event.getBlockPlaced().getType() == Material.DEEPSLATE_DIAMOND_ORE) {
            plugin.getXrayerIndexManager().reduceXrayerIndex(player, 1);
        }

        if (event.getBlockPlaced().getType() == Material.ANCIENT_DEBRIS) {
            plugin.getXrayerIndexManager().reduceXrayerIndex(player, 5);
        }
    }
}
