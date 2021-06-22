package io.github.divios.dailyShop.economies;

import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

public abstract class economy implements Serializable {

    protected final String currency;
    private final String name;

    public economy(String currency, String name) {
        this.currency = currency;
        this.name = name;
    }

    public abstract void test();
    public abstract boolean hasMoney(Player p, Double price);
    public abstract void witchDrawMoney(Player p, Double price);
    public abstract void depositMoney(Player p, Double price);

    public String getName() { return name; }

    public String serialize() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(this.getClass().getName());
            dataOutput.writeObject(this.currency);

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Unable to serialize economy.", e);
        }
    }

    public static economy deserialize(String base64) {
        try {

            ByteArrayInputStream InputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(InputStream);

            Class<?> clazz = Class.forName((String) dataInput.readObject());
            Constructor<?> ctor = clazz.getConstructor(String.class);
            Object object = ctor.newInstance(dataInput.readObject());
            dataInput.close();

            return (economy) object;

        } catch (Exception e) {
            throw new IllegalStateException("Unable to deserialize economy.", e);
        }
    }
}
