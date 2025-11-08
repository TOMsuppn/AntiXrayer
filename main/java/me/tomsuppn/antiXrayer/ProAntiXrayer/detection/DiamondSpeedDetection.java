package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.entity.Player;

public class DiamondSpeedDetection implements Detection {
    @Override
    public int check(Player player, PlayerData data) {
        if (player.hasPermission("antixrayer.bypass")) {
            return 0;
        }

        data.resetIfExpired(); // 确保窗口过期自动清零

        int diamondCount = data.getDiamondMined();
        int recordCount = data.getRecords().size();

        if (diamondCount > 30 && recordCount >= 150 ) {
            data.updateActivity(); // 数据增长
            return 5;
        }
        return 0;
    }
}
