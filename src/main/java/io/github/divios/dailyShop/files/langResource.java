package io.github.divios.dailyShop.files;

import com.google.common.base.Splitter;

import java.util.List;

public class langResource extends resource{

    // CONFIRM GUI
    public String CONFIRM_GUI_BUY_NAME;
    public String CONFIRM_GUI_SELL_NAME;
    public String CONFIRM_GUI_ACTION_NAME;
    public String CONFIRM_GUI_ADD_PANE;
    public String CONFIRM_GUI_REMOVE_PANE;
    public String CONFIRM_GUI_RETURN_NAME;
    public String CONFIRM_GUI_RETURN_PANE_LORE;
    public String CONFIRM_GUI_SELL_ITEM;
    public String CONFIRM_GUI_YES;
    public List<String> CONFIRM_GUI_YES_LORE;
    public String CONFIRM_GUI_NO;
    public List<String> CONFIRM_GUI_NO_LORE;

    // SHOPS ITEMS

    public List<String> SHOPS_ITEMS_LORE;

    // SHOPS MANAGER

    public String SHOPS_MANAGER_TITLE;
    public String SHOPS_MANAGER_CREATE;
    public List<String> SHOPS_MANAGER_CREATE_LORE;
    public String SHOPS_MANAGER_RETURN;
    public List<String> SHOPS_MANAGER_RETURN_LORE;
    public List<String> SHOPS_MANAGER_LORE;

    // ADD ITEMS GUI

    public String ADD_ITEMS_TITLE;
    public String ADD_ITEMS_FROM_ZERO;
    public List<String> ADD_ITEMS_FROM_ZERO_LORE;
    public String ADD_ITEMS_FROM_EXISTING;
    public List<String> ADD_ITEMS_FROM_EXISTING_LORE;
    public String ADD_ITEMS_FROM_BUNDLE;
    public List<String> ADD_ITEMS_FROM_BUNDLE_LORE;
    public String ADD_ITEMS_RETURN;
    public List<String> ADD_ITEMS_RETURN_LORE;

    // DAILY ITEMS MENU

    public String DAILY_ITEMS_TITLE;
    public String DAILY_ITEMS_ADD;
    public List<String> DAILY_ITEMS_ADD_LORE;
    public String DAILY_ITEMS_NEXT;
    public String DAILY_ITEMS_PREVIOUS;
    public String DAILY_ITEMS_RETURN;
    public String DAILY_ITEMS_BUY_PRICE;
    public String DAILY_ITEMS_SELL_PRICE;
    public String DAILY_ITEMS_STOCK;
    public String DAILY_ITEMS_CURRENCY;
    public String DAILY_ITEMS_RARITY;
    public String DAILY_ITEMS_BUY_FOR;
    public List<String> DAILY_ITEMS_MANAGER_LORE;

    // CUSTOMIZE MENU

    public String CUSTOMIZE_TITLE;
    public String CUSTOMIZE_CRAFT;
    public List<String> CUSTOMIZE_CRAFT_LORE;

    public String CUSTOMIZE_RETURN;
    public List<String> CUSTOMIZE_RETURN_LORE;

    public String CUSTOMIZE_UNAVAILABLE;

    public String CUSTOMIZE_PRICE_NAME;
    public List<String> CUSTOMIZE_PRICE_LORE;

    public String CUSTOMIZE_ECON_NAME;
    public List<String> CUSTOMIZE_ECON_LORE;

    public List<String> CUSTOMIZE_RARITY_NAME;
    public List<String> CUSTOMIZE_RARITY_TYPES;

    public String CUSTOMIZE_SEARCH_NAME;
    public List<String> CUSTOMIZE_SEARCH_LORE;
    public String CUSTOMIZE_SEARCH_CANCEL;

    public String CUSTOMIZE_CONFIRM_GUI_NAME;
    public List<String> CUSTOMIZE_CONFIRM_GUI_LORE;

    public String CUSTOMIZE_RENAME_NAME;
    public List<String> CUSTOMIZE_RENAME_LORE;
    public String CUSTOMIZE_RENAME_TITLE;

    public String CUSTOMIZE_MATERIAL_NAME;
    public List<String> CUSTOMIZE_MATERIAL_LORE;

    public String CUSTOMIZE_LORE_NAME;
    public List<String> CUSTOMIZE_LORE_LORE;
    public String CUSTOMIZE_LORE_TITLE;

    public String CUSTOMIZE_ENCHANTS_NAME;
    public List<String> CUSTOMIZE_ENCHANTS_LORE;

    public String CUSTOMIZE_STOCK_NAME;
    public List<String> CUSTOMIZE_STOCK_LORE;
    public List<String> CUSTOMIZE_STOCK_LORE_ON;

    public String CUSTOMIZE_BUNDLE_NAME;
    public List<String> CUSTOMIZE_BUNDLE_LORE;

