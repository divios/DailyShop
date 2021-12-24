package io.github.divios.dailyShop.economies;

import me.realized.tokenmanager.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;

public interface economy {

    void test();

    default boolean hasMoney(Player p, Double price) {
        return getBalance(p) >= price;
    }

    void witchDrawMoney(Player p, Double price);

    void depositMoney(Player p, Double price);

    double getBalance(Player p);

    String getName();

    String getCurrency();

    String getKey();

    String serialize();

    static economy deserialize(String base64) {
        try (ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64))) {
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream)) {
                String key = (String) dataInput.readObject();
                String currency = (String) dataInput.readObject();
                return getFromKey(key, currency);
            }
        } catch (Error | Exception ignored) {
            return new vault();
        }
    }

    static economy getFromKey(String key, String currency) {
        try {
            for (econTypes value : econTypes.values()) {
                if (value.name().equalsIgnoreCase(key))
                    return value.getEconomy(currency);
            }
        } catch (Error | Exception ignored) {
            Log.info("Cannot get economy " + currency + ", check if the corresponding plugin is enabled. Setting it to vault");
        }
        return new vault();
    }

}
