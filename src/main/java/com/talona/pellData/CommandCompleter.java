package com.talona.pellData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandCompleter implements TabCompleter {

    private static final List<String> MAIN_ARGS = Arrays.asList("stats", "globalstats", "ranking");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (args.length == 1) {
            for (String arg : MAIN_ARGS) {
                if (arg.startsWith(args[0].toLowerCase())) {
                    result.add(arg);
                }
            }
        }

        return result;
    }
}
