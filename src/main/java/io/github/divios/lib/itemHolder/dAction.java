package io.github.divios.lib.itemHolder;

import io.github.divios.core_lib.inventory.dynamicGui;
import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyrandomshop.DRShop;
import io.github.divios.dailyrandomshop.guis.settings.shopGui;
import io.github.divios.dailyrandomshop.lorestategy.loreStrategy;
import io.github.divios.dailyrandomshop.lorestategy.shopItemsLore;
import io.github.divios.lib.managers.shopsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public enum dAction {

    EMPTY((p,s) -> {}),
    OPEN_SHOP((p, s) -> {
        shopsManager.getInstance()
                .getShop(s).ifPresent(shop1 -> shop1.getGui().open(p));}),
    RUN_CMD((p, s) -> {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                Msg.singletonMsg(s).add("%player%", p.getName()).build());
    }),

    SHOW_ALL_ITEMS((p,s) -> {           //TODO: create dyanmic gui with all items in shop
        new dynamicGui.Builder()
                .contents(() -> shopsManager.getInstance().getShop(s)
                        .get().getItems().stream()
                        .map(dItem -> {
                            loreStrategy strategy = new shopItemsLore(dShop.dShopT.buy);
                            ItemStack aux = dItem.getRawItem().clone();
                            strategy.setLore(aux);
                            return aux;
                        }).collect(Collectors.toList()))
                .back(player -> shopGui.open(p, s))
                .plugin(DRShop.getInstance())
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