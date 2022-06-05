# DailyShop ![Spigot Donwloads](https://badges.spiget.org/resources/downloads/Downloads-yellow-86907.svg)
DailyShop is an advanced shop plugin that aims to transform the economy of every server.

There are currently two forms of Minecraft economies that exists on modern servers. The first is vanilla which does not use plugins to assist in functionality within the economy. The second is a server-shop, vanilla blend, this method uses player-to-player trades and a server-wide shop that includes set prices for items to be bought and sold. The second method is often preferred on SMP/vanilla as it provides higher liquidity and engagement in the market than vanilla. However, on anarchy/semi-anarchy servers the vanilla-economy is provided as vanilla mechanics are often preferred.

Both methods have significant issues. Vanilla being the most obvious with incredibly low engagement in the market as trades are strictly player-to-player bartering. To fix this issue servers and plugin-makers have developed a server-economy. Plugins such as essentials and Vault’s API allows for a medium of exchange within Minecraft. This has drastically fixed a lot of issues with the bartering system within Minecraft. However, the issue of low market engagement persisted. This has had attempts to be “fixed” however, by introducing server-wide shop plugins, often in the form of shop GUI’s or sign-shops. This is the current “preferred” method of economies in Minecraft servers.

DailyShop aims to fix the still present problems in both methods by introducing shops with random items, picked from a configurable pool of items which are replaced by new random items after a certain amount of time. This feature allows players to engage more with the server economy and with more advanced features, such as stock for the items, dynamic prices, permissions per items and shopKeepers (Future update), admins will be able to control the volatility of the shop, have players compete for the items and keep them interested if stock is enabled and prevent whales from buying large amounts of items and break the economy of the server.​
This is not the only feature, the plugin is built with a in-game gui to customize everything (every aspect of the items and the shops displays) and get rid of .yml format. There are also other features, such as stock items or bundles/packages to make your shop unique.

# How does it work?
Every shop has an internal (customizable) timer. When it reachs 0, the shop cleans up the current items and replace them with random new items. Collected items can have weight, which means that some items are more likely to appear than others.

# Wiki
The plugin has a lot of features that are not listed here and make it unique. You can check all the features of the plugin on the dedicate [wiki](https://github.com/divios/DailyShop/wiki).

# API
To access the API you can either get it from [github](https://github.com/divios/DailyShop/actions), or on [spigot](https://www.spigotmc.org/resources/daily-shop-free-random-items-shop-customizable-items-timer-nbt-support-and-more.86907/) throught the free version.

## With Github Packages

You can use the artifacts hosted on Github Packages to access the api.

```groovy
repositories {
    maven { url = "https://maven.pkg.github.com/divios/DailyShop" }
}
```

``` groovy
dependencies {
        compileOnly 'io.github.divios:DailyShop:Tag'
}
```

Replace `Tag` with a valid DailyShop version. Example `6.0.1`.

# Build:

To use build the project locally you need to add the dependency [core_lib](https://github.com/divios/core_lib), follow the steps there to know how to. The libraries [jCommands](https://github.com/divios/jCommands) and [jText](https://github.com/divios/jText) are also used but are shipped automatically via jitpack.

```
git clone https://github.com/divios/DailyShop
cd DailyShop
./gradlew jar
```

If you encounter any problems feel free to contact me


