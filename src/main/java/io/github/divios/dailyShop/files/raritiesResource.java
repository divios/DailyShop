package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.dLib.rarities.Rarity;
import io.github.divios.lib.dLib.rarities.RarityManager;
import io.github.divios.lib.dLib.rarities.RarityParser;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class raritiesResource extends resource {

    private static final RarityManager rManager = DailyShop.get().getRarityManager();

    protected raritiesResource() {
        super("rarities.yml", false);
    }

    @Override
    protected String getStartMessage() {
        return "Reading rarities.yml...";
    }

    @Override
    protected String getCanceledMessage() {
        return "Skipping, no changes made on rarities.yml";
    }

    @Override
    protected String getFinishedMessage(long time) {
        return String.format("Imported rarities in %d ms", time);
    }

    @Override
    protected void init() {
        ConfigurationSection section = super.yaml.getConfigurationSection("rarities");
        Objects.requireNonNull(section, "Bad rarity format");

        List<Rarity> rarityList = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigurationSection subSection = section.getConfigurationSection(key);

            try {
                rarityList.add(RarityParser.parse(key, subSection));
            } catch (Exception e) {
                Log.warn("Could not parse rarity %s, cause: %s", key, e.getMessage());
            }
        }

        Log.info("Loaded %d rarities", rarityList.size());

        if (firstTime) {
            rManager.addAll(rarityList);
        }
        else
            rManager.set(rarityList);
    }

}
