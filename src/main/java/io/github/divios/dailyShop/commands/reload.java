package io.github.divios.dailyShop.commands;

import com.cryptomorin.xseries.messages.Titles;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonElement;
import io.github.divios.core_lib.commands.abstractCommand;
import io.github.divios.core_lib.commands.cmdTypes;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Timer;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

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

        /*
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

        long[] totalJsonTime = {0};
        long[] totalBukkitTime = {0};
        long[] totalNBTTime = {0};

        int total[] = {0};

        CompletableFuture.runAsync(() -> {
            IntStream.range(0, 50000).forEach(value -> {
                Log.info("Concurrency: " + value);
                shopsManager.getInstance().getShop("ore").get().getItems().forEach(item -> {
                    Timer timer = Timer.create();
                    JsonElement element = dItem.serializeOptions().json().toJson(item);
                    dItem.serializeOptions().json().fromJson(element);
                    timer.stop();
                    totalJsonTime[0] += timer.getTime();

                    timer = Timer.create();
                    String s = dItem.serializeOptions().bukkit().serialize(item);
                    dItem.serializeOptions().bukkit().deserialize(s);
                    timer.stop();
                    totalBukkitTime[0] += timer.getTime();

                    timer = Timer.create();
                    String s1 = item.toBase64();
                    dItem.fromBase64(s1);
                    timer.stop();
                    totalNBTTime[0] += timer.getTime();

                    total[0]++;
                });
            });
        }).thenAccept(unused -> {
            Log.info("Total json timer " + (double) totalJsonTime[0] / total[0] + " ms");
            Log.info("Total bukkit timer " + (double) totalBukkitTime[0] / total[0] + " ms");
            Log.info("Total NBT timer " + (double) totalNBTTime[0] / total[0] + " ms");
        });*/

    }
}
