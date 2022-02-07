package io.github.divios.lib.dLib.synchronizedGui;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;

import java.util.UUID;

public class syncHashMenu extends abstractSyncMenu {

    private syncHashMenu(dShop shop) {
        super(shop);
    }

    public static syncHashMenu create(dShop shop) {
        return new syncHashMenu(shop);
    }

    public static syncHashMenu fromJson(JsonElement json, dShop shop) {
        syncHashMenu newMenu = new syncHashMenu(shop);
        newMenu.base = singleGui.fromJson(json, shop);
        return newMenu;
    }

    @Override
    protected BiMap<UUID, singleGui> createMap() {
        return HashBiMap.create();
    }

}
