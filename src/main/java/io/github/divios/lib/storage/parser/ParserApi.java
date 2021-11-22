package io.github.divios.lib.storage.parser;

import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.events.updateShopEvent;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.FutureUtils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.parser.states.dShopState;

import java.io.*;
import java.util.*;

public class ParserApi {

    public static void saveShopToFile(dShop shop) {
        try {
            File data = new File(DailyShop.getInstance().getDataFolder() + File.separator + "parser", shop.getName() + ".yml");
            FileUtils.toYaml(Serializer.serializeShop(shop), data);
        } catch (Exception e) {
            Log.info("There was a problem saving the shop " + shop.getName());
        }
        Log.info("Converted all items correctly of shop " + shop.getName());
    }

    public static void deserialize(String fileName) {

        Set<dItem> newItems = new LinkedHashSet<>();
        String json = null;
        shopsManager sManager = shopsManager.getInstance();

        File data = new File(DailyShop.getInstance().getDataFolder() + File.separator + "parser", fileName + ".yml");
        if (!data.exists()) {
            Log.info("That shop doesn't exist on the parser folder");
            return;
        }

        dShopState state = Deserializer.deserializeShop(data);

        if (sManager.getShop(state.getId()).isPresent()) {

        }

        else {
            FutureUtils.waitFor(sManager.createShop(state.getId()));
            dShop newShop = sManager.getShop(state.getId()).get();
            Events.callEvent(new updateShopEvent(newShop, state.getInvState().build(), true));
            newShop.setItems(state.getItemsCollect().stream().map());
        }
    }

}
