package io.github.divios.lib.dLib.synchronizedGui.taskPool;

import com.google.common.collect.Sets;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.dailyShop.utils.DebugLog;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class updatePool {

    private static final Set<singleGui> bucket = Sets.newConcurrentHashSet();
    private static final ExecutorService asyncPool = Executors.newWorkStealingPool();

    static {
        Schedulers.async().runRepeating(() -> bucket.forEach(singleGui -> asyncPool.execute(singleGui::updateTask)),
                500, TimeUnit.MILLISECONDS, 500, TimeUnit.MILLISECONDS);
    }

    public static void subscribe(singleGui gui) {
        bucket.add(gui);
        DebugLog.info("Subscribed new gui, size: " + bucket.size());
    }

    public static void unsubscribe(singleGui gui) {
        bucket.remove(gui);
        DebugLog.info("New bucket size: " + bucket.size());
    }

    public static void stop() {
        asyncPool.shutdown();
        try {
            asyncPool.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
