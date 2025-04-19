package com.talona.pellData;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(String dbFilePath) {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            this.createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Could not connect to database", e);
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = this.connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid TEXT PRIMARY KEY,
                    blocks_placed INTEGER DEFAULT 0,
                    blocks_broken INTEGER DEFAULT 0,
                    killed_mobs INTEGER DEFAULT 0,
                    deaths INTEGER DEFAULT 0,
                    playtime_seconds INTEGER DEFAULT 0,
                    chat_messages INTEGER DEFAULT 0,
                    pvp_kills INTEGER DEFAULT 0
                );
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS mob_kill_types (
                    uuid TEXT,
                    mob_type TEXT,
                    amount INTEGER DEFAULT 0,
                    PRIMARY KEY (uuid, mob_type)
                );
            """);
        }
    }

    public void ensurePlayerExists(String uuid) {
        try (PreparedStatement ps = this.connection.prepareStatement(
                "INSERT OR IGNORE INTO player_stats (uuid) VALUES (?)")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void increment(String uuid, String column) {
        ensurePlayerExists(uuid);
        try (PreparedStatement ps = this.connection.prepareStatement(
                "UPDATE player_stats SET " + column + " = " + column + " + 1 WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void incrementBlockPlaced(String uuid) { increment(uuid, "blocks_placed"); }
    public void incrementBlockBroken(String uuid) { increment(uuid, "blocks_broken"); }
    public void incrementDeaths(String uuid) { increment(uuid, "deaths"); }
    public void incrementMobKills(String uuid) { increment(uuid, "killed_mobs"); }
    public void incrementPvPKills(String uuid) { increment(uuid, "pvp_kills"); }
    public void incrementChatMessages(String uuid) { increment(uuid, "chat_messages"); }

    public void addPlaytime(String uuid, int seconds) {
        ensurePlayerExists(uuid);
        try (PreparedStatement ps = this.connection.prepareStatement(
                "UPDATE player_stats SET playtime_seconds = playtime_seconds + ? WHERE uuid = ?")) {
            ps.setInt(1, seconds);
            ps.setString(2, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getValue(String uuid, String column) {
        ensurePlayerExists(uuid);
        try (PreparedStatement ps = this.connection.prepareStatement(
                "SELECT " + column + " FROM player_stats WHERE uuid = ?")) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBlocksPlaced(String uuid) { return getValue(uuid, "blocks_placed"); }
    public int getBlocksBroken(String uuid) { return getValue(uuid, "blocks_broken"); }
    public int getMobsKilled(String uuid) { return getValue(uuid, "killed_mobs"); }
    public int getPvPKills(String uuid) { return getValue(uuid, "pvp_kills"); }
    public int getDeaths(String uuid) { return getValue(uuid, "deaths"); }
    public int getPlaytime(String uuid) { return getValue(uuid, "playtime_seconds"); }
    public int getChatMessages(String uuid) { return getValue(uuid, "chat_messages"); }

    public int getSum(String column) {
        try (PreparedStatement ps = this.connection.prepareStatement(
                "SELECT SUM(" + column + ") AS total FROM player_stats")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalBlocksPlaced() { return getSum("blocks_placed"); }
    public int getTotalBlocksBroken() { return getSum("blocks_broken"); }
    public int getTotalMobKills() { return getSum("killed_mobs"); }
    public int getTotalPvPKills() { return getSum("pvp_kills"); }
    public int getTotalDeaths() { return getSum("deaths"); }
    public int getTotalPlaytime() { return getSum("playtime_seconds"); }
    public int getTotalChatMessages() { return getSum("chat_messages"); }

    public void incrementMobKillType(String uuid, String mobType) {
        try (PreparedStatement ps = this.connection.prepareStatement("""
            INSERT INTO mob_kill_types (uuid, mob_type, amount)
            VALUES (?, ?, 1)
            ON CONFLICT(uuid, mob_type) DO UPDATE SET amount = amount + 1;
        """)) {
            ps.setString(1, uuid);
            ps.setString(2, mobType);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<StatEntry> getTopStats(String type, int limit) {
        List<StatEntry> top = new ArrayList<>();
        String column;

        switch (type.toLowerCase()) {
            case "placed" -> column = "blocks_placed";
            case "broken" -> column = "blocks_broken";
            case "killed", "mobs" -> column = "killed_mobs";
            case "deaths" -> column = "deaths";
            case "playtime" -> column = "playtime_seconds";
            case "chat" -> column = "chat_messages";
            case "pvp" -> column = "pvp_kills";
            default -> column = null;
        }

        if (column == null) return top;

        try (PreparedStatement ps = this.connection.prepareStatement(
                "SELECT uuid, " + column + " AS value FROM player_stats ORDER BY value DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                top.add(new StatEntry(rs.getString("uuid"), rs.getInt("value")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return top;
    }

    public List<String> getAllStoredPlayerNames() {
        List<String> names = new ArrayList<>();
        try (PreparedStatement ps = this.connection.prepareStatement("SELECT uuid FROM player_stats")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                if (player.getName() != null) {
                    names.add(player.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    public void resetStats(String uuid) {
        ensurePlayerExists(uuid);
        try (PreparedStatement ps = this.connection.prepareStatement("""
            UPDATE player_stats
            SET blocks_placed = 0,
                blocks_broken = 0,
                killed_mobs = 0,
                deaths = 0,
                playtime_seconds = 0,
                chat_messages = 0,
                pvp_kills = 0
            WHERE uuid = ?
        """)) {
            ps.setString(1, uuid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
