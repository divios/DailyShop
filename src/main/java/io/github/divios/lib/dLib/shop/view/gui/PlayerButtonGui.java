package io.github.divios.lib.dLib.shop.view.gui;

import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.shop.view.buttons.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerButtonGui extends ButtonGui {

    private final Player player;

    public PlayerButtonGui(Player player, ButtonGui gui) {
        super(gui.getTitle(), Bukkit.createInventory(null, gui.getSize(), Utils.JTEXT_PARSER.parse(gui.getTitle())));
        this.player = player;

        buttons.putAll(gui.getButtons());
        update();

        player.openInventory(getInv());
    }

    @Override
    public void open(Player p) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setButton(int slot, Button button) {
        super.setButton(slot, button);
        inv.setItem(slot, button.getItem(player));
    }

    @Override
    public void update() {
        buttons.forEach((integer, button) -> {
            if ((integer < inv.getSize()) && (integer >= 0))
                inv.setItem(integer, button.getItem(player));
        });
    }

}
