package io.github.divios.dailyShop.commands;

import io.github.divios.core_lib.misc.FormatUtils;
import io.github.divios.core_lib.utils.Log;
import io.github.divios.dailyShop.DailyShop;
import io.github.divios.dailyShop.guis.settings.shopsItemsManagerGui;
import io.github.divios.dailyShop.utils.Utils;
import io.github.divios.dailyShop.utils.valuegenerators.FixedValueGenerator;
import io.github.divios.jcommands.JCommand;
import io.github.divios.jcommands.arguments.Argument;
import io.github.divios.jcommands.arguments.types.StringArgument;
import io.github.divios.lib.dLib.dItem;
import io.github.divios.lib.dLib.shop.dShop;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.shop.Shop;
import net.brcdev.shopgui.shop.ShopManager;
import org.black_ixx.bossshop.BossShop;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.BSShop;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class importShops {

    public JCommand getCommand() {
        return JCommand.create("import")
                .assertPermission("DailyRandomShop.import")
                .assertUsage(getUsage())
                .withSubcommands(getShopGuiCommand(), getBossShopProCommand());
    }

    private String getUsage() {
        return FormatUtils.color("&8- &6/rdshop import [plugin] [shop] [_shop] ] &8- &7Imports the given items _shop to a shop");
    }

    private JCommand getShopGuiCommand() {
        return JCommand.create("shopGuiPlus")
                .assertUsage(getUsage())
                .assertRequirements(commandSender -> Utils.isOperative("ShopGUIPlus"))
                .withArguments(getShopGuiArgument(), getDailyShopsArgument())
                .executesPlayer((player, args) -> {
                    DailyShop.get().getShopsManager().getShop(args.get("dailyShop").getAsString())
                            .ifPresent(shop -> {

                                Shop shopGuiShop;
                                if ((shopGuiShop = ShopGuiPlusApi.getShop(args.get("shopGui").getAsString())) == null) {
                                    Utils.sendRawMsg(player, "&7That shopGuiShop does not exists");
                                    return;
                                }

                                shopGuiShop.getShopItems()
                                        .forEach(shopItem -> {

                                            if (shopItem.getType().equals(ShopManager.ItemType.DUMMY)) return;

                                            dItem newItem = dItem.of(shopItem.getItem(), shop);

                                            newItem.setBuyPrice(new FixedValueGenerator(shopItem.getBuyPrice()));
                                            newItem.setSellPrice(new FixedValueGenerator(shopItem.getSellPrice()));
                                            shop.addItem(newItem);
                                        });
                                Utils.sendRawMsg(player, "&7Items imported successfully");
                                shopsItemsManagerGui.open(player, shop);
                            });
                })
                .executesConsole((consoleCommandSender, valueMap) ->
                        consoleCommandSender.sendMessage("This command can only be executed by players"));
    }

    private Argument getShopGuiArgument() {
        return new StringArgument("shopGui")
                .overrideSuggestions(() -> new ArrayList<>(ShopGuiPlusApi.getPlugin().getShopManager().shops.keySet()));
    }

    final BossShop plugin = ((BossShop) Bukkit.getPluginManager().getPlugin("BossShopPro"));

    private JCommand getBossShopProCommand() {
        return JCommand.create("BossShopPro")
                .assertUsage(getUsage())
                .assertRequirements(commandSender -> Utils.isOperative("BossShopPro"))
                .withArguments(getBossShopProArgument(), getDailyShopsArgument())
                .executesPlayer((player, args) -> {
                    DailyShop.get().getShopsManager().getShop(args.get("dailyShop").getAsString())
                            .ifPresent(shop -> {

                                BSShop bossShopShop;
                                if ((bossShopShop = plugin.getAPI().getShop(args.get("bossShop").getAsString())) == null) {
                                    Utils.sendRawMsg(player, "&7That BossShop does not exists");
                                    return;
                                }

                                for (BSBuy bsBuy : bossShopShop.getItems()) {
                                    try {
                                    dItem newItem = dItem.of(bsBuy.getItem(), shop);

                                    double buyPrice = Double.parseDouble(
                                            String.valueOf(bsBuy.getPrice(null) == null ?
                                                    bsBuy.getPrice(ClickType.LEFT) : bsBuy.getPrice(null))
                                    ) / bsBuy.getItem().getAmount();

                                    double sellPrice = Double.parseDouble(
                                            String.valueOf(bsBuy.getPrice(null) == null ?
                                                    bsBuy.getPrice(ClickType.RIGHT) : bsBuy.getPrice(null))
                                    ) / bsBuy.getItem().getAmount();


                                        newItem.setBuyPrice(new FixedValueGenerator(buyPrice));
                                        newItem.setSellPrice(new FixedValueGenerator(sellPrice));

                                        shop.addItem(newItem);
                                    } catch (Exception e) {
                                        Log.info("Could not import item of name " + bsBuy.getName());
                                    }
                                }
                                Utils.sendRawMsg(player, "&7Items imported successfully");
                                shopsItemsManagerGui.open(player, shop);
                            });
                });
    }

    private Argument getBossShopProArgument() {
        return new StringArgument("bossShop")
                .overrideSuggestions(() -> plugin.getClassManager().getShops().getShops().values()
                        .stream()
                        .map(BSShop::getShopName)
                        .collect(Collectors.toList()));
    }

    private Argument getDailyShopsArgument() {
        return new StringArgument("dailyShop")
                .overrideSuggestions(() -> DailyShop.get().getShopsManager()
                        .getShops()
                        .stream()
                        .map(dShop::getName)
                        .collect(Collectors.toList())
                )
                .setAsImperative();
    }

}
