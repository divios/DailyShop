package io.github.divios.dailyShop;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.commands.CommandManager;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.commands.*;
import io.github.divios.dailyShop.files.configManager;
import io.github.divios.dailyShop.hooks.hooksManager;
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
        Core_lib.setPlugin(this);       // Set plugin for aux library

        localeManager = new LocaleManager();

                                /* Init hooks  */
        hooksManager.getInstance();

                                /* Init conf & msgs & modifiers*/
        modifiers = new priceModifierManager();
        configM = configManager.generate();

                                /* Initiate database + getAllItems + timer */
        databaseManager.getInstance();
        shopsManager.getInstance();
                                /* Register Commands */
        CommandManager.register(INSTANCE.getCommand("DailyShop"));
        registerAllCmds();

        CommandManager.setNotPerms(configM.getSettingsYml().PREFIX + configM.getLangYml().MSG_NOT_PERMS);
        CommandManager.setDefault(new helpCmd());
        Msg.setPREFIX(configM.getSettingsYml().PREFIX);

        try { Class.forName("io.github.divios.core_lib.inventory.materialsPrompt");  // loads all materials
        } catch (ClassNotFoundException ignored) {}

        try { Class.forName("io.github.divios.lib.dLib.confirmMenu.buyConfirmMenu");  // loads Events
        } catch (ClassNotFoundException ignored) {}

    }

    private void registerAllCmds() {
        CommandManager.addCommand(new add(), new helpCmd(),
                new open(), new manager(), new reStock(), new importShops(), new reload(), new logCmd());
    }

    @Override
    public void onDisable() {
        shopsManager.getInstance().getShops()       // Updates all the guis before disable
                .forEach(shop -> {
                    databaseManager.getInstance().updateGui(shop.getName(), shop.getGuis());
                });
    }

    public void reload() {
        configM.reload();
        Msg.setPREFIX(configM.getSettingsYml().PREFIX);
    }

    public static shopsManager getShopsManager() {
        return shopsManager.getInstance();
    }

    public static DailyShop get() {
        return INSTANCE;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public priceModifierManager getPriceModifiers() { return modifiers; }
}
