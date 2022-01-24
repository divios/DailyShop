package io.github.divios.dailyShop;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.dailyShop.commands.commandsManager;
import io.github.divios.dailyShop.files.resourceManager;
import io.github.divios.dailyShop.hooks.Hooks;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jcommands.JCommands;
import io.github.divios.lib.dLib.dShop;
import io.github.divios.lib.dLib.priceModifiers.priceModifierManager;
import io.github.divios.lib.dLib.synchronizedGui.taskPool.updatePool;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.serialize.serializerApi;
import io.github.divios.lib.storage.databaseManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class DailyShop extends JavaPlugin {

    private static DailyShop INSTANCE;
    private resourceManager resourcesManager;
    private priceModifierManager modifiers;
    private databaseManager dManager;
    private shopsManager sManager;

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
        Utils.JTEXT_PARSER.getTemplates();     /* Init JText

        /* Init hooks  */
        Hooks.B_STATS.getApi();

        /* Init conf & msgs & modifiers*/
        modifiers = new priceModifierManager();
        resourcesManager = resourceManager.generate();

        /* Initiate database + getAllItems + timer */
        dManager = new databaseManager();
        sManager = new shopsManager(dManager);

        /* Load commands */
        new commandsManager().loadCommands();

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
        updatePool.stop();
    }

    public void reload() {
        resourcesManager.reload();
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


    private void meetsStartRequirements() {
        if (!Utils.isOperative("Vault"))
            throw new RuntimeException("Vault is not installed");

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new RuntimeException("No economy provider found");
        }
    }

}
