package io.github.divios.dailyShop.economies;

import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

public abstract class economy implements Serializable {

    protected final String currency;
    private final Supplier<String> name;
    private final econTypes key;

    protected economy(String currency, Supplier<String> name, econTypes key) {
        this.currency = currency;
        this.name = name;
        this.key = key;
    }

    protected economy(String currency, String name, econTypes key) {
        this.currency = currency;
        this.name = () -> name;
        this.key = key;
    }

    public abstract void test();

    public boolean hasMoney(Player p, Double price) {
        return getBalance(p) >= price;
    }

    public abstract void witchDrawMoney(Player p, Double price);

    public abstract void depositMoney(Player p, Double price);

    public abstract double getBalance(Player p);

    public String getName() {
        return name.get();
    }

    public String getCurrency() {
        return currency;
    }

    public String getKey() {
        return key.name();
    }

    public String serialize() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeObject(getKey());
                dataOutput.writeObject(this.currency);
                return Base64Coder.encodeLines(outputStream.toByteArray());
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize economy.", e);
        }
    }

    public static economy deserialize(String base64) {
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

    public static economy getFromKey(String key, String currency) {
        try {
            return econTypes.valueOf(key).getEconomy(currency);
        } catch (Exception e) {
            return new vault();
        }
    }

}
