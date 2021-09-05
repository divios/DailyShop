package io.github.divios.lib.dLib.synchronizedGui.taskPool;

import com.google.common.collect.Sets;
import io.github.divios.core_lib.Schedulers;
import io.github.divios.lib.dLib.synchronizedGui.singleGui.singleGui;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class updatePool {

    private static final Set<singleGui> bucket = Sets.newConcurrentHashSet();

    static {
        Schedulers.async().runRepeating(() -> bucket.stream().parallel().forEach(singleGui::updateTask),
                10L, 10L);
    }

    public static void subscribe(singleGui gui) {
        bucket.add(gui);
    }

    public static void cancel(singleGui gui) {
        bucket.remove(gui);
    }


}
