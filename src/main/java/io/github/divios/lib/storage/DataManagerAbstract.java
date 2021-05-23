package io.github.divios.lib.storage;

import io.github.divios.dailyrandomshop.DRShop;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

class DataManagerAbstract {

    private static DataManagerAbstract instance = null;
    protected final SQLiteConnector databaseConnector;
    protected final Plugin plugin;

    private static final Map<String, LinkedList<Runnable>> queues = new HashMap<>();

    public static DataManagerAbstract getInstance() {
        if (instance == null) {
            instance = new DataManagerAbstract(new SQLiteConnector(DRShop.getInstance()), DRShop.getInstance());
        }
        return instance;
    }

    private DataManagerAbstract(SQLiteConnector databaseConnector, Plugin plugin) {
        this.databaseConnector = databaseConnector;
        this.plugin = plugin;
    }

    /**
     * @return the prefix to be used by all table names
     */
    public String getTablePrefix() {
        return this.plugin.getDescription().getName().toLowerCase() + '_';
    }


    /**
     * Queue a task to be run asynchronously. <br>
     *
     * @param runnable task to run
     */
    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Queue a task to be run synchronously.
     *
     * @param runnable task to run on the next server tick
     */
    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    /**
     * Queue tasks to be ran asynchronously.
     *
     * @param runnable task to put into queue.
     * @param queueKey the queue key to add the runnable to.
     */
    public void queueAsync(Runnable runnable, String queueKey) {
        if (queueKey == null) return;
        List<Runnable> queue = queues.computeIfAbsent(queueKey, t -> new LinkedList<>());
        queue.add(runnable);
        if (queue.size() == 1) runQueue(queueKey);
    }

    private void runQueue(String queueKey) {
        doQueue(queueKey, (s) -> {
            if (!queues.get(queueKey).isEmpty())
                runQueue(queueKey);
        });
    }

    private void doQueue(String queueKey, Consumer<Boolean> callback) {
        Runnable runnable = queues.get(queueKey).getFirst();
        async(() -> {
            runnable.run();
            sync(() -> {
                queues.get(queueKey).remove(runnable);
                callback.accept(true);
            });
        });
    }

}