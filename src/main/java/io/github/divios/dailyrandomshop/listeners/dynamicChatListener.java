package io.github.divios.dailyrandomshop.listeners;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.Titles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class dynamicChatListener implements Listener {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    private final Player p;
    private final Consumer<String> c;
    private final BukkitTask TaskID;

    public dynamicChatListener(Player p, Consumer<String> c, String title, String subtitle) {
        this.p = p;
        this.c = c;
        Bukkit.getPluginManager().registerEvents(this, main);

        TaskID = Bukkit.getScheduler().runTaskLater(main, () -> {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_TIMER_EXPIRED);
            AsyncPlayerChatEvent.getHandlerList().unregister(this);
            c.accept("");
        }, 400);

        p.closeInventory();
        Titles.sendTitle(p, 20, 60, 20,
                title, subtitle);

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatListener(AsyncPlayerChatEvent e) {

        if (e.getPlayer() != p) return;
        if(e.getMessage().isEmpty()) return;

        e.setCancelled(true);

        Titles.clearTitle(p);
        Bukkit.getScheduler().runTaskLater(main, () -> c.accept(e.getMessage()), 1);
        Bukkit.getScheduler().cancelTask(TaskID.getTaskId());
        AsyncPlayerChatEvent.getHandlerList().unregister(this);

    }



}
