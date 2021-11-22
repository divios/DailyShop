package io.github.divios.lib.storage.parser.states;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Preconditions;
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

    private String id;
    private String name;
    private List<String> lore = new ArrayList<>();
    private Material material;
    private Integer quantity;
    private Map<String, Integer> enchantments = new HashMap<>();
    private String action;
    private int slot;
    private Boolean air;
    private JsonObject nbt;

    public static dButtonStateBuilder builder() { return new dButtonStateBuilder(); }

    public static dButtonState of(dItem item) {
        return new dButtonState(item);
    }

    protected dButtonState() {}

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

    public dItem parseItem() {

        NBTItem item = new NBTItem(ItemBuilder.of(XMaterial.matchXMaterial(material))
                .setName(name).setLore(lore));

        item.mergeCompound(new NBTContainer(nbt.toString()));

        dItem newItem = dItem.of(item.getItem().clone());
        newItem.setUid(id);
        newItem.setQuantity(quantity);
        if (air != null && air) newItem.setAIR();
        newItem.setSlot(slot);

        if (enchantments != null)
            enchantments.forEach((s, integer) -> newItem.addEnchantments(Enchantment.getByName(s), integer));

        String[] values = action.split(":");
        newItem.setAction(dAction.valueOf(values[0]), values.length >= 2 ? values[1] : "");

        return newItem;
    }


    public static final class dButtonStateBuilder {
        private String id;
        private String name;
        private List<String> lore = new ArrayList<>();
        private String material;
        private Integer quantity;
        private Map<String, Integer> enchantments = new HashMap<>();
        private String action;
        private int slot;
        private Boolean air;
        private JsonObject nbt;

        private dButtonStateBuilder() {
        }

        public static dButtonStateBuilder adButtonState() {
            return new dButtonStateBuilder();
        }

        public dButtonStateBuilder withID(String id) {
            this.id = id;
            return this;
        }

        public dButtonStateBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public dButtonStateBuilder withLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public dButtonStateBuilder withMaterial(String material) {
            this.material = material;
            return this;
        }

        public dButtonStateBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public dButtonStateBuilder withEnchantments(Map<String, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        public dButtonStateBuilder withAction(String action) {
            this.action = action;
            return this;
        }

        public dButtonStateBuilder withSlot(int slot) {
            this.slot = slot;
            return this;
        }

        public dButtonStateBuilder withAir(Boolean air) {
            this.air = air;
            return this;
        }

        public dButtonStateBuilder withNbt(JsonObject nbt) {
            this.nbt = nbt;
            return this;
        }

        public dButtonState build() {
            checkPreConditions();

            dButtonState dButtonState = new dButtonState();
            dButtonState.id = id;
            dButtonState.enchantments = this.enchantments;
            dButtonState.slot = this.slot;
            dButtonState.air = this.air;
            dButtonState.action = this.action;
            dButtonState.name = FormatUtils.color(this.name);
            dButtonState.lore = this.lore;
            dButtonState.nbt = this.nbt;
            dButtonState.material = XMaterial.matchXMaterial(this.material).get().parseMaterial();
            dButtonState.quantity = this.quantity;
            return dButtonState;
        }

        private void checkPreConditions() {
            Preconditions.checkArgument(!id.isEmpty(), "Id cannot be empty");
            if (name == null) name = "";
            if (lore == null) lore = Collections.emptyList();
            if (action == null) action = "EMPTY:";
            Preconditions.checkArgument(XMaterial.matchXMaterial(material).isPresent(), "The item material does not exist");
            if (quantity == null || quantity <= 0 || quantity > 64) quantity = 1;
            if (enchantments == null) enchantments = Collections.EMPTY_MAP;
            enchantments.forEach((s, integer) -> {
                try {
                    Enchantment.getByName(s);
                } catch (Exception e) {
                    throw new RuntimeException("The enchantment " + s + " does not exist");
                }
            });
            if (nbt == null) nbt = new JsonObject();
        }
    }
}
