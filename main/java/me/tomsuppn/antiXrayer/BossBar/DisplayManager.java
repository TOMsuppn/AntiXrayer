package me.tomsuppn.antiXrayer.BossBar;


import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class DisplayManager {
    private static BossBar bar;

    public static void createBar(String text) {
        if (bar == null) {
            bar = Bukkit.createBossBar(text, BarColor.YELLOW, BarStyle.SOLID);
            bar.setVisible(true);
        } else {
            bar.setTitle(text);
        }
    }

    public static void showToPlayer(Player player) {
        if (bar != null) {
            bar.addPlayer(player);
        }
    }

    public static void hideFromPlayer(Player player) {
        if (bar != null) {
            bar.removePlayer(player);
        }
    }

    public static void updateText(String text) {
        if (bar != null) {
            bar.setTitle(text);
        }
    }
}
