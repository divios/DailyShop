package io.github.divios.dailyrandomshop.tasks;

import org.bukkit.Bukkit;

public class taskManager {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static taskManager instance = null;
    private listsTask ListTask;
    private timerTask TimerTask;

    private taskManager() {
    }

    public static taskManager getInstance() {
        if (instance == null) {
            instance = new taskManager();
            instance.ListTask = listsTask.getInstance();
            instance.TimerTask = timerTask.getInstance();
        }
        return instance;
    }

    public void resetTimer() {
        TimerTask.resetTimer();
    }

    public int getTimer() {
        return TimerTask.getTimer();
    }


}
