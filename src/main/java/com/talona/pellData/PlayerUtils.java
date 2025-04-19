/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 */
package com.talona.pellData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerUtils {
    public static List<String> getAllPlayerNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String name = offlinePlayer.getName();
            if (name == null || name.isEmpty()) continue;
            names.add(name);
        }
        return names;
    }

    public static UUID getUUIDFromName(String name) {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (offlinePlayer.getName() == null || !offlinePlayer.getName().equalsIgnoreCase(name)) continue;
            return offlinePlayer.getUniqueId();
        }
        return null;
    }
}

