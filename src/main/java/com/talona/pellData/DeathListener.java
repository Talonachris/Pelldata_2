/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.PlayerDeathEvent
 */
package com.talona.pellData;

import com.talona.pellData.DatabaseManager;
import com.talona.pellData.LocalesManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathListener
implements Listener {
    private final DatabaseManager db;
    private final LocalesManager locales;

    public DeathListener(DatabaseManager db, LocalesManager locales) {
        this.db = db;
        this.locales = locales;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String uuid = event.getEntity().getUniqueId().toString();
        this.db.incrementDeaths(uuid);
    }
}

