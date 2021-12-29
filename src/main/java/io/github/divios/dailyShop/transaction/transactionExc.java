package io.github.divios.dailyShop.transaction;

import io.github.divios.dailyShop.files.Messages;
import org.bukkit.entity.Player;

public class transactionExc extends Exception {

    private final err motive;

    public transactionExc(err motive) {
        this.motive = motive;
    }


    public void sendErrorMsg(Player p) {
        switch (motive) {
            case noMoney:
                Messages.MSG_NOT_MONEY.send(p);
                break;
            case noPerms:
                Messages.MSG_NOT_PERMS_ITEM.send(p);
                break;
            case noSpace:
                Messages.MSG_INV_FULL.send(p);
                break;
            case noStock:
                Messages.MSG_NOT_STOCK.send(p);
                break;
        }

    }

    public enum err {
        noPerms,
        noMoney,
        noSpace,
        noStock
    }

}
