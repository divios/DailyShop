package io.github.divios.dailyrandomshop.transaction;

import io.github.divios.dailyrandomshop.conf_msg;
import org.bukkit.entity.Player;

public class transactionExc extends Exception{

    private final err motive;

    public transactionExc(err motive) {
        this.motive = motive;
    }


    public void sendErrorMsg(Player p) { //TODO
        switch (motive) {
            case noMoney:
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_ENOUGH_MONEY);
                break;
            case noPerms:
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_NOT_PERMS_ITEM);
                break;
            case noSpace:
                p.sendMessage(conf_msg.PREFIX + conf_msg.MSG_INVENTORY_FULL);
                break;
        }

    }

    public enum err {
        noPerms,
        noMoney,
        noSpace
    }

}
