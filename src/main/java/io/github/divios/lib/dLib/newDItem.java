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
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;
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
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class newDItem implements Cloneable {

    private static final String ID_KEY = "rds_id";
    private static final newDItem AIR;

    private static final Gson gson = new Gson();
    private static final TypeToken<List<String>> listStringToken = new TypeToken<List<String>>() {
    };

    private String ID;
    private ItemStack item;
    private int slot = -1;
    private dStock stock;
    private dPrice buyPrice;
    private dPrice sellPrice;
    private economy econ = Economies.vault.getEconomy();
    private dRarity rarity = new dRarity();
    private WrapperAction action;
    private List<String> buyPerms;
    private List<String> sellPerms;
    private List<String> commands;
    private List<String> bundle;
    private boolean confirmGui = true;

    private final boolean isAir;

    static {
        ItemStack airItem = ItemBuilder.of(XMaterial.GRAY_STAINED_GLASS_PANE)
                .addEnchant(Enchantment.ARROW_FIRE, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS);

        AIR = new newDItem(airItem, UUID.randomUUID().toString(), true);
    }

    public static newDItem AIR() {
        return AIR.copy();
    }

    public static newDItem fromJson(@NotNull JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        if (object.has("air")) return AIR();

        Preconditions.checkArgument(!object.get("id").isJsonNull(), "No ID found");
        Preconditions.checkArgument(!object.get("item").isJsonNull(), "No item found");

        String id = object.get("id").getAsString();
        NBTContainer container = new NBTContainer(object.get("item").getAsString());
        newDItem item = new newDItem(NBTItem.convertNBTtoItem(container), id);

        item.slot = object.get("slot").getAsInt();
        if (!object.get("stock").isJsonNull()) item.stock = dStock.fromJson(object.get("stock"));
        if (!object.get("buyPrice").isJsonNull())
            item.buyPrice = dPrice.fromString(object.get("buyPrice").getAsString());
        if (!object.get("sellPrice").isJsonNull())
            item.sellPrice = dPrice.fromString(object.get("sellPrice").getAsString());
        item.econ = economy.fromString(object.get("econ").getAsString());
        item.rarity = dRarity.fromKey(object.get("rarity").getAsString());
        if (!object.get("action").isJsonNull()) item.action = WrapperAction.fromJson(object.get("action"));
        if (!object.get("buyPerms").isJsonNull())
            item.buyPerms = gson.fromJson(object.get("buyPerms"), listStringToken.getType());
        if (!object.get("sellPerms").isJsonNull())
            item.sellPerms = gson.fromJson(object.get("sellPerms"), listStringToken.getType());
        if (!object.get("commands").isJsonNull())
            item.commands = gson.fromJson(object.get("commands"), listStringToken.getType());
        if (!object.get("bundle").isJsonNull())
            item.bundle = gson.fromJson(object.get("bundle"), listStringToken.getType());
        item.confirmGui = object.get("confirmGui").getAsBoolean();

        return item;
    }

    public static newDItem of(Material material) {
        return new newDItem(new ItemStack(material));
    }

    public static newDItem of(XMaterial material) {
        return new newDItem(material.parseItem());
    }

    public static newDItem of(ItemStack item) {
        return new newDItem(item, UUID.randomUUID().toString());
    }

    public static newDItem from(Material material, String id) {
        return new newDItem(new ItemStack(material), id);
    }

    public static newDItem from(XMaterial material, String id) {
        return new newDItem(material.parseItem(), id);
    }

    public newDItem(Material material) {
        this(new ItemStack(material));
    }

    public newDItem(XMaterial material) {
        this(material.parseItem());
    }

    public newDItem(ItemStack item) {
        this(item, UUID.randomUUID().toString());
    }

    public newDItem(Material material, String id) {
        this(new ItemStack(material), id);
    }

    public newDItem(XMaterial material, String id) {
        this(material.parseItem(), id);
    }

    public newDItem(ItemStack item, String id) {
        this(item, id, false);
    }

    private newDItem(ItemStack item, String id, boolean isAir) {
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
        return stock.clone();
    }

    public void incrementStock(@NotNull Player p, int amount) {
        stock.increment(p, amount);
    }

    public void incrementStock(@NotNull UUID uuid, int amount) {
        stock.increment(uuid, amount);
    }

    public void decrementStock(@NotNull Player p, int amount) {
        stock.decrement(p, amount);
    }

    public void decrementStock(@NotNull UUID uuid, int amount) {
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
        return buyPrice == null ? -1 : buyPrice.getPrice() / item.getAmount();
    }

    public double getBuyPrice(@Nullable Player p, @Nullable dShop shop) {
        if (buyPrice == null) return -1;

        double modifier = DailyShop.get().getPriceModifiers().getModifier(p, shop == null ? null : shop.getName(), ID, priceModifier.type.BUY);
        double price = getBuyPrice();

        return price + (price * modifier);
    }

    @Nullable
    public dPrice getDSellPrice() {
        return sellPrice == null ? null : sellPrice.clone();
    }

    public double getSellPrice() {
        return sellPrice == null ? -1 : sellPrice.getPrice() / item.getAmount();
    }

    public double getSellPrice(Player p, dShop shop) {
        if (sellPrice == null) return -1;

        double modifier = DailyShop.get().getPriceModifiers().getModifier(p, shop == null ? null : shop.getName(), ID, priceModifier.type.SELL);
        double price = getSellPrice();

        return price + (price * modifier);
    }

    @NotNull
    public economy getEcon() {
        return econ;
    }

    @NotNull
    public dRarity getRarity() {
        return rarity.clone();
    }

    @Nullable
    public WrapperAction getAction() {
        return action;      // action is already immutable
    }

    @Nullable
    public List<String> getBuyPerms() {
        return buyPerms == null ? null : Collections.unmodifiableList(buyPerms);
    }

    @Nullable
    public List<String> getSellPerms() {
        return sellPerms == null ? null : Collections.unmodifiableList(sellPerms);
    }

    @Nullable
    public List<String> getCommands() {
        return commands == null ? null : Collections.unmodifiableList(commands);
    }

    @Nullable
    public List<String> getBundle() {
        return bundle == null ? null : Collections.unmodifiableList(bundle);
    }

    public boolean isConfirmGui() {
        return confirmGui;
    }

    public boolean isAir() {
        return isAir;
    }

    public newDItem setItem(@NotNull ItemStack item) {
        Preconditions.checkArgument(!ItemUtils.isEmpty(item), "Item cannot be null/AIR!");
        this.item = item.clone();

        return this;
    }

    public newDItem setItemQuantity(int amount) {
        Preconditions.checkArgument(amount > 0, "Amount cannot be less than 0");
        ItemStack newItem = item.clone();
        newItem.setAmount(amount);
        item = newItem;

        return this;
    }

    public newDItem setSlot(int slot) {
        this.slot = slot;

        return this;
    }

    public newDItem setBuyPrice(int minPrice, int maxPrice) {
        buyPrice = new dPrice(minPrice, maxPrice);

        return this;
    }

    public newDItem setBuyPrice(int price) {
        buyPrice = new dPrice(price);

        return this;
    }

    public newDItem setBuyPrice(@Nullable dPrice buyPrice) {
        this.buyPrice = buyPrice == null ? null : buyPrice.clone();

        return this;
    }

    public newDItem setSellPrice(@Nullable dPrice sellPrice) {
        this.sellPrice = sellPrice == null ? null : sellPrice.clone();

        return this;
    }

    public newDItem setEcon(@NotNull economy econ) {
        this.econ = econ;

        return this;
    }

    public newDItem setRarity(@NotNull String key) {
        rarity = dRarity.fromKey(key);

        return this;
    }

    public newDItem setRarity(@NotNull dRarity rarity) {
        this.rarity = rarity.clone();

        return this;
    }

    public newDItem setStock(@Nullable dStock stock) {
        this.stock = stock == null ? null : stock.clone();

        return this;
    }

    public newDItem setAction(@Nullable dAction type) {
        return type == null ? null : setAction(WrapperAction.of(type));
    }

    public newDItem setAction(@NotNull dAction type, @NotNull String data) {
        return setAction(WrapperAction.of(type, data));
    }

    private newDItem setAction(@NotNull WrapperAction action) {
        this.action = action;

        return this;
    }

    public newDItem setBuyPerms(@Nullable List<String> buyPerms) {
        this.buyPerms = buyPerms == null ? null : new ArrayList<>(buyPerms);

        return this;
    }

    public newDItem setSellPerms(@Nullable List<String> sellPerms) {
        this.sellPerms = sellPerms == null ? null : new ArrayList<>(sellPerms);

        return this;
    }

    public newDItem setCommands(@Nullable List<String> commands) {
        this.commands = commands == null ? null : new ArrayList<>(commands);

        return this;
    }

    public newDItem setBundle(@Nullable List<String> bundle) {
        this.bundle = bundle == null ? null : new ArrayList<>(bundle);

        return this;
    }

    public newDItem setConfirmGui(boolean confirmGui) {
        this.confirmGui = confirmGui;

        return this;
    }

    @NotNull
    public JsonElement toJson() {
        if (isAir)
            return JsonBuilder.object()
                    .add("air", true)
                    .build();

        return JsonBuilder.object()
                .add("id", ID)
                .add("item", NBTItem.convertItemtoNBT(item).toString())
                .add("slot", slot)
                .add("stock", stock == null ? null : stock.toJson())
                .add("buyPrice", buyPrice == null ? null : buyPrice.toString())
                .add("sellPrice", sellPrice == null ? null : sellPrice.toString())
                .add("econ", econ.toString())
                .add("rarity", rarity.getKey())
                .add("action", action == null ? null : action.toJson())
                .add("commands", gson.toJsonTree(commands))
                .add("buyPerms", gson.toJsonTree(buyPerms))
                .add("sellPerms", gson.toJsonTree(sellPerms))
                .add("bundle", gson.toJsonTree(bundle))
                .add("confirmGui", confirmGui)
                .build();
    }

    @Override
    public newDItem clone() {
        newDItem T;
        try {
            T = (newDItem) super.clone();
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
    public newDItem copy() {
        newDItem cloned = clone();
        cloned.ID = UUID.randomUUID().toString();

        return cloned;
    }

    public boolean isSimilar(@Nullable newDItem dItem) {
        if (dItem == null) return false;
        if ((this == dItem) || isAir && dItem.isAir) return true;

        return slot == dItem.slot
                && DailyObject.isSimilar(stock, dItem.stock)
                && confirmGui == dItem.confirmGui
                && isAir == dItem.isAir
                && Objects.equals(ID, dItem.ID)
                && item.equals(dItem.item)          // Equals to also compare item amount
                && DailyObject.isSimilar(buyPrice, dItem.buyPrice)
                && DailyObject.isSimilar(sellPrice, sellPrice)
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
        newDItem newDItem = (newDItem) o;

        return slot == newDItem.slot
                && Objects.equals(stock, newDItem.stock)
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
                confirmGui,
                isAir
        );
    }

    public static class WrapperAction {

        public static WrapperAction fromJson(JsonElement element) {
            JsonObject object = new JsonObject();

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

        public static boolean isSimilar(@Nullable newDItem a, @Nullable newDItem b) {
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
