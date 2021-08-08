package io.github.divios.lib.dLib.synchronizedGui;

import io.github.divios.lib.dLib.dGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.Semaphore;

public abstract class abstractSyncMenu implements syncMenu {

    protected final Map<UUID, dGui> guis;
    protected dGui base;
    protected final Semaphore turn;
    protected boolean isAvailable = true;   // TODO

    protected abstractSyncMenu() {
        this.guis = createMap();

        this.turn = new Semaphore(1);
    }

    protected abstract Map<UUID, dGui> createMap();

    @Override
    public void generate(Player p) {
        acquire();
        dGui newGui = base.clone();
        guis.put(p.getUniqueId(), newGui);
        newGui.open(p);
        turn.release();
    }

    @Override
    public dGui get(UUID key) {
        return guis.get(key);
    }

    @Override
    public Collection<dGui> getMenus() {
        acquire();
        Collection<dGui> guis = Collections.unmodifiableCollection(this.guis.values());
        turn.release();
        return guis;
    }

    @Override
    public int size() {
        return guis.size();
    }

    @Override
    public void invalidate(UUID key) {
        dGui removed = guis.remove(key);
        if (removed == null) return;
        // destroy gui //TODO
    }

    @Override
    public void invalidateAll() {
        // close all guis TODO
        this.guis.clear();
    }

    @Override
    public void renovate() {
        acquire();
        Set<UUID> players = guis.keySet();
        // close all inventories
        //base.renovate();
        // update all inventories with the base // TODO
        turn.release();

        players.forEach(uuid -> {           // re-opens inventory for all
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;
            this.generate(p);
        });
    }

    @Override
    public void manageItems(Player p) {
        // Open inventory manager TODO
    }

    @Override
    public void customizeGui(Player p) {
        acquire();
        // close al inventories and open gui TODO
        turn.release();
    }

    @Override
    public abstract String toJson();

    private void acquire() {
        try { turn.acquire(); }
        catch (Exception e) { e.printStackTrace(); }
    }


}
