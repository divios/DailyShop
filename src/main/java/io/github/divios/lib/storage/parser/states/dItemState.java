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
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class dItemState {

    private String id;
    private String name;
    private List<String> lore = new ArrayList<>();
    private Material material;
    private Integer quantity;
    private Map<String, Integer> enchantments = new HashMap<>();
    private dItemMetaState dailyShop_meta;
    private JsonObject nbt;

    public static dItemStateBuilder builder() {
        return new dItemStateBuilder();
    }

    public static dItemState of(dItem item) {
        return of(item.getRawItem());
    }

    public static dItemState of(ItemStack item) {
        return new dItemState(item);
    }

    protected dItemState() {
    }

    private dItemState(ItemStack item) {

        name = FormatUtils.unColor(ItemUtils.getName(item));
        List<String> coloredLore = ItemUtils.getLore(item);
        coloredLore.forEach(s -> lore.add(FormatUtils.unColor(s)));
        material = ItemUtils.getMaterial(item);
        quantity = item.getAmount();
        dailyShop_meta = dItemMetaState.of(item);

        item.getEnchantments().forEach((enchantment, integer) -> enchantments.put(enchantment.getName(), integer));

        NBTItem nbtItem = new NBTItem(item);
        nbt = new Gson().fromJson(nbtItem.toString(), JsonObject.class);

        if (nbt.has("rds_setItems")) {
            quantity = null;
        }

        // Remove already cached metadata
        nbt.remove("rds_UUID");
        nbt.remove("rds_rarity");
        nbt.remove("rds_sellPrice");
        nbt.remove("dailySlots");
        nbt.remove("rds_buyPrice");
        nbt.remove("rds_stock");
        nbt.remove("rds_cmds");
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

        //Preconditions;
        if (name.isEmpty()) name = null;
        if (lore.isEmpty()) lore = null;
        if (quantity != null && quantity == 1) quantity = null;
        if (enchantments.isEmpty()) enchantments = null;
        if (nbt.size() == 0) nbt = null;

    }

    public String getId() {
        return id;
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

    public int getQuantity() {
        return quantity;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public dItemMetaState getDailyShop_meta() {
        return dailyShop_meta;
    }

    public JsonObject getNbt() {
        return nbt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setEnchantments(Map<String, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public void setNbt(JsonObject nbt) {
        this.nbt = nbt;
    }

    public void setDailyShop_meta(dItemMetaState dailyShop_meta) {
        this.dailyShop_meta = dailyShop_meta;
    }

    public dItem parseItem() {

        // Preconditions
        if (name == null) name = "";
        name = FormatUtils.color(name);
        if (lore == null) lore = Collections.emptyList();
        if (quantity == null) quantity = 1;
        if (nbt == null) nbt = new JsonObject();

        NBTItem item = new NBTItem(ItemBuilder.of(XMaterial.matchXMaterial(material))
                .setName(name).setLore(lore));

        item.mergeCompound(new NBTContainer(nbt.toString()));

        dItem newItem = dItem.of(item.getItem().clone(), id);
        newItem.setQuantity(quantity);

        if (enchantments != null)
            enchantments.forEach((s, integer) -> newItem.addEnchantments(Enchantment.getByName(s), integer));

        if (dailyShop_meta != null)
            Utils.tryCatchAbstraction(() -> dailyShop_meta.applyValues(newItem), e -> {
                Log.info("There was an error trying to parse the item of id " + newItem.getUid());
                e.printStackTrace();
            });

        return newItem;
    }


    public static final class dItemStateBuilder {
        private String id;
        private String name;
        private List<String> lore = new ArrayList<>();
        private String material;
        private Integer quantity;
        private Map<String, Integer> enchantments = new HashMap<>();
        private dItemMetaState dailyShop_meta;
        private JsonObject nbt;

        private dItemStateBuilder() {
        }

        public dItemStateBuilder withID(String id) {
            this.id = id;
            return this;
        }

        public dItemStateBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public dItemStateBuilder withLore(List<String> lore) {
            this.lore = lore;
            return this;
        }

        public dItemStateBuilder withMaterial(String material) {
            this.material = material;
            return this;
        }

        public dItemStateBuilder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public dItemStateBuilder withEnchantments(Map<String, Integer> enchantments) {
            this.enchantments = enchantments;
            return this;
        }

        public dItemStateBuilder withDailyShop_meta(dItemMetaState dailyShop_meta) {
            this.dailyShop_meta = dailyShop_meta;
            return this;
        }

        public dItemStateBuilder withNbt(JsonObject nbt) {
            this.nbt = nbt;
            return this;
        }

        public dItemState build() {
            checkPreConditions();

            dItemState dItemState = new dItemState();
            dItemState.setId(id);
            dItemState.setName(name);
            dItemState.setLore(lore);
            dItemState.setMaterial(XMaterial.matchXMaterial(material).get().parseMaterial());
            dItemState.setQuantity(quantity);
            dItemState.setEnchantments(enchantments);
            dItemState.setDailyShop_meta(dailyShop_meta);
            dItemState.setNbt(nbt);
            return dItemState;
        }

        private void checkPreConditions() {
            Preconditions.checkArgument(!id.isEmpty(), "Id cannot be empty");
            if (lore == null) lore = Collections.emptyList();
            Preconditions.checkArgument(XMaterial.matchXMaterial(material).isPresent(), "The item material does not exist");
            if (quantity <= 0 || quantity > 64) quantity = 1;
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
