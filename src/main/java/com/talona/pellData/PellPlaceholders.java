/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 */
package com.talona.pellData;

import com.talona.pellData.DatabaseManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PellPlaceholders
extends PlaceholderExpansion {
    private final DatabaseManager db;

    public PellPlaceholders(DatabaseManager db) {
        this.db = db;
    }

    public String getIdentifier() {
        return "pelldata";
    }

    public String getAuthor() {
        return "Talonachris";
    }

    public String getVersion() {
        return "1.1";
    }

    public boolean persist() {
        return true;
    }

    public boolean canRegister() {
        return true;
    }

    public String onRequest(OfflinePlayer player, String params) {
        String uuid;
        if (this.db == null) {
            return "";
        }
        for (String stat : new String[]{"blocks_placed", "blocks_broken", "killed_mobs", "deaths", "playtime", "chat", "pvp"}) {
            if (!params.toLowerCase().startsWith(stat + "_")) continue;
            String name = params.substring(stat.length() + 1);
            OfflinePlayer target = Bukkit.getOfflinePlayer((String)name);
            if (target == null || target.getUniqueId() == null) {
                return "N/A";
            }
            String uuid2 = target.getUniqueId().toString();
            return switch (stat) {
                case "blocks_placed" -> String.valueOf(this.db.getBlocksPlaced(uuid2));
                case "blocks_broken" -> String.valueOf(this.db.getBlocksBroken(uuid2));
                case "killed_mobs" -> String.valueOf(this.db.getMobsKilled(uuid2));
                case "deaths" -> String.valueOf(this.db.getDeaths(uuid2));
                case "playtime" -> this.formatTime(this.db.getPlaytime(uuid2));
                case "chat" -> String.valueOf(this.db.getChatMessages(uuid2));
                case "pvp" -> String.valueOf(this.db.getPvPKills(uuid2));
                default -> "N/A";
            };
        }
        String string = uuid = player != null ? player.getUniqueId().toString() : null;
        if (uuid == null) {
            return "";
        }
        return switch (params.toLowerCase()) {
            case "blocks_placed" -> String.valueOf(this.db.getBlocksPlaced(uuid));
            case "blocks_broken" -> String.valueOf(this.db.getBlocksBroken(uuid));
            case "killed_mobs" -> String.valueOf(this.db.getMobsKilled(uuid));
            case "deaths" -> String.valueOf(this.db.getDeaths(uuid));
            case "playtime" -> this.formatTime(this.db.getPlaytime(uuid));
            case "chat" -> String.valueOf(this.db.getChatMessages(uuid));
            case "pvp" -> String.valueOf(this.db.getPvPKills(uuid));
            case "global_blocks_placed" -> String.valueOf(this.db.getTotalBlocksPlaced());
            case "global_blocks_broken" -> String.valueOf(this.db.getTotalBlocksBroken());
            case "global_killed_mobs" -> String.valueOf(this.db.getTotalMobKills());
            case "global_deaths" -> String.valueOf(this.db.getTotalDeaths());
            case "global_playtime" -> this.formatTime(this.db.getTotalPlaytime());
            case "global_chat" -> String.valueOf(this.db.getTotalChatMessages());
            case "global_pvp" -> String.valueOf(this.db.getTotalPvPKills());
            default -> null;
        };
    }

    private String formatTime(int seconds) {
        int h = seconds / 3600;
        int m = seconds % 3600 / 60;
        return h + "h " + m + "m";
    }
}

