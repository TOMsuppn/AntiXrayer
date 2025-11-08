package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.entity.Player;

public class RandomMiningDetection implements Detection {
    @Override
    public int check(Player player, PlayerData data) {
        data.resetIfExpired(); // 窗口过期自动清零

        if (player.hasPermission("antixrayer.bypass")) {
            return 0;
        }
        // 钻石数 >= 30启用无序挖矿检测
        if (data.getDiamondMined() < 30) return 0;

        int directionChanges = data.getDirectionChanges();
        int recordCount = data.getRecords().size();

        if (directionChanges > 13 && recordCount > 200) {
            data.updateActivity(); // 数据增长刷新窗口
            return 15;
        }

        return 0;
    }
}
