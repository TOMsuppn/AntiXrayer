package me.tomsuppn.antiXrayer;

import me.tomsuppn.antiXrayer.ProAntiXrayer.listener.BlockBreakListener;
import me.tomsuppn.antiXrayer.ProAntiXrayer.manager.DetectionManager;
import me.tomsuppn.antiXrayer.Vanish.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public final class AntiXrayer extends JavaPlugin {

    private static AntiXrayer instance;

    private XrayerIndexManager xrayerIndexManager;
    private DetectionManager detectionManager;

    private File vanishFile;
    private FileConfiguration vanishConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        // 初始化检测管理器
        detectionManager = new DetectionManager(this);

        // 注册监听器
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(detectionManager), this);

        // 初始化 XrayerIndex 管理器
        xrayerIndexManager = new XrayerIndexManager(this);

        // 注册方块监听器
        Bukkit.getPluginManager().registerEvents(new DIAMOND_ORE(), this);
        Bukkit.getPluginManager().registerEvents(new DEEPSLATE_DIAMOND_ORE(), this);
        Bukkit.getPluginManager().registerEvents(new ANCIENT_DEBRIS(), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);


        // 注册命令
        this.getCommand("XrayerDetector").setExecutor(new XrayerDetectorCommand(this));

        AxUnbanCommand unbanCommand = new AxUnbanCommand();
        this.getCommand("axunban").setExecutor(unbanCommand);
        this.getCommand("axunban").setTabCompleter(unbanCommand);

        this.getCommand("axv").setExecutor(new VanishCommand());
        getServer().getPluginManager().registerEvents(new VanishListener(), this);

        new antixrayerbackCommand(this);
        getCommand("axview").setExecutor(new AxViewCommand(this));

        // 初始化 vanish 配置
        createVanishConfig();
        loadVanishData();


        getLogger().warning("AntiXrayer 1.2.0 By TOMsupnn 已启动");


    }

    @Override
    public void onDisable() {
        saveVanishData();
        getLogger().warning("AntiXrayer 1.2.0 By TOMsupnn 已关闭");
    }

    // ------------------- 全局访问 -------------------
    public static AntiXrayer getInstance() {
        return instance;
    }

    public XrayerIndexManager getXrayerIndexManager() {
        return xrayerIndexManager;
    }

    public DetectionManager getDetectionManager() {
        return detectionManager;
    }

    // ------------------- vanish 配置管理 -------------------
    private void createVanishConfig() {
        vanishFile = new File(getDataFolder(), "vanish.yml");
        if (!vanishFile.exists()) {
            vanishFile.getParentFile().mkdirs();
            saveResource("vanish.yml", false);
        }
        vanishConfig = YamlConfiguration.loadConfiguration(vanishFile);
    }

    private void loadVanishData() {
        List<String> uuids = getVanishConfig().getStringList("vanished");
        for (String id : uuids) {
            VanishManager.addVanishedUUID(UUID.fromString(id));
        }
    }

    private void saveVanishData() {
        List<String> uuids = VanishManager.getVanishedUUIDs().stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        getVanishConfig().set("vanished", uuids);
        saveVanishConfig();
    }

    public FileConfiguration getVanishConfig() {
        return vanishConfig;
    }

    public void saveVanishConfig() {
        try {
            vanishConfig.save(vanishFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
