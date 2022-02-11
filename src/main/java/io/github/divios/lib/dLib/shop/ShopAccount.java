package io.github.divios.lib.dLib.shop;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.dailyShop.utils.valuegenerators.ValueGenerator;
import org.apache.commons.lang.Validate;

import java.util.Objects;

public class ShopAccount {

    public static ShopAccount fromJson(JsonElement element) {
        JsonObject json = element.getAsJsonObject();

        Preconditions.checkArgument(json.has("start_balance"), "No generator");

        double maxBalance = json.has("max_balance")
                ? json.get("max_balance").getAsDouble()
                : Double.MAX_VALUE;
        ValueGenerator generator = ValueGenerator.fromJson(json.get("start_balance"));
        double balance = json.has("current_balance")
                ? json.get("current_balance").getAsDouble()
                : generator.generate();

        return new ShopAccount(maxBalance, generator, balance);
    }

    private final double maxBalance;
    private final ValueGenerator generator;
    private double balance;

    public ShopAccount(ValueGenerator generator) {
        this(Double.MAX_VALUE, generator);
    }

    public ShopAccount(double maxBalance, ValueGenerator generator) {
        this(maxBalance, generator, generator.generate());
    }

    public ShopAccount(double maxBalance, ValueGenerator generator, double balance) {
        this.maxBalance = maxBalance;
        this.generator = generator;
        this.balance = balance;
    }

    public double getMaxBalance() {
        return maxBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void withdraw(double amount) {
        Validate.isTrue(amount >= 0, "Amount cannot be negative. Got: " + amount);
        if (balance <= 0) return;

        balance = Math.max(0, Utils.round(balance - amount, 2));
    }

    public void deposit(double amount) {
        Validate.isTrue(amount >= 0, "Amount cannot be negative. Got: " + amount);
        if (balance >= maxBalance) return;

        balance = Math.min(maxBalance, Utils.round(balance + amount, 2));
    }

    public void generateNewBalance() {
        balance = generator.generate();
    }

    @Override
    public String
    toString() {
        return "ShopAccount{" +
                "maxBalance=" + maxBalance +
                ", generator=" + generator +
                ", balance=" + balance +
                '}';
    }

    public boolean isSimilar(ShopAccount account) {
        if (account == null) return false;

        return Double.compare(maxBalance, account.maxBalance) == 0
                && generator.isSimilar(account.generator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShopAccount that = (ShopAccount) o;
        return Double.compare(that.maxBalance, maxBalance) == 0
                && Double.compare(that.balance, balance) == 0
                && generator.isSimilar(that.generator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxBalance, balance);
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.add("start_balance", generator.toJson());
        if (Double.compare(maxBalance, Double.MAX_VALUE) != 0)
            json.addProperty("max_balance", maxBalance);
        json.addProperty("current_balance", balance);

        return json;
    }

}
