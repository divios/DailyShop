package io.github.divios.lib.dLib.shop.cashregister.exceptions;

import io.github.divios.dailyShop.files.Messages;
import org.bukkit.entity.Player;

public class IllegalPrecondition extends RuntimeException {

    private final Messages messageErr;

    public IllegalPrecondition(Messages messageErr) {
        this.messageErr = messageErr;
    }

    public void sendErrMsg(Player p) {
        messageErr.send(p);
    }

}
