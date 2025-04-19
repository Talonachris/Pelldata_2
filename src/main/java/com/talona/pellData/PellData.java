package com.talona.pellData;

import org.bukkit.plugin.java.JavaPlugin;

public class PellData extends JavaPlugin {

    private DatabaseManager db;
    private LocalesManager locales;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.db = new DatabaseManager(getDataFolder() + "/data.db");
        this.locales = new LocalesManager(this);

        // Menüs erstellen
        StatsMenu statsMenu = new StatsMenu(locales, db);
        RankingMenu rankingMenu = new RankingMenu(db, locales);
        GlobalStatsMenu globalStatsMenu = new GlobalStatsMenu(db, locales);

        // Events registrieren (Listener)
        getServer().getPluginManager().registerEvents(new BlockListener(db), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(db), this);
        getServer().getPluginManager().registerEvents(new DeathListener(db, locales), this);
        getServer().getPluginManager().registerEvents(new PlaytimeListener(db, this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(db), this);
        getServer().getPluginManager().registerEvents(
                new MenuClickListener(statsMenu, rankingMenu, globalStatsMenu, locales), this);

        // Menüs selbst als Listener (Klickschutz)
        getServer().getPluginManager().registerEvents(statsMenu, this);
        getServer().getPluginManager().registerEvents(globalStatsMenu, this);
        getServer().getPluginManager().registerEvents(rankingMenu, this); // 🔥 wichtig!

        // Befehl registrieren
        getCommand("pelldata").setExecutor(new CommandHandler(db, locales));
        getCommand("pelldata").setExecutor(new CommandHandler(db, locales));
        getCommand("pelldata").setTabCompleter(new CommandCompleter()); // ✅ wichtig!


        // Datenbank-Backup starten
        int interval = getConfig().getInt("backup.interval_minutes", 5);
        if (interval > 0) {
            long ticks = interval * 60L * 20L;
            new DatabaseBackupTask(this).runTaskTimer(this, ticks, ticks);
            getLogger().info("[Pelldata] Backup aktiviert: alle " + interval + " Minuten.");
        } else {
            getLogger().info("[Pelldata] Backup deaktiviert.");
        }

        getLogger().info("Pelldata erfolgreich geladen.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Pelldata deaktiviert.");
    }
}
