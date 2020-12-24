package io.github.divios.dailyrandomshop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Random;

public class Utils {

    public boolean IntegerListContains(int[] list, int i) {

        for (int j: list) {
            if (j == i) {
                return true;
            }
        }
        return false;
    }

    public int randomValue(int minValue, int maxValue) {

        return minValue + (int)(Math.random() * ((maxValue - minValue) + 1));
    }

    public boolean inventoryFull(ItemStack[] inventory) {

        for (ItemStack i: inventory) {
            if (i == null) {
                return false;
            }
        }
        return true;
    }

    public Material getEntry(Map<String, Double[]> list, int index) {
        int i = 0;
        for (String material: list.keySet()) {
            if (index == i) return Material.valueOf(material);
            i++;
        }
        return null;
    }


}
