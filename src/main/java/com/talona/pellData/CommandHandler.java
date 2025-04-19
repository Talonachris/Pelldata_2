package com.talona.pellData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {

    private final DatabaseManager db;
    private final LocalesManager locales;
    private final StatsMenu statsMenu;
    private final RankingMenu rankingMenu;
    private final GlobalStatsMenu globalStatsMenu;

    public CommandHandler(DatabaseManager db, LocalesManager locales) {
        this.db = db;
        this.locales = locales;
        this.statsMenu = new StatsMenu(locales, db);
        this.rankingMenu = new RankingMenu(db, locales);
        this.globalStatsMenu = new GlobalStatsMenu(db, locales);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(locales.get(null, "command.no_player"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("stats")) {
            statsMenu.openMainMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("globalstats")) {
            globalStatsMenu.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("ranking")) {
            if (args.length == 1) {
                rankingMenu.openMainMenu(player);
            } else {
                String type = args[1].toLowerCase();
                rankingMenu.openTopList(player, type);
            }
            return true;
        }

        player.sendMessage(locales.get(player, "command.help.header"));
        player.sendMessage(locales.get(player, "command.help.stats"));
        player.sendMessage(locales.get(player, "command.help.global"));
        player.sendMessage(locales.get(player, "command.help.ranking"));
        return true;
    }
}
