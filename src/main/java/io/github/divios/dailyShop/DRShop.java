package io.github.divios.dailyShop;

import io.github.divios.core_lib.commands.CommandManager;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.commands.*;
import io.github.divios.dailyShop.hooks.hooksManager;
import io.github.divios.dailyShop.utils.conf_updater;
import io.github.divios.lib.managers.shopsManager;
import io.github.divios.lib.storage.dataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DRShop extends JavaPlugin {

    private static DRShop INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        INSTANCE.saveDefaultConfig();
                                /* Init hooks  */
        hooksManager.getInstance();
                                /* Init conf & msgs*/
        conf_msg.init();
                                /* Initiate database + getAllItems + timer */
        conf_updater.check();
        dataManager.getInstance();
        shopsManager.getInstance();

        CommandManager.register(INSTANCE.getCommand("DailyShop"));
        CommandManager.addCommand(new add(), new helpCmd(),
                new open(), new Manager(), new reStock(), new importShops(), new reload());
        CommandManager.setNotPerms(conf_msg.PREFIX + conf_msg.MSG_NOT_PERMS);
        CommandManager.setDefault(new helpCmd());

        Msg.setPREFIX(conf_msg.PREFIX);

        try {
            Class.forName("io.github.divios.core_lib.inventory.materialsPrompt");  // loads all materials
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        shopsManager.getInstance().getShops()       // Updates all the guis before disable
                .forEach(shop -> dataManager.getInstance()
                        .syncUpdateGui(shop.getName(), shop.getGui()));
    }

    public void reloadPlugin() {
        conf_msg.reload();

        //shopsManager.getInstance().reload();
    }

    public static DRShop getInstance() {
        return INSTANCE;
    }

}
