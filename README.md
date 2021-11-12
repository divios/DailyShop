# DailyShop
DailyShop is an advanced shop plugin that aims to transform the economy of every server.

There are currently two forms of Minecraft economies that exists on modern servers. The first is vanilla which does not use plugins to assist in functionality within the economy. The second is a server-shop, vanilla blend, this method uses player-to-player trades and a server-wide shop that includes set prices for items to be bought and sold. The second method is often preferred on SMP/vanilla as it provides higher liquidity and engagement in the market than vanilla. However, on anarchy/semi-anarchy servers the vanilla-economy is provided as vanilla mechanics are often preferred.

Both methods have significant issues. Vanilla being the most obvious with incredibly low engagement in the market as trades are strictly player-to-player bartering. To fix this issue servers and plugin-makers have developed a server-economy. Plugins such as essentials and Vault’s API allows for a medium of exchange within Minecraft. This has drastically fixed a lot of issues with the bartering system within Minecraft. However, the issue of low market engagement persisted. This has had attempts to be “fixed” however, by introducing server-wide shop plugins, often in the form of shop GUI’s or sign-shops. This is the current “preferred” method of economies in Minecraft servers.

DailyShop aims to fix the still present problems in both methods by introducing shops with random items, picked from a configurable pool of items which are replaced by new random items after a certain amount of time. This feature allows players to engage more with the server economy and with more advanced features, such as stock for the items, dynamic prices, permissions per items and shopKeepers (Future update), admins will be able to control the volatility of the shop, have players compete for the items and keep them interested if stock is enabled and prevent whales from buying large amounts of items and break the economy of the server.​
This is not the only feature, the plugin is built with a in-game gui to customize everything (every aspect of the items and the shops displays) and get rid of .yml format. There are also other features, such as stock items or bundles/packages to make your shop unique.

# How does it work?
Every shop has an internal (customizable) timer. When it reachs 0, the shop cleans up the current items and replace them with random new items. Collected items can have weight, which means that some items are more likely to appear than others.

# Notes:
Although the plugin is open source, it uses a private lib repository internally, so you won't be able to compile the plugin yourself. The purpose of this open source is to collaborate or verify and learn from the code, which is something that has served me more than once and has allowed me to develop better programming skills. Making this repository public is an act of gratitude to that cause.
