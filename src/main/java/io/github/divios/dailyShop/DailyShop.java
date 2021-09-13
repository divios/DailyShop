package io.github.divios.dailyShop;

import io.github.divios.core_lib.Core_lib;
import io.github.divios.core_lib.commands.CommandManager;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.commands.*;
import io.github.divios.dailyShop.files.configManager;
import io.github.divios.dailyShop.hooks.hooksManager;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.dataManager;
import me.pikamug.localelib.LocaleManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DailyShop extends JavaPlugin {

    private static DailyShop INSTANCE;
    private LocaleManager localeManager;
    public configManager configM;

    @Override
    public void onEnable() {

        INSTANCE = this;
        Core_lib.setPlugin(this);       // Set plugin for aux library

        localeManager = new LocaleManager();

                                /* Init hooks  */
        hooksManager.getInstance();

                                /* Init conf & msgs*/
        configM = configManager.generate();

                                /* Initiate database + getAllItems + timer */
        dataManager.getInstance();
        shopsManager.getInstance();

                                /* Register Commands */
        CommandManager.register(INSTANCE.getCommand("DailyShop"));
        CommandManager.addCommand(new add(), new helpCmd(),
                new open(), new manager(), new reStock(), new importShops(), new reload(), new parser());

        CommandManager.setNotPerms(configM.getSettingsYml().PREFIX + configM.getLangYml().MSG_NOT_PERMS);
        CommandManager.setDefault(new helpCmd());
        Msg.setPREFIX(configM.getSettingsYml().PREFIX);

        try { Class.forName("io.github.divios.core_lib.inventory.materialsPrompt");  // loads all materials
        } catch (ClassNotFoundException ignored) {}

    }

    @Override
    public void onDisable() {
        shopsManager.getInstance().getShops()       // Updates all the guis before disable
                .forEach(shop -> dataManager.getInstance().syncUpdateGui(shop.getName(), shop.getGuis()));
    }

    public void reloadPlugin() {
        configM.reload();
        //shopsManager.getInstance().reload();
    }

    public static shopsManager getShopsManager() {
        return shopsManager.getInstance();
    }

    public static DailyShop getInstance() {
        return INSTANCE;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }
}
