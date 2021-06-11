package io.github.divios.lib.itemHolder;


import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.XCore.XMaterial;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyrandomshop.economies.economy;
import io.github.divios.dailyrandomshop.economies.vault;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class dItem implements Serializable, Cloneable {

    private NBTItem item;
    private String shop = ""; // TODO: not sure

    public dItem(@NotNull ItemStack item) {
        this(item, -1);
    }

    public dItem(@NotNull ItemStack item, int slot) {
        setItem(item);
        setSlot(slot);
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
        if (getUid() == null)
            setUid(UUID.randomUUID());
    }

    /**
     * Sets the slot of this item
     * @param slot
     */
    public void setSlot(int slot) { item.setInteger("dailySlots", slot); }

    /**
     * Gets the slot of this item
     * @return
     */
    public int getSlot() { return item.getInteger("dailySlots"); }

    /**
     * Sets the meta of the item
     * @param meta
     */
    public void setMeta(ItemMeta meta) {
        ItemStack itemA = getItem();
        itemA.setItemMeta(meta);
        setItem(itemA);
    }

    /**
     * Sets the display name of the item
     * @param name
     */
    public void setDisplayName(@NotNull String name) {
        setItem(new ItemBuilder(getItem()).setName(name));
    }

    /**
     * Gets the displayName of the item
     * @return
     */
    public String getDisplayName() {
        return getItem().getItemMeta().getDisplayName();
    }

    /**
     * Sets the lore of the item. Supports Color Codes
     * @param lore
     */
    public void setLore(@NotNull List<String> lore) {
        setItem(new ItemBuilder(getItem()).setLore(lore));
    }

    /**
     * Gets the lore of the item
     * @return
     */
    public @NotNull List<String> getLore() {
        return ItemUtils.getLore(getItem());
    }

    /**
     * Sets the material of the item
     * @param m
     */
    public void setMaterial(Material m) {
        ItemStack item = getItem();
        item.setType(m);
        setItem(item);
    }

    /**
     * Gets the material of the item
     * @return
     */
    public Material getMaterial() {
        return getItem().getType();
    }

    /**
     * Sets the durability of the item
     * @param durability
     */
    public void setDurability(short durability) {
        ItemStack item = getItem();
        item.setDurability((short) (item.getType().getMaxDurability() - durability));
        setItem(item);
    }

    /**
     * Gets the durability of the item
     * @return
     */
    public short getDurability() {
        return getItem().getDurability();
    }

    /**
     * Adds enchantment to item
     * @param ench
     */
    public void addEnchantments(@NotNull Enchantment ench, int lvl) {
        ItemStack item = getItem();
        item.addUnsafeEnchantment(ench, lvl);
        setItem(item);
    }

    /**
     * Removes enchantment from item
     * @param ench
     */
    public void removeEnchantments(@NotNull Enchantment ench) {
        ItemStack item = getItem();
        item.removeEnchantment(ench);
        setItem(item);
    }

    /**
     * gets a map containing all the enchants of this item
     * @return
     */
    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        return getItem().getEnchantments();
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
     * Return if the item has a flag
     * @param flag
     * @return
     */
    public boolean hasFlag(ItemFlag flag) {
        return ItemUtils.hasItemFlags(getItem(), flag);
    }

    /**
     * Toggles a flag from the item
     * @param flag
     */
    public void toggleFlag(ItemFlag flag) {
        ItemStack aux = getItem();

        if (ItemUtils.hasItemFlags(aux, flag))
            ItemUtils.removeItemFlags(aux, flag);
        else
            ItemUtils.addItemFlags(aux, flag);

        setItem(aux);
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
    public @Nullable UUID getUid() {
        return item.getObject("rds_UUID", UUID.class);
    }


    public static UUID getUid(ItemStack item) {
        return new NBTItem(item).getObject("rds_UUID", UUID.class);
    }

    /**
     * Sets uuid
     * @param uid
     */
    private void setUid(UUID uid) {
        item.setObject("rds_UUID", uid);
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
        if (!item.hasKey("rds_stock"))
            return null;
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
    public @NotNull economy getEconomy() {
        economy econR = new vault();
        byte [] data = Base64.getDecoder().decode(item.getString("rds_econ"));
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new ByteArrayInputStream(data));
            econR = (economy) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return econR;
    }

    /**
     * Set an economy for this item
     *
     * @param econ
     */
    public void setEconomy(@NotNull economy econ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject(econ);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        item.setString("rds_econ", Base64.getEncoder().encodeToString(baos.toByteArray()));
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
        if (!item.hasKey("rds_setItems"))
            return null;
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

    private void setAIR() { item.setBoolean("rds_AIR", true); }

    public boolean isAIR() { return item.hasKey("rds_AIR"); }

    /**
     * Gets item serializable as base64
     * @return
     */
    public String serialize() {
        NBTCompound itemData = NBTItem.convertItemtoNBT(item.getItem());
        return Base64.getEncoder().encodeToString(itemData.toString().getBytes());
    }

    /**
     * Constructs dItem from base 64
     * @param base64
     * @return dItem constructed
     */
    public static dItem deserialize(String base64) {
        NBTCompound itemData = new NBTContainer(new String(Base64.getDecoder().decode(base64)));
        ItemStack item = NBTItem.convertNBTtoItem(itemData);

        dItem newItem = new dItem();
        newItem.setItem(item);

        return newItem;
    }

    /**
     * Returns a deep copy of the object
     * @return
     */
    public dItem clone() {
        return deserialize(serialize());
    }

    public static dItem empty() {
        dItem empty = new dItem(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE)
                .setName("&c").addItemFlags(ItemFlag.HIDE_ENCHANTS)
                .addEnchant(Enchantment.DAMAGE_ALL, 1));

        empty.setAIR();
        return empty;
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


    //>>>>>> Serialize stuff <<<<<<//
    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(this.serialize());
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        NBTCompound itemData = new NBTContainer(new String(Base64.getDecoder()
                .decode((String) in.readObject())));
        ItemStack item = NBTItem.convertNBTtoItem(itemData);
        setItem(item);
    }

    private void readObjectNoData()
            throws ObjectStreamException {

    }


}
