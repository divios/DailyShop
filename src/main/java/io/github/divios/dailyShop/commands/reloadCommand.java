package io.github.divios.dailyShop.commands;

import com.cryptomorin.xseries.messages.Titles;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.jcommands.JCommand;
import org.bukkit.entity.Player;

public class reloadCommand {

    public JCommand getCommand() {
        return JCommand.create("reload")
                .assertPermission("DailyRandomShop.reload")
                .assertUsage(FormatUtils.color("&8- &6/rdshop reload &8- &7Reload plugin"))
                .executes((commandSender, values) -> {
                    DailyShop.get().reload();
                    if (commandSender instanceof Player)
                        Titles.sendTitle((Player) commandSender, 25, 40, 25,
                                FormatUtils.color("&a&lPlugin Reloaded"), "");
                    else
                        commandSender.sendMessage("Plugin Reloaded");
                });
    }

}
