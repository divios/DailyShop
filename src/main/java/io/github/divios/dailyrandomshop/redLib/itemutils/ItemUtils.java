package io.github.divios.dailyrandomshop.redLib.itemutils;

import io.github.divios.dailyrandomshop.utils.utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * A utility class to easily modify items
 * @author Redempt
 *
 */
public class ItemUtils {

    /**
     * Renames an ItemStack, functionally identical to {@link ItemUtils#setName(ItemStack, String)} but kept for legacy reasons
     * @param item The ItemStack to be renamed
     * @param name The name to give the ItemStack
     * @return The renamed ItemStack
     */
    public static ItemStack rename(ItemStack item, String name) {
        ItemStack clone = item.clone();
        utils.setDisplayName(clone, name);
        return clone;
    }

    /**
     * Renames an ItemStack
     * @param item The ItemStack to be renamed
     * @param name The name to give the ItemStack
     * @return The renamed ItemStack
     */
    public static ItemStack setName(ItemStack item, String name) {
        return rename(item, name);
    }

    /**
     * Set a single line of lore for an ItemStack
     * @param item The ItemStack to be given lore
     * @param line The line of lore to be given
     * @return The modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(utils.formatString(line));
        meta.setLore(lore);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Set multiple lines of lore for an ItemStack
     * @param item The ItemStack to be given lore
     * @param lore The lines of lore to be given
     * @return The modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        List<String> formatedLore = new ArrayList<>();
        for (String line: lore)
            formatedLore.add(utils.formatString(line));
        meta.setLore(formatedLore);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Add a line of lore to an ItemStack
     * @param item The ItemStack to be given lore
     * @param line The line of lore to add
     * @return The modified ItemStack
     */
    public static ItemStack addLore(ItemStack item, String line) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore = lore == null ? new ArrayList<>() : lore;
        lore.add(utils.formatString(line));
        meta.setLore(lore);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Adds multiple lines of lore to an ItemStack
     * @param item The ItemStack to be given lore
     * @param lines The lines or lore to add
     * @return The modified ItemStack
     */
    public static ItemStack addLore(ItemStack item, Iterable<String> lines) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore = lore == null ? new ArrayList<>() : lore;
        for (String line: lines)
            lore.add(utils.formatString(line));
        meta.setLore(lore);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Set multiple lines of lore for an ItemStack
     * @param item The ItemStack to be given lore
     * @param lore The lines of lore to be given
     * @return The modified ItemStack
     */
    public static ItemStack setLore(ItemStack item, String... lore) {
        return setLore(item, Arrays.asList(lore));
    }

