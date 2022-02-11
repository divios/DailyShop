package io.github.divios.lib.dLib;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.economies.Economies;
import io.github.divios.dailyShop.economies.Economy;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.stock.dStock;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class represents a Daily Item. Contains all the daily
 * meta that is used for various functions and mechanics.
 */
@SuppressWarnings({"unused", "UnstableApiUsage", "UnusedReturnValue"})
public class dItem implements Cloneable {

    private static final String ID_KEY = "rds_id";

    public static @Nullable
    String getIdKey(@NotNull ItemStack item) {
        return new NBTItem(item).getString(ID_KEY);
    }

    public static @Nullable
    UUID getUUIDKey(@NotNull ItemStack item) {
        String id = getIdKey(item);
        if (id == null) return null;
        return UUID.nameUUIDFromBytes(id.getBytes());
    }

    private static final dItem AIR;

    private static final Gson gson = new Gson();
    private static final TypeToken<List<String>> listStringToken = new TypeToken<List<String>>() {
    };

    private String ID;
    private ItemStack item;
    private int slot = -1;
    private dStock stock;
    private dPrice buyPrice;
    private dPrice sellPrice;
    private Economy econ = Economies.vault.getEconomy();
    private dRarity rarity = new dRarity();
    private WrapperAction action = WrapperAction.of(dAction.EMPTY, "");
    private List<String> buyPerms;
    private List<String> sellPerms;
    private List<String> commands;
    private List<String> bundle;
    private boolean staticSlot = false;
    private boolean confirmGui = true;

    private final boolean isAir;

    static {
        ItemStack airItem = ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE)
                .addEnchant(Enchantment.ARROW_FIRE, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS);

