package io.github.divios.lib.dLib.synchronizedGui.taskPool;

import com.google.common.collect.Sets;
import io.github.divios.core_lib.scheduler.Schedulers;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;

import java.util.Set;

public class updatePool {

    private static final Set<singleGui> bucket = Sets.newConcurrentHashSet();

    static {
        Schedulers.async().runRepeating(() -> bucket.forEach(singleGui::updateTask),
                10L, 10L);
    }

    public static void subscribe(singleGui gui) {
        bucket.add(gui);
    }

    public static void unsubscribe(singleGui gui) {
        bucket.remove(gui);
    }


}
