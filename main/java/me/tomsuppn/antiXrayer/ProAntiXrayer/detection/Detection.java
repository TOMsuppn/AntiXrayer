package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;


import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.entity.Player;

public interface Detection {
    int check(Player player, PlayerData data);
}
