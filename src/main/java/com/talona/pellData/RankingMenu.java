package com.talona.pellData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class RankingMenu implements Listener {

    private final DatabaseManager db;
    private final LocalesManager locales;

    public RankingMenu(DatabaseManager db, LocalesManager locales) {
        this.db = db;
        this.locales = locales;
    }

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, locales.get(player, "gui.ranking.title"));

        inv.setItem(10, createItem(Material.STONE, locales.get(player, "gui.ranking.blocks"), null));
        inv.setItem(11, createItem(Material.IRON_SWORD, locales.get(player, "gui.ranking.pvp"), null));
        inv.setItem(12, createItem(Material.SKELETON_SKULL, locales.get(player, "gui.ranking.mobs"), null));
        inv.setItem(13, createItem(Material.DIAMOND_SWORD, locales.get(player, "gui.ranking.deaths"), null));
        inv.setItem(14, createItem(Material.CLOCK, locales.get(player, "gui.ranking.playtime"), null));
        inv.setItem(15, createItem(Material.PAPER, locales.get(player, "gui.ranking.chat"), null));

        player.openInventory(inv);
    }

    public void openTopList(Player player, String type) {
        String title = locales.get(player, "gui.ranking.title_" + type);
        Inventory inv = Bukkit.createInventory(null, 54, title);

        List<StatEntry> top = db.getTopStats(type, 10);

        for (int i = 0; i < top.size(); i++) {
            StatEntry entry = top.get(i);
            String uuid = entry.getUuid();
            int value = entry.getValue();

            OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            String name = (offline.getName() != null) ? offline.getName() : uuid;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            if (meta != null) {
                meta.setOwningPlayer(offline);
                meta.setDisplayName("§e#" + (i + 1) + " §f" + name);

                String valueStr = (type.equals("playtime")) ? formatPlaytime(value) : String.valueOf(value);

                String lore = locales.get(player, "gui.ranking.value_only")
                        .replace("{stat}", locales.get(player, "gui.ranking." + type))
                        .replace("{value}", valueStr);

                meta.setLore(List.of(lore));
                head.setItemMeta(meta);
            }

            inv.setItem(i, head);
        }

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(locales.get(player, "gui.back"));
            back.setItemMeta(backMeta);
        }
        inv.setItem(49, back);

        player.openInventory(inv);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e" + name);
            if (lore != null) meta.setLore(lore);
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

        if (title.equalsIgnoreCase(locales.get(player, "gui.ranking.title"))) {
            event.setCancelled(true);
        }

        for (String type : List.of("placed", "pvp", "mobs", "deaths", "playtime", "chat")) {
            if (title.equalsIgnoreCase(locales.get(player, "gui.ranking.title." + type))) {
                event.setCancelled(true);
            }
        }
    }
}
