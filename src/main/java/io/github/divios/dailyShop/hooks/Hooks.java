package io.github.divios.dailyShop.hooks;

public class Hooks {

    public final static bstatsHook B_STATS;
    public final static vaultHook VAULT;
    public final static elementalGemsHook ELEMENTAL_GEMS;
    public final static gemsEconomyHook GEMS_ECONOMY;
    public final static MPointsHook M_POINTS;
    public final static placeholderApiHook PLACEHOLDER_API;
    public final static playerPointsHook PLAYER_POINTS;
    public final static shopGuiPlusHook SHOP_GUI_PLUS;
    public final static tokenEnchantsHook TOKEN_ENCHANT;
    public final static tokenManagerHook TOKEN_MANAGER;
    public final static UltraEconomyHook ULTRA_ECONOMY;

    static {
        B_STATS = new bstatsHook();
        VAULT = new vaultHook();
        ELEMENTAL_GEMS = new elementalGemsHook();
        GEMS_ECONOMY = new gemsEconomyHook();
        M_POINTS = new MPointsHook();
        PLACEHOLDER_API = new placeholderApiHook();
        PLAYER_POINTS = new playerPointsHook();
        SHOP_GUI_PLUS = new shopGuiPlusHook();
        TOKEN_ENCHANT = new tokenEnchantsHook();
        TOKEN_MANAGER = new tokenManagerHook();
        ULTRA_ECONOMY = new UltraEconomyHook();
    }

    private Hooks() {
        throw new RuntimeException("This class cannot be instantiated");
    }

}
