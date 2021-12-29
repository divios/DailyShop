package io.github.divios.dailyShop.transaction;

import io.github.divios.dailyShop.economies.economy;

import java.util.ArrayList;
import java.util.List;

public class summary {

    private int slots = 0;
    private double price = 0;
    private List<Runnable> r = new ArrayList<>();
    private economy econ = null;

    public void concat(summary s) {
        price += s.getPrice();
        slots += s.getSlots();
        r.addAll(s.getRunnables());

    }

    public int getSlots() {
        return slots;
    }

    public double getPrice() {
        return price;
    }

    public List<Runnable> getRunnables() {
        return r;
    }

    public economy getEcon() {
        return econ;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRunnables(List<Runnable> r) {
        this.r = r;
    }

    public void setEcon(economy econ) {
        this.econ = econ;
    }

    public void addRunnable(Runnable _r) {
        r.add(_r);
    }
}
