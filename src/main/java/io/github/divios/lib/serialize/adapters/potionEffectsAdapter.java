package io.github.divios.lib.serialize.adapters;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.gson.*;
import io.github.divios.dailyShop.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class potionEffectsAdapter implements JsonSerializer<PotionMeta>, JsonDeserializer<PotionMeta> {


    @Override
    public PotionMeta deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        Preconditions.checkArgument(object.has("type"), "A potion needs a type");
        Preconditions.checkArgument(Utils.testRunnable(() -> PotionType.valueOf(object.get("type").getAsString())), "Invalid Potion effect");

        PotionMeta meta = (PotionMeta) Bukkit.getItemFactory().getItemMeta(Material.POTION);

        PotionType potionType = PotionType.valueOf(object.get("type").getAsString());
        boolean extended = object.has("upgraded") && object.get("upgraded").getAsBoolean();
        boolean upgraded = object.has("level") && object.get("level").getAsInt() > 1;

        meta.setBasePotionData(new PotionData(potionType, extended, upgraded));
        if (object.has("color")) meta.setColor(WrappedColor.parseColor(object.get("color").getAsString()).getColor());

        return meta;
    }

    @Override
    public JsonElement serialize(PotionMeta potionMeta, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();

        PotionData potionEffect = potionMeta.getBasePotionData();
        object.addProperty("type", potionEffect.getType().name());
        object.addProperty("level", potionEffect.isUpgraded() ? 2 : 1);
        if (potionEffect.isUpgraded()) object.addProperty("extended", true);
        if (potionMeta.hasColor()) object.addProperty("color", serializeColor(potionMeta.getColor()));

        return object;
    }


    //////////////////////////////////////////////////////////////////////////
    /* Utils */

    private String serializeColor(Color color) {
        return Arrays.stream(WrappedColor.values())
                .filter(wrappedColor -> wrappedColor.getColor().asRGB() == color.asRGB())
                .map(wrappedColor -> wrappedColor.name())
                .findFirst()
                .orElse(String.valueOf(color.getRed() + color.getGreen() + color.getBlue()));
    }

    private enum WrappedColor {

        WHITE(() -> Color.fromRGB(0xFFFFFF)),
        SILVER(() -> Color.fromRGB(0xC0C0C0)),
        GRAY(() -> Color.fromRGB(0x808080)),
        BLACK(() -> Color.fromRGB(0x000000)),
        RED(() -> Color.fromRGB(0xFF0000)),
        MAROON(() -> Color.fromRGB(0x800000)),
        YELLOW(() -> Color.fromRGB(0xFFFF00)),
        OLIVE(() -> Color.fromRGB(0x808000)),
        LIME(() -> Color.fromRGB(0x00FF00)),
        GREEN(() -> Color.fromRGB(0x008000)),
        AQUA(() -> Color.fromRGB(0x00FFFF)),
        TEAL(() -> Color.fromRGB(0x008080)),
        BLUE(() -> Color.fromRGB(0x0000FF)),
        NAVY(() -> Color.fromRGB(0x000080)),
        FUCHSIA(() -> Color.fromRGB(0xFF00FF)),
        PURPLE(() -> Color.fromRGB(0x800080)),
        ORANGE(() -> Color.fromRGB(0xFFA500));

        public static WrappedColor parseColor(String color) {
            return fromKey(color).orElseGet(() -> {
                WrappedColor toReturn = ORANGE;
                if (color.split(";").length == 3) {  // Is RGB format
                    String[] rgb = color.split(";");
                    toReturn.color = () -> Color.fromRGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                } else
                    toReturn.color = () -> Color.fromRGB(Integer.parseInt(color));

                return toReturn;
            });
        }

        public static Optional<WrappedColor> fromKey(String s) {
            return Arrays.stream(values())
                    .filter(wrappedColor -> wrappedColor.name().equals(s))
                    .findFirst();
        }

        private Supplier<Color> color;

        WrappedColor(Supplier<Color> color) {
            this.color = color;
        }

        public Color getColor() {
            return color.get();
        }

    }

}
