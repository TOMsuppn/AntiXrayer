package me.tomsuppn.antiXrayer.ProAntiXrayer.manager;

import org.bukkit.Material;
import org.bukkit.Location;
import java.util.*;

public class PlayerData {
    private final UUID uuid;
    private final List<BlockBreakRecord> records = new ArrayList<>();
    private Location lastBreakLocation;
    private Location lastLocation;

    private int directionChanges = 0;
    private long darkTicks = 0;
    private int diamondMined = 0;
    private int otherOreMined = 0;

    private int lastAlertLevel = 0;

    // BlockMiningProtectionDetection（初始值为0）
    private int protectionAccumulatedScore = 0;

    /** 被 BlockMiningProtectionDetection 调用，记录一次尝试应加的分（不会马上加入总分，等检测周期 drain） */
    public void addProtectionAccumulatedScore(int score) {
        this.protectionAccumulatedScore += score;
    }

    public int getProtectionAccumulatedScore() {
        return protectionAccumulatedScore;
    }

    /** 在检测周期中被 BlockMiningProtectionDetection.check() 调用，返回并清零累计分 */
    public int drainProtectionAccumulatedScore() {
        int s = this.protectionAccumulatedScore;
        this.protectionAccumulatedScore = 0;
        return s;
    }

    // 10分钟窗口管理
    private long windowStart = -1L; // -1 表示窗口未启动
    public static final long WINDOW_MS = 900_000L;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }


    // --- 窗口管理 ---
    private void ensureWindowStartedIfNeeded(long delta) {
        if (delta <= 0) return;
        long now = System.currentTimeMillis();
        if (windowStart == -1L) {
            windowStart = now;
        } else if (now - windowStart > WINDOW_MS) {
            resetAll();
            windowStart = now;
        }
    }

    public void resetIfExpired() {
        if (windowStart == -1L) return;
        if (System.currentTimeMillis() - windowStart > WINDOW_MS) {
            resetAll();
        }
    }

    public void resetAll() {
        directionChanges = 0;
        darkTicks = 0;
        diamondMined = 0;
        otherOreMined = 0;
        lastBreakLocation = null;
        lastLocation = null;
        records.clear();
        windowStart = -1L;
    }

    public void updateActivity() {
        if (windowStart == -1L) windowStart = System.currentTimeMillis();
    }

    // --- 数据累积 ---
    public void addRecord(Material type, Location loc) {
        long now = System.currentTimeMillis();
        records.add(new BlockBreakRecord(type, now, loc));
        records.removeIf(r -> now - r.time > WINDOW_MS);

        if (lastBreakLocation != null && !isSameDirection(lastBreakLocation, loc)) {
            addDirectionChange();
        }
        lastBreakLocation = loc.clone();

        if (type == Material.DIAMOND_ORE || type == Material.DEEPSLATE_DIAMOND_ORE) {
            addDiamond();
        }else if(type == Material.ANCIENT_DEBRIS) {
            addDiamond();
            addDiamond();
            addDiamond();
            addDiamond();
            addDiamond();
        } else if (
                type == Material.REDSTONE_ORE || type == Material.DEEPSLATE_REDSTONE_ORE ||
                        type == Material.LAPIS_ORE || type == Material.DEEPSLATE_LAPIS_ORE ||
                        type == Material.COPPER_ORE || type == Material.DEEPSLATE_COPPER_ORE ||
                        type == Material.IRON_ORE || type == Material.DEEPSLATE_IRON_ORE ||
                        type == Material.COAL_ORE || type == Material.DEEPSLATE_COAL_ORE ||
                        type == Material.GOLD_ORE || type == Material.DEEPSLATE_GOLD_ORE ||
                        type == Material.NETHER_GOLD_ORE ||
                        type == Material.NETHER_QUARTZ_ORE
        ) {
            addOtherOre();
        }
    }


    private boolean isSameDirection(Location a, Location b) {
        return a.getBlockX() == b.getBlockX()
                || a.getBlockY() == b.getBlockY()
                || a.getBlockZ() == b.getBlockZ();
    }

    public void addDirectionChange() {
        ensureWindowStartedIfNeeded(1);
        directionChanges++;
        updateActivity();
    }

    public void addDarkTicks(long ticks) {
        if (ticks <= 0) return;
        ensureWindowStartedIfNeeded(ticks);
        darkTicks += ticks;
        updateActivity();
    }

    public void addDiamond() {
        ensureWindowStartedIfNeeded(1);
        diamondMined++;
        updateActivity();
    }

    public void addOtherOre() {
        ensureWindowStartedIfNeeded(1);
        otherOreMined++;
        updateActivity();
    }

    // --- 移动检测 ---
    public boolean checkAndUpdateMovement(Location loc) {
        if (loc == null) return false;
        boolean moved = lastLocation == null || lastLocation.distanceSquared(loc) > 0.01;
        lastLocation = loc.clone();
        return moved;
    }

    // --- getters ---
    public UUID getUuid() { return uuid; }
    public int getDirectionChanges() { return directionChanges; }
    public long getDarkTicks() { return darkTicks; }
    public int getDiamondMined() { return diamondMined; }
    public int getOtherOreMined() { return otherOreMined; }
    public List<BlockBreakRecord> getRecords() { return records; }
    public Location getLastLocation() { return lastLocation; }
    public void setLastLocation(Location loc) {
        this.lastLocation = loc;
    }

    // --- BlockBreakRecord ---
    public static class BlockBreakRecord {
        public final Material type;
        public final long time;
        public final Location location;

        public BlockBreakRecord(Material type, long time, Location location) {
            this.type = type;
            this.time = time;
            this.location = location;
        }
    }
    private final Map<Integer, Long> lastAlertTime = new HashMap<>();

    public long getLastAlertTime(int level) {
        return lastAlertTime.getOrDefault(level, 0L);
    }

    public void setLastAlertTime(int level, long time) {
        lastAlertTime.put(level, time);
    }

    public int getLastAlertLevel() {
        return lastAlertLevel;
    }

    public void setLastAlertLevel(int level) {
        this.lastAlertLevel = level;
    }


}
