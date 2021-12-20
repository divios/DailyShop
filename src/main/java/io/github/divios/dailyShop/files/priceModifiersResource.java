package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.priceModifiers.priceModifier;

import java.util.HashSet;
import java.util.Set;

public class priceModifiersResource extends resource{

    protected priceModifiersResource() {
        super("priceModifiers.yml");
    }

    @Override
    protected String getStartMessage() {
        return "Reading priceModifiers.yml...";
    }

    @Override
    protected String getCanceledMessage() {
        return "No changes were made in priceModifier.yml, skipping...";
    }

    @Override
    protected String getFinishedMessage(long time) {
        return "Imported priceModifier.yml in " + time + " ms";
    }

    @Override
    protected void init() {
        Set<priceModifier> modifiers = new HashSet<>();

        if (yaml.contains("modifiers")) {
            yaml.getConfigurationSection("modifiers").getKeys(false).forEach(s -> {   // IDs
                String innerPath = "modifiers." + s + ".";
                try {
                    modifiers.add(
                            priceModifier.getFactory()
                                    .withId(s)
                                    .withScope(yaml.getString(innerPath + "scope"))
                                    .withType(yaml.getString(innerPath + "type"))
                                    .withValue(yaml.getDouble(innerPath + "value"))
                                    .withShopID(yaml.getString(innerPath + "shop"))
                                    .withItemID(yaml.getString(innerPath + "itemID"))
                                    .buildModifier()
                    );
                } catch (Exception e) {
                    Log.warn("There was a problem parsing the priceModifier " + s);
                    Log.warn(e.getMessage());
                }
            });
        }

        if (super.firstTime) {
            DailyShop.get().getPriceModifiers().addAll(modifiers);
        }
        else {
            DailyShop.get().getPriceModifiers().clearAll();
            DailyShop.get().getPriceModifiers().addAll(modifiers);
        }

    }
}
