package com.talona.pellData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.logging.Level;

public class DatabaseBackupTask extends BukkitRunnable {

    private final PellData plugin;

    public DatabaseBackupTask(PellData plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int interval = plugin.getConfig().getInt("backup.interval_minutes", 5);
        if (interval <= 0) {
            plugin.getLogger().info("[Pelldata] Backup deaktiviert (Intervall = 0).");
            return;
        }

        File dbFile = new File(plugin.getDataFolder(), "data.db");
        File backupFile = new File(plugin.getDataFolder(), "data_backup.db");

        if (!dbFile.exists()) {
            warn("Datenbankdatei nicht gefunden – Backup übersprungen.");
            return;
        }

        try (FileInputStream in = new FileInputStream(dbFile);
             FileOutputStream out = new FileOutputStream(backupFile)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            plugin.getLogger().info("[Pelldata] Backup erfolgreich gespeichert: data_backup.db");

        } catch (IOException e) {
            warn("Fehler beim Schreiben des Backups: " + e.getMessage());
        }
    }

    private void warn(String message) {
        plugin.getLogger().warning("[Pelldata] " + message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("pelldata.database")) {
                player.sendMessage("§c[Pelldata] " + message);
            }
        }
    }
}
