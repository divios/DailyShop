package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.hooksManager;
import me.TechsCode.UltraEconomy.UltraEconomyAPI;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.entity.Player;

public class ultraEconomyE extends economy{

    private static final UltraEconomyAPI api = hooksManager.getInstance().getUltraEconomyApi();
    private final Currency currency;

    public ultraEconomyE(String currency) {
        super(currency, currency);
        this.currency = api.getCurrencies().name(currency).get();
    }

    @Override
    public void test() {
        api.getAccounts();
    }

    @Override
    public boolean hasMoney(Player p, Double price) {
        Account account = api.getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().orElse(null);
        return account != null && account.getBalance(currency).getOnHand() >= price;
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Account account = api.getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().orElse(null);
        if (account == null) return;
        account.getBalance(currency).removeHand(price.floatValue());
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Account account = api.getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().orElse(null);
        if (account == null) return;
        account.getBalance(currency).addHand(price.floatValue());
    }

    @Override
    public double getBalance(Player p) {
        return api.getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().get().getBalance(currency).getOnHand();
    }
}
