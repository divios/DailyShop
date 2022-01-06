package io.github.divios.dailyShop.economies;

import io.github.divios.dailyShop.hooks.Hooks;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.entity.Player;

public class ultraEconomyE extends Economy {

    private final Currency currency;

    ultraEconomyE(String currency) {
        super(currency, currency, Economies.ultraEconomy);
        this.currency = Hooks.ULTRA_ECONOMY.getApi().getCurrencies().name(currency).get();
    }

    @Override
    public void test() {
        Hooks.ULTRA_ECONOMY.getApi().getAccounts();
    }

    @Override
    public void witchDrawMoney(Player p, Double price) {
        Account account = Hooks.ULTRA_ECONOMY.getApi().getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().orElse(null);
        if (account == null) return;
        account.getBalance(currency).removeHand(price.floatValue());
    }

    @Override
    public void depositMoney(Player p, Double price) {
        Account account = Hooks.ULTRA_ECONOMY.getApi().getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().orElse(null);
        if (account == null) return;
        account.getBalance(currency).addHand(price.floatValue());
    }

    @Override
    public double getBalance(Player p) {
        return Hooks.ULTRA_ECONOMY.getApi().getAccounts().stream()
                .filter(account1 -> account1.getUuid().equals(p.getUniqueId()))
                .findFirst().orElse(null).getBalance(currency).getOnHand();
    }
}
