package me.tomsuppn.antiXrayer.ProAntiXrayer.manager;

import me.tomsuppn.antiXrayer.AntiXrayer;
import me.tomsuppn.antiXrayer.ProAntiXrayer.detection.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;


public class DetectionManager {
    private final Map<UUID, PlayerData> playerData = new HashMap<>();
    private final List<Detection> detections = new ArrayList<>();

    private final BlockMiningProtectionDetection blockProtection = new BlockMiningProtectionDetection();

    public void enableBlockProtectionFor(Player target) {
        blockProtection.startProtection(target.getUniqueId());
    }
    public boolean handleBlockBreakDuringProtection(Player player, Material material) {
        PlayerData data = getData(player.getUniqueId());
        return blockProtection.handleBlockBreakAttempt(player, material, data);
    }


    public DetectionManager(AntiXrayer plugin) {
        detections.add(new DiamondSpeedDetection());
        detections.add(new OnlyDiamondDetection());
        detections.add(new LightLevelDetection());
        detections.add(new DirectionChangeDetection());
        detections.add(new RandomMiningDetection());

        // debug
        //detections.add(new debuging_info());

        detections.add(blockProtection);
    }

    public PlayerData getData(UUID uuid) {
        return playerData.computeIfAbsent(uuid, PlayerData::new);
    }

    public void check(Player player) {
        PlayerData data = getData(player.getUniqueId());
        if (player.hasPermission("antixrayer.bypass")) {
            return;
        }
        data.resetIfExpired();

        int totalScore = 0;
        for (Detection detection : detections) {
            if ((detection instanceof DirectionChangeDetection
                    || detection instanceof RandomMiningDetection)
                    && data.getDiamondMined() < 30) {
                continue; // 忽略此检测
            }
            int score = detection.check(player, data);
            if (score > 0) {
                data.updateActivity();
            totalScore += score;
        }

        long now = System.currentTimeMillis();
        // 分级警报
        int[] levels = {5, 10, 15, 20, 25, 30};
        int alertLevel = 0;

        // 找到对应等级
        for (int i = levels.length - 1; i >= 0; i--) {
            if (totalScore >= levels[i]) {
                alertLevel = i + 1;
                if (alertLevel > data.getLastAlertLevel()) {
                    data.setLastAlertLevel(alertLevel);

                    enableBlockProtectionFor(player);

                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.hasPermission("antixrayer.alarm")) {
                            Component msg = Component.text("§c§l反矿透 §4§l▶ §6§l"
                                            + player.getName()
                                            + " §c§l疑似使用矿物透视 §4(LV." + alertLevel + " §4§l" + totalScore + "§4)")
                                    .clickEvent(ClickEvent.runCommand("/axview " + player.getName()))
                                    .hoverEvent(HoverEvent.showText(Component.text("§c§l开关审查模式")));
                            onlinePlayer.sendMessage(msg);

                            if (levels[i] >= 30) {
                                String cmd = "xrayerdetector " + player.getName();
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                                onlinePlayer.sendMessage("§c§l反矿透 §4§l▶ §b§l因 §c§l" + player.getName() + " §b§l行径过于可疑，已采取冻结账号手段终止其行为，请管理员审核该玩家");
                            }
                        }
                    }
                }
                break;
            }
        }


    }
}}
