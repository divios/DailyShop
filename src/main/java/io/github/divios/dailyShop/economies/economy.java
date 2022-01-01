package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.files.Settings;
import me.realized.tokenmanager.util.Log;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class economy implements Serializable {

    public static economy fromString(String str) {
        String[] econ = str.split(":");
        return getFromKey(econ[0], econ.length >= 2 ? econ[1] : "");
    }

    protected static final DailyShop plugin = DailyShop.get();

    protected final String currency;
    private final Supplier<String> name;
    private final Economies key;

    protected economy(String currency, Supplier<String> name, Economies key) {
        this.currency = currency;
        this.name = name;
        this.key = key;
    }

    protected economy(String currency, String name, Economies key) {
        this.currency = currency;
        this.name = () -> name;
        this.key = key;
    }

    public abstract void test();

    public abstract void witchDrawMoney(Player p, Double price);

    public abstract void depositMoney(Player p, Double price);

    public boolean hasMoney(Player p, double amount) {
        return getBalance(p) >= amount;
    }

    public abstract double getBalance(Player p);

    public String getName() {
        return Settings.ECON_NAMES.getEconNameOrDefault(name.get(), name.get());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        economy economy = (economy) o;
        return Objects.equals(currency, economy.currency)
                && Objects.equals(name.get(), economy.name.get())
                && key == economy.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, name, key);
    }

    @Override
    public String toString() {
        return name.get() + ":" + currency;
    }

    public static economy deserialize(String base64) {
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

    public static economy getFromKey(String key, String currency) {
        try {
            for (Economies value : Economies.values()) {
                if (value.name().equalsIgnoreCase(key))
                    return value.getEconomy(currency);
            }
        } catch (Error | Exception ignored) {
            Log.info("Cannot get economy " + currency + ", check if the corresponding plugin is enabled. Setting it to vault");
        }
        return new vault();
    }
}
