package io.github.divios.lib.dLib.log.options;

import com.google.common.base.Preconditions;
import io.github.divios.core_lib.itemutils.ItemUtils;
import io.github.divios.core_lib.misc.FormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class dLogEntry {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    private final String p;
    private final String shopID;
    private final UUID itemUUID;
    private final ItemStack rawItem;
    private final Type type;
    private final double price;
    private final int quantity;
    private final Timestamp timestamp;

    private dLogEntry(String p, String shopID, UUID itemUUID, ItemStack rawItem, Type type, double price, int quantity, Timestamp timestamp) {
        this.p = p;
        this.shopID = shopID;
        this.itemUUID = itemUUID;
        this.rawItem = rawItem;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getPlayer() {
        return p;
    }

    public String getShopID() {
        return shopID;
    }

    public UUID getItemUUID() {
        return itemUUID;
    }

    public ItemStack getRawItem() {
        return rawItem;
    }

    public Type getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public static dLogEntryBuilder builder() { return new dLogEntryBuilder(); }

    public dLogEntryState toState() {
        return new dLogEntryState(p, shopID, itemUUID.toString(), rawItem.getType().name(), type.name(), price, quantity, FORMAT.format(timestamp));
    }


    public static final class dLogEntryBuilder {
        private String p;
        private String shopID;
        private UUID itemUUID;
        private ItemStack rawItem;
        private Type type;
        private Double price;
        private Integer quantity;
        private Timestamp timestamp;

        private dLogEntryBuilder() {}

        public dLogEntryBuilder withPlayer(Player p) {
            return withPlayer(p.getDisplayName());
        }

        public dLogEntryBuilder withPlayer(String p) {
            this.p = FormatUtils.stripColor(p);
            return this;
        }

        public dLogEntryBuilder withShopID(String shopID) {
            this.shopID = shopID;
            return this;
        }

        public dLogEntryBuilder withItemUUID(UUID itemUUID) {
            this.itemUUID = itemUUID;
            return this;
        }

        public dLogEntryBuilder withItemUUID(String itemUUID) {
            this.itemUUID = UUID.fromString(itemUUID);
            return this;
        }

        public dLogEntryBuilder withRawItem(ItemStack rawItem) {
            this.rawItem = rawItem;
            return this;
        }

        public dLogEntryBuilder withType(Type type) {
            this.type = type;
            return this;
        }

        public dLogEntryBuilder withPrice(double price) {
            this.price = price;
            return this;
        }

        public dLogEntryBuilder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public dLogEntryBuilder withTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public dLogEntryBuilder withTimestamp(Date timestamp) {
            this.timestamp = new Timestamp(timestamp.getTime());
            return this;
        }

        public dLogEntry build() {

            Preconditions.checkNotNull(p, "player");
            Preconditions.checkNotNull(shopID, "shopID");
            Preconditions.checkNotNull(itemUUID, "itemUUID");
            Preconditions.checkNotNull(rawItem, "rawItem");
            Preconditions.checkNotNull(type, "type");
            Preconditions.checkNotNull(price, "price");

            if (quantity == null) quantity = 1;
            if (timestamp == null) timestamp = new Timestamp(System.currentTimeMillis());

            return new dLogEntry(p, shopID, itemUUID, rawItem, type, price, quantity, timestamp);
        }
    }

    public static final class dLogEntryState {

        private final String p;
        private final String shopID;
        private final String itemUUID;
        private final String item;
        private final String type;
        private final double price;
        private final int quantity;
        private final String timestamp;

        public dLogEntryState(String p, String shopID, String itemUUID, String rawItem, String type, double price, int quantity, String timestamp) {
            this.p = p;
            this.shopID = shopID;
            this.itemUUID = itemUUID;
            this.item = rawItem;
            this.type = type;
            this.price = price;
            this.quantity = quantity;
            this.timestamp = timestamp;
        }

        public String getP() {
            return p;
        }

        public String getShopID() {
            return shopID;
        }

        public String getItemUUID() {
            return itemUUID;
        }

        public String getItem() {
            return item;
        }

        public String getType() {
            return type;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public dLogEntry build() {

            Date date = null;
            try {
                date = FORMAT.parse(timestamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return new dLogEntry(p, shopID, UUID.fromString(itemUUID), ItemUtils.deserialize(item), Type.valueOf(type), price, quantity, new Timestamp(date.getTime()));
        }

    }

    public enum Type {
        BUY,
        SELL
    }

}
