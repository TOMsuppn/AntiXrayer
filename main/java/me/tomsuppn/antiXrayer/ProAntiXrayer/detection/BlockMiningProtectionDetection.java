package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BlockMiningProtectionDetection implements Detection {

    private final Map<UUID, Long> startTimes = new HashMap<>();

    private static final long TOTAL_MS = 30_000L;
    private static final long PROTECT_MS = 8_000L;
    private static final Set<Material> WATCH = Set.of(
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.ANCIENT_DEBRIS
    );

    public void startProtection(UUID uuid) {
        startTimes.put(uuid, System.currentTimeMillis());
    }

    private Long getStart(UUID uuid) {
        return startTimes.get(uuid);
    }

    private void remove(UUID uuid) {
        startTimes.remove(uuid);
    }

    private boolean isExpired(UUID uuid) {
        Long t = getStart(uuid);
        if (t == null) return true;
        return System.currentTimeMillis() - t >= TOTAL_MS;
    }

    public boolean handleBlockBreakAttempt(Player player, Material material, PlayerData data) {
        UUID uuid = player.getUniqueId();
        Long t = getStart(uuid);
        if (t == null) return false; // 未启用保护

        long elapsed = System.currentTimeMillis() - t;
        if (elapsed >= TOTAL_MS) {
            remove(uuid); // 到期自动清理
            return false;
        }

        if (!WATCH.contains(material)) {
            return false;
        }

        // 在保护期内一律拒绝挖掘请求
        if (elapsed >= PROTECT_MS) {
            data.addProtectionAccumulatedScore(5);
            data.updateActivity(); // 有数据增长，刷新活动窗口（你已有的方法）
        }
        return true; // 拒绝挖掘
    }

    @Override
    public int check(Player player, PlayerData data) {
        return data.getProtectionAccumulatedScore();
    }
}
