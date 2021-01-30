package io.github.divios.dailyrandomshop;

import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.listeners.ExpiredTimerListener;
import io.github.divios.dailyrandomshop.tasks.taskManager;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin {

    private static io.github.divios.dailyrandomshop.main main;

    @Override
    public void onEnable() {
        main = this;
        main.saveDefaultConfig();
                                /* Init conf & msgs*/
        conf_msg.init();
                                /* Initiate database + getAllItems + timer */
        dataManager.getInstance();
                                /* Initiate tasks */
        taskManager.getInstance();
                                /* Register commands & tabComplete */
        commands.getInstance();
        tabComplete.getInstance();
                                /* Register Listeners */
        ExpiredTimerListener.getInstance();

    }

    @Override
    public void onDisable() {
        dataManager.getInstance().updateSyncBuyItems();
        dataManager.getInstance().updateSyncSellItems();
        dataManager.getInstance().updateSyncTimer(taskManager.getInstance().getTimer());
    }

    public void realoadPlugin() {
        conf_msg.reload();
    }

    public static io.github.divios.dailyrandomshop.main getInstance() {
        return main;
    }

}
