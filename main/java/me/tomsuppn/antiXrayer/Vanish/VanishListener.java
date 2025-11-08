package me.tomsuppn.antiXrayer.Vanish;

import me.tomsuppn.antiXrayer.AntiXrayer;
import me.tomsuppn.antiXrayer.BossBar.DisplayManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VanishListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (VanishManager.isVanished(player)) {
            // 隐藏自己
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.equals(player)) {
                    online.hidePlayer(AntiXrayer.getInstance(), player);
                }
            }

            // 恢复夜视
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));

            // 显示 BossBar 提示
            DisplayManager.createBar("§6§l已隐身 输入 §e§l/axb §6§l退出");
            DisplayManager.showToPlayer(player);
        }
    }
}
