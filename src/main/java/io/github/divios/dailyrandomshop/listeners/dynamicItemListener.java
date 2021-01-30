package io.github.divios.dailyrandomshop.listeners;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.guis.customizerguis.customizerMainGuiIH;
import io.github.divios.dailyrandomshop.guis.settings.dailyGuiSettings;
import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.Titles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class dynamicItemListener implements Listener {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private final Player p;
    private final BukkitTask TaskID;
    
    public dynamicItemListener(Player p) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.p = p;

        TaskID = Bukkit.getScheduler().runTaskLater(main, () -> {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_TIMER_EXPIRED);
            PlayerInteractEvent.getHandlerList().unregister(this);
            dailyGuiSettings.openInventory(p);
        }, 200);

        /*try {
            p.sendTitle(conf_msg.MSG_ADD_ITEM_TITLE,
                    conf_msg.MSG_ADD_ITEM_SUBTITLE, 20, 60, 20);
        } catch (NoSuchMethodError e) {
            p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_ADD_ITEM_TITLE + conf_msg.MSG_ADD_ITEM_SUBTITLE);
        }*/

        Titles.sendTitle(p, 20, 60, 20, conf_msg.MSG_ADD_ITEM_TITLE, conf_msg.MSG_ADD_ITEM_SUBTITLE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void OnPlayerClick(PlayerInteractEvent e) {

        if (e.getPlayer() != p) return;

        e.setCancelled(true);

        if (utils.isEmpty(e.getItem())) {
            return;
        }

        ItemStack item = e.getItem().clone();
        item.setAmount(1);
        item.setDurability((short) 0);

        Titles.clearTitle(p);
        Bukkit.getScheduler().runTaskLater(main, () ->
                        customizerMainGuiIH.openInventory(p, item, null)
                , 1L);

        Bukkit.getScheduler().cancelTask(TaskID.getTaskId());
        PlayerInteractEvent.getHandlerList().unregister(this);


    }

}
