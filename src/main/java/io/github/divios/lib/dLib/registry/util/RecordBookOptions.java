package io.github.divios.lib.dLib.registry.util;

import io.github.divios.lib.dLib.dTransaction.Transactions;
import io.github.divios.lib.dLib.registry.RecordBookEntry;

import java.util.UUID;

public class RecordBookOptions {

    private String fPlayer = null;
    private String fShopId = null;
    private UUID fItemUUID = null;
    private Transactions.Type fType = null;
    private quantityFilter fQuantity = null;
    private boolean display = true;

    public static RecordBookOptions emptyOption() {
        return new RecordBookOptions();
    }

    RecordBookOptions() {
    }

    RecordBookOptions(String fPlayer, 
                      String fShopId,
                      UUID fItemUUID,
                      Transactions.Type fType, 
                      quantityFilter fQuantity, 
                      boolean display
    ) {
        this.fPlayer = fPlayer;
        this.fShopId = fShopId;
        this.fItemUUID = fItemUUID;
        this.fType = fType;
        this.fQuantity = fQuantity;
        this.display = display;
    }

    public RecordBookOptions setfPlayer(String fPlayer) {
        this.fPlayer = fPlayer;
        return this;
    }

    public RecordBookOptions setfShopId(String fShopId) {
        this.fShopId = fShopId;
        return this;
    }

    public RecordBookOptions setfItemUUID(UUID fItemUUID) {
        this.fItemUUID = fItemUUID;
        return this;
    }

    public RecordBookOptions setfType(Transactions.Type fType) {
        this.fType = fType;
        return this;
    }

    public RecordBookOptions setfQuantity(quantityFilter fQuantity) {
        this.fQuantity = fQuantity;
        return this;
    }

    public RecordBookOptions setDisplay(boolean display) {
        this.display = display;
        return this;
    }

    public RecordBookOptions switchDisplay() {
        display = !display;
        return this;
    }

    public String getfPlayer() {
        return fPlayer;
    }

    public String getfShopId() {
        return fShopId;
    }

    public UUID getfItemUUID() {
        return fItemUUID;
    }

    public Transactions.Type getfType() {
        return fType;
    }

    public quantityFilter getfQuantity() {
        return fQuantity;
    }

    public boolean isDisplay() {
        return display;
    }

    public static final class quantityFilter {

    }

    public static final class timeStampFilter {

    }

}
