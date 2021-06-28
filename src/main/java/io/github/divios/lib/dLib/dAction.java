package io.github.divios.lib.dLib;

import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.core_lib.misc.Task;
import io.github.divios.dailyShop.DRShop;
import io.github.divios.dailyShop.lorestategy.loreStrategy;
import io.github.divios.dailyShop.lorestategy.shopItemsLore;
import io.github.divios.dailyShop.utils.utils;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum dAction {

    EMPTY((p, s) -> {
    }),
    OPEN_SHOP((p, s) -> {
        shopsManager.getInstance()
                .getShop(s).ifPresent(shop1 -> shop1.getGui().open(p));
    }),
    RUN_CMD((p, s) -> {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                Msg.singletonMsg(s).add("%player%", p.getName()).build());
    }),

    SHOW_ALL_ITEMS((p, s) -> {
        loreStrategy strategy = new shopItemsLore(dShop.dShopT.buy);
        new dynamicGui.Builder()
                .contents(() -> shopsManager.getInstance().getShop(s)
                        .get().getItems().parallelStream()
                        .map(dItem -> dItem.getItem().clone())
                        .peek(strategy::setLore)
                        .collect(Collectors.toList()))
                .title(integer -> "&6&l" + shopsManager.getInstance().getShop(s).get().getName() + " items")
                .back(player -> shopsManager.getInstance().getShop(s).get().openGui(p))
                .plugin(DRShop.getInstance())
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

}