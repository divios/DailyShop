package io.github.divios.lib.itemHolder;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.divios.dailyrandomshop.economies.economy;
import io.github.divios.dailyrandomshop.economies.vault;
import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class dItem implements Serializable {

    private NBTItem item;
    private String shop = ""; // TODO: not sure

    public dItem(@NotNull ItemStack item) {
        this.item = new NBTItem(item);
        setUid(UUID.randomUUID());
        setRarity(new dRarity());       //Defaults to Common
        setConfirm_gui(true);           // Defaults true
        setEconomy(new vault());        // Default Vault
    }

    private dItem() {}

    /**
     *
     * @return the ItemStack that this instance holds
     */
    public ItemStack getItem() {
        return item.getItem();
    }

    /**
     *
     * @param item the new item to be held by this instance
     */
    public void setItem(@NotNull ItemStack item) {
        this.item = new NBTItem(item);
        if (getUid() != null)
            setUid(UUID.randomUUID());
    }

    /**
     * Sets the display name of the item
     * @param name
     */
    public void setDisplayName(@NotNull String name) {
        ItemStack itemA = getItem();
        utils.setDisplayName(itemA, name);
        setItem(itemA);
    }

    /**
     * Sets the lore of the item
     * @param lore
     */
    public void setLore(@NotNull List<String> lore) {
        ItemStack itemA = getItem();
        utils.setLore(itemA, lore);
        setItem(itemA);
    }

    /**
     * Sets the enchants of the item
     * @param enchants
     */
    public void setEnchantments(@NotNull List<Enchantment> enchants) {

    }

    /**
     * Sets the amount of the item
     * @param amount
     */
    public void setAmount(int amount) {
        ItemStack auxI = item.getItem();
        auxI.setAmount(amount);
        this.item = new NBTItem(auxI);
    }

    /**
     * Gets the amount of the item
     * @return
     */
    public int getAmount() {
        return item.getItem().getAmount();
    }

    /**
     *
     * @return the price of the item. Can be random price between the values asigned
     */
    public double getBuyPrice() {
        return item.getObject("rds_buyPrice", dPrice.class)
                .generateRandomPrice();
    }

    /**
     *  Set the price of the item as a fixed value
     *
     * @param price Fixed price for the item
     */
    public void setBuyPrice(double price) {
        item.setObject("rds_buyPrice", new dPrice(price));
    }

    /**
     * Set the price of the item as a random value between minPrice and maxPrice
     *
     * @param minPrice lower limit price
     * @param maxPrice upper limit price
     */
    public void setBuyPrice(double minPrice, double maxPrice) {
        item.setObject("rds_buyPrice", new dPrice(minPrice, maxPrice));
    }

    /**
     *
     * @return the price of the item. Can be random price between the values asigned
     */
    public double getSellPrice() {
        return item.getObject("rds_sellPrice", dPrice.class)
                .generateRandomPrice();
    }

    /**
     *  Set the price of the item as a fixed value
     *
     * @param price Fixed price for the item
     */
    public void setSellPrice(double price) {
        item.setObject("rds_sellPrice", new dPrice(price));
    }

    /**
     * Set the price of the item as a random value between minPrice and maxPrice
     *
     * @param minPrice lower limit price
     * @param maxPrice upper limit price
     */
    public void setSellPrice(double minPrice, double maxPrice) {
        item.setObject("rds_sellPrice", new dPrice(minPrice, maxPrice));
    }

    /**
     * Gets the uuid of the item
     *
     * @return the uuid of this item
     */
    public UUID getUid() {
        return UUID.fromString(item.getString("rds_UUID"));
    }


    public static UUID getUid(ItemStack item) {
        return new NBTItem(item).getString("rds_UUID");
    }

    /**
     * Sets uuid
     * @param uid
     */
    private void setUid(UUID uid) {
        item.setString("rds_UUID", uid.toString());
    }

    /**
     * Set the stock of the item
     *
     * @param stock the stock to set
     */
    public void setStock(@Nullable Integer stock) {
        item.setInteger("rds_stock", stock);
    }

    /**
     * Returns the stock of the item
     *
     * @return returns the stock of the item. Can be null and means that
     * the feature is disabled
     */
    public @Nullable Integer getStock() {
        return item.getInteger("rds_stock");
    }

    /**
     * Sets the rarity of the item
     *
     * @param rarity rarity to set, can be null
     */
    public void setRarity(@NotNull dRarity rarity) {
        item.setObject("rds_rarity", rarity);
    }

    /**
     * Gets the rarity of the item
     *
     * @return an integer symbolizing a rarity. Use utils to format to itemStack or String
     */
    public @NotNull dRarity getRarity() {
        return item.getObject("rds_rarity", dRarity.class);
    }

    /**
     * Set the next Rarity
     */
    public void nextRarity() {
        setRarity(getRarity().next());
    }

    /**
     * Gets the economy of this item
     *
     * @return
     */
    public economy getEconomy() {
        return item.getObject("rds_econ", economy.class);
    }

    /**
     * Set an economy for this item
     *
     * @param econ
     */
    public void setEconomy(@NotNull economy econ) {
        item.setObject("rds_econ", econ);
    }

    /**
     * Gets commands to run when this item is bought
     *
     * @return list of Strings representing commands
     */
    public @Nullable List<String> getCommands() {
        return item.getObject("rds_cmds", List.class);
    }

    /**
     * Sets the commands to run when this item is bought
     *
     * @param commands a list of Strings representing commands
     */
    public void setCommands(@Nullable List<String> commands) {
        item.setObject("rds_cmds", commands);
    }

    /**
     * Gets permissions that a player needs to buy/sell this item
     *
     * @return list of Strings representing permissions
     */
    public @Nullable List<String> getPerms() {
        return item.getObject("rds_perms", List.class);
    }

    /**
     * Sets the permission that a player needs to buy/sell this item
     *
     * @param perms a list of Strings representing permissions
     */
    public void setPerms(@Nullable List<String> perms) {
        item.setObject("rds_perms", perms);
    }

    /**
     * Gets if confirm_Gui is enable for this item
     *
     * @return true if enabled; false is disabled
     */
    public boolean getConfirm_gui() {
        return item.getBoolean("rds_confirm_gui");
    }

    /**
     * Enable/disable confirm_Gui for this item
     *
     * @param b true to enable; false to disable
     */
    public void setConfirm_gui(boolean b) {
        item.setBoolean("rds_confirm_gui", b);
    }

    /**
     * Toggles the value of Confirm_GUI
     */
    public void toggleConfirm_gui() {
        setConfirm_gui(!getConfirm_gui());
    }

    /**
     * Gets the amount for the set of items
     *
     * @return
     */
    public @Nullable Integer getSetItems() {
        return item.getInteger("rds_setItems");
    }

    /**
     * Set amount for the set of items
     * @param setItems
     */
    public void setSetItems(@Nullable Integer setItems) {
        item.setInteger("rds_setItems", setItems);
    }

    /**
     * Gets a list of uuid, which represents the uuids of items in this bundle
     *
     * @return null if disabled.
     */
    public @Nullable List<UUID> getBundle() {
        return item.getObject("rds_bundle", List.class);
    }

    /**
     * Sets uuid of the items that this bundle has
     *
     * @param bundle null if want to disabled it
     */
    public void setBundle(@Nullable List<UUID> bundle) {
        item.setObject("rds_bundle", bundle);
    }

    /**
     * Gets item serializable as base64
     * @return
     */
    public String getItemSerial() {
        NBTCompound itemData = NBTItem.convertItemtoNBT(item.getItem());
        return Base64.getEncoder().encodeToString(itemData.toString().getBytes());
    }

    /**
     * Constructs dItem from base 64
     * @param base64
     * @return dItem constructed
     */
    public static dItem constructFromBase64(String base64) {
        NBTCompound itemData = new NBTContainer(new String(Base64.getDecoder().decode(base64)));
        ItemStack item = NBTItem.convertNBTtoItem(itemData);

        dItem newItem = new dItem();
        newItem.setItem(item);

        return newItem;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof dItem))
            return false;
        return this.getUid().equals(((dItem) o).getUid());
    }

    @Override
    public int hashCode() {
        return getUid().hashCode();
    }


}
