package io.github.divios.lib.dLib.synchronizedGui;

import com.google.common.collect.HashBiMap;
import io.github.divios.lib.dLib.dGui;

import java.util.Map;
import java.util.UUID;

public class syncHashMenu extends abstractSyncMenu {

    private syncHashMenu() {
        super();
    }

    public static syncMenu create() {
        return new syncHashMenu();
    }

    public static syncMenu fromJson(String json) {
        // TODO
        return null;
    }

    @Override
    protected Map<UUID, dGui> createMap() {
        return HashBiMap.create();
    }

    @Override
    public String toJson() {
        return null;
    }
}
