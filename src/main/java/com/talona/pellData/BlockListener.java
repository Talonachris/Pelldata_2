/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package com.talona.pellData;

import com.talona.pellData.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class BlockListener
implements Listener {
    private final DatabaseManager db;

    public BlockListener(DatabaseManager db) {
        this.db = db;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        this.db.incrementBlockPlaced(uuid);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        this.db.incrementBlockBroken(uuid);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.db.ensurePlayerExists(event.getPlayer().getUniqueId().toString());
    }
}