    public String CUSTOMIZE_DURABILITY_NAME;
    public List<String> CUSTOMIZE_DURABILITY_LORE;

    public String CUSTOMIZE_COMMANDS_NAME;
    public List<String> CUSTOMIZE_COMMANDS_LORE;
    public String CUSTOMIZE_COMMANDS_NAME_ON;
    public List<String> CUSTOMIZE_COMMANDS_LORE_ON;
    public String CUSTOMIZE_COMMANDS_TITLE;

    public String CUSTOMIZE_PERMS_NAME;
    public List<String> CUSTOMIZE_PERMS_LORE;
    public List<String> CUSTOMIZE_PERMS_LORE_ON;

    public String CUSTOMIZE_SET_NAME;
    public List<String> CUSTOMIZE_SET_LORE;
    public List<String> CUSTOMIZE_SET_LORE_ON;

    public String CUSTOMIZE_TOGGLE_ENCHANTS_NAME;
    public List<String> CUSTOMIZE_TOGGLE_ENCHANTS_LORE;
    public String CUSTOMIZE_TOGGLE_ATTIBUTES_NAME;
    public List<String> CUSTOMIZE_TOGGLE_ATTIBUTES_LORE;
    public String CUSTOMIZE_TOGGLE_EFFECTS_NAME;
    public List<String> CUSTOMIZE_TOGGLE_EFFECTS_LORE;

    // MESSAGES

    public String MSG_BUY_ACTION;
    public String MSG_SELL_ACTION;
    public String MSG_BUY_ITEM;
    public String MSG_NOT_MONEY;
    public String MSG_INV_FULL;
    public String MSG_NOT_ITEMS;
    public String MSG_NOT_STOCK;
    public String MSG_RESTOCK;
    public String MSG_NOT_PERMS;
    public String MSG_NOT_PERMS_ITEM;
    public String MSG_OUT_STOCK;
    public String MSG_INVALID_BUY;
    public String MSG_INVALID_SELL;
    public String MSG_CURRENCY_ERROR;
    public String MSG_INVALID_OPERATION;
    public String MSG_NOT_INTEGER;

    protected langResource() {
        super("lang.yml");
    }

