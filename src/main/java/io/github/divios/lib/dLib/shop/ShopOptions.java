package io.github.divios.lib.dLib.shop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.divios.lib.dLib.dTransaction.Transactions;
import org.bukkit.event.inventory.ClickType;

public class ShopOptions {

    public static final ShopOptions DEFAULT;

    static {
        DEFAULT = new ShopOptions(Transactions.Type.SELL, Transactions.Type.BUY);
    }

    public static ShopOptions fromJson(JsonElement element) {
        JsonObject json = element.getAsJsonObject();

        Transactions.Type leftClick = Transactions.Type.getOptionalByKey(json.get("LEFT").getAsString())
                .orElseThrow(() -> new RuntimeException("Invalid LEFT click action"));

        Transactions.Type rightClick = Transactions.Type.getOptionalByKey(json.get("RIGHT").getAsString())
                .orElseThrow(() -> new RuntimeException("Invalid RIGHT click action"));

        return new ShopOptions(rightClick, leftClick);
    }

    private final Transactions.Type rightClick;
    private final Transactions.Type leftClick;

    public ShopOptions(Transactions.Type rightClick, Transactions.Type leftClick) {
        this.rightClick = rightClick;
        this.leftClick = leftClick;
    }

    public Transactions.Type matchClickAction(ClickType type) {
        Transactions.Type match = null;
        if (type == ClickType.LEFT)
            match = leftClick;
        else if (type == ClickType.RIGHT)
            match = rightClick;

        return match;
    }

    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("LEFT", leftClick.name());
        json.addProperty("RIGHT", rightClick.name());

        return json;
    }
}
