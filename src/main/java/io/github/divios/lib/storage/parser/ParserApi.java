package io.github.divios.lib.storage.parser;

import com.google.common.base.Preconditions;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dShop;

import java.io.File;
import java.util.Objects;

public class ParserApi {

    private static final DailyShop plugin = DailyShop.getInstance();
    private static final File shopsFolder = new File(plugin.getDataFolder(), "shops");

    public static void saveShopToFile(dShop shop) {
        try {
            File data = new File(shopsFolder, shop.getName() + ".yml");
            FileUtils.toYaml(dShop.serializeOptions().json().toJson(shop), data);
        } catch (Exception e) {
            Log.info("There was a problem saving the shop " + shop.getName());
            e.printStackTrace();
        }
        //Log.info("Converted all items correctly of shop " + shop.getName());
    }

    public static dShop getShopFromFile(File data) {
        Objects.requireNonNull(data, "data cannot be null");
        Preconditions.checkArgument(data.exists(), "The file does not exist");
        return dShop.serializeOptions().json().fromJson(Utils.getJsonFromFile(data));
    }

}
