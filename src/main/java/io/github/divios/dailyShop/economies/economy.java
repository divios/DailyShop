package io.github.divios.dailyShop.economies;

import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
        } catch (Exception e) {
            return new vault();
        }
    }

    static economy getFromKey(String key, String currency) {
        try {
            return econTypes.valueOf(key).getEconomy(currency);
        } catch (Exception e) {
            return new vault();
        }
    }

}
