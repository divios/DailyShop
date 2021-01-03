package io.github.divios.dailyrandomshop.Listeners;


import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.customizerItem.customizerMainGuiIH;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class customAddItemListener implements Listener {

    private final DailyRandomShop main;
    private final Player p;
    private boolean terminated = false;

    public customAddItemListener(DailyRandomShop main, Player p) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
        this.p = p;

        Bukkit.getScheduler().runTaskLaterAsynchronously(main, () -> {
            if(!terminated) {
                p.sendMessage(main.config.PREFIX + ChatColor.GRAY + "Timer expired");
                PlayerInteractEvent.getHandlerList().unregister(this);
            }
        }, 200);

        try {
            p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Click item",
                    ChatColor.GRAY + "In hand to add it", 1, 3, 1);
        } catch (NoSuchMethodError e) {
            p.sendMessage(main.config.PREFIX + ChatColor.GRAY+ "Click item in hand to add it");
        }
    }

    @EventHandler
    private void OnPlayerClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();

        if (e.getPlayer() != p) return;

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        e.setCancelled(true);

        new customizerMainGuiIH(main, p, item, null);
        PlayerInteractEvent.getHandlerList().unregister(this);
        terminated = true;

    }



}
