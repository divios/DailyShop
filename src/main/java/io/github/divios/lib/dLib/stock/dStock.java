package io.github.divios.lib.dLib.stock;

import com.google.gson.GsonBuilder;
import io.github.divios.dailyShop.events.searchStockEvent;
import io.github.divios.dailyShop.utils.InterfaceAdapter;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import org.bukkit.Bukkit;
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

    default Integer get(Player p) { return get(p.getUniqueId()); }

    void set(UUID p, int stock);

    default void set(Player p, int stock) { set(p.getUniqueId(), stock); }

    boolean exists(UUID uuid);

    default boolean exists(Player p) { return exists(p.getUniqueId()); }

    default void increment(Player p) { increment(p.getUniqueId()); }

    void increment(UUID p);

    default void decrement(Player p) { decrement(p.getUniqueId()); }

    void decrement(UUID p);

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

        Bukkit.getPluginManager().callEvent(new searchStockEvent(p, shop, id, result));
        return result;
    }

}
