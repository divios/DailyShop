package io.github.divios.dailyrandomshop.utils;

import io.github.divios.dailyrandomshop.database.dataManager;
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

    public static final double version = 235.2; //2.3.5B
    public static boolean priceFormat = false;

    public static void check() {
        int pace = 1;
        List<String> _v = new ArrayList<>
                (Arrays.asList(main.getConfig().getString("version").split("\\.")));
        if (_v.get(_v.size() - 1).length() == 2) { //si hay una letra
            pace = 2;
            String letter = _v.get(_v.size() - 1).substring(1, 2);
            _v.set(_v.size() - 1, _v.get(_v.size() - 1).substring(0, 1));
            _v.add(letter);
        }
        double _version = 0;
        for (int i = 0; i < _v.size(); i++) {

            if (_v.get(i).contains("A")) _version += 0.1;
            else if (_v.get(i).equals("B")) _version += 0.2;
            else if (_v.get(i).equals("C")) _version += 0.3;

            else _version += Integer.parseInt(_v.get(i)) * Math.pow(10, _v.size() - pace - i);
        }

        priceFormat = _version < 235; //version desde donde se empieza este cambio
        //main.getLogger().severe(priceFormat + "");

        if (_version == version)
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
        main.saveDefaultConfig();
        //restoreOldValues(newFile);
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

            FileConfiguration yamlNewFile = YamlConfiguration.loadConfiguration(oldFile);

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
            String quotes = "\"";

            for (String s : linesInDefaultConfig) {
                boolean added = false;

                if (s.contains("- ")) continue;

                for (String s2 : oldValues.keySet()) {
                    if (!s.startsWith(s2 + ":")) continue;

                    if (yamlOldFile.isList(s2)) {
                        newLines.add(s2 + ":");
                        for (String s3: yamlOldFile.getStringList(s2))
                            newLines.add("  - " + quotes + s3 + quotes);
                    } else
                        newLines.add(s2 + ": " + quotes + yamlOldFile.getString(s2) + quotes);

                    added = true;
                    break;
                }

                if (!added) {
                    newLines.add(s);
                    if (s.startsWith("#") || s.isEmpty()) continue;

                    String _s = s.substring(0, s.length() - 1);
                    if (yamlNewFile.isList(_s))
                        for (String s2: yamlNewFile.getStringList(_s))
                            newLines.add("  - " + quotes + s2 + quotes);
                }
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