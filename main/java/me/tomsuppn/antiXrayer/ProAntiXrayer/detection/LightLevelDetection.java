package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LightLevelDetection implements Detection {
    @Override
    public int check(Player player, PlayerData data) {
        if (player.hasPermission("antixrayer.bypass")) {
            return 0;
        }
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            return 0;
        }

        data.resetIfExpired(); // 确保窗口过期自动清零

        int light = player.getLocation().getBlock().getLightLevel();
        int diamondCount = data.getDiamondMined();
        Location currentLoc = player.getLocation();

        // 判断玩家是否在移动
        Location lastLoc = data.getLastLocation();
        boolean isMoving = lastLoc == null || lastLoc.distanceSquared(currentLoc) > 0.25; // 半格阈值

        // 更新玩家位置
        data.setLastLocation(currentLoc.clone());

        if (light == 0 && player.isOnGround() && !player.isInsideVehicle() && isMoving) {
            data.addDarkTicks(20);
            data.updateActivity();
        }

        if (data.getDarkTicks() > 5500 && diamondCount > 25) {
            return 10;
        }

        return 0;
    }
}
