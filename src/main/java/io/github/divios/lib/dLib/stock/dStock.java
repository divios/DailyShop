package io.github.divios.lib.dLib.stock;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.dailyShop.events.searchStockEvent;
import io.github.divios.dailyShop.utils.InterfaceAdapter;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unused", "UnstableApiUsage", "unchecked"})
public abstract class dStock implements Cloneable, Serializable {

    private static final Gson gson = new Gson();
    private static final TypeToken<Map<UUID, Integer>> mapToken = new TypeToken<Map<UUID, Integer>>() {
    };

    @Deprecated
    public static dStock legacyFromBase64(String base64) {
        return legacyFromJson(Base64Coder.decodeString(base64));
    }

    @Deprecated
    public static dStock legacyFromJson(String json) {
        return new GsonBuilder().registerTypeAdapter(dStock.class, new InterfaceAdapter<dStock>()).create()
                .fromJson(json, dStock.class);
    }

    public static dStock fromJson(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkNotNull(object.get("type"), "There needs to be a type");
        Preconditions.checkNotNull(object.get("defaultStock"), "No default stock");
        Preconditions.checkNotNull(object.get("stocks"), "No stocks");

        dStock stock;
        String type = object.get("type").getAsString();
        int defaultStock = object.get("defaultStock").getAsInt();
        Map<UUID, Integer> stocks = gson.fromJson(object.get("stocks"), mapToken.getType());

        switch (type) {
            case "GLOBAL":
                stock = dStockFactory.GLOBAL(defaultStock);
                stock.stocks.putAll(stocks);
                break;
            case "INDIVIDUAL":
                stock = dStockFactory.INDIVIDUAL(defaultStock);
                stock.stocks.putAll(stocks);
                break;
            default:
                throw new RuntimeException("Invalid type");
        }

        return stock;
    }

    protected final int defaultStock;
    protected ConcurrentHashMap<UUID, Integer> stocks = new ConcurrentHashMap<>();

    protected dStock(int defaultStock, Map<UUID, Integer> stocks) {
        this.defaultStock = defaultStock;
        this.stocks.putAll(stocks);
    }

    protected dStock(int defaultStock) {
        this.defaultStock = defaultStock;
    }

    public abstract String getName();

    public int getDefault() {
        return defaultStock;
    }

    public Integer get(@NotNull Player p) {
        return get(p.getUniqueId());
    }

    public Integer get(@NotNull UUID p) {
        return stocks.getOrDefault(getKey(p), defaultStock);
    }

    public void set(@NotNull Player p, int stock) {
        set(p.getUniqueId(), stock);
    }

    public void set(@NotNull UUID p, int stock) {
        stocks.put(getKey(p), stock);
    }

    public boolean exists(@NotNull Player p) {
        return exists(p.getUniqueId());
    }

    public boolean exists(@NotNull UUID p) {
        return stocks.containsKey(getKey(p));
    }

    public void increment(@NotNull Player p, int amount) {
        increment(p.getUniqueId(), amount);
    }

    public void increment(@NotNull UUID p, int amount) {
        stocks.compute(getKey(p), (uuid, integer) -> (integer == null ? defaultStock : integer) + amount);
    }

    public void decrement(@NotNull Player p, int amount) {
        decrement(p.getUniqueId(), amount);
    }

    public void decrement(@NotNull UUID p, int amount) {
        stocks.compute(getKey(p), (uuid, integer) -> (integer == null ? defaultStock : integer) - amount);
    }

    public void reset(@NotNull Player p) {
        reset(p.getUniqueId());
    }

    public void reset(@NotNull UUID p) {
        stocks.put(getKey(p), defaultStock);
    }

    public void resetAll() {
        stocks.entrySet().forEach(uuidIntegerEntry -> uuidIntegerEntry.setValue(defaultStock));
    }

    public Map<UUID, Integer> getAll() {
        return Collections.unmodifiableMap(stocks);
    }

    public abstract boolean isIndividual();

    protected abstract UUID getKey(UUID uuid);

    @Override
    public String toString() {
        return getName() + ":" + defaultStock;
    }

    @Override
    public dStock clone() {
        try {
            dStock cloned = (dStock) super.clone();
            cloned.stocks = new ConcurrentHashMap<>(stocks);

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Compares stocks without having into account
     * the stocks of players saved
     */
    public boolean isSimilar(@NotNull dStock stock) {
        return (this == stock) || (Objects.equals(getName(), stock.getName())
                && defaultStock == stock.defaultStock);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        dStock dStock = (dStock) o;
        return dStock.getName().equals(getName())
                && defaultStock == dStock.defaultStock
                && Objects.equals(stocks, dStock.stocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), defaultStock, stocks);
    }

    public JsonElement toJson() {
        return JsonBuilder.object()
                .add("type", getName())
                .add("defaultStock", defaultStock)
                .add("stocks", gson.toJsonTree(stocks))
                .build();
    }

    @Deprecated
    public String legacyToJson() {
        return new GsonBuilder().registerTypeAdapter(dStock.class, new InterfaceAdapter<dStock>()).create().toJson(this, dStock.class);
    }

    @Deprecated
    public String legacyToBase64() {
        return Base64Coder.encodeString(legacyToJson());
    }

    public static int searchStock(Player p, dShop shop, UUID id) {
        searchStockEvent event = new searchStockEvent(p, shop, id);
        Events.callEvent(event);
        return event.getRespond();
    }

}