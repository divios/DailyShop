package io.github.divios.dailyrandomshop.tasks;

import io.github.divios.dailyrandomshop.conf_msg;
import io.github.divios.dailyrandomshop.database.dataManager;
import io.github.divios.dailyrandomshop.events.expiredTimerEvent;
import org.bukkit.Bukkit;

class timerTask {

    private static final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();
    private static final dataManager dbManager = dataManager.getInstance();
    private static timerTask TimerTask = null;
    private int time = 0;

    private void timetTask() {
    }

    ;

    public static timerTask getInstance() {
        if (TimerTask == null) {
            TimerTask = new timerTask();
            TimerTask.time = dbManager.getTimer();
            TimerTask.initTimer();
        }
        return TimerTask;
    }

    private void initTimer() {

        Bukkit.getScheduler().runTaskTimerAsynchronously(main, () -> {
            if (time == 0) {
                Bukkit.getPluginManager().callEvent(new expiredTimerEvent());
                resetTimer();
                return;
            }
            if (time % 180 == 0) {
                dbManager.updateAsyncTimer(time);
            }
        }, 20L, 20L);
    }

    public void resetTimer() {
        time = conf_msg.TIMER;
    }

    public int getTimer() {
        return time;
    }
}
