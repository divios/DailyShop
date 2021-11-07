package io.github.divios.dailyShop.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.lib.managers.shopsManager;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileUtils {

    private static final DailyShop plugin = DailyShop.getInstance();

    public static void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createParentDirectory() {
        File localeDirectory = plugin.getDataFolder();

        if (!localeDirectory.exists())
            localeDirectory.mkdir();

    }

    public static void createParserFolder() {
        File parser = new File(plugin.getDataFolder(), "parser");
        parser.mkdir();
    }

    public static void createDatabaseFile() {
        File db = new File(plugin.getDataFolder(), "dailyshop.db");
        if (!db.exists()) {
            plugin.saveResource("dailyshop.db", false);
            Schedulers.sync().runLater(() -> {
                shopsManager.getInstance().getShops().forEach(shop -> {
                    shop.getGuis().reStock(true);
                });
            }, 60);
        }

    }

    public static void toYaml(Object o, File data) {

        if (!data.exists()) {
            FileUtils.createFile(data);
        }

        String json = new Gson().toJson(o);
        //Log.warn(json);
        Map map = new GsonBuilder().registerTypeAdapter(new TypeToken<Map <String, Object>>(){}.getType(),  new MapDeserializerDoubleAsIntFix())
                .create().fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());

        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setAllowUnicode(true);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        try (FileWriter fw = new FileWriter(data)) {
            new Yaml(options).dump(map, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

        @Override  @SuppressWarnings("unchecked")
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return (Map<String, Object>) read(json);
        }

        public Object read(JsonElement in) {

            if(in.isJsonArray()){
                List<Object> list = new ArrayList<Object>();
                JsonArray arr = in.getAsJsonArray();
                for (JsonElement anArr : arr) {
                    list.add(read(anArr));
                }
                return list;
            }else if(in.isJsonObject()){
                Map<String, Object> map = new LinkedTreeMap<>();
                JsonObject obj = in.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
                for(Map.Entry<String, JsonElement> entry: entitySet){
                    map.put(entry.getKey(), read(entry.getValue()));
                }
                return map;
            }else if( in.isJsonPrimitive()){
                JsonPrimitive prim = in.getAsJsonPrimitive();
                if(prim.isBoolean()){
                    return prim.getAsBoolean();
                }else if(prim.isString()){
                    return prim.getAsString();
                }else if(prim.isNumber()){

                    Number num = prim.getAsNumber();
                    // here you can handle double int/long values
                    // and return any type you want
                    // this solution will transform 3.0 float to long values
                    if(Math.ceil(num.doubleValue())  == num.longValue())
                        return num.longValue();
                    else{
                        return num.doubleValue();
                    }
                }
            }
            return null;
        }
    }

}
