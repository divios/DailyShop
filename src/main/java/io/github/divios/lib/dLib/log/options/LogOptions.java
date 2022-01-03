package io.github.divios.lib.dLib.log.options;

import java.util.UUID;

public class LogOptions {

    private String fPlayer = null;
    private String fShopId = null;
    private UUID fItemUUID = null;
    private dLogEntry.Type fType = null;
    private quantityFilter fQuantity = null;
    private boolean display = true;

    public static LogOptions emptyOption() {
        return new LogOptions();
    }

    LogOptions() {
    }

    LogOptions(String fPlayer, String fShopId, UUID fItemUUID, dLogEntry.Type fType, quantityFilter fQuantity, boolean display) {
        this.fPlayer = fPlayer;
        this.fShopId = fShopId;
        this.fItemUUID = fItemUUID;
        this.fType = fType;
        this.fQuantity = fQuantity;
        this.display = display;
    }

    public LogOptions setfPlayer(String fPlayer) {
        this.fPlayer = fPlayer;
        return this;
    }

    public LogOptions setfShopId(String fShopId) {
        this.fShopId = fShopId;
        return this;
    }

    public LogOptions setfItemUUID(UUID fItemUUID) {
        this.fItemUUID = fItemUUID;
        return this;
    }

    public LogOptions setfType(dLogEntry.Type fType) {
        this.fType = fType;
        return this;
    }

    public LogOptions setfQuantity(quantityFilter fQuantity) {
        this.fQuantity = fQuantity;
        return this;
    }

    public LogOptions setDisplay(boolean display) {
        this.display = display;
        return this;
    }

    public LogOptions switchDisplay() {
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

    public dLogEntry.Type getfType() {
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
