package io.github.divios.dailyShop;

import dev.lone.itemsadder.api.Events.ItemsAdderFirstLoadEvent;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.events.Events;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.commands.commandsManager;
import io.github.divios.dailyShop.economies.Economies;
import io.github.divios.dailyShop.files.resourceManager;
import io.github.divios.dailyShop.hooks.Hooks;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jcommands.JCommands;
import io.github.divios.lib.dLib.priceModifiers.priceModifierManager;
import io.github.divios.lib.dLib.registry.RecordBook;
import io.github.divios.lib.dLib.shop.dShop;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.lib.storage.databaseManager;
import me.pikamug.localelib.LocaleManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class DailyShop extends JavaPlugin {

    private static DailyShop INSTANCE;
    private resourceManager resourcesManager;
    private priceModifierManager modifiers;
    private databaseManager dManager;
    private shopsManager sManager;

    private final LocaleManager localeLib = new LocaleManager();  // Material Transalations

    public DailyShop() {
        super();
    }

    protected DailyShop(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {
        try {
            meetsStartRequirements();           // Check hard dependencies
        } catch (Exception | Error e) {
            getLogger().severe("Disabled due to: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        INSTANCE = this;
        Core_lib.setPlugin(this);       /* Set plugin for aux libraries */
        JCommands.register(this);
        Utils.JTEXT_PARSER.getTemplates();     /* Init JText */

        /* Init hooks  */
        Hooks.B_STATS.getApi();

        /* Load commands */
        new commandsManager().loadCommands();

        if (Utils.isOperative("ItemsAdder"))            // Wait for ItemsAdder to load data (it does it async)
            waitForItemsAdderLoad();
        else
            run();
    }

    private void waitForItemsAdderLoad() {
        Events.subscribe(ItemsAdderLoadDataEvent.class)
                .biHandler((s, e) -> {
                    s.unregister();
                    run();
                });
    }

    private void run() {
        /* Init conf & msgs & modifiers*/
        modifiers = new priceModifierManager();
        resourcesManager = resourceManager.generate();

        /* Initiate database + getAllItems + timer */
        dManager = new databaseManager();
        sManager = new shopsManager(dManager);

        resourcesManager.readYamlFiles();       // Read yaml files

        Schedulers.sync().runLater(RecordBook::initiate, 5, TimeUnit.SECONDS);      // Wait to not lock database

        try {
            Class.forName("io.github.divios.core_lib.inventory.materialsPrompt");  // loads all materials
        } catch (ClassNotFoundException ignored) {
        }

        try {
            Class.forName("io.github.divios.lib.dLib.confirmMenu.BuyConfirmMenu");  // loads Events
        } catch (ClassNotFoundException ignored) {
        }

    }

    @Override
    public void onDisable() {
        if (sManager == null) return;
        sManager.saveAllShopsToDatabase();
        dManager.finishAsyncQueries();
        sManager.getShops().forEach(dShop::destroy);
        serializerApi.stop();
    }

    public void reload() {
        resourcesManager.reload();
        Economies.reload();
    }

    public shopsManager getShopsManager() {
        return sManager;
    }

    public databaseManager getDatabaseManager() {
        return dManager;
    }

    public static DailyShop get() {
        return INSTANCE;
    }

    public resourceManager getResources() {
        return resourcesManager;
    }

    public priceModifierManager getPriceModifiers() {
        return modifiers;
    }

    public LocaleManager getLocaleLib() {
        return localeLib;
    }

    private void meetsStartRequirements() {
        if (!Utils.isOperative("Vault"))
            throw new RuntimeException("Vault is not installed");

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new RuntimeException("No economy provider found");
        }
    }

}
