package io.github.divios.dailyrandomshop.builders;

import io.github.divios.dailyrandomshop.utils.utils;
import io.github.divios.dailyrandomshop.xseries.XMaterial;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

public class dynamicGui implements InventoryHolder, Listener {

    private final io.github.divios.dailyrandomshop.main main = io.github.divios.dailyrandomshop.main.getInstance();

    private final Player p;
    private final contentX contentX;
    private final Function<Integer, String> title;
    private final Consumer<Player> back;
    private final Integer rows2fill;
    private final Function<InventoryClickEvent, Response> contentAction;
    private final BiFunction<Integer, Player, Response> nonContentAction;
    private final BiConsumer<Inventory, Integer> addItems;
    private final boolean searchOn;
    private final boolean isSearch;
    private final Integer page;

    private final List<Inventory> invsList = new ArrayList<>();

    private dynamicGui(
            Player p,
            contentX contentX,
            Function<Integer, String> title,
            Consumer<Player> back,
            Integer rows2fill,
            Function<InventoryClickEvent, Response> contentAction,
            BiFunction<Integer, Player, Response> nonContentAction,
            BiConsumer<Inventory, Integer> addItems,
            boolean searchOn,
            boolean isSearch,
            Integer page

    ) {
        this.p = p;
        this.contentX = contentX;
        this.title = title;
        this.back = back;
        this.rows2fill = rows2fill;
        this.contentAction = contentAction;
        this.nonContentAction = nonContentAction;
        this.addItems = addItems;
        this.searchOn = searchOn;
        this.isSearch = isSearch;
        this.page = page;

        Bukkit.getPluginManager().registerEvents(this, main);

        createStructure();
        p.openInventory(invsList.get(page));

    }

    private static class contentX {
        public Supplier<List<ItemStack>> contentS;
        public List<ItemStack> searchContent;

        public contentX(Supplier contentS) {
            this.contentS = contentS;
        }
    }

    private void createStructure() {

        List<ItemStack> content;
        if (!isSearch) content = contentX.contentS.get();
        else content = contentX.searchContent;

        double nD = content.size() / Double.valueOf(rows2fill);
        int n = (int) Math.ceil(nD);

        for (int i = 0; i < n; i++) {
            if (i + 1 == n) {
                invsList.add(createSingleInv(i + 1, 2));
            } else if (i == 0) invsList.add(createSingleInv(i + 1, 0));
            else invsList.add(createSingleInv(i + 1, 1));
        }

        if (invsList.isEmpty()) {
            Inventory firstInv = Bukkit.createInventory(this, 54, utils.formatString(title.apply(0)));
            setDefaultItems(firstInv);
            invsList.add(firstInv);
        }
    }

    private Inventory createSingleInv(int page, int pos) {
        List<ItemStack> content;
        if (!isSearch) content = contentX.contentS.get();
        else content = contentX.searchContent;

        int slot = 0;
        Inventory returnGui = Bukkit.createInventory(this, 54, utils.formatString(title.apply(page)));
        setDefaultItems(returnGui);
        if (pos == 0 && content.size() > 44) setNextItem(returnGui);
        if (pos == 1) {
            setNextItem(returnGui);
            setPreviousItem(returnGui);
        }
        if (pos == 2 && content.size() > 44) {
            setPreviousItem(returnGui);
        }

        for (ItemStack item : content) {
            if (slot == rows2fill * page) break;
            if (slot >= (page - 1) * rows2fill) returnGui.setItem(slot - (page - 1) * rows2fill, item);

            slot++;
        }
        return returnGui;
    }

    private Inventory processNextGui(Inventory inv, int dir) {
        return invsList.get(invsList.indexOf(inv) + dir);
    }

    private void setDefaultItems(Inventory inv) {

        ItemStack backItem = XMaterial.OAK_SIGN.parseItem();   //back button
        utils.setDisplayName(backItem, "&c&lReturn");
        utils.setLore(backItem, Arrays.asList("&7Click to go back"));
        inv.setItem(49, backItem);

        if (addItems != null) addItems.accept(inv, page);

        if(!searchOn) return;

        ItemStack search = null;
        if (!isSearch) {
            search = XMaterial.COMPASS.parseItem();
            utils.setDisplayName(search, "&b&lSearch");
            utils.setLore(search, Arrays.asList("&7Click to search item"));
        } else {
            search = XMaterial.REDSTONE_BLOCK.parseItem();
            utils.setDisplayName(search, "&c&lCancel search");
            utils.setLore(search, Arrays.asList("&7Click to cancel search"));
        }
        inv.setItem(51, search);

    }

    private void setNextItem(Inventory inv) {
        ItemStack next = new ItemStack(Material.ARROW);
        utils.setDisplayName(next, "&6&lNext");
        inv.setItem(53, next);
    }

    private void setPreviousItem(Inventory inv) {
        ItemStack previous = new ItemStack(Material.ARROW);
        utils.setDisplayName(previous, "&6&lPrevious");
        inv.setItem(45, previous);
    }

