package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.utils.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class settingsResource extends resource{

    public String PREFIX;
    public boolean DEBUG;
    public double DEFAULT_BUY;
    public double DEFAULT_SELL;
    public int DEFAULT_TIMER;
    public boolean INTEGER_VAL;
    public Map<String, String> ECONNAMES;

    protected settingsResource() {
        super("settings.yml");
    }

    @Override
    protected void init() {

        PREFIX = FormatUtils.color(yaml.getString("settings.prefix"));
        DEBUG = yaml.getBoolean("settings.debug");
        DEFAULT_BUY = yaml.getDouble("settings.default-buy-price");
        DEFAULT_SELL = yaml.getDouble("settings.default-sell-price");
        DEFAULT_TIMER = yaml.getInt("settings.default_timer");
        INTEGER_VAL = yaml.getBoolean("settings.integer-bal");

        ECONNAMES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        yaml.getConfigurationSection("settings.econ-names").getValues(false).forEach((s, o) -> {
            ECONNAMES.put(s, String.valueOf(o));
        });



    }

}
