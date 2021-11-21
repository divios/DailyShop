package io.github.divios.lib.dLib.stock;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.gson.GsonSerializable;
import io.github.divios.core_lib.gson.JsonBuilder;
import io.github.divios.dailyShop.events.searchStockEvent;
import io.github.divios.dailyShop.utils.InterfaceAdapter;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface dStock {

    static dStock fromBase64(String base64) {
        return fromJson(Base64Coder.decodeString(base64));
    }

    static dStock fromJson(String json) {
        return new GsonBuilder().registerTypeAdapter(dStock.class, new InterfaceAdapter<dStock>()).create()
                .fromJson(json, dStock.class);
    }

    String getName();

    int getDefault();

    Integer get(UUID p);

    boolean exists(UUID uuid);

    default Integer get(Player p) { return get(p.getUniqueId()); }

    void set(UUID p, int stock);

    default void set(Player p, int stock) { set(p.getUniqueId(), stock); }

    default boolean exists(Player p) { return exists(p.getUniqueId()); }

    default void increment(Player p) { increment(p.getUniqueId(), 1); }

    default void increment(Player p, int amount) { increment(p.getUniqueId(), amount); }

    void increment(UUID p, int amount);

    default void decrement(Player p) { decrement(p.getUniqueId(), 1); }

    default void decrement(Player p, int amount) { decrement(p.getUniqueId(), amount); }

    void decrement(UUID p, int amount);

    default void reset(Player p) { reset(p.getUniqueId()); }

    void reset(UUID p);

    void resetAll();

    Map<UUID, Integer> getAll();

    boolean isIndividual();

    default String toJson() {
        return new GsonBuilder().registerTypeAdapter(dStock.class, new InterfaceAdapter<dStock>()).create().toJson(this, dStock.class);
    }

    default String toBase64() { return Base64Coder.encodeString(toJson()); }

    static CompletableFuture<Integer> searchStock(Player p, dShop shop, UUID id) {
        CompletableFuture<Integer> result = new CompletableFuture<>();

        Events.callEvent(new searchStockEvent(p, shop, id, result));
        return result;
    }

}
