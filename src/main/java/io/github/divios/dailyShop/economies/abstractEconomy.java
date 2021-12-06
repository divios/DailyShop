package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.DailyShop;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.Supplier;

public abstract class abstractEconomy implements Serializable, economy {

    protected static final DailyShop plugin = DailyShop.getInstance();

    protected final String currency;
    private final Supplier<String> name;
    private final econTypes key;

    protected abstractEconomy(String currency, Supplier<String> name, econTypes key) {
        this.currency = currency;
        this.name = name;
        this.key = key;
    }

    protected abstractEconomy(String currency, String name, econTypes key) {
        this.currency = currency;
        this.name = () -> name;
        this.key = key;
    }

    public abstract void test();

    public abstract void witchDrawMoney(Player p, Double price);

    public abstract void depositMoney(Player p, Double price);

    public abstract double getBalance(Player p);

    public String getName() {
        return plugin.configM.getSettingsYml().ECONNAMES.containsKey(name.get().toLowerCase()) ?
                plugin.configM.getSettingsYml().ECONNAMES.get(name.get().toLowerCase()) : name.get();
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
    public String toString() {
        return name.get() + ":" + currency;
    }
}
