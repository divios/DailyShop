package io.github.divios.lib.dLib;


import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonElement;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.cache.Lazy;
import io.github.divios.core_lib.gson.GsonSerializable;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.economies.vault;
import io.github.divios.dailyShop.utils.MMOUtils;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class dItem implements Serializable, Cloneable {

    private static final long serialVersionUID = 6529685098267757690L;  // Avoid problems with serialization
    private static final DailyShop plugin = DailyShop.getInstance();

    private NBTItem item;
    private dStock stock = null;
    private Lazy<ItemStack> rawItem;

    public static dItem of(ItemStack item) {
        return new dItem(item);
    }

    public static dItem of(ItemStack item, String id) {
        return new dItem(item, id);
    }

    public static dItem of(ItemStack item, String id, int slot) {
        return new dItem(item, id, slot);
    }

    public dItem(@NotNull ItemStack item) {
        this(item, -1);
    }

    public dItem(@NotNull ItemStack item, int slot) {
        this(item, UUID.randomUUID().toString(), slot);
    }

    public dItem(ItemStack item, String id) {
        this(item, id, -1);
    }

    public dItem(ItemStack item, String id, int slot) {
        setItem(item, id, slot);
    }

    private dItem() {
    }

    /**
     * @return the ItemStack that this instance holds
     */
    public ItemStack getItem() {
        saveStock();
        return item.getItem();
    }

    private void setRawItem(@NotNull ItemStack rawItem) {
        item.setString("rds_rawItem", ItemUtils.serialize(rawItem));
        this.rawItem = Lazy.suppliedBy(() -> ItemUtils.deserialize(item.getString("rds_rawItem")));
    }

    public ItemStack getRawItem() {
        return getRawItem(false);
    }

    /**
     * Gets the raw item, this is, the item's held
     * by this instance without all the daily metadata
     *
     * @return
     */
    public ItemStack getRawItem(boolean getAsNewItem) {

        if (getAsNewItem && MMOUtils.isMMOItemsOn() && MMOUtils.isMMOItem(rawItem.get().clone())) {
            try {
                return MMOUtils.createNewMMOItem(rawItem.get().clone());
            } catch (Exception e) {
                return rawItem.get();
            }
        }

        return rawItem.get();
    }

    /**
     * @param item the new item to be held by this instance
     */
    public void setItem(@NotNull ItemStack item, String id, int slot) {
        this.item = new NBTItem(item);
        if (getID() == null || getID().isEmpty()) {
            Log.info("oke");
            setRawItem(item);
            setID(id);
            setSlot(slot);
            setRarity(new dRarity());       //Defaults to Common
            setConfirm_gui(true);           // Defaults true
            setEconomy(new vault());        // Default Vault
            setBuyPrice(plugin.configM.getSettingsYml().DEFAULT_BUY); // Default buy price
            setSellPrice(plugin.configM.getSettingsYml().DEFAULT_SELL); // Default sell price
        }

        rawItem = Lazy.suppliedBy(() -> ItemUtils.deserialize(this.item.getString("rds_rawItem")));
        migratePerms();
        stock = retrieveStock();
    }

    public void setItem(@NotNull ItemStack item) {
        setItem(item, null, -1);
    }

    /**
     * Private method to transfer all daily item meta
     *
     * @param item
     */
    private ItemStack copyAllMetadata(@NotNull ItemStack item) {
        dItem transfer = dItem.of(item);
        transfer.setID(getID());
        transfer.setDisplayName(getDisplayName());
        transfer.setLore(getLore());
        getAction().stream(transfer::setAction);
        transfer.setStock(getStock());
        transfer.setSetItems(getSetItems().get());
        transfer.setQuantity(getQuantity());
        transfer.setBundle(getBundle().get());
        transfer.setBuyPrice(getBuyPrice().get());
        transfer.setSellPrice(getSellPrice().get());
        transfer.setEconomy(getEconomy());
        transfer.setDurability(getDurability(), false);
        transfer.setRarity(getRarity());
        transfer.setPermsBuy(getPermsBuy().get());
        transfer.setPermsSell(getPermsSell().get());
        transfer.setConfirm_gui(isConfirmGuiEnabled());

        return transfer.getItem();
    }

    /**
     * Sets the slot of this item
     *
     * @param slot
     */
    public void setSlot(int slot) {
        item.setInteger("dailySlots", slot);
    }

    /**
     * Gets the slot of this item
     *
     * @return
     */
    public int getSlot() {
        return item.getInteger("dailySlots");
    }

    /**
     * Sets the meta of the item
     *
     * @param meta
     */
    public void setMeta(ItemMeta meta) {
        ItemStack itemA = getItem();
        itemA.setItemMeta(meta);
        setItem(itemA);
    }

    /**
     * Sets the display name of the item
     *
     * @param name
     */
    public void setDisplayName(@NotNull String name) {
        setItem(ItemUtils.setName(getItem(), name));
        setRawItem(ItemUtils.setName(getRawItem(), name));
    }

    /**
     * Gets the displayName of the item
     *
     * @return
     */
    public String getDisplayName() {
        return utils.isEmpty(ItemUtils.getName(getItem())) ?
                getItem().getType().name() :
                ItemUtils.getName(getItem());
    }

    /**
     * Sets the lore of the item. Supports Color Codes
     *
     * @param lore
     */
    public void setLore(@NotNull List<String> lore) {
        setItem(ItemUtils.setLore(getItem(), lore));
        setRawItem(ItemUtils.setLore(getRawItem(), lore));
    }

    /**
     * Gets the lore of the item
     *
     * @return
     */
    public @NotNull
    List<String> getLore() {
        return ItemUtils.getLore(getItem());
    }

    /**
     * Sets the material of the item
     *
     * @param m
     */
    public void setMaterial(@NotNull XMaterial m) {
        setItem(ItemUtils.setMaterial(getItem(), m));
        setRawItem(ItemUtils.setMaterial(getRawItem(), m));
        if (m.name().contains("GLASS"))
            setDurability(m.parseItem().getDurability(), true);

    }

    /**
     * Gets the material of the item
     *
     * @return
     */
    public @NotNull
    Material getMaterial() {
        return getItem().getType();
    }

    /**
     * Sets the durability of the item
     *
     * @param durability
     */
    public void setDurability(short durability, boolean glass) {
        if (!glass) {
            setItem(ItemUtils.setDurability(getItem(), (short) (getItem().getType().getMaxDurability() - durability)));
            setRawItem(ItemUtils.setDurability(getRawItem(), (short) (getRawItem().getType().getMaxDurability() - durability)));
        } else {
            setItem(ItemUtils.setDurability(getItem(), durability));
            setRawItem(ItemUtils.setDurability(getRawItem(), durability));
        }
    }

    /**
     * Gets the durability of the item
     *
     * @return
     */
    public short getDurability() {
        return getItem().getDurability();
    }

    /**
     * Adds enchantment to item
     *
     * @param ench
     */
    public void addEnchantments(@NotNull Enchantment ench, int lvl) {
        setItem(ItemUtils.addEnchant(getItem(), ench, lvl));
        setRawItem(ItemUtils.addEnchant(getRawItem(), ench, lvl));
    }

    /**
     * Removes enchantment from item
     *
     * @param ench
     */
    public void removeEnchantments(@NotNull Enchantment ench) {
        setItem(ItemUtils.removeEnchant(getItem(), ench));
        setRawItem(ItemUtils.removeEnchant(getRawItem(), ench));

    }

    /**
     * gets a map containing all the enchants of this item
     *
     * @return
     */
    public @NotNull
    Map<Enchantment, Integer> getEnchantments() {
        return getItem().getEnchantments();
    }

    /**
     * Sets the amount of the item
     *
     * @param amount
     */
    public void setQuantity(int amount) {
        ItemStack auxI = getItem();
        auxI.setAmount(amount);
        ItemStack auxE = getRawItem();
        auxE.setAmount(amount);
        setItem(auxI);
        setRawItem(auxE);
    }

    /**
     * Gets the amount of the item
     *
     * @return
     */
    public int getQuantity() {
        return item.getItem().getAmount();
    }

    /**
     * Returns the max stack size of this item
     *
     * @return
     */
    public int getMaxStackSize() {
        return item.getItem().getMaxStackSize();
    }

    /**
     * Return if the item has a flag
     *
     * @param flag
     * @return
     */
    public boolean hasFlag(ItemFlag flag) {
        return ItemUtils.hasItemFlags(getItem(), flag);
    }

    /**
     * Toggles a flag from the item
     *
     * @param flag
     */
    public void toggleFlag(ItemFlag flag) {

        if (ItemUtils.hasItemFlags(getItem(), flag)) {
            setItem(ItemUtils.removeItemFlags(getItem(), flag));
            setRawItem(ItemUtils.removeItemFlags(getRawItem(), flag));
        } else {
            setItem(ItemUtils.addItemFlags(getItem(), flag));
            setRawItem(ItemUtils.addItemFlags(getRawItem(), flag));
        }
    }

    /**
     * @return the price of the item. Can be random price between the values asigned
     */
    public Optional<dPrice> getBuyPrice() {
        return Optional.ofNullable(item.getObject("rds_buyPrice", dPrice.class));
    }

    /**
     * Set the price of the item as a fixed value
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
     * Sets the buy price with a dPrice object
     *
     * @param price
     */
    public void setBuyPrice(dPrice price) {
        item.setObject("rds_buyPrice", price);
    }

    /**
     * Generates a new price
     */
    public void generateNewBuyPrice() {
        getBuyPrice().ifPresent(dPrice -> {
            dPrice.generateNewPrice();
            setBuyPrice(dPrice);
        });
    }

    /**
     * @return the price of the item. Can be random price between the values asigned
     */
    public Optional<dPrice> getSellPrice() {
        return Optional.ofNullable(item.getObject("rds_sellPrice", dPrice.class));
    }

    /**
     * Set the price of the item as a fixed value
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

    public void setSellPrice(dPrice price) {
        item.setObject("rds_sellPrice", price);
    }

    public void generateNewSellPrice() {
        getSellPrice().ifPresent(dPrice -> {
            dPrice.generateNewPrice();
            setSellPrice(dPrice);
        });
    }

    public String getID() {
        return item.getString("rds_UUID");
    }

    public void setID(String id) {
        item.setString("rds_UUID", id);
    }

    /**
     * Gets the uuid of the item
     *
     * @return the uuid of this item
     */
    public UUID getUid() {
        return UUID.nameUUIDFromBytes(getID().getBytes());
    }


    public static @Nullable
    UUID getUid(ItemStack item) {
        return dItem.of(item).getUid();
    }

    /**
     * Set the stock of the item
     *
     * @param stock the stock to set
     */
    public void setStock(@Nullable dStock stock) {
        this.stock = stock;
    }


    public boolean hasStock() {
        return stock != null;
    }

    /**
     * Returns the stock of the item
     *
     * @return returns the stock of the item. Can be null and means that
     * the feature is disabled
     */
    public dStock getStock() {
        return stock;
    }

    /**
     * Gets the deserialized stock from the item nbt
     */
    private dStock retrieveStock() {

        if (!item.hasKey("rds_stock")) return null;

        try {                                               // Convert legacy Stock
            item.getString("rds_stock");
        } catch (ClassCastException e) {
            int legacyStock = item.getInteger("rds_stock");
            item.setString("rds_stock", dStockFactory.GLOBAL(legacyStock).toBase64());
        }

        String base64 = item.getString("rds_stock");
        if (base64 == null || base64.isEmpty()) {               // Legacy stock
            return dStockFactory.GLOBAL(item.getInteger("rds_stock"));
        }

        return dStock.fromBase64(base64);
    }

    /**
     * Saves the stock as base64
     */
    private void saveStock() {
        item.setString("rds_stock", stock == null ? null : stock.toBase64());    // Check null to reset Stock
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
    public @NotNull
    dRarity getRarity() {
        if (!item.hasKey("rds_rarity")) return new dRarity();
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
    public @NotNull
    economy getEconomy() {
        economy econ = new vault();
        if (item.hasKey("rds_econ")) {
            econ = economy.deserialize(item.getString("rds_econ"));
            try {
                econ.test();
            } catch (NoClassDefFoundError e) {
                econ = new vault();
            }
        }

        return econ;
    }

    /**
     * Set an economy for this item
     *
     * @param econ
     */
    public void setEconomy(@NotNull economy econ) {
        item.setString("rds_econ", econ.serialize());
    }

    /**
     * Gets commands to run when this item is bought
     *
     * @return list of Strings representing commands
     */
    public Optional<List<String>> getCommands() {
        return Optional.ofNullable(item.getObject("rds_cmds", List.class));
    }

    /**
     * Sets the commands to run when this item is bought
     *
     * @param commands a list of Strings representing commands
     */
    public void setCommands(@Nullable List<String> commands) {
        item.setObject("rds_cmds", commands);
    }


    private void migratePerms() {
        if (item.hasKey("rds_perms")) {
            setPermsBuy(item.getObject("rds_perms", List.class));
            item.removeKey("rds_perms");
        }
    }

    /**
     * Gets permissions that a player needs to buy this item
     *
     * @return list of Strings representing permissions
     */
    public Optional<List<String>> getPermsBuy() {
        return Optional.ofNullable(item.getObject("rds_perms_buy", List.class));
    }

    /**
     * Sets the permission that a player needs to buy this item
     *
     * @param perms a list of Strings representing permissions
     */
    public void setPermsBuy(@Nullable List<String> perms) {
        item.setObject("rds_perms_buy", perms);
    }

    /**
     * Gets permissions that a player needs to buy this item
     *
     * @return list of Strings representing permissions
     */
    public Optional<List<String>> getPermsSell() {
        return Optional.ofNullable(item.getObject("rds_perms_sell", List.class));
    }

    /**
     * Sets the permission that a player needs to buy this item
     *
     * @param perms a list of Strings representing permissions
     */
    public void setPermsSell(@Nullable List<String> perms) {
        item.setObject("rds_perms_sell", perms);
    }

    /**
     * Gets if confirm_Gui is enable for this item
     *
     * @return true if enabled; false is disabled
     */
    public boolean isConfirmGuiEnabled() {
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
        setConfirm_gui(!isConfirmGuiEnabled());
    }

    /**
     * Gets the amount for the set of items
     *
     * @return
     */
    public Optional<Integer> getSetItems() {
        if (!item.hasKey("rds_setItems"))
            return Optional.empty();
        return Optional.ofNullable(item.getInteger("rds_setItems"));
    }

    /**
     * Set amount for the set of items
     *
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
    public Optional<List<UUID>> getBundle() {
        List<String> aux = item.getObject("rds_bundle", List.class);
        if (aux != null)
            return Optional.ofNullable(aux.stream().map(UUID::fromString).collect(Collectors.toList()));
        else
            return Optional.empty();
    }

    /**
     * Sets uuid of the items that this bundle has
     *
     * @param bundle null if want to disabled it
     */
    public void setBundle(@Nullable List<UUID> bundle) {
        if (bundle == null) item.removeKey("rds_bundle");
        else item.setObject("rds_bundle", bundle.stream()        // Cast to string due to bug
                .map(UUID::toString).collect(Collectors.toList()));
    }

    /**
     * Returns the action of the dItem
     *
     * @return Optional.ofNullable(dAction)
     */
    public Pair<dAction, String> getAction() {
        return item.hasKey("rds_action") ?
                Pair.deserialize(item.getString("rds_action"), dAction.class, String.class) :
                Pair.of(dAction.EMPTY, "");

    }

    /**
     * Sets the action of this item
     */
    public void setAction(@Nullable dAction action, String s) {
        item.setString("rds_action", Pair.of(action, s).serialize());
    }

    /**
     * Private method to set Item as AIR
     */
    public void setAIR() {
        item.setBoolean("rds_AIR", true);
    }

    /**
     * private method to set Item as SIGN for dGui sell purposes
     */
    private void setSIGN() {
        item.setBoolean("rds_SIGN", true);
    }


    /**
     * Check if an dItem is masked as AIR
     *
     * @return
     */
    public boolean isAIR() {
        return item.hasKey("rds_AIR");
    }

    public boolean isSIGN() {
        return item.hasKey("rds_SIGN");
    }

    public String toJson() {
        saveStock();            // Save stock before serializing the hold item
        return NBTItem.convertItemtoNBT(item.getItem()).toString();
    }

    /**
     * Gets item serializable as base64
     *
     * @return
     */
    public String toBase64() {
        return Base64Coder.encodeString(toJson());
    }

    /**
     * Constructs dItem from base 64
     *
     * @param base64
     * @return dItem constructed
     */
    public static dItem fromBase64(String base64) {
        return fromJson(Base64Coder.decodeString(base64));
    }

    public static dItem fromJson(String json) {
        NBTCompound itemData = new NBTContainer(json);
        ItemStack item = NBTItem.convertNBTtoItem(itemData);

        return new dItem(item);
    }

    /**
     * Returns a copy of this dItem but different UUID (generated randomly)
     *
     * @return
     */
    public dItem copy() {
        dItem cloned = new dItem(getItem());
        cloned.setID(UUID.randomUUID().toString());
        cloned.setStock(getStock());
        return cloned;
    }

    /**
     * Returns a deep copy of the object, same UUID
     *
     * @return
     */
    @Override
    public dItem clone() {
        return new dItem(getItem());
    }

    public static dItem AIR() {
        dItem empty = new dItem(ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE)
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
        out.writeObject(this.toBase64());
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
