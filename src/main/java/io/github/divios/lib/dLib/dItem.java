package io.github.divios.lib.dLib;


import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.cache.Lazy;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.Pair;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.economies.vault;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.utils.MMOUtils;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.stock.dStock;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import io.github.divios.lib.serialize.adapters.dItemAdapter;
import io.github.divios.lib.serialize.jsonSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
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
     * @return
     */
    public dItem setSlot(int slot) {
        item.setInteger("dailySlots", slot);
        return this;
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
     * @return
     */
    public dItem setMeta(ItemMeta meta) {
        ItemStack itemA = getItem();
        itemA.setItemMeta(meta);
        setItem(itemA);
        return this;
    }

    /**
     * Sets the display name of the item
     *
     * @param name
     * @return
     */
    public dItem setDisplayName(@NotNull String name) {
        setItem(ItemUtils.setName(getItem(), name));
        setRawItem(ItemUtils.setName(getRawItem(), name));
        return this;
    }

    /**
     * Gets the displayName of the item
     *
     * @return
     */
    public String getDisplayName() {
        return Utils.isEmpty(ItemUtils.getName(getItem())) ?
                getItem().getType().name() :
                ItemUtils.getName(getItem());
    }

    /**
     * Sets the lore of the item. Supports Color Codes
     *
     * @param lore
     * @return
     */
    public dItem setLore(@NotNull List<String> lore) {
        setItem(ItemUtils.setLore(getItem(), lore));
        setRawItem(ItemUtils.setLore(getRawItem(), lore));
        return this;
    }

    public dItem applyLore(loreStrategy strategy, Object... data) {
        setItem(strategy.applyLore(item.getItem(), data));
        //setRawItem(strategy.applyLore(getRawItem(), data));
        return this;
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
     * @return
     */
    public dItem setMaterial(@NotNull XMaterial m) {
        setItem(ItemUtils.setMaterial(getItem(), m));
        setRawItem(ItemUtils.setMaterial(getRawItem(), m));
        if (m.name().contains("GLASS"))
            setDurability(m.parseItem().getDurability(), true);
        return this;

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
     * @return
     */
    public dItem setDurability(short durability, boolean glass) {
        if (!glass) {
            setItem(ItemUtils.setDurability(getItem(), (short) (getItem().getType().getMaxDurability() - durability)));
            setRawItem(ItemUtils.setDurability(getRawItem(), (short) (getRawItem().getType().getMaxDurability() - durability)));
        } else {
            setItem(ItemUtils.setDurability(getItem(), durability));
            setRawItem(ItemUtils.setDurability(getRawItem(), durability));
        }
        return this;
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
     * @return
     */
    public dItem addEnchantments(@NotNull Enchantment ench, int lvl) {
        setItem(ItemUtils.addEnchant(getItem(), ench, lvl));
        setRawItem(ItemUtils.addEnchant(getRawItem(), ench, lvl));
        return this;
    }

    /**
     * Removes enchantment from item
     *
     * @param ench
     * @return
     */
    public dItem removeEnchantments(@NotNull Enchantment ench) {
        setItem(ItemUtils.removeEnchant(getItem(), ench));
        setRawItem(ItemUtils.removeEnchant(getRawItem(), ench));
        return this;

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
     * @return
     */
    public dItem setQuantity(int amount) {
        ItemStack auxI = getItem();
        auxI.setAmount(amount);
        ItemStack auxE = getRawItem();
        auxE.setAmount(amount);
        setItem(auxI);
        setRawItem(auxE);
        return this;
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
     * @return
     */
    public dItem toggleFlag(ItemFlag flag) {

        if (ItemUtils.hasItemFlags(getItem(), flag)) {
            setItem(ItemUtils.removeItemFlags(getItem(), flag));
            setRawItem(ItemUtils.removeItemFlags(getRawItem(), flag));
        } else {
            setItem(ItemUtils.addItemFlags(getItem(), flag));
            setRawItem(ItemUtils.addItemFlags(getRawItem(), flag));
        }
        return this;
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
     * @return
     */
    public dItem setBuyPrice(double price) {
        item.setObject("rds_buyPrice", new dPrice(price));
        return this;
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
     * @return
     */
    public dItem setBuyPrice(dPrice price) {
        item.setObject("rds_buyPrice", price);
        return this;
    }

    /**
     * Generates a new price
     *
     * @return
     */
    public dItem generateNewBuyPrice() {
        getBuyPrice().ifPresent(dPrice -> {
            dPrice.generateNewPrice();
            setBuyPrice(dPrice);
        });
        return this;
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
     * @return
     */
    public dItem setSellPrice(double price) {
        item.setObject("rds_sellPrice", new dPrice(price));
        return this;
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

    public dItem generateNewSellPrice() {
        getSellPrice().ifPresent(dPrice -> {
            dPrice.generateNewPrice();
            setSellPrice(dPrice);
        });
        return this;
    }

    public String getID() {
        return item.getString("rds_UUID");
    }

    public dItem setID(String id) {
        item.setString("rds_UUID", id);
        return this;
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
        return UUID.nameUUIDFromBytes(new NBTItem(item).getString("rds_UUID").getBytes());
    }

    /**
     * Set the stock of the item
     *
     * @param stock the stock to set
     * @return
     */
    public dItem setStock(@Nullable dStock stock) {
        this.stock = stock;
        return this;
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
     * @return
     */
    public dItem setRarity(@NotNull dRarity rarity) {
        item.setObject("rds_rarity", rarity);
        return this;
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
     *
     * @return
     */
    public dItem nextRarity() {
        setRarity(getRarity().next());
        return this;
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
     * @return
     */
    public dItem setEconomy(@NotNull economy econ) {
        item.setString("rds_econ", econ.serialize());
        return this;
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
     * @return
     */
    public dItem setCommands(@Nullable List<String> commands) {
        item.setObject("rds_cmds", commands);
        return this;
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

    public JsonObject getNBT() {
        return new Gson().fromJson(item.toString(), JsonObject.class);
    }

    public dItem setNBT(JsonObject nbt) {
        item.mergeCompound(new NBTContainer(nbt.toString()));
        return this;
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
        out.writeObject(encodeOptions.REFLECTION.serialize(this));
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

    public static final class encodeOptions {

        public transient static final jsonSerialization JSON = new jsonSerialization();
        public transient static final bukkitSerialization BUKKIT = new bukkitSerialization();
        public transient static final reflectionSerialization REFLECTION = new reflectionSerialization();

        private encodeOptions() {
        }

    }

    public static final class jsonSerialization implements jsonSerializer<dItem> {

        private transient static final Gson gson = new GsonBuilder()
                .registerTypeAdapter(dItem.class, new dItemAdapter())
                .create();

        @Override
        public JsonElement toJson(dItem item) {
            return gson.toJsonTree(item);
        }

        @Override
        public dItem fromJson(JsonElement element) {
            return gson.fromJson(element, dItem.class);
        }

        private jsonSerialization() {}

    }

    public static final class bukkitSerialization {

        public String serialize(dItem item) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                    dataOutput.writeObject(item.getItem());
                    return Base64.getEncoder().encodeToString(outputStream.toByteArray());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public dItem deserialize(String s) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(s))) {
                try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                    return dItem.of((ItemStack) dataInput.readObject());
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private bukkitSerialization() {}

    }

    public static final class reflectionSerialization {

        public String serialize(dItem item) {
            item.saveStock();
            return Base64Coder.encodeString(NBTItem.convertItemtoNBT(item.getItem()).toString());
        }

        public dItem deserialize(String s) {
            NBTCompound itemData = new NBTContainer(Base64Coder.decodeString(s));
            ItemStack item = NBTItem.convertNBTtoItem(itemData);

            return new dItem(item);
        }

    }


}
