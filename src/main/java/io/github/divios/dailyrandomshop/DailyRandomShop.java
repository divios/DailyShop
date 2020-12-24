package io.github.divios.dailyrandomshop;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import io.github.divios.dailyrandomshop.GUIs.buyGui;
import io.github.divios.dailyrandomshop.GUIs.confirmGui;
import io.github.divios.dailyrandomshop.GUIs.sellGui;
import io.github.divios.dailyrandomshop.Listeners.buyGuiListener;
import io.github.divios.dailyrandomshop.Listeners.confirmGuiListener;
import io.github.divios.dailyrandomshop.Listeners.sellGuiListener;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class DailyRandomShop extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public Economy econ = null;
    public Permission perms = null;
    public Chat chat = null;
    public Map<String, Double[]> listMaterials;
    public buyGui BuyGui;
    public sellGui SellGui;
    public confirmGui ConfirmGui;
    public Utils utils;
    public Config config;
    private int time = 0;

    @Override
    public void onDisable() {
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));

    }

    @Override
    public void onEnable() {

        int pluginId = 9721;
        Metrics metrics = new Metrics(this, pluginId);

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            createConfig();
        } catch (IOException e) {
            log.severe("Something went wrong with the .yml files");
            getServer().getPluginManager().disablePlugin(this);
        }

        utils = new Utils();
        BuyGui = new buyGui(this);
        SellGui = new sellGui(this);
        ConfirmGui = new confirmGui(this);

        buyGuiListener buyguiListener = new buyGuiListener(this);
        sellGuiListener sellguiListener = new sellGuiListener(this, SellGui.getDailyItemsSlots());
        confirmGuiListener confirmguiListener = new confirmGuiListener(this);
        getServer().getPluginManager().registerEvents(buyguiListener, this);
        getServer().getPluginManager().registerEvents(sellguiListener, this);
        getServer().getPluginManager().registerEvents(confirmguiListener, this);

        getCommand("rdShop").setExecutor(new Commands(this));
        getCommand("rdShop").setTabCompleter(this);
        //setupPermissions();
        //setupChat();

        initTimer();

    }

    private void initTimer() {

        final File customFile = new File(getDataFolder(), "time.yml");
        final FileConfiguration file = YamlConfiguration.loadConfiguration(customFile);

        if (customFile.exists()) {
            time = file.getInt("currentime.time");
        } else resetTime();


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (time == 0) {
                    BuyGui.createRandomItems();
                    resetTime();
                    return;
                }
                time--;
                if (time %60 == 0) {
                    file.set("currentime.time", time);
                    try {
                        file.save(customFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 20L, 20L);
    }

    public void createConfig() throws IOException {

        File customFile;
        FileConfiguration file;

        saveDefaultConfig();
        saveResource("items.yml", true);
        config = new Config(this);

        customFile = new File(getDataFolder(), "items.yml");

        if (!customFile.exists()) { // si no existe items.yml lo creamos
            customFile.createNewFile();

            try (InputStream in = this.getResource("items.yml")) {
                OutputStream out = new FileOutputStream(customFile);
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
        }

        listMaterials = new HashMap<String, Double[]>();

        file = YamlConfiguration.loadConfiguration(customFile);
       for (String key: file.getKeys(false)) {

           try{
               Material.valueOf(key.toUpperCase(Locale.ROOT));
           }catch(IllegalArgumentException e) {
               log.warning("The material " + key.toUpperCase(Locale.ROOT) + " doesnt exist on this version of minecraft, skipping material");
               continue;
           }

           Double buyPrice = Double.parseDouble(file.getString(key + ".buyPrice"));
           Double sellPrice = Double.parseDouble(file.getString(key + ".sellPrice"));

           if (buyPrice < 0  || sellPrice < 0 ) {
               log.warning("Negative values on " + key + " , skipping item");
               continue;
           }

           Double[] prices = {buyPrice, sellPrice};

           listMaterials.put(key.toUpperCase(Locale.ROOT), prices);

           //file.set(material + ".buyPrice", buyPrice);
           //file.set(material + ".sellPrice", sellPrice);

       }

       if (listMaterials.isEmpty()) {
           log.severe("items.yml is either empty, with negative values or materials not supported in this version, please check it");
           getServer().getPluginManager().disablePlugin(this);
       }

       // time data
        customFile = new File(getDataFolder(), "time.yml");
        file = YamlConfiguration.loadConfiguration(customFile);

        if (customFile.exists()) {
            time = Integer.parseInt(file.getString("currentime.time"));
        }else time = 86400;

        //file.save(customFile);

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            commands.add("renovate");
            commands.add("reload");
            return commands;
        }

        return null;
    }

    public void resetTime () {
        time = getConfig().getInt("timer-duration");
    }

}
