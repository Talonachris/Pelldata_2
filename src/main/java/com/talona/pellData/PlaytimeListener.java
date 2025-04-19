package com.talona.pellData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeListener implements Listener {

    private final DatabaseManager db;
    private final PellData plugin;
    private final Map<UUID, Long> lastActive = new HashMap<>();

    public PlaytimeListener(DatabaseManager db, PellData plugin) {
        this.db = db;
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.isOnline() && !p.isDead()) {
                        UUID uuid = p.getUniqueId();
                        long now = System.currentTimeMillis();
                        long last = lastActive.getOrDefault(uuid, now);
                        long diff = now - last;

                        if (diff <= 10 * 60 * 1000) { // max 10 Minuten AFK
                            db.addPlaytime(uuid.toString(), 5); // +5 Sekunden Spielzeit
                        }

                        lastActive.put(uuid, now);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 100L); // alle 5 Sekunden
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        lastActive.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastActive.remove(event.getPlayer().getUniqueId());
    }
}
