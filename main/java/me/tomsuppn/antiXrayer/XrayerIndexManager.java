package me.tomsuppn.antiXrayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XrayerIndexManager {

    private final AntiXrayer plugin;

    // 玩家数据存储
    private final Map<UUID, Integer> xrayerIndex = new HashMap<>();
    // 倒计时任务
    private final Map<UUID, Boolean> activeTimer = new HashMap<>();

    public XrayerIndexManager(AntiXrayer plugin) {
        this.plugin = plugin;
    }

    public void addXrayerIndex(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int newValue = xrayerIndex.getOrDefault(uuid, 0) + amount;
        xrayerIndex.put(uuid, newValue);

        if (!activeTimer.getOrDefault(uuid, false)) {
            activeTimer.put(uuid, true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    xrayerIndex.put(uuid, 0); // 清零
                    activeTimer.put(uuid, false); // 重置标记
                }
            }.runTaskLater(plugin, 20L * 600); // 600秒 = 10分钟
        }

    }

    public void reduceXrayerIndex(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = xrayerIndex.getOrDefault(uuid, 0);

        int newValue = current - amount;
        xrayerIndex.put(uuid, newValue);
    }

    public int getXrayerIndex(Player player) {
        return xrayerIndex.getOrDefault(player.getUniqueId(), 0);
    }
}
