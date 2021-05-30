package io.github.divios.dailyrandomshop.guis.customizerguis;

import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class miniCustomizeGui implements Listener, InventoryHolder{

        private final Player p;
        private final ItemStack item;
        private final Consumer<ItemStack> consumer;
        private final Inventory inventory = getInventory();

        public miniCustomizeGui(Player p,
                              ItemStack item,
                              Consumer<ItemStack> consumer) {
            this.p = p;
            this.item = item;
            this.consumer = consumer;

            consumer.accept(XMaterial.GRASS_PATH.parseItem());
        }


    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, "");

        ItemStack changeName = XMaterial.NAME_TAG.parseItem();
        utils.setDisplayName(changeName, "&b&lChange name of the item");

        ItemStack changeMaterial = XMaterial.SLIME_BALL.parseItem();
        utils.setDisplayName(changeMaterial, "&b&lChange material");

        return inv;
    }

}
