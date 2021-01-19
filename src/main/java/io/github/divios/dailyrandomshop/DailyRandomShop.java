package io.github.divios.dailyrandomshop;

import io.github.divios.dailyrandomshop.database.DataManager;
import io.github.divios.dailyrandomshop.database.sqlite;
import io.github.divios.dailyrandomshop.guis.buyGui;
import io.github.divios.dailyrandomshop.guis.settings.dailyGuiSettings;
import io.github.divios.dailyrandomshop.guis.settings.sellGuiSettings;
import io.github.divios.dailyrandomshop.placeholders.timePlaceHolder;
import io.github.divios.dailyrandomshop.utils.ConfigUtils;
import io.github.divios.dailyrandomshop.utils.Utils;
import me.xanium.gemseconomy.api.GemsEconomyAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class DailyRandomShop extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public Economy econ = null;
    public Permission perms = null;
    public Chat chat = null;
    public Map<ItemStack, Double> listDailyItems, listSellItems;
    public buyGui BuyGui;
    public dailyGuiSettings DailyGuiSettings;
    public sellGuiSettings SellGuiSettings;

    public Utils utils;
    public Config config;
    public int time = 0;
    public sqlite db = new sqlite(this);
    public DataManager dbManager = new DataManager(db, this);
    public BukkitTask updateListID;
    public GemsEconomyAPI gemsApi = null;

    public DailyRandomShop() {
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this); //cancelo las tasks
        dbManager.updateAllDailyItems();
        dbManager.updateAllSellItems();
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    @Override
    public void onEnable() {

        int pluginId = 9721;
        Metrics metrics = new Metrics(this, pluginId);

        utils = new Utils(this);

        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            getLogger().info("Found PlaceholderApi");
            new timePlaceHolder(this).register();
        }

        if(getServer().getPluginManager().getPlugin("GemsEconomy") != null) {
            getLogger().info("Found GemsEconomy");
            gemsApi = new GemsEconomyAPI();
        }

        try {
            ConfigUtils.reloadConfig(this, false);
        } catch (IOException e) {
            log.severe("Something went wrong with the .yml files");
            getServer().getPluginManager().disablePlugin(this);
        }

        BuyGui = new buyGui(this);

        getCommand("rdShop").setExecutor(new Commands(this));
        getCommand("rdShop").setTabCompleter(new TabComplete());


        //setupPermissions();
        //setupChat();

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


}