    private void searchAction(ItemStack item) {
        if (item.getType() == XMaterial.REDSTONE_BLOCK.parseMaterial()) {
            new dynamicGui(p, contentX, title, back, rows2fill, contentAction,
                    nonContentAction, addItems, searchOn, false, page);
        } else {
            final List<ItemStack> lists = new ArrayList<>();
            new AnvilGUI.Builder()
                    .onClose(player -> {
                        utils.runTaskLater(() -> {
                            new dynamicGui(p, contentX, title, back, rows2fill, contentAction,
                                    nonContentAction, addItems, searchOn, true, page);
                        }, 1L);
                    })
                    .onComplete((player, text) -> {

                        for (ItemStack s : contentX.contentS.get()) {
                            String name = utils.trimString(s.getItemMeta().getDisplayName());
                            if (name.toLowerCase().startsWith(text.toLowerCase())) {
                                lists.add(s);
                            }
                        }
                        contentX.searchContent = lists;
                        return AnvilGUI.Response.close();

                    })
                    .text("Insert text to search")
                    .itemLeft(new ItemStack(XMaterial.COMPASS.parseMaterial()))
                    .title(utils.formatString("&6&lSearch"))
                    .plugin(main)
                    .open(p);
        }
    }

    public List<Inventory> getinvs() { return invsList; };

    @Override
    public Inventory getInventory() {
        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);

        int slot = e.getSlot();
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        Inventory inv = e.getView().getTopInventory();

        if (slot == 49) {
            unregister();
            back.accept(p);
        }

        else if (slot == 53 && !utils.isEmpty(item)) p.openInventory(processNextGui(inv, 1));
        else if (slot == 45 && !utils.isEmpty(item)) p.openInventory(processNextGui(inv, -1));

        else if (e.getSlot() == 51 && searchOn) searchAction(item);                             /* Search button */

        else {
            Response response;
            if (slot >= 0 && slot < rows2fill && !utils.isEmpty(item)) {
                response = contentAction.apply(e);
            } else {
                response = nonContentAction.apply(slot, p);
            }

            if (response == null) return;

            if (response.getResponse() == ResponseX.CLOSE) p.closeInventory();
            else if (response.getResponse() == ResponseX.UPDATE) {
                new dynamicGui(p, contentX, title, back, rows2fill, contentAction,
                        nonContentAction, addItems, searchOn, isSearch, page);

            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() != this) return;
        e.setCancelled(true);
    }


    public void unregister() {
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
    }


    public static class Builder {

        private Player p;
        private contentX content = null;
        private Function<Integer, String> title = integer -> "";
        private Consumer<Player> back = player -> {
        };
        private Integer rows2fill = 45;
        private Function<InventoryClickEvent, Response> contentAction = InventoryClickEvent -> {
            return null;
        };
        private BiFunction<Integer, Player, Response> nonContentAction = (integer, Player) -> {
            return null;
        };
        private BiConsumer<Inventory, Integer> addItems = (itemStacks, integer) -> {
        };

        private boolean searchOn = true;
        private boolean isSearch = false;
        private Integer page = 0;

        public Builder contents(Supplier<List<ItemStack>> content) {
            this.content = new contentX(content);
            return this;
        }

        public Builder title(Function<Integer, String> title) {
            this.title = title;
            return this;
        }

        public Builder back(Consumer<Player> back) {
            this.back = back;
            return this;
        }

        public Builder rows(int rows) {
            this.rows2fill = rows;
            return this;
        }

        public Builder contentAction(Function<InventoryClickEvent, Response> contentAction) {
            this.contentAction = contentAction;
            return this;
        }

        public Builder nonContentAction(BiFunction<Integer, Player, Response> nonContentAction) {
            this.nonContentAction = nonContentAction;
            return this;
        }

        public Builder addItems(BiConsumer<Inventory, Integer> addItems) {
            this.addItems = addItems;
            return this;
        }

        public Builder setSearch(boolean status) {
            searchOn = status;
            return this;
        }

        public Builder isSearch(boolean isSearch) {
            this.isSearch = isSearch;
            return this;
        }

        public Builder page(Integer page) {
            this.page = page;
            return this;
        }

        public dynamicGui open(Player p) {
            this.p = p;

            return new dynamicGui(p, content, title, back, rows2fill, contentAction,
                    nonContentAction, addItems, searchOn, isSearch, page);
        }
    }

    public static class Response {

        private final ResponseX response;

        private Response(ResponseX response) {
            this.response = response;
        }

        public ResponseX getResponse() {
            return this.response;
        }

        public static Response close() {
            return new Response(ResponseX.CLOSE);
        }

        public static Response update() {
            return new Response(ResponseX.UPDATE);
        }

        public static Response nu() {
            return new Response(ResponseX.NU);
        }
    }

    private enum ResponseX {
        NU,
        UPDATE,
        CLOSE
    }

}
