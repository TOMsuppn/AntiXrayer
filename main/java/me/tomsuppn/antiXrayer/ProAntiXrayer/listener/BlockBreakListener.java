package me.tomsuppn.antiXrayer.ProAntiXrayer.listener;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.DetectionManager;
import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final DetectionManager manager;

    public BlockBreakListener(DetectionManager manager) {
        this.manager = manager;
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.getGameMode() == GameMode.CREATIVE) return;

        Material mat = event.getBlock().getType();

        // 检查是否在保护期
        if (manager.handleBlockBreakDuringProtection(player, mat)) {
            event.setCancelled(true);
            return;
        }

        PlayerData data = manager.getData(player.getUniqueId());

        // 只有未取消的挖掘才计入数据
        data.addRecord(mat, event.getBlock().getLocation());

        if (data.getDiamondMined() >= 30) {
            // directionChanges / randomMining 自动累积
        }

        data.resetIfExpired();
        manager.check(player);
    }
}

