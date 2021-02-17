package io.github.divios.dailyrandomshop.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class conf_updater {

    private static final io.github.divios.dailyrandomshop.main main =
            io.github.divios.dailyrandomshop.main.getInstance();

    public static void check() {
        String version = "2.2.3";
        if (main.getConfig().getString("version", version).equals(version))
            return;
        update();
    }

    private static void update() {
        main.getLogger().warning("Updating files");
        generateOldConfig();
        restoreLocalValues();
    }


    private static void generateOldConfig() {
        File oldFile = new File(main.getDataFolder() + File.separator + "config.yml");
        File newFile = new File(main.getDataFolder() + File.separator + "oldConfig.yml");
        newFile.delete();
        oldFile.renameTo(newFile);
        oldFile.delete();
        restoreOldValues(newFile);
    }

    private static void restoreOldValues(File oldFile) {
        main.saveDefaultConfig();
        FileConfiguration yamlOldFile = YamlConfiguration.loadConfiguration(oldFile);

        Map<String, Object> oldValues = yamlOldFile.getValues(false);

        ArrayList<String> linesInDefaultConfig = new ArrayList<>();
        try {

            Scanner scanner = new Scanner(
                    new File(main.getDataFolder().getAbsolutePath()
                            + File.separator + "config.yml"), "UTF-8");
            while (scanner.hasNextLine()) {
                linesInDefaultConfig.add(scanner.nextLine() + "");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String> newLines = new ArrayList<>();

        for (String s : linesInDefaultConfig) {
            if (s.startsWith("version:")) {
                newLines.add(s);
                continue;
            }
            boolean added = false;
            for (String s2 : oldValues.keySet()) {
                if (!s.startsWith(s2 + ":")) continue;

                String quotes = "";

                if (s2.startsWith("prefix") || s2.startsWith("vault-") ||
                        s2.equalsIgnoreCase("language"))
                    quotes = "\"";

                newLines.add(s2 + ": " + quotes + oldValues.get(s2).toString() + quotes);
                added = true;
                break;
            }
            if (!added) newLines.add(s);
        }

        BufferedWriter fw;
        String[] linesArray = newLines.toArray(new String[linesInDefaultConfig.size()]);
        try {
            fw = Files.newBufferedWriter(new File(main.getDataFolder().getAbsolutePath() +
                    File.separator + "config.yml").toPath(), StandardCharsets.UTF_8);
            for (String s : linesArray) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void restoreLocalValues() {
        List<String> locales = new ArrayList<>(Arrays.asList(
                "en_US",
                "es_ES",
                "ru_RU",
                "cn_CN"));

        for (String local: locales) {
            File oldFile = new File(main.getDataFolder() + File.separator + "locales" +
                    File.separator + local + ".yml");

            if (!oldFile.exists()) break;
            File newFile = new File(main.getDataFolder() + File.separator + "locales" +
                    File.separator + "old_" + local + ".yml");
            newFile.delete();
            oldFile.renameTo(newFile);
            oldFile.delete();


            try {
                oldFile.createNewFile();
                InputStream in = main.getResource("locales/" + local + ".yml");
                OutputStream out = new FileOutputStream(oldFile);
                byte[] buffer = new byte[1024];
                int lenght = in.read(buffer);
                while (lenght != -1) {
                    out.write(buffer, 0, lenght);
                    lenght = in.read(buffer);
                }
                //ByteStreams.copy(in, out); BETA method, data losses ahead
            } catch (IOException e) {
                e.printStackTrace();
            }


            FileConfiguration yamlOldFile = YamlConfiguration.loadConfiguration(newFile);
            Map<String, Object> oldValues = yamlOldFile.getValues(false);

            ArrayList<String> linesInDefaultConfig = new ArrayList<>();
            try {

                Scanner scanner = new Scanner(
                        oldFile, "UTF-8");
                while (scanner.hasNextLine()) {
                    linesInDefaultConfig.add(scanner.nextLine() + "");
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ArrayList<String> newLines = new ArrayList<>();

            for (String s : linesInDefaultConfig) {
                boolean added = false;

                if (s.contains("- ")) continue;

                for (String s2 : oldValues.keySet()) {
                    if (!s.startsWith(s2 + ":")) continue;

                    String quotes = "\"";

                    if(s.contains("lore") &&
                            !s.equals("customize_change_lore") &&
                            !s.equals("customize_change_lore_anvil_title") &&
                            !s.equals("customize_change_lore_default_text") &&
                            !s.equals("daily-items-lore-rarity") &&
                            !s.equals("daily-items-lore-currency") &&
                            !s.equals("daily-items-lore-price") ) {
                        newLines.add(s);
                        for(String lore: yamlOldFile.getStringList(s2)) {
                            newLines.add("  - " + quotes + lore + quotes);
                        }
                        added = true;
                        break;
                    }

                    newLines.add(s2 + ": " + quotes + oldValues.get(s2).toString() + quotes);
                    added = true;
                    break;
                }
                if (!added) newLines.add(s);
            }

            oldFile.delete();
            try {
                oldFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter fw;
            String[] linesArray = newLines.stream().filter(s -> !s.equals("null")).toArray(String[]::new);
            try {
                fw = Files.newBufferedWriter(oldFile.toPath(), StandardCharsets.UTF_8);
                for (String s : linesArray) {
                    fw.write(s + "\n");
                }
                fw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            newFile.delete();
        }

    }


    private static void createFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}