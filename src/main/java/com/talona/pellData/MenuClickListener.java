package com.talona.pellData;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;

import java.util.List;

public class MenuClickListener implements Listener {

    private final StatsMenu statsMenu;
    private final RankingMenu rankingMenu;
    private final GlobalStatsMenu globalStatsMenu;
    private final LocalesManager locales;

    public MenuClickListener(StatsMenu statsMenu, RankingMenu rankingMenu, GlobalStatsMenu globalStatsMenu, LocalesManager locales) {
        this.statsMenu = statsMenu;
        this.rankingMenu = rankingMenu;
        this.globalStatsMenu = globalStatsMenu;
        this.locales = locales;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player player)) return;

        InventoryView view = event.getView();
        String title = view.getTitle();
        int slot = event.getRawSlot();

        // Your Stats
        if (title.equalsIgnoreCase(locales.get(player, "gui.stats.title"))) {
            event.setCancelled(true);
            return;
        }

        // Global Stats
        if (title.equalsIgnoreCase(locales.get(player, "gui.global.title"))) {
            event.setCancelled(true);
            return;
        }

        // Ranking Hauptmenü
        if (title.equalsIgnoreCase(locales.get(player, "gui.ranking.title"))) {
            event.setCancelled(true);
            switch (slot) {
                case 10 -> rankingMenu.openTopList(player, "placed");   // ✅ statt "blocks"
                case 11 -> rankingMenu.openTopList(player, "pvp");
                case 12 -> rankingMenu.openTopList(player, "mobs");
                case 13 -> rankingMenu.openTopList(player, "deaths");
                case 14 -> rankingMenu.openTopList(player, "playtime");
                case 15 -> rankingMenu.openTopList(player, "chat");
            }
            return;
        }


        // Untermenüs
        for (String type : List.of("placed", "pvp", "mobs", "deaths", "playtime", "chat")) {
            String expected = locales.get(player, "gui.ranking.title_" + type);
            if (title.equalsIgnoreCase(expected)) {
                event.setCancelled(true);
                if (slot == 49) {
                    rankingMenu.openMainMenu(player);  // ← Back funktioniert wieder!
                }
                return;
            }
        }

        }
    }
