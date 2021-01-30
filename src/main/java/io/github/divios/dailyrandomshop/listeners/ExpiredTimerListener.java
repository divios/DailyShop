package io.github.divios.dailyrandomshop.listeners;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.events.expiredTimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ExpiredTimerListener implements Listener {

    private static io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static ExpiredTimerListener instance = null;

    private ExpiredTimerListener() {};
    public static ExpiredTimerListener getInstance() {
        if(instance == null) {
            instance = new ExpiredTimerListener();
            Bukkit.getPluginManager().registerEvents(instance, main);
        }
        return instance;
    }

    @EventHandler
    public void onTimerExpired(expiredTimerEvent e) {
        main.getServer().broadcastMessage(conf_msg.PREFIX + conf_msg.MSG_NEW_DAILY_ITEMS);
        /* DO STUFF HERE, RENOVATE ITEMS */
    }

}