    /**
     * Sets an item to be unbreakable
     * @param item The item to make unbreakable
     * @return The unbreakable item
     */
    public static ItemStack setUnbreakable(ItemStack item) {
        item = item.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Add an enchantment to an ItemStack
     * @param item The ItemStack to be enchanted
     * @param enchant The Enchantment to add to the ItemStack
     * @param level The level of the Enchantment
     * @return The enchanted ItemStack
     */
    public static ItemStack addEnchant(ItemStack item, Enchantment enchant, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(enchant, level, true);
        if (level == 0) {
            meta.removeEnchant(enchant);
        }
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Add an attribute to the item
     * @param item The item to have an attribute added
     * @param attribute The Attribute to be added
     * @param modifier The AttributeModifier to be added
     * @return The modified ItemStack
     */
    public static ItemStack addAttribute(ItemStack item, Attribute attribute, AttributeModifier modifier) {
        ItemMeta meta = item.getItemMeta();
        meta.addAttributeModifier(attribute, modifier);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Add an attribute to the item
     * @param item The item to have an attribute added
     * @param attribute The Attribute to be added
     * @param amount The amount to modify it by
     * @param operation The operation by which the value will be modified
     * @return The modified item
     */
    public static ItemStack addAttribute(ItemStack item, Attribute attribute, double amount, Operation operation) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(attribute.toString(), amount, operation);
        meta.addAttributeModifier(attribute, modifier);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Adds ItemFlags to the item
     * @param item The item to add ItemFlags to
     * @param flags The ItemFlags to add
     * @return The modified item
     */
    public static ItemStack addItemFlags(ItemStack item, ItemFlag... flags) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(flags);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }


    /**
     * Add an attribute to the item
     * @param item The item to have an attribute added
     * @param attribute The Attribute to be added
     * @param amount The amount to modify it by
     * @param operation The operation by which the value will be modified
     * @param slot The slot this attribute will be effective in
     * @return The modified item
     */
    public static ItemStack addAttribute(ItemStack item, Attribute attribute, double amount, Operation operation, EquipmentSlot slot) {
        ItemMeta meta = item.getItemMeta();
        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attribute.toString(), amount, operation, slot);
        meta.addAttributeModifier(attribute, modifier);
        ItemStack clone = item.clone();
        clone.setItemMeta(meta);
        return clone;
    }

    /**
     * Counts the number of the given item in the given inventory
     * @param inv The inventory to count the items in
     * @param item The item to count
     * @param comparison A filter to compare items for counting
     * @return The number of items found
     */
    public static int count(Inventory inv, ItemStack item, BiPredicate<ItemStack, ItemStack> comparison) {
        int count = 0;
        for (ItemStack i : inv) {
            if (comparison.test(item, i)) {
                count += i.getAmount();
            }
        }
        return count;
    }

    /**
     * Counts the number of the given item in the given inventory
     * @param inv The inventory to count the items in
     * @param item The item to count
     * @return The number of items found
     */
    public static int count(Inventory inv, ItemStack item) {
        return count(inv, item, ItemStack::isSimilar);
    }

    /**
     * Counts the number of items of the given type in the given inventory
     * @param inv The inventory to count the items in
     * @param type The type of item to count
     * @return The number of items found
     */
    public static int count(Inventory inv, Material type) {
        return count(inv, new ItemStack(type), (a, b) -> compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Removes the specified amount of the given item from the given inventory
     * @param inv The inventory to remove the items from
     * @param item The item to be removed
     * @param amount The amount of items to remove
     * @param comparison A filter to compare items for removal
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    public static boolean remove(Inventory inv, ItemStack item, int amount, BiPredicate<ItemStack, ItemStack> comparison) {
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length && amount > 0; i++) {
            if (!comparison.test(item, contents[i])) {
                continue;
            }
            if (amount >= contents[i].getAmount()) {
                amount -= contents[i].getAmount();
                contents[i] = null;
                if (amount == 0) {
                    inv.setContents(contents);
                    return true;
                }
                continue;
            }
            contents[i].setAmount(contents[i].getAmount() - amount);
            inv.setContents(contents);
            return true;
        }
        inv.setContents(contents);
        return false;
    }

    /**
     * Removes the specified amount of the given item from the given inventory
     * @param inv The inventory to remove the items from
     * @param item The item to be removed
     * @param amount The amount of items to remove
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    public static boolean remove(Inventory inv, ItemStack item, int amount) {
        return remove(inv, item, amount, ItemStack::isSimilar);
    }

    /**
     * Removes the specified amount of the given item type from the given inventory
     * @param inv The inventory to remove the items from
     * @param type The item type to be removed
     * @param amount The amount of items to remove
     * @return Whether the amount specified could be removed. False if it removed less than specified.
     */
    public static boolean remove(Inventory inv, Material type, int amount) {
        return remove(inv, new ItemStack(type), amount, (a, b) -> compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param item The item to count and remove
     * @param max The maximum number of items to remove
     * @param comparison A filter to compare items for counting and removal
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, ItemStack item, int max, BiPredicate<ItemStack, ItemStack> comparison) {
        int count = count(inv, item, comparison);
        count = Math.min(max, count);
        remove(inv, item, count, comparison);
        return count;
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param item The item to count and remove
     * @param max The maximum number of items to remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, ItemStack item, int max) {
        return countAndRemove(inv, item, max, ItemStack::isSimilar);
    }

    /**
     * Remove all matching items up to a maximum, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param type The item type to count and remove
     * @param max The maximum number of items to remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, Material type, int max) {
        return countAndRemove(inv, new ItemStack(type), max, (a, b) -> compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Remove all matching items, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param item The item to count and remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, ItemStack item) {
        return countAndRemove(inv, item, Integer.MAX_VALUE, ItemStack::isSimilar);
    }

    /**
     * Remove all items of a specified type, returning the number that were removed
     * @param inv The inventory to count and remove items from
     * @param type The item type to count and remove
     * @return How many items were removed
     */
    public static int countAndRemove(Inventory inv, Material type) {
        return countAndRemove(inv, new ItemStack(type), Integer.MAX_VALUE, (a, b) -> ItemUtils.compare(a, b, ItemTrait.TYPE));
    }

    /**
     * Give the player the specified items, dropping them on the ground if there is not enough room
     * @param player The player to give the items to
     * @param items The items to be given
     */
    public static void give(Player player, ItemStack... items) {
        player.getInventory().addItem(items).values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
    }

    /**
     * Gives the player the specified amount of the specified item, dropping them on the ground if there is not enough room
     * @param player The player to give the items to
     * @param item The item to be given to the player
     * @param amount The amount the player should be given
     */
    public static void give(Player player, ItemStack item, int amount) {
        if (amount < 1) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        int stackSize = item.getType().getMaxStackSize();
        while (amount > stackSize) {
            ItemStack clone = item.clone();
            clone.setAmount(stackSize);
            give(player, clone);
            amount -= stackSize;
        }
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        give(player, clone);
    }

    /**
     * Gives the player the specified amount of the specified item type, dropping them on the ground if there is not enough room
     * @param player The player to give the items to
     * @param type The item type to be given to the player
     * @param amount The amount the player should be given
     */
    public static void give(Player player, Material type, int amount) {
        give(player, new ItemStack(type), amount);
    }

    /**
     * Compares the traits of two items
     * @param first The first ItemStack
     * @param second The second ItemStack
     * @param traits The ItemTraits to compare
     * @return Whether the two items are identical in terms of the traits provided. Returns true if both items are null, and false if only one is null.
     */
    public static boolean compare(ItemStack first, ItemStack second, ItemTrait... traits) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        for (ItemTrait trait : traits) {
            if (!trait.compare(first, second)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares the type, name, and lore of two items
     * @param first The first ItemStack
     * @param second The second ItemStack
     * @return Whether the two items are identical in terms of type, name, and lore. Returns true if both items are null, and false if only one is null.
     */
    public static boolean compare(ItemStack first, ItemStack second) {
        return compare(first, second, ItemTrait.TYPE, ItemTrait.NAME, ItemTrait.LORE);
    }

    /**
     * Creates a mock inventory clone of the given inventory. Do not try to open this inventory for players,
     * it will throw an error.
     * @param inv The inventory to clone
     * @return A mock clone inventory
     */
    public static Inventory cloneInventory(Inventory inv) {
        return null;
    }

}