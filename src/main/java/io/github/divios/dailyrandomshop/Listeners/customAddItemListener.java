package io.github.divios.dailyrandomshop.Listeners;


import io.github.divios.dailyrandomshop.DailyRandomShop;
import io.github.divios.dailyrandomshop.GUIs.customizerItem.customizerMainGuiIH;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class customAddItemListener implements Listener {

    private final DailyRandomShop main;
    private final Player p;
    private final BukkitTask TaskID;

    public customAddItemListener(DailyRandomShop main, Player p) {
        Bukkit.getPluginManager().registerEvents(this, main);
        this.main = main;
        this.p = p;

       TaskID = Bukkit.getScheduler().runTaskLater(main, () -> {
                p.sendMessage(main.config.PREFIX + ChatColor.GRAY + "Ey! The time to select an item expired, try it again");
                PlayerInteractEvent.getHandlerList().unregister(this);
                p.openInventory(main.DailyGuiSettings.getFirstGui());
        }, 200);

        try {
            p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "Click item",
                    ChatColor.GRAY + "In hand to add it", 20, 60, 20);
        } catch (NoSuchMethodError e) {
            p.sendMessage(main.config.PREFIX + ChatColor.GRAY+ "Click item in hand to add it");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    private void OnPlayerClick(PlayerInteractEvent e) {

        if (e.getPlayer() != p) return;

        e.setCancelled(true);
        ItemStack item = e.getItem().clone();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        item.setAmount(1);

        new customizerMainGuiIH(main, p, item, null);
        Bukkit.getScheduler().cancelTask(TaskID.getTaskId());
        PlayerInteractEvent.getHandlerList().unregister(this);

    }



}
