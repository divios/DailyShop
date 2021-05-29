package io.github.divios.lib.itemHolder;

import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class dGui implements InventoryHolder, Listener {

    private Inventory inv;
    String title = "";  // for some reason is throwing noSuckMethod
    private boolean available = true;

    public dGui(String base64) {
        inv = deserialize(base64);
    }

    public dGui(String title, @NotNull List<ItemStack> items) {

        this.title = title;
        inv = Bukkit.createInventory(this,
                !items.isEmpty() ? Math.round(items.size() % 9): 9, title);
        items.forEach(item -> inv.addItem(item));
    }

    public dGui() { inv = Bukkit.createInventory(this, 9, title); }

    /**
     * Open the inventorty for the given player
     * @param p
     */
    public void open(Player p) {
        p.openInventory(inv);
    }

    /**
     * Closes the inventory for all the viewers
     */
    public void closeAll() {
        try {
            inv.getViewers().forEach(HumanEntity::closeInventory);
        } catch (Exception ignored) {}
    }

    public void setAvailable(boolean status) {
        available = status;
    }

    public boolean getAvailable() {
        return available;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    /**
     * Updates the inventory from a base64
     * @param base64
     */
    public void updateInventory(String base64) {
        inv = deserialize(base64);
    }

    /**
     * Sets this instance inventory holder
     * @param inv
     */
    public void updateInventory(Inventory inv) {
        this.inv.clear();
        Arrays.stream(inv.getContents()).iterator()
                .forEachRemaining(item -> this.inv.addItem(item));
    }

    /**
     * Gets the inventory serialized
     * @return the serialized inventory in base64
     */
    public String serialize() {
        String base64 = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream BOOS = new BukkitObjectOutputStream(byteArrayOutputStream);
            BOOS.writeObject(title);
            BOOS.writeInt(inv.getSize());
            for (ItemStack item : inv.getContents()) {
                BOOS.writeObject(item);
            }
            BOOS.close();
            base64 = Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    /**
     * Deserializes the given base64 to an inventory
     * @param base64 base64 to deserialize
     * @return the new inventory
     */
    private Inventory deserialize(String base64) {
        Inventory inventory = null;

        BukkitObjectInputStream BOIS = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BOIS = new BukkitObjectInputStream(byteArrayInputStream);

            title = (String) BOIS.readObject();
            inventory = Bukkit.getServer().createInventory(this, BOIS.readInt(), title);

            while (true) {
                ItemStack item = (ItemStack) BOIS.readObject();
                if (utils.isEmpty(item)) continue;
                inventory.addItem(item);
            }

        } catch (IOException | ClassNotFoundException e) {
            return inventory;
        } finally {
            try {
                BOIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onDragEvent(InventoryDragEvent e) {
        if (e.getInventory().getHolder() != this) return;

        e.setCancelled(true);
    }

}