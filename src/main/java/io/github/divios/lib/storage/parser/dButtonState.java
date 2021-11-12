package io.github.divios.lib.storage.parser;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import io.github.divios.core_lib.itemutils.ItemBuilder;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.lib.dLib.dAction;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

public class dButtonState {

    private String name;
    private List<String> lore = new ArrayList<>();
    private Material material;
    private Integer quantity;
    private Map<String, Integer> enchantments = new HashMap<>();
    private String action;
    private int slot;
    private Boolean air;
    private JsonObject nbt;


    public static dButtonState of(dItem item) {
        return new dButtonState(item);
    }

    public dButtonState(dItem item) {

        name = FormatUtils.unColor(ItemUtils.getName(item.getItem()));
        List<String> coloredLore = ItemUtils.getLore(item.getItem());
        coloredLore.forEach(s -> lore.add(FormatUtils.unColor(s)));
        if (lore.isEmpty()) lore = null;
        material = item.getMaterial();
        quantity = item.getQuantity();
        item.getEnchantments().forEach((enchantment, integer) -> enchantments.put(enchantment.getName(), integer));
        item.getAction().stream((dAction, s) -> {
            if (dAction.equals(io.github.divios.lib.dLib.dAction.EMPTY))
                action = null;
            else action = dAction.name() + ":" + s;
        });
        slot = item.getSlot();
        air = item.isAIR();

        NBTItem nbtItem = new NBTItem(item.getItem());
        nbt = new Gson().fromJson(nbtItem.toString(), JsonObject.class);

        // Remove already cached metadata
        nbt.remove("rds_UUID");
        nbt.remove("rds_rarity");
        nbt.remove("rds_sellPrice");
        nbt.remove("dailySlots");
        nbt.remove("rds_buyPrice");
        nbt.remove("rds_stock");
        nbt.remove("rds_cmds");
        nbt.remove("rds_AIR");
        nbt.remove("rds_action");
        nbt.remove("dailySlots");
        nbt.remove("rds_setItems");
        nbt.remove("rds_perms_buy");
        nbt.remove("rds_perms_sell");
        nbt.remove("rds_confirm_gui");
        nbt.remove("rds_bundle");
        nbt.remove("rds_econ");
        nbt.remove("rds_rawItem");
        nbt.remove("rds_econ");
        nbt.remove("display");
        nbt.remove("lore");
        nbt.remove("Enchantments");

        if (enchantments.isEmpty()) enchantments = null;
        if (nbt.size() == 0) nbt = null;
        if (!air) air = null;

    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public String getAction() {
        return action;
    }

    public int getSlot() {
        return slot;
    }

    public JsonObject getNbt() {
        return nbt;
    }

    public dItem parseItem(UUID uuid) {

        // Preconditions
        if (name == null) name = "";
        name = FormatUtils.color(name);
        if (lore == null) lore = Collections.emptyList();
        if (quantity == null) quantity = 1;
        if (action == null) action = "EMPTY:";
        if (nbt == null) nbt = new JsonObject();

        NBTItem item = new NBTItem(ItemBuilder.of(XMaterial.matchXMaterial(material))
                .setName(name).setLore(lore));

        item.mergeCompound(new NBTContainer(nbt.toString()));

        dItem newItem = dItem.of(item.getItem().clone());
        newItem.setUid(uuid);
        newItem.setQuantity(quantity);
        if (air != null && air) newItem.setAIR();
        newItem.setSlot(slot);

        if (enchantments != null)
            enchantments.forEach((s, integer) -> newItem.addEnchantments(Enchantment.getByName(s), integer));

        String[] values = action.split(":");
        newItem.setAction(dAction.valueOf(values[0]), values.length >= 2 ? values[1] : "");

        return newItem;
    }



}
