/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.EntityType
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDeathEvent
 */
package com.talona.pellData;

import com.talona.pellData.DatabaseManager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobKillListener
implements Listener {
    private final DatabaseManager db;

    public MobKillListener(DatabaseManager db) {
        this.db = db;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        String uuid = event.getEntity().getKiller().getUniqueId().toString();
        EntityType type = event.getEntityType();
        if (type != EntityType.PLAYER) {
            this.db.incrementMobKills(uuid);
        }
    }
}

