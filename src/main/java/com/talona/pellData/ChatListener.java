/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 */
package com.talona.pellData;

import com.talona.pellData.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener
implements Listener {
    private final DatabaseManager db;

    public ChatListener(DatabaseManager db) {
        this.db = db;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        this.db.incrementChatMessages(uuid);
    }
}

