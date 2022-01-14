package io.github.divios.dailyShop.files;

import com.google.common.base.Splitter;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.jtext.wrappers.Template;
import org.bukkit.entity.Player;

import java.util.List;

public enum Lang {

    CONFIRM_GUI_BUY_NAME("lang.confirm_gui.buy_name"),
    CONFIRM_GUI_SELL_NAME("lang.confirm_gui.sell_name"),
    CONFIRM_GUI_ACTION_NAME("lang.confirm_gui.action_name"),
    CONFIRM_GUI_ADD_PANE("lang.confirm_gui.add_pane"),
    CONFIRM_GUI_REMOVE_PANE("lang.confirm_gui.remove_pane"),
    CONFIRM_GUI_SET_PANE("lang.confirm_gui.set_pane"),
    CONFIRM_GUI_RETURN_NAME("lang.confirm_gui.return_name"),
    CONFIRM_GUI_RETURN_PANE_LORE("lang.confirm_gui.return_pane_lore"),
    CONFIRM_GUI_SELL_ITEM("lang.confirm_gui.sell_item"),
    CONFIRM_GUI_YES("lang.confirm_gui.yes_name"),
    CONFIRM_GUI_YES_LORE("lang.confirm_gui.yes_lore"),
    CONFIRM_GUI_NO("lang.confirm_gui.no_name"),
    CONFIRM_GUI_NO_LORE("lang.confirm_gui.no_lore"),
    CONFIRM_GUI_STATS_NAME("lang.confirm_gui.stats_name"),
    CONFIRM_GUI_STATS_LORE("lang.confirm_gui.stats_lore"),

    SHOPS_ITEMS_LORE("lang.shop_items.lore"),
    SHOPS_ITEMS_STOCK("lang.shop_items.stock"),

    SHOPS_MANAGER_TITLE("lang.shops_manager.title"),
    SHOPS_MANAGER_CREATE("lang.shops_manager.create"),
    SHOPS_MANAGER_CREATE_LORE("lang.shops_manager.create_lore"),
    SHOPS_MANAGER_RETURN("lang.shops_manager.return"),
    SHOPS_MANAGER_RETURN_LORE("lang.shops_manager.return_lore"),
    SHOPS_MANAGER_LORE("lang.shops_manager.lore"),

    ADD_ITEMS_TITLE("lang.add_item_gui.title"),
    ADD_ITEMS_FROM_ZERO("lang.add_item_gui.from_zero"),
    ADD_ITEMS_FROM_ZERO_LORE("lang.add_item_gui.from_zero_lore"),
    ADD_ITEMS_FROM_EXISTING("lang.add_item_gui.from_existing"),
    ADD_ITEMS_FROM_EXISTING_LORE("lang.add_item_gui.from_existing_lore"),
    ADD_ITEMS_FROM_BUNDLE("lang.add_item_gui.bundle"),
    ADD_ITEMS_FROM_BUNDLE_LORE("lang.add_item_gui.bundle_lore"),
    ADD_ITEMS_RETURN("lang.add_item_gui.return"),
    ADD_ITEMS_RETURN_LORE("lang.add_item_gui.return_lore"),

    DAILY_ITEMS_TITLE("lang.daily_items_menu.title"),
    DAILY_ITEMS_ADD("lang.daily_items_menu.add"),
    DAILY_ITEMS_ADD_LORE("lang.daily_items_menu.add_lore"),
    DAILY_ITEMS_NEXT("lang.daily_items_menu.next"),
    DAILY_ITEMS_PREVIOUS("lang.daily_items_menu.previous"),
    DAILY_ITEMS_RETURN("lang.daily_items_menu.return"),
    DAILY_ITEMS_BUY_PRICE("lang.daily_items_menu.buy_price"),
    DAILY_ITEMS_SELL_PRICE("lang.daily_items_menu.sell_price"),
    DAILY_ITEMS_STOCK("lang.daily_items_menu.stock"),
    DAILY_ITEMS_CURRENCY("lang.daily_items_menu.currency"),
    DAILY_ITEMS_RARITY("lang.daily_items_menu.rarity"),
    DAILY_ITEMS_BUY_FOR("lang.daily_items_menu.buy_for"),
    DAILY_ITEMS_MANAGER_LORE("lang.daily_items_menu.manager_items_lore"),

