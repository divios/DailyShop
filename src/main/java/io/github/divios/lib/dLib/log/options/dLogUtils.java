package io.github.divios.lib.dLib.log.options;

import com.google.gson.GsonBuilder;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class dLogUtils {

    private static final DailyShop plugin = DailyShop.get();

    public static CompletableFuture<Void> importToYaml(Collection<dLogEntry.dLogEntryState> entries) {

        return CompletableFuture.runAsync(() -> {
            File directory = new File(plugin.getDataFolder(), "logs");
            directory.mkdir();

            File data = new File(plugin.getDataFolder() + File.separator + "logs", new SimpleDateFormat("MM-dd-yyyy HH-mm-ss").format(new Date(System.currentTimeMillis())) + ".json");

            if (!data.exists()) {
                FileUtils.createFile(data);
            }

            try (FileWriter fw = new FileWriter(data)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(entries, fw);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

}
