package io.github.divios.dailyShop;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.commands.commandsManager;
import io.github.divios.dailyShop.files.configManager;
import io.github.divios.dailyShop.hooks.hooksManager;
import io.github.divios.jcommands.JCommands;
import io.github.divios.lib.dLib.priceModifiers.priceModifierManager;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.databaseManager;
import me.pikamug.localelib.LocaleManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class DailyShop extends JavaPlugin {

    private static DailyShop INSTANCE;
    private LocaleManager localeManager;
    public configManager configM;
    private priceModifierManager modifiers;
    private databaseManager dManager;
    private shopsManager sManager;

    public DailyShop() {
        super();
    }

    protected DailyShop(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file)
    {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onEnable() {

        INSTANCE = this;
        Core_lib.setPlugin(this);       /* Set plugin for aux libraries */
        JCommands.register(this);

        localeManager = new LocaleManager();

                                /* Init hooks  */
        hooksManager.getInstance();

                                /* Init conf & msgs & modifiers*/
        modifiers = new priceModifierManager();
        configM = configManager.generate();

                                /* Initiate database + getAllItems + timer */
        dManager = new databaseManager();
        sManager = new shopsManager(dManager);

                                /* Load commands */
        new commandsManager().loadCommands();

                                /* Register prefix */
        Msg.setPREFIX(configM.getSettingsYml().PREFIX);

        try { Class.forName("io.github.divios.core_lib.inventory.materialsPrompt");  // loads all materials
        } catch (ClassNotFoundException ignored) {}

        try { Class.forName("io.github.divios.lib.dLib.confirmMenu.buyConfirmMenu");  // loads Events
        } catch (ClassNotFoundException ignored) {}

    }

    @Override
    public void onDisable() {
        sManager.getShops()       // Updates all the guis before disable
                .forEach(shop -> dManager.updateGui(shop.getName(), shop.getGuis()));
    }

    public void reload() {
        configM.reload();
        Msg.setPREFIX(configM.getSettingsYml().PREFIX);
    }

    public shopsManager getShopsManager() {
        return sManager;
    }

    public databaseManager getDatabaseManager() { return dManager; }

    public static DailyShop get() {
        return INSTANCE;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public priceModifierManager getPriceModifiers() { return modifiers; }
}
