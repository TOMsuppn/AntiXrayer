package me.tomsuppn.antiXrayer.Vanish;

import me.tomsuppn.antiXrayer.AntiXrayer;
import me.tomsuppn.antiXrayer.BossBar.DisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {
    private static final Set<UUID> vanishedPlayers = new HashSet<>();

    public static void vanish(Player player){
        if(vanishedPlayers.contains(player.getUniqueId())) return;

        vanishedPlayers.add(player.getUniqueId());
        for(Player online : Bukkit.getOnlinePlayers()){
            if (!online.equals(player)){
                online.hidePlayer(AntiXrayer.getInstance(), player);
            }
        }
        player.sendMessage("§c§l反矿透 §4§l▶ §6§l已进入隐身模式");
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));

        // BossBar提示
        DisplayManager.createBar("§6§l已隐身 输入 §e§l/axb §6§l退出");
        DisplayManager.showToPlayer(player);
    }

    public static void unvanish(Player player){
        if(!vanishedPlayers.contains(player.getUniqueId())) return;

        vanishedPlayers.remove(player.getUniqueId());
        for(Player online : Bukkit.getOnlinePlayers()){
            if(!online.equals(player)){
                online.showPlayer(AntiXrayer.getInstance(), player);
            }
        }
        player.sendMessage("§c§l反矿透 §4§l▶ §6§l已退出隐身模式");
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);

        // BossBar提示
        DisplayManager.hideFromPlayer(player);
    }

    public static boolean isVanished(Player player){
        return vanishedPlayers.contains(player.getUniqueId());
    }

    // 提供持久化支持
    public static Set<UUID> getVanishedUUIDs() {
        return vanishedPlayers;
    }

    public static void addVanishedUUID(UUID uuid) {
        vanishedPlayers.add(uuid);
    }
}
