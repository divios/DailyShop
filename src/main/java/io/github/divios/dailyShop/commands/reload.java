package io.github.divios.dailyShop.commands;

import com.cryptomorin.xseries.messages.Titles;
import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.dailyShop.DailyShop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class reload extends abstractCommand {

    public reload() {
        super(cmdTypes.BOTH);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public boolean validArgs(List<String> args) {
        return true;
    }

    @Override
    public String getHelp() {
        return FormatUtils.color("&8- &6/rdshop reload&8 " +
                "- &7Reload plugin");
    }

    @Override
    public List<String> getPerms() {
        return Collections.singletonList("DailyRandomShop.reload");
    }

    @Override
    public List<String> getTabCompletition(List<String> args) {
        return Collections.emptyList();
    }

    @Override
    public void run(CommandSender sender, List<String> args) {
        DailyShop.getInstance().reloadPlugin();
        if (sender instanceof Player) Titles.sendTitle((Player) sender, 25, 40, 25,
                FormatUtils.color("&a&lPlugin Reloaded"), "");
        else sender.sendMessage("Plugin Reloaded");
    }
}
