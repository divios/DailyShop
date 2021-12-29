package io.github.divios.dailyShop.files;

import io.github.divios.dailyShop.DailyShop;
import io.github.divios.jcommands.utils.Value;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public enum Settings {

    PREFIX("settings.prefix"),
    DEBUG("settings.debug"),
    DEFAULT_BUY("settings.default-buy-price"),
    DEFAULT_SELL("settings.default-sell-price"),
    DEFAULT_TIMER("settings.default_timer"),
    INTEGER_VAL("settings.integer-bal"),
    ECON_NAMES("settings.econ-names");

    private final String path;

    Settings(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Value getValue() {
        return Value.ofString(DailyShop.get().getResources().getSettingsYml().getString(path));
    }

    public ConfigurationSection getSection() {
        return DailyShop.get().getResources().getSettingsYml().getConfigurationSection(path);
    }

    public String getEconNameOrDefault(String key, String defaultStr) {
        String name = null;
        for (Map.Entry<String, Object> entry : DailyShop.get().getResources().getSettingsYml()
                .getConfigurationSection(ECON_NAMES.path)
                .getValues(false).entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) {
                name = String.valueOf(entry.getValue());
                break;
            }
        }
        return name == null ? defaultStr : name;
    }

}
