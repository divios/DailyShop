package io.github.divios.dailyShop.files;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jcommands.util.Value;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public enum Settings {

    PREFIX("settings.prefix"),
    DEBUG("settings.debug"),
    DEFAULT_BUY("settings.default-buy-price"),
    DEFAULT_SELL("settings.default-sell-price"),
    DEFAULT_TIMER("settings.default_timer"),
    INTEGER_VAL("settings.integer-bal"),
    TIME_FORMAT("settings.time_format"),
    LOGS_REMOVED("settings.removed-logs-days"),
    ECON_NAMES("settings.econ-names"),
    ECON_FORMATTER("settings.econ_formatter");

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

    Gson gson = new Gson();
    public JsonElement getAsJson() {
        return gson.toJsonTree((getSection().getValues(true)), LinkedHashMap.class);
    }

    public static String getEconNameOrDefault(String key, String defaultStr) {
        String name = null;
        for (Map.Entry<String, Object> entry : DailyShop.get().getResources().getSettingsYml()
                .getConfigurationSection(ECON_NAMES.path)
                .getValues(false).entrySet()) {

            if (entry.getKey().equalsIgnoreCase(key)) {
                name = String.valueOf(entry.getValue());
                break;
            }

        }
        return (name == null) ? defaultStr : name;
    }

    public static FormatterData getEconFormatter(String key) {
        FormatterData data = null;

        for (Map.Entry<String, Object> entry : DailyShop.get().getResources().getSettingsYml()
                .getConfigurationSection(ECON_FORMATTER.path)
                .getValues(false).entrySet()) {

            if (entry.getKey().equalsIgnoreCase(key)) {

                try {
                    MemorySection section = (MemorySection) entry.getValue();
                    data = new FormatterData(
                            Objects.requireNonNull(section.getString("format")),
                            Objects.requireNonNull(section.getString("locale"))
                    );
                } catch (Exception ignored) {}

                break;
            }
        }

        return data;
    }

    @Override
    public String toString() {
        return Utils.JTEXT_PARSER.parse(getValue().getAsString());
    }


    public static final class FormatterData {
        public final String format;
        public final String locale;

        public FormatterData(String format, String locale) {
            this.format = format;
            this.locale = locale;
        }
    }

}
