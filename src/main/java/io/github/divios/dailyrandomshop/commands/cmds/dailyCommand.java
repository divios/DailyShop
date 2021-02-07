package io.github.divios.dailyrandomshop.commands.cmds;

import org.bukkit.entity.Player;

import java.util.List;

public interface dailyCommand {
    void run(Player p);
    void help(Player p);
    void command(Player p, List<String> s);
}
