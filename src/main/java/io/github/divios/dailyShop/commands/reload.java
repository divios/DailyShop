package io.github.divios.dailyShop.commands;

import com.cryptomorin.xseries.messages.Titles;
import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.parser.ParserApi;
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
        /*DailyShop.getInstance().reloadPlugin();
        if (sender instanceof Player) Titles.sendTitle((Player) sender, 25, 40, 25,
                FormatUtils.color("&a&lPlugin Reloaded"), "");
        else sender.sendMessage("Plugin Reloaded"); */

        long[] totalJsonTime = {0};
        long[] totalBukkitTime = {0};

        int total[] = {0};

        shopsManager.getInstance().getShop("ore").get().getItems().forEach(item -> {
            Timer timer = Timer.create();
            dItem.serializeOptions().json().toJson(item);
            timer.stop();
            totalJsonTime[0] += timer.getTime();

            timer = Timer.create();
            dItem.serializeOptions().bukkit().serialize(item);
            timer.stop();
            totalBukkitTime[0] += timer.getTime();
            total[0]++;
        });

        Log.info("Total json timer " + (double) totalJsonTime[0] / total[0] + " ms");
        Log.info("Total bukkit timer " + (double) totalBukkitTime[0] / total[0] + " ms");

        shopsManager.getInstance().saveAllShops();
    }
}
