package io.github.divios.dailyrandomshop.commands.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface dailyCommand {
    /*
       Runs the command itself
     */
    void run(CommandSender p);

    /*
        Sends the help message corresponded to that command
     */

    void help(Player p);

    /*
        For tab complete uses
     */
    void command(Player p, List<String> s);
}
