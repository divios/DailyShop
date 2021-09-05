package io.github.divios.dailyShop.transaction;

import io.github.divios.core_lib.misc.Msg;
import io.github.divios.dailyShop.DailyShop;
import org.bukkit.entity.Player;

public class transactionExc extends Exception{

    private final err motive;
    private final static DailyShop plugin = DailyShop.getInstance();

    public transactionExc(err motive) {
        this.motive = motive;
    }


    public void sendErrorMsg(Player p) {
        switch (motive) {
            case noMoney:
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_MONEY);
                break;
            case noPerms:
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_PERMS_ITEM);
                break;
            case noSpace:
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_INV_FULL);
                break;
            case noStock:
                Msg.sendMsg(p, plugin.configM.getLangYml().MSG_NOT_STOCK);
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
