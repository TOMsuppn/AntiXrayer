package me.tomsuppn.antiXrayer.ProAntiXrayer.detection;

import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class debuging_info implements Detection {
    private static final String OBJECTIVE_NAME = "AX_Debug";

    @Override
    public int check(Player player, PlayerData data) {
        data.resetIfExpired();

        int recordCount = data.getRecords().size();
        int directionChanges = data.getDirectionChanges();
        int otherCount = data.getOtherOreMined();
        int diamondCount = data.getDiamondMined();
        long night_long = data.getDarkTicks();
        int night = (int) night_long;


        showDebugBoard(player, recordCount, directionChanges, otherCount, diamondCount, night);

        return 0;
    }

    private void showDebugBoard(Player player, int recordCount, int directionChanges, int otherCount, int diamondCount, int night) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard board = player.getScoreboard();
        if (board == null || board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = manager.getNewScoreboard();
        }

        Objective obj = board.getObjective(OBJECTIVE_NAME);
        if (obj == null) {
            obj = board.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, ChatColor.RED + "AntiXrayer Debug");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        setLineAsScore(board, obj, "Records", ChatColor.YELLOW + "开采总值: ", recordCount, 5);
        setLineAsScore(board, obj, "Dchange", ChatColor.YELLOW + "D改: ", directionChanges, 4);
        setLineAsScore(board, obj, "Oscore", ChatColor.YELLOW + "O数量: ", otherCount, 3);
        setLineAsScore(board, obj, "dm+ad", ChatColor.YELLOW + "总量: ", diamondCount, 2);
        setLineAsScore(board, obj, "Dscore", ChatColor.YELLOW + "动态指数: ", night, 1);

        player.setScoreboard(board);
    }

    private void setLineAsScore(Scoreboard board, Objective obj, String entryKey, String title, int value, int order) {
        Team team = board.getTeam(entryKey);
        if (team == null) {
            team = board.registerNewTeam(entryKey);
            team.addEntry(entryKey);
        }

        team.setPrefix(title);
        obj.getScore(entryKey).setScore(value);
    }
}