    @Override
    protected void init() {

        CONFIRM_GUI_BUY_NAME = yaml.getString("lang.confirm_gui.buy_name");
        CONFIRM_GUI_SELL_NAME = yaml.getString("lang.confirm_gui.sell_name");
        CONFIRM_GUI_ACTION_NAME = yaml.getString("lang.confirm_gui.action_name");
        CONFIRM_GUI_ADD_PANE = yaml.getString("lang.confirm_gui.add_pane");
        CONFIRM_GUI_REMOVE_PANE = yaml.getString("lang.confirm_gui.remove_pane");
        CONFIRM_GUI_RETURN_NAME = yaml.getString("lang.confirm_gui.return_name");
        CONFIRM_GUI_RETURN_PANE_LORE = yaml.getString("lang.confirm_gui.return_pane_lore");
        CONFIRM_GUI_SELL_ITEM = yaml.getString("lang.confirm_gui.sell_item");
        CONFIRM_GUI_YES = yaml.getString("lang.confirm_gui.yes_name");
        CONFIRM_GUI_YES_LORE = parseLore(yaml.getString("lang.confirm_gui.yes_lore"));
        CONFIRM_GUI_NO = yaml.getString("lang.confirm_gui.no_name");
        CONFIRM_GUI_NO_LORE = parseLore(yaml.getString("lang.confirm_gui.no_lore"));

        SHOPS_ITEMS_LORE = parseLore(yaml.getString("lang.shop_items.lore"));

        SHOPS_MANAGER_TITLE = yaml.getString("lang.shops_manager.title");
        SHOPS_MANAGER_CREATE = yaml.getString("lang.shops_manager.create");
        SHOPS_MANAGER_CREATE_LORE = parseLore(yaml.getString("lang.shops_manager.create_lore"));
        SHOPS_MANAGER_RETURN = yaml.getString("lang.shops_manager.return");
        SHOPS_MANAGER_RETURN_LORE = parseLore(yaml.getString("lang.shops_manager.return_lore"));
        SHOPS_MANAGER_LORE = parseLore(yaml.getString("lang.shops_manager.lore"));

        ADD_ITEMS_TITLE = yaml.getString("lang.add_item_gui.title");
        ADD_ITEMS_FROM_ZERO = yaml.getString("lang.add_item_gui.from_zero");
        ADD_ITEMS_FROM_ZERO_LORE = parseLore(yaml.getString("lang.add_item_gui.from_zero_lore"));
        ADD_ITEMS_FROM_EXISTING = yaml.getString("lang.add_item_gui.from_existing");
        ADD_ITEMS_FROM_EXISTING_LORE = parseLore(yaml.getString("lang.add_item_gui.from_existing_lore"));
        ADD_ITEMS_FROM_BUNDLE = yaml.getString("lang.add_item_gui.bundle");
        ADD_ITEMS_FROM_BUNDLE_LORE = parseLore(yaml.getString("lang.add_item_gui.bundle_lore"));
        ADD_ITEMS_RETURN = yaml.getString("lang.add_item_gui.return");
        ADD_ITEMS_RETURN_LORE = parseLore(yaml.getString("lang.add_item_gui.return_lore"));

        DAILY_ITEMS_TITLE = yaml.getString("lang.daily_items_menu.title");
        DAILY_ITEMS_ADD = yaml.getString("lang.daily_items_menu.add");
        DAILY_ITEMS_ADD_LORE = parseLore(yaml.getString("lang.daily_items_menu.add_lore"));
        DAILY_ITEMS_NEXT = yaml.getString("lang.daily_items_menu.next");
        DAILY_ITEMS_PREVIOUS = yaml.getString("lang.daily_items_menu.previous");
        DAILY_ITEMS_RETURN = yaml.getString("lang.daily_items_menu.return");
        DAILY_ITEMS_BUY_PRICE = yaml.getString("lang.daily_items_menu.buy_price");
        DAILY_ITEMS_SELL_PRICE = yaml.getString("lang.daily_items_menu.sell_price");
        DAILY_ITEMS_STOCK = yaml.getString("lang.daily_items_menu.stock");
        DAILY_ITEMS_CURRENCY = yaml.getString("lang.daily_items_menu.currency");
        DAILY_ITEMS_RARITY = yaml.getString("lang.daily_items_menu.rarity");
        DAILY_ITEMS_BUY_FOR = yaml.getString("lang.daily_items_menu.buy_for");
        DAILY_ITEMS_MANAGER_LORE = parseLore(yaml.getString("lang.daily_items_menu.manager_items_lore"));

        CUSTOMIZE_TITLE = yaml.getString("lang.customize_menu.title");
        CUSTOMIZE_CRAFT = yaml.getString("lang.customize_menu.craft");
        CUSTOMIZE_CRAFT_LORE = parseLore(yaml.getString("lang.customize_menu.craft_lore"));
        CUSTOMIZE_RETURN = yaml.getString("lang.customize_menu.return");
        CUSTOMIZE_RETURN_LORE = parseLore(yaml.getString("lang.customize_menu.return_lore"));
        CUSTOMIZE_UNAVAILABLE = yaml.getString("lang.customize_menu.unavailable");
        CUSTOMIZE_PRICE_NAME = yaml.getString("lang.customize_menu.price.name");
        CUSTOMIZE_PRICE_LORE = parseLore(yaml.getString("lang.customize_menu.price.lore"));
        CUSTOMIZE_ECON_NAME = yaml.getString("lang.customize_menu.econ.name");
        CUSTOMIZE_ECON_LORE = parseLore(yaml.getString("lang.customize_menu.econ.lore"));
        CUSTOMIZE_RARITY_NAME = parseLore(yaml.getString("lang.customize_menu.rarity.name"));
        CUSTOMIZE_RARITY_TYPES = parseLore(yaml.getString("lang.customize_menu.rarity.types"));
        CUSTOMIZE_SEARCH_NAME = yaml.getString("lang.customize_menu.search.name");
        CUSTOMIZE_SEARCH_LORE = parseLore(yaml.getString("lang.customize_menu.search.lore"));
        CUSTOMIZE_SEARCH_CANCEL = yaml.getString("lang.customize_menu.search.cancel");
        CUSTOMIZE_CONFIRM_GUI_NAME = yaml.getString("lang.customize_menu.confirm_gui.name");
        CUSTOMIZE_CONFIRM_GUI_LORE = parseLore(yaml.getString("lang.customize_menu.confirm_gui.lore"));
        CUSTOMIZE_RENAME_NAME = yaml.getString("lang.customize_menu.rename.name");
        CUSTOMIZE_RENAME_LORE = parseLore(yaml.getString("lang.customize_menu.rename.lore"));
        CUSTOMIZE_RENAME_TITLE = yaml.getString("lang.customize_menu.rename.title");
        CUSTOMIZE_MATERIAL_NAME = yaml.getString("lang.customize_menu.material.name");
        CUSTOMIZE_MATERIAL_LORE = parseLore(yaml.getString("lang.customize_menu.material.lore"));
        CUSTOMIZE_LORE_NAME = yaml.getString("lang.customize_menu.lore.name");
        CUSTOMIZE_LORE_LORE = parseLore(yaml.getString("lang.customize_menu.lore.lore"));
        CUSTOMIZE_LORE_TITLE = yaml.getString("lang.customize_menu.lore.title");
        CUSTOMIZE_ENCHANTS_NAME = yaml.getString("lang.customize_menu.enchants.name");
        CUSTOMIZE_ENCHANTS_LORE = parseLore(yaml.getString("lang.customize_menu.enchants.lore"));
        CUSTOMIZE_STOCK_NAME = yaml.getString("lang.customize_menu.stock.name");
        CUSTOMIZE_STOCK_LORE = parseLore(yaml.getString("lang.customize_menu.stock.lore"));
        CUSTOMIZE_STOCK_LORE_ON = parseLore(yaml.getString("lang.customize_menu.stock.lore_on"));
        CUSTOMIZE_BUNDLE_NAME = yaml.getString("lang.customize_menu.bundle.name");
        CUSTOMIZE_BUNDLE_LORE = parseLore(yaml.getString("lang.customize_menu.bundle.lore"));
        CUSTOMIZE_DURABILITY_NAME = yaml.getString("lang.customize_menu.durability.name");
        CUSTOMIZE_DURABILITY_LORE = parseLore(yaml.getString("lang.customize_menu.durability.lore"));
        CUSTOMIZE_COMMANDS_NAME = yaml.getString("lang.customize_menu.commands.name");
        CUSTOMIZE_COMMANDS_LORE = parseLore(yaml.getString("lang.customize_menu.commands.lore"));
        CUSTOMIZE_COMMANDS_NAME_ON = yaml.getString("lang.customize_menu.commands.name_on");
        CUSTOMIZE_COMMANDS_LORE_ON = parseLore(yaml.getString("lang.customize_menu.commands.lore_on"));
        CUSTOMIZE_COMMANDS_TITLE = yaml.getString("lang.customize_menu.commands.title");
        CUSTOMIZE_PERMS_NAME = yaml.getString("lang.customize_menu.perms.name");
        CUSTOMIZE_PERMS_LORE = parseLore(yaml.getString("lang.customize_menu.perms.lore"));
        CUSTOMIZE_PERMS_LORE_ON = parseLore(yaml.getString("lang.customize_menu.perms.lore_on"));
        CUSTOMIZE_SET_NAME = yaml.getString("lang.customize_menu.set.name");
        CUSTOMIZE_SET_LORE = parseLore(yaml.getString("lang.customize_menu.set.lore"));
        CUSTOMIZE_SET_LORE_ON = parseLore(yaml.getString("lang.customize_menu.set.lore_on"));
        CUSTOMIZE_TOGGLE_ENCHANTS_NAME = yaml.getString("lang.customize_menu.toggle.enchants.name");
        CUSTOMIZE_TOGGLE_ENCHANTS_LORE = parseLore(yaml.getString("lang.customize_menu.toggle.enchants.lore"));
        CUSTOMIZE_TOGGLE_ATTIBUTES_NAME = yaml.getString("lang.customize_menu.toggle.attributes.name");
        CUSTOMIZE_TOGGLE_ATTIBUTES_LORE = parseLore(yaml.getString("lang.customize_menu.toggle.attributes.lore"));
        CUSTOMIZE_TOGGLE_EFFECTS_NAME = yaml.getString("lang.customize_menu.toggle.effects.name");
        CUSTOMIZE_TOGGLE_EFFECTS_LORE = parseLore(yaml.getString("lang.customize_menu.toggle.effects.lore"));

        MSG_BUY_ACTION = yaml.getString("lang.messages.buy_action");
        MSG_SELL_ACTION = yaml.getString("lang.messages.sell_action");
        MSG_BUY_ITEM = yaml.getString("lang.messages.buy_item");
        MSG_NOT_MONEY = yaml.getString("lang.messages.not_enough_money");
        MSG_INV_FULL = yaml.getString("lang.messages.inventory_full");
        MSG_NOT_ITEMS = yaml.getString("lang.messages.not_enough_items");
        MSG_NOT_STOCK = yaml.getString("lang.messages.not_stock");
        MSG_RESTOCK = yaml.getString("lang.messages.restock");
        MSG_NOT_PERMS = yaml.getString("lang.messages.not_perms");
        MSG_NOT_PERMS_ITEM = yaml.getString("lang.messages.not_perms_item");
        MSG_OUT_STOCK = yaml.getString("lang.messages.out_of_stock");
        MSG_INVALID_BUY = yaml.getString("lang.messages.invalid_buy");
        MSG_INVALID_SELL = yaml.getString("lang.messages.invalid_sell");
        MSG_CURRENCY_ERROR = yaml.getString("lang.messages.currency_error");
        MSG_INVALID_OPERATION = yaml.getString("lang.messages.invalid_operation");
        MSG_NOT_INTEGER = yaml.getString("lang.messages.not_integer");

    }

    private List<String> parseLore(String lore) {
        return Splitter.on("|").splitToList(lore);
    }
}
