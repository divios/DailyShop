package io.github.divios.lib.dLib;

import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public enum dAction {

    EMPTY((p, s) -> {
    }),

    OPEN_SHOP((p, s) -> {
        DailyShop.get().getShopsManager().getShop(s).ifPresent(shop1 -> shop1.openShop(p));
    }),

    RUN_CMD((p, s) -> {
        for (String command : s.split(";:")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    Utils.JTEXT_PARSER
                            .withTag("%", "%")
                            .withTemplate("player", p.getName())
                            .parse(command));
        }
    }),

    RUN_PLAYER_CMD((p, s) -> {
        for (String command : s.split(";:")) {
            p.performCommand(Utils.JTEXT_PARSER
                    .withTag("%", "%")
                    .withTemplate("player", p.getName())
                    .parse(command, p));
        }
    }),

    SHOW_ALL_ITEMS((p, s) -> {
        shopsManager manager = DailyShop.get().getShopsManager();
        loreStrategy strategy = new shopItemsLore();
        new dynamicGui.Builder()
                .contents(() -> manager.getShop(s)
                        .get().getItems()
                        .parallelStream()
                        .map(dItem -> strategy.applyLore(dItem.getDailyItem().clone(), p))
                        .collect(Collectors.toList()))
                .title(integer -> "&6&l" + manager.getShop(s).get().getName() + " items")
                .back(player -> manager.getShop(s).get().openShop(p))
                .plugin(DailyShop.get())
                .setSearch(false)
                .open(p);
    });

    private final BiConsumer<Player, String> action;

    dAction(BiConsumer<Player, String> action) {
        this.action = action;
    }

    public void run(Player p, String s) {
        action.accept(p, s);
    }

    @Override
    public String toString() {
        return "dAction{" +
                "action=" + action +
                '}';
    }
}