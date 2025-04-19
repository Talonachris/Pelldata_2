package com.talona.pellData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class StatsMenu implements Listener {

    private final LocalesManager locales;
    private final DatabaseManager db;

    public StatsMenu(LocalesManager locales, DatabaseManager db) {
        this.locales = locales;
        this.db = db;
    }

    public void openMainMenu(Player player) {
        Inventory inv = createMainInventory(player);
        player.openInventory(inv);
    }

    private Inventory createMainInventory(Player player) {
        String uuid = player.getUniqueId().toString();
        Inventory inv = Bukkit.createInventory(null, 27, locales.get(player, "gui.stats.title"));

        inv.setItem(10, createItem(Material.STONE, locales.get(player, "gui.stats.blocks.title"), List.of(
                locales.get(player, "gui.stats.blocks.placed").replace("{value}", String.valueOf(db.getBlocksPlaced(uuid))),
                locales.get(player, "gui.stats.blocks.broken").replace("{value}", String.valueOf(db.getBlocksBroken(uuid)))
        )));

        inv.setItem(11, createItem(Material.IRON_SWORD, locales.get(player, "gui.stats.killed.title"), List.of(
                locales.get(player, "gui.stats.killed.value").replace("{value}", String.valueOf(db.getMobsKilled(uuid)))
        )));

        inv.setItem(12, createItem(Material.DIAMOND_SWORD, locales.get(player, "gui.stats.pvp.title"), List.of(
                locales.get(player, "gui.stats.pvp.value").replace("{value}", String.valueOf(db.getPvPKills(uuid)))
        )));

        inv.setItem(13, createItem(Material.SKELETON_SKULL, locales.get(player, "gui.stats.deaths.title"), List.of(
                locales.get(player, "gui.stats.deaths.value").replace("{value}", String.valueOf(db.getDeaths(uuid)))
        )));

        inv.setItem(14, createItem(Material.CLOCK, locales.get(player, "gui.stats.playtime.title"), List.of(
                locales.get(player, "gui.stats.playtime.value").replace("{value}", formatPlaytime(db.getPlaytime(uuid)))
        )));

        inv.setItem(15, createItem(Material.PAPER, locales.get(player, "gui.stats.chat.title"), List.of(
                locales.get(player, "gui.stats.chat.value").replace("{value}", String.valueOf(db.getChatMessages(uuid)))
        )));

        return inv;
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private String formatPlaytime(int seconds) {
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        return h + "h " + m + "m";
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();
        if (title.equalsIgnoreCase(locales.get(player, "gui.stats.title"))) {
            event.setCancelled(true);
        }
    }
}