    CUSTOMIZE_TITLE("lang.customize_menu.title"),
    CUSTOMIZE_CRAFT("lang.customize_menu.craft"),
    CUSTOMIZE_CRAFT_LORE("lang.customize_menu.craft_lore"),
    CUSTOMIZE_RETURN("lang.customize_menu.return"),
    CUSTOMIZE_RETURN_LORE("lang.customize_menu.return_lore"),
    CUSTOMIZE_UNAVAILABLE("lang.customize_menu.unavailable"),
    CUSTOMIZE_PRICE_NAME("lang.customize_menu.price.name"),
    CUSTOMIZE_PRICE_LORE("lang.customize_menu.price.lore"),
    CUSTOMIZE_ECON_NAME("lang.customize_menu.econ.name"),
    CUSTOMIZE_ECON_LORE("lang.customize_menu.econ.lore"),
    CUSTOMIZE_RARITY_NAME("lang.customize_menu.rarity.name"),
    CUSTOMIZE_RARITY_TYPES("lang.customize_menu.rarity.types"),
    CUSTOMIZE_SEARCH_NAME("lang.customize_menu.search.name"),
    CUSTOMIZE_SEARCH_LORE("lang.customize_menu.search.lore"),
    CUSTOMIZE_SEARCH_CANCEL("lang.customize_menu.search.cancel"),
    CUSTOMIZE_CONFIRM_GUI_NAME("lang.customize_menu.confirm_gui.name"),
    CUSTOMIZE_CONFIRM_GUI_LORE("lang.customize_menu.confirm_gui.lore"),
    CUSTOMIZE_RENAME_NAME("lang.customize_menu.rename.name"),
    CUSTOMIZE_RENAME_LORE("lang.customize_menu.rename.lore"),
    CUSTOMIZE_RENAME_TITLE("lang.customize_menu.rename.title"),
    CUSTOMIZE_MATERIAL_NAME("lang.customize_menu.material.name"),
    CUSTOMIZE_MATERIAL_LORE("lang.customize_menu.material.lore"),
    CUSTOMIZE_LORE_NAME("lang.customize_menu.lore.name"),
    CUSTOMIZE_LORE_LORE("lang.customize_menu.lore.lore"),
    CUSTOMIZE_LORE_TITLE("lang.customize_menu.lore.title"),
    CUSTOMIZE_ENCHANTS_NAME("lang.customize_menu.enchants.name"),
    CUSTOMIZE_ENCHANTS_LORE("lang.customize_menu.enchants.lore"),
    CUSTOMIZE_STOCK_NAME("lang.customize_menu.stock.name"),
    CUSTOMIZE_STOCK_LORE("lang.customize_menu.stock.lore"),
    CUSTOMIZE_STOCK_LORE_ON("lang.customize_menu.stock.lore_on"),
    CUSTOMIZE_BUNDLE_NAME("lang.customize_menu.bundle.name"),
    CUSTOMIZE_BUNDLE_LORE("lang.customize_menu.bundle.lore"),
    CUSTOMIZE_DURABILITY_NAME("lang.customize_menu.durability.name"),
    CUSTOMIZE_DURABILITY_LORE("lang.customize_menu.durability.lore"),
    CUSTOMIZE_COMMANDS_NAME("lang.customize_menu.commands.name"),
    CUSTOMIZE_COMMANDS_LORE("lang.customize_menu.commands.lore"),
    CUSTOMIZE_COMMANDS_NAME_ON("lang.customize_menu.commands.name_on"),
    CUSTOMIZE_COMMANDS_LORE_ON("lang.customize_menu.commands.lore_on"),
    CUSTOMIZE_COMMANDS_TITLE("lang.customize_menu.commands.title"),
    CUSTOMIZE_PERMS_NAME("lang.customize_menu.perms.name"),
    CUSTOMIZE_PERMS_NAME_BUY("lang.customize_menu.perms.name_buy"),
    CUSTOMIZE_PERMS_NAME_SELL("lang.customize_menu.perms.name_sell"),
    CUSTOMIZE_PERMS_LORE_DEFAULT("lang.customize_menu.perms.lore_default"),
    CUSTOMIZE_PERMS_LORE("lang.customize_menu.perms.lore"),
    CUSTOMIZE_PERMS_LORE_ON("lang.customize_menu.perms.lore_on"),
    CUSTOMIZE_SET_NAME("lang.customize_menu.set.name"),
    CUSTOMIZE_SET_LORE("lang.customize_menu.set.lore"),
    CUSTOMIZE_SET_LORE_ON("lang.customize_menu.set.lore_on"),
    CUSTOMIZE_TOGGLE_ENCHANTS_NAME("lang.customize_menu.toggle.enchants.name"),
    CUSTOMIZE_TOGGLE_ENCHANTS_LORE("lang.customize_menu.toggle.enchants.lore"),
    CUSTOMIZE_TOGGLE_ATTRIBUTES_NAME("lang.customize_menu.toggle.attributes.name"),
    CUSTOMIZE_TOGGLE_ATTRIBUTES_LORE("lang.customize_menu.toggle.attributes.lore"),
    CUSTOMIZE_TOGGLE_EFFECTS_NAME("lang.customize_menu.toggle.effects.name"),
    CUSTOMIZE_TOGGLE_EFFECTS_LORE("lang.customize_menu.toggle.effects.lore"),

    BUY_ACTION_NAME("lang.messages.buy_action"),
    SELL_ACTION_NAME("lang.messages.sell_action");


    private final String path;

    Lang(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getAsString() {
        return getAsString((Player) null);
    }

    public String getAsString(Player p) {
        return Utils.JTEXT_PARSER.parse(getStringFromPath(), p);
    }

    public String getAsString(Template... templates) {
        return getAsString(null, templates);
    }

    public String getAsString(Player p, Template... templates) {
        return Utils.JTEXT_PARSER
                .withTemplate(templates)
                .parse(getStringFromPath(), p);
    }

    public List<String> getAsListString() {
        return getAsListString((Player) null);
    }

    public List<String> getAsListString(Player p) {
        return Utils.JTEXT_PARSER.parse(getListStringFromPath(), p);
    }

    public List<String> getAsListString(Template... templates) {
        return getAsListString(null, templates);
    }

    public List<String> getAsListString(Player p, Template... templates) {
        return Utils.JTEXT_PARSER
                .withTemplate(templates)
                .parse(getListStringFromPath(), p);
    }

    private String getStringFromPath() {
        return DailyShop.get().getResources().getLangYml().getString(path);
    }

    private List<String> getListStringFromPath() {
        return Splitter.on("|").splitToList(getStringFromPath());
    }

}
