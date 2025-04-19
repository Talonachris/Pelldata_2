/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package com.talona.pellData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LocalesManager {
    private final Plugin plugin;
    private final String language;
    private final Map<String, YamlConfiguration> loadedLocales = new HashMap<String, YamlConfiguration>();

    public LocalesManager(Plugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.language = plugin.getConfig().getString("language", "en_us").toLowerCase();
        File customFile = new File(plugin.getDataFolder(), "locales/custom.yml");
        if (!customFile.exists()) {
            plugin.saveResource("locales/custom.yml", false);
        }
        if (this.language.equals("custom")) {
            this.loadLocale("custom");
        } else {
            this.copyDefaultLocale("en_us");
            this.copyDefaultLocale("de_de");
            this.loadLocale("en_us");
            this.loadLocale(this.language);
        }
    }

    private void loadLocale(String locale) {
        File localeFile = new File(this.plugin.getDataFolder(), "locales/" + locale + ".yml");
        if (!localeFile.exists()) {
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration((File)localeFile);
        this.loadedLocales.put(locale, config);
    }

    private void copyDefaultLocale(String locale) {
        File localeFile = new File(this.plugin.getDataFolder(), "locales/" + locale + ".yml");
        if (!localeFile.exists()) {
            this.plugin.getLogger().info("[PellData] LocalesManager: Copying " + locale + ".yml...");
            this.plugin.saveResource("locales/" + locale + ".yml", false);
        }
    }

    public String get(Player player, String key) {
        YamlConfiguration langFile = this.loadedLocales.get(this.language);
        if (langFile == null) {
            return key;
        }
        String msg = langFile.getString(key);
        if (msg == null) {
            YamlConfiguration fallback = this.loadedLocales.get("en_us");
            msg = fallback != null ? fallback.getString(key, key) : key;
        }
        String prefix = langFile.getString("prefix", "&e[Pelldata] &r");
        msg = msg.replace("{prefix}", prefix);
        return ChatColor.translateAlternateColorCodes((char)'&', (String)msg);
    }

    public String getPrefixed(Player player, String key) {
        return this.get(player, key);
    }
}

