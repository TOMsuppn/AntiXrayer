package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.entity.Player;

public class DirectionChangeDetection implements Detection {
    @Override
    public int check(Player player, PlayerData data) {
        if (player.hasPermission("antixrayer.bypass")) {
            return 0;
        }

        data.resetIfExpired(); // 确保10分钟窗口过期自动清零

        if (data.getDiamondMined() < 25) return 0;

        int changes = data.getDirectionChanges();
        if (changes > 8) {

            data.updateActivity(); // 数据增长
            return 5;

        }

        return 0;
    }
}
