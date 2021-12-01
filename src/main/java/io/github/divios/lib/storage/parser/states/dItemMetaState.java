package io.github.divios.lib.storage.parser.states;

import io.github.divios.core_lib.utils.Primitives;
import io.github.divios.dailyShop.economies.economy;
import io.github.divios.dailyShop.economies.vault;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.dRarity;
import io.github.divios.lib.dLib.stock.factory.dStockFactory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class dItemMetaState {

    private String buyPrice;
    private String sellPrice;
    private String stock;
    private Integer set;
    private List<String> buyPerms;
    private List<String> sellPerms;
    private List<String> commands;
    private List<UUID> bundle;
    private boolean confirm_gui;
    private String rarity;
    private String econ;

    public static dItemMetaStateBuilder builder() {
        return new dItemMetaStateBuilder();
    }

    public static dItemMetaState of(dItem item) {
        return new dItemMetaState(item);
    }

    public static dItemMetaState of(ItemStack item) {
        return new dItemMetaState(item);
    }

    protected dItemMetaState() {
    }

    public dItemMetaState(dItem item) {
        buyPrice = item.getBuyPrice().get().toString().isEmpty() ? null : item.getBuyPrice().get().toString();
        sellPrice = item.getSellPrice().get().toString().isEmpty() ? null : item.getSellPrice().get().toString();
        stock = item.hasStock() ? item.getStock().getName() + ":" + item.getStock().getDefault() : null;
        set = item.getSetItems().orElse(null);
        buyPerms = item.getPermsBuy().orElse(null);
        sellPerms = item.getPermsSell().orElse(null);
        commands = item.getCommands().orElse(null);
        bundle = item.getBundle().orElse(null);
        confirm_gui = item.isConfirmGuiEnabled();
        rarity = item.getRarity().getKey();
        econ = item.getEconomy().getKey() + ":" + item.getEconomy().getCurrency();
    }

    public dItemMetaState(ItemStack item) {
        this(dItem.of(item));
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public String getStock() {
        return stock;
    }

    public Integer getSet() {
        return set;
    }

    public List<String> getBuyPerms() {
        return buyPerms;
    }

    public List<String> getSellPerms() {
        return sellPerms;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<UUID> getBundle() {
        return bundle;
    }

    public boolean isConfirm_gui() {
        return confirm_gui;
    }

    public String getRarity() {
        return rarity;
    }

    public String getEcon() {
        return econ;
    }

    public void applyValues(dItem item) {

        if (buyPrice != null) {
            String[] prices = buyPrice.split(":");
            if (prices.length == 2) item.setBuyPrice(Double.parseDouble(prices[0]), Double.parseDouble(prices[1]));
            else item.setBuyPrice(Double.parseDouble(prices[0]));
        } else item.setBuyPrice(-1);

        if (sellPrice != null) {
            String[] prices = sellPrice.split(":");
            if (prices.length == 2) item.setSellPrice(Double.parseDouble(prices[0]), Double.parseDouble(prices[1]));
            else item.setSellPrice(Double.parseDouble(prices[0]));
        } else item.setSellPrice(-1);

        if (stock != null) {
            String[] _stock = stock.split(":");
            if (_stock[0].equalsIgnoreCase("GLOBAL"))
                item.setStock(dStockFactory.GLOBAL(Integer.parseInt(_stock[1])));
            else if (_stock[0].equalsIgnoreCase("INDIVIDUAL"))
                item.setStock(dStockFactory.INDIVIDUAL(Integer.parseInt(_stock[1])));
        }

        if (set != null) {
            item.setSetItems(set);
            item.setQuantity(set);
        }

        if (!buyPerms.isEmpty()) item.setPermsBuy(buyPerms);
        if (!sellPerms.isEmpty()) item.setPermsSell(sellPerms);
        if (!commands.isEmpty()) item.setCommands(commands);
        if (!bundle.isEmpty()) item.setBundle(bundle);
        item.setConfirm_gui(confirm_gui);
        if (rarity != null) item.setRarity(dRarity.fromKey(rarity));

        try {
            String[] keys = econ.split(":");
            economy econ = economy.getFromKey(keys[0], keys.length == 2 ? keys[1] : "");
            econ.test();
            item.setEconomy(econ);
        } catch (Exception e) {
            item.setEconomy(new vault());
        }
    }

    public static final class dItemMetaStateBuilder {
        private String buyPrice;
        private String sellPrice;
        private String stock;
        private Integer set;
        private List<String> buyPerms;
        private List<String> sellPerms;
        private List<String> commands;
        private List<UUID> bundle;
        private boolean confirm_gui;
        private String rarity;
        private String econ;

        private dItemMetaStateBuilder() {
        }

        public static dItemMetaStateBuilder adItemMetaState() {
            return new dItemMetaStateBuilder();
        }

        public dItemMetaStateBuilder withBuyPrice(String buyPrice) {
            this.buyPrice = buyPrice;
            return this;
        }

        public dItemMetaStateBuilder withSellPrice(String sellPrice) {
            this.sellPrice = sellPrice;
            return this;
        }

        public dItemMetaStateBuilder withStock(String stock) {
            this.stock = stock;
            return this;
        }

        public dItemMetaStateBuilder withSet(Integer set) {
            this.set = set;
            return this;
        }

        public dItemMetaStateBuilder withBuyPerms(List<String> buyPerms) {
            this.buyPerms = buyPerms;
            return this;
        }

        public dItemMetaStateBuilder withSellPerms(List<String> sellPerms) {
            this.sellPerms = sellPerms;
            return this;
        }

        public dItemMetaStateBuilder withCommands(List<String> commands) {
            this.commands = commands;
            return this;
        }

        public dItemMetaStateBuilder withBundleStr(List<String> bundle) {
            return withBundle(bundle.stream().map(UUID::fromString).collect(Collectors.toList()));
        }

        public dItemMetaStateBuilder withBundle(List<UUID> bundle) {
            this.bundle = bundle;
            return this;
        }

        public dItemMetaStateBuilder withConfirm_gui(boolean confirm_gui) {
            this.confirm_gui = confirm_gui;
            return this;
        }

        public dItemMetaStateBuilder withRarity(String rarity) {
            this.rarity = rarity;
            return this;
        }

        public dItemMetaStateBuilder withEcon(String econ) {
            this.econ = econ;
            return this;
        }

        public dItemMetaState build() {
            runChecks();
            dItemMetaState dItemMetaState = new dItemMetaState();
            dItemMetaState.bundle = this.bundle;
            dItemMetaState.buyPrice = this.buyPrice;
            dItemMetaState.commands = this.commands;
            dItemMetaState.set = this.set;
            dItemMetaState.rarity = this.rarity;
            dItemMetaState.stock = this.stock;
            dItemMetaState.econ = this.econ;
            dItemMetaState.sellPrice = this.sellPrice;
            dItemMetaState.buyPerms = this.buyPerms;
            dItemMetaState.sellPerms = this.sellPerms;
            dItemMetaState.confirm_gui = this.confirm_gui;
            return dItemMetaState;
        }

        private void runChecks() {
            if (!Primitives.isDouble(buyPrice)) buyPrice = null;
            if (!Primitives.isDouble(sellPrice)) sellPrice = null;
            if (set <= 0) set = null;
            if (!Utils.testRunnable(() -> dRarity.fromKey(rarity))) rarity = "Common";
            if (!checkEconFormat()) econ = "Vault:";
        }

        private boolean checkEconFormat() {
            String[] econFormatted = econ.split(":");
            if (econFormatted.length != 2) return false;
            if (!Utils.testRunnable(() -> economy.getFromKey(econFormatted[0], econFormatted[1]).test())) return false;
            return true;
        }

    }
}
