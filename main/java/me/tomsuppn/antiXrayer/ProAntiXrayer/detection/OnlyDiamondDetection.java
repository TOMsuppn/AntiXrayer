package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.entity.Player;

public class OnlyDiamondDetection implements Detection {
    @Override
    public int check(Player player, PlayerData data) {
        if (player.hasPermission("antixrayer.bypass")) {
            return 0;
        }

        data.resetIfExpired(); // 窗口过期自动清零

        int diamondCount = data.getDiamondMined();
        int otherCount = data.getOtherOreMined();
        int recordCount = data.getRecords().size();

        if (diamondCount > 30 && otherCount <= 15 && recordCount >= 150) {
            data.updateActivity(); // 数据增长刷新窗口
            return 10;
        }
        return 0;
    }
}