        AIR = new dItem(airItem, UUID.randomUUID().toString(), true);
    }

    public static dItem AIR() {
        return AIR.copy();
    }

    public static dItem fromJson(@NotNull JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("id"), "No ID found");
        Preconditions.checkArgument(object.has("item"), "No item found");

        String id = object.get("id").getAsString();

        if (object.get("item").getAsString().equals("air")) {           // Deserialize air item
            dItem air = AIR().setID(object.get("id").getAsString());
            air.setSlot(object.get("slot").getAsInt());
            return air;
        }

        NBTContainer container = new NBTContainer(object.get("item").getAsString());
        dItem item = new dItem(NBTItem.convertNBTtoItem(container), id);

        item.slot = object.get("slot").getAsInt();
        if (object.has("stock") && !object.get("stock").isJsonNull())
            item.stock = dStock.fromJson(object.get("stock"));

        if (object.has("buyPrice") && !object.get("buyPrice").isJsonNull())
            item.buyPrice = dPrice.fromJson(object.get("buyPrice"));

        if (object.has("sellPrice") && !object.get("sellPrice").isJsonNull())
            item.sellPrice = dPrice.fromJson(object.get("sellPrice"));

        item.econ = Economy.fromString(object.get("econ").getAsString());
        item.rarity = dRarity.fromKey(object.get("rarity").getAsString());
        item.action = WrapperAction.fromJson(object.get("action"));

        if (object.has("buyPerms") && !object.get("buyPerms").isJsonNull())
            item.buyPerms = gson.fromJson(object.get("buyPerms"), listStringToken.getType());

        if (object.has("sellPerms") && !object.get("sellPerms").isJsonNull())
            item.sellPerms = gson.fromJson(object.get("sellPerms"), listStringToken.getType());

        if (object.has("commands") && !object.get("commands").isJsonNull())
            item.commands = gson.fromJson(object.get("commands"), listStringToken.getType());

        if (object.has("bundle") && !object.get("bundle").isJsonNull())
            item.bundle = gson.fromJson(object.get("bundle"), listStringToken.getType());

        item.staticSlot = object.get("static").getAsBoolean();
        item.confirmGui = object.get("confirmGui").getAsBoolean();

        return item;
    }

    public static dItem of(Material material) {
        return new dItem(new ItemStack(material));
    }

    public static dItem of(XMaterial material) {
        return new dItem(material.parseItem());
    }

    public static dItem of(ItemStack item) {
        return new dItem(item, UUID.randomUUID().toString());
    }

    public static dItem from(Material material, String id) {
        return new dItem(new ItemStack(material), id);
    }

    public static dItem from(XMaterial material, String id) {
        return new dItem(material.parseItem(), id);
    }

    public dItem(Material material) {
        this(new ItemStack(material));
    }

    public dItem(XMaterial material) {
        this(material.parseItem());
    }

    public dItem(ItemStack item) {
        this(item, UUID.randomUUID().toString());
    }

    public dItem(Material material, String id) {
        this(new ItemStack(material), id);
    }

    public dItem(XMaterial material, String id) {
        this(material.parseItem(), id);
    }

    public dItem(ItemStack item, String id) {
        this(item, id, false);
    }

    private dItem(ItemStack item, String id, boolean isAir) {
        this.item = item.clone();
        this.ID = id;
        this.isAir = isAir;
    }

    @NotNull
    public String getID() {
        return ID;
    }

    @NotNull
    public UUID getUUID() {
        return UUID.nameUUIDFromBytes(ID.getBytes());
    }

    @NotNull
    public ItemStack getItem() {
        return item.clone();
    }

    @NotNull
    public ItemStack getItemWithId() {
        NBTItem nbtItem = new NBTItem(item.clone());
        nbtItem.setString(ID_KEY, ID);

        return nbtItem.getItem();
    }

    public int getSlot() {
        return slot;
    }

    @Nullable
    public dStock getDStock() {
        return stock == null ? null : stock.clone();
    }

    public void incrementStock(@NotNull Player p, int amount) {
        if (stock != null)
            stock.increment(p, amount);
    }

    public void incrementStock(@NotNull UUID uuid, int amount) {
        if (stock != null)
            stock.increment(uuid, amount);
    }

    public void decrementStock(@NotNull Player p, int amount) {
        if (stock != null)
            stock.decrement(p, amount);
    }

    public void decrementStock(@NotNull UUID uuid, int amount) {
        if (stock != null)
            stock.decrement(uuid, amount);
    }

    public int getPlayerStock(Player p) {
        return stock == null ? -1 : stock.get(p);
    }

    @Nullable
    public dPrice getDBuyPrice() {
        return buyPrice == null ? null : buyPrice.clone();
    }

    public double getBuyPrice() {
        return buyPrice == null ? -1 : Utils.round(buyPrice.getPrice(), 2);
    }

    public double getPlayerBuyPrice(@Nullable Player p, @Nullable dShop shop) {
        if (buyPrice == null) return -1;

        double modifier = DailyShop.get().getPriceModifiers().getModifier(p, shop == null ? null : shop.getName(), ID, priceModifier.type.BUY);
        double price = getBuyPrice();

        return Utils.round(price + (price * modifier), 2);
    }

    @Nullable
    public dPrice getDSellPrice() {
        return sellPrice == null ? null : sellPrice.clone();
    }

    public double getSellPrice() {
        return sellPrice == null ? -1 : Utils.round(sellPrice.getPrice(), 2);
    }

    public double getPlayerSellPrice(Player p, dShop shop) {
        if (sellPrice == null) return -1;

        double modifier = DailyShop.get().getPriceModifiers().getModifier(p, shop == null ? null : shop.getName(), ID, priceModifier.type.SELL);
        double price = getSellPrice();

        return Utils.round(price + (price * modifier), 2);
    }

    @NotNull
    public Economy getEcon() {
        return econ;
    }

    @NotNull
    public dRarity getRarity() {
        return rarity.clone();
    }

    @NotNull
    public WrapperAction getAction() {
        return action;      // action is already immutable
    }

    @Nullable
    public LinkedList<String> getBuyPerms() {
        return buyPerms == null ? null : new LinkedList<>(buyPerms);
    }

    @Nullable
    public LinkedList<String> getSellPerms() {
        return sellPerms == null ? null : new LinkedList<>(sellPerms);
    }

    @Nullable
    public LinkedList<String> getCommands() {
        return commands == null ? null : new LinkedList<>(commands);
    }

    @Nullable
    public List<String> getBundle() {
        return bundle == null ? null : Collections.unmodifiableList(bundle);
    }

    public boolean isStaticSlot() {
        return staticSlot;
    }

    public boolean isConfirmGui() {
        return confirmGui;
    }

    public boolean isAir() {
        return isAir;
    }

    /**
     * Returns a copy of this item with the id specified
     */
    public dItem setID(@NotNull String id) {
        Preconditions.checkNotNull(id, "id is null");
        dItem cloned = clone();
        cloned.ID = id;

        return cloned;
    }

    public dItem setItem(@NotNull ItemStack item) {
        Preconditions.checkArgument(!ItemUtils.isEmpty(item), "Item cannot be null/AIR!");
        this.item = item.clone();

        return this;
    }

    public dItem setItemQuantity(int amount) {
        Preconditions.checkArgument(amount > 0, "Amount cannot be less than 0");
        ItemStack newItem = item.clone();
        newItem.setAmount(amount);
        item = newItem;

        return this;
    }

    public dItem setSlot(int slot) {
        this.slot = slot;

        return this;
    }

    public dItem setBuyPrice(@NotNull ValueGenerator generator) {
        buyPrice = new dPrice(generator);

        return this;
    }

    public dItem setBuyPrice(@Nullable dPrice buyPrice) {
        this.buyPrice = buyPrice == null ? null : buyPrice.clone();

        return this;
    }

    public dItem generateNewBuyPrice() {
        if (buyPrice != null)
            buyPrice.generateNewPrice();

        return this;
    }

    public dItem setSellPrice(ValueGenerator generator) {
        sellPrice = new dPrice(generator);

        return this;
    }

    public dItem setSellPrice(@Nullable dPrice sellPrice) {
        this.sellPrice = sellPrice == null ? null : sellPrice.clone();

        return this;
    }

    public dItem generateNewSellPrice() {
        if (sellPrice != null)
            sellPrice.generateNewPrice();

        return this;
    }

    public dItem setEcon(@NotNull Economy econ) {
        Preconditions.checkNotNull(econ, "Econ is null");
        this.econ = econ;

        return this;
    }

    public dItem setRarity(@NotNull String key) {
        Preconditions.checkNotNull(key, "key is null");
        rarity = dRarity.fromKey(key);

        return this;
    }

    public dItem setRarity(@NotNull dRarity rarity) {
        Preconditions.checkNotNull(rarity, "rarity is null");
        this.rarity = rarity.clone();

        return this;
    }

    public dItem nextRarity() {
        rarity.next();

        return this;
    }

    public dItem setStock(@Nullable dStock stock) {
        this.stock = stock == null ? null : stock.clone();

        return this;
    }

    public dItem setAction(@Nullable dAction type) {
        return type == null ? null : setAction(WrapperAction.of(type));
    }

    public dItem setAction(@NotNull dAction type, @NotNull String data) {
        Preconditions.checkNotNull(type, "Type is null");
        Preconditions.checkNotNull(data, "data is null");
        return setAction(WrapperAction.of(type, data));
    }

    private dItem setAction(@NotNull WrapperAction action) {
        this.action = action;

        return this;
    }

    public dItem setBuyPerms(@Nullable List<String> buyPerms) {
        this.buyPerms = (buyPerms == null || buyPerms.isEmpty()) ? null : new ArrayList<>(buyPerms);

        return this;
    }

    public dItem setSellPerms(@Nullable List<String> sellPerms) {
        this.sellPerms = (sellPerms == null || sellPerms.isEmpty()) ? null : new ArrayList<>(sellPerms);

        return this;
    }

    public dItem setCommands(@Nullable List<String> commands) {
        this.commands = (commands == null) ? null : new ArrayList<>(commands);

        return this;
    }

    public dItem setBundle(@Nullable List<String> bundle) {
        this.bundle = (bundle == null || bundle.isEmpty()) ? null : new ArrayList<>(bundle);

        return this;
    }

    public dItem setStaticSlot(boolean staticSlot) {
        this.staticSlot = staticSlot;
        return this;
    }

    public dItem setConfirmGui(boolean confirmGui) {
        this.confirmGui = confirmGui;

        return this;
    }

    @NotNull
    public JsonElement toJson() {

        if (isAir)
            return JsonBuilder.object()
                    .add("id", ID)
                    .add("item", "air")
                    .add("slot", slot)
                    .build();

        return JsonBuilder.object()
                .add("id", ID)
                .add("item", NBTItem.convertItemtoNBT(item).toString())
                .add("slot", slot)
                .add("stock", stock == null ? null : stock.toJson())
                .add("buyPrice", buyPrice == null ? null : buyPrice.toJson())
                .add("sellPrice", sellPrice == null ? null : sellPrice.toJson())
                .add("econ", econ.toString())
                .add("rarity", rarity.getKey())
                .add("action", action.toJson())
                .add("commands", gson.toJsonTree(commands))
                .add("buyPerms", gson.toJsonTree(buyPerms))
                .add("sellPerms", gson.toJsonTree(sellPerms))
                .add("bundle", gson.toJsonTree(bundle))
                .add("static", staticSlot)
                .add("confirmGui", confirmGui)
                .build();
    }

    @Override
    public dItem clone() {
        dItem T;
        try {
            T = (dItem) super.clone();
            T.item = item.clone();
            if (stock != null) T.stock = stock.clone();
            if (buyPrice != null) T.buyPrice = buyPrice.clone();
            if (sellPrice != null) T.sellPrice = sellPrice.clone();
            T.rarity = rarity.clone();
            if (commands != null) T.commands = new ArrayList<>(commands);
            if (buyPerms != null) T.buyPerms = new ArrayList<>(buyPerms);
            if (sellPerms != null) T.sellPerms = new ArrayList<>(sellPerms);
            if (bundle != null) T.bundle = new ArrayList<>(bundle);

            return T;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns a deep copy of this item with
     * a different ID
     */
    public dItem copy() {
        dItem cloned = clone();
        cloned.ID = UUID.randomUUID().toString();

        return cloned;
    }

    public boolean isSimilar(@Nullable dItem dItem) {
        if (dItem == null) return false;
        if ((this == dItem) || isAir && dItem.isAir) return true;

        return slot == dItem.slot
                && DailyObject.isSimilar(stock, dItem.stock)
                && staticSlot == dItem.staticSlot
                && confirmGui == dItem.confirmGui
                && isAir == dItem.isAir
                && Objects.equals(ID, dItem.ID)
                && item.equals(dItem.item)          // Equals to also compare item amount
                && DailyObject.isSimilar(buyPrice, dItem.buyPrice)
                && DailyObject.isSimilar(sellPrice, dItem.sellPrice)
                && Objects.equals(econ, dItem.econ)
                && Objects.equals(rarity, dItem.rarity)
                && Objects.equals(action, dItem.action)
                && Objects.equals(buyPerms, dItem.buyPerms)
                && Objects.equals(sellPerms, dItem.sellPerms)
                && Objects.equals(commands, dItem.commands)
                && Objects.equals(bundle, dItem.bundle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        dItem newDItem = (dItem) o;

        return slot == newDItem.slot
                && Objects.equals(stock, newDItem.stock)
                && staticSlot == newDItem.staticSlot
                && confirmGui == newDItem.confirmGui
                && isAir == newDItem.isAir
                && Objects.equals(ID, newDItem.ID)
                && item.equals(newDItem.item)
                && Objects.equals(buyPrice, newDItem.buyPrice)
                && Objects.equals(sellPrice, newDItem.sellPrice)
                && Objects.equals(econ, newDItem.econ)
                && Objects.equals(rarity, newDItem.rarity)
                && Objects.equals(action, newDItem.action)
                && Objects.equals(buyPerms, newDItem.buyPerms)
                && Objects.equals(sellPerms, newDItem.sellPerms)
                && Objects.equals(commands, newDItem.commands)
                && Objects.equals(bundle, newDItem.bundle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                ID,
                item,
                slot,
                stock,
                buyPrice,
                sellPrice,
                econ,
                rarity,
                action,
                buyPerms,
                sellPerms,
                commands,
                bundle,
                staticSlot,
                confirmGui,
                isAir
        );
    }

    public static class WrapperAction {

        public static WrapperAction fromJson(JsonElement element) {
            JsonObject object = element.getAsJsonObject();

            Preconditions.checkNotNull(object.get("type"));
            Preconditions.checkNotNull(object.get("data"));

            dAction action = dAction.valueOf(object.get("type").getAsString());
            String data = object.get("data").getAsString();
            return new WrapperAction(action, data);
        }

        private final dAction action;
        private final String data;

        public static WrapperAction of(@NotNull dAction action) {
            return new WrapperAction(action, "");
        }

        public static WrapperAction of(@NotNull dAction action, String data) {
            return new WrapperAction(action, data);
        }

        public WrapperAction(@NotNull dAction action, String data) {
            this.action = action;
            this.data = data;
        }

        public dAction getAction() {
            return action;
        }

        public String getData() {
            return data;
        }

        public void execute(@NotNull Player p) {
            action.execute(p, data);
        }

        public JsonElement toJson() {
            return JsonBuilder.object()
                    .add("type", action.name())
                    .add("data", data)
                    .build();
        }

        @Override
        public String toString() {
            return "WrapperAction{" +
                    "action=" + action +
                    ", data='" + data + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WrapperAction that = (WrapperAction) o;
            return action == that.action && Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(action, data);
        }
    }

    public static final class DailyObject {

        public static boolean isSimilar(@Nullable dItem a, @Nullable dItem b) {
            return (a == b) || (a != null && b != null && a.isSimilar(b));
        }

        public static boolean isSimilar(@Nullable dStock a, @Nullable dStock b) {
            return (a == b) || (a != null && b != null && a.isSimilar(b));
        }

        public static boolean isSimilar(@Nullable dPrice a, @Nullable dPrice b) {
            return (a == b) || (a != null && b != null && a.isSimilar(b));
        }

        private DailyObject() {
            throw new RuntimeException("This class cannot be instantiated");
        }

    }

}
