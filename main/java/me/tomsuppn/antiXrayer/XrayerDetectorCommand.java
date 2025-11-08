package me.tomsuppn.antiXrayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class XrayerDetectorCommand implements CommandExecutor, TabCompleter, Listener {

    private final AntiXrayer plugin;
    private final Set<UUID> frozenPlayers = new HashSet<>();


    public XrayerDetectorCommand(AntiXrayer plugin) {
        this.plugin = plugin;
        // 注册监听器
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§c§l反矿透 §4§l▶ " + "§6§l用法: /" + "xrayerdetector" + " <玩家>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage("§c§l反矿透 §4§l▶ §e§l" + args[0] + " §6§l不在线");
            return true;
        }

        // 加入冻结名单
        frozenPlayers.add(target.getUniqueId());

        // 添加失明效果
        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 10, 1, false, false));

        String bantitle = plugin.getConfig().getString("messages.ban-title","你的账号因可疑行为被暂时冻结");
        String bansubtitle = plugin.getConfig().getString("messages.ban-subtitle","请立刻截图为你后续的申诉提供证据");

        // 公告
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§c§l反矿透 §4§l▶ §e§l" + "§8§l=========================");
        Bukkit.broadcastMessage("§c§l反矿透 §4§l▶ §e§l" + target.getName() + " §6§l因行径可疑被暂时冻结账号💀");
        Bukkit.broadcastMessage("§c§l反矿透 §4§l▶ §e§l" + "§8§l=========================");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("");


        // 显示标题
        target.sendTitle(
                ChatColor.RED + "" + ChatColor.BOLD + bantitle,
                ChatColor.YELLOW + "" + ChatColor.BOLD + bansubtitle,
                10, 200, 20
        );

        // 崩端（半成品）
        /*
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.EXPLOSION);
        Location location = target.getLocation();
        packet.getDoubles().write(0,location.getX());
        packet.getDoubles().write(0,location.getY());
        packet.getDoubles().write(0,location.getZ());
        packet.getFloat().write(0,Float.MAX_VALUE);
        packet.getSpecificModifier(List.class).write(0, new ArrayList(0));
        packet.getFloat().write(1,Float.MAX_VALUE);
        packet.getFloat().write(2,Float.MAX_VALUE);
        packet.getFloat().write(3,Float.MAX_VALUE);
        for (int i = 0; i <30; ++i){
            pm.sendServerPacket(target , packet , false);
        }

         */

        sender.sendMessage("§c§l反矿透 §4§l▶ §e§l" + target.getName() + " §6§l现已被临时封禁");

        // 延迟 10 秒后封禁
        new BukkitRunnable() {
            @Override
            public void run() {
                String reason = plugin.getConfig().getString("messages.ban-reason","检测到疑似使用矿物透视");
                String appeal = plugin.getConfig().getString("messages.appeal","请联系管理员");
                long durationMillis = TimeUnit.HOURS.toMillis(10);
                Date expireDate = new Date(System.currentTimeMillis() + durationMillis);

                frozenPlayers.remove(target.getUniqueId());

                target.kickPlayer(ChatColor.RED + "你的账号已被临时封禁!\n"
                        + ChatColor.RED + "原因: " + reason + "\n\n"
                        + ChatColor.RED + "申诉方式: "
                        + ChatColor.RED + appeal);

                boolean uselitebans = plugin.getConfig().getBoolean("settings.use-litebans", true);
                if (uselitebans){
                    String cmd = "tempban " + target.getName() + " -s 10h " + reason;
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                    return;
                }
                Bukkit.getServer().getBanList(org.bukkit.BanList.Type.NAME)
                        .addBan(target.getName(), reason, expireDate, sender.getName());


            }
        }.runTaskLater(plugin, 20 * 10);

        return true;
    }

    // 监听玩家移动事件
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            if (event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getY() != event.getTo().getY()
                    || event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom()); // 阻止移动
            }
        }
    }

    // 禁止破坏方块
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // 禁止放置方块
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    // 禁止攻击实体
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (frozenPlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // 禁止打开背包
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            if (frozenPlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // 禁止点击背包物品
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (frozenPlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    // 禁止使用物品
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (frozenPlayers.contains(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return Arrays.asList();
    }
}
