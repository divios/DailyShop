package io.github.divios.dailyShop.files;

import io.github.divios.core_lib.misc.FormatUtils;

public class settingsResource extends resource{

    public String PREFIX;
    public boolean DEBUG;
    public double DEFAULT_BUY;
    public double DEFAULT_SELL;
    public int DEFAULT_TIMER;
    public String VAULT_NAME;

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
        VAULT_NAME = FormatUtils.color(yaml.getString("settings.vault_name"));

    }

}
