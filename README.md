## What is DailyShop? [![Java CI with Gradle](https://github.com/divios/DailyShop/actions/workflows/gradle.yml/badge.svg)](https://github.com/divios/DailyShop/actions/workflows/gradle.yml)
DailyShop is a shop plugin but the items displayed are random items collected from a collection of your wish. This items are renewed after a certain time. In short, the items are not completetly random, you have to add items to the shop and then the plugin will randomly choose  from these items.

This is not the only feature, the plugin is built with a in-game gui to customize everything (every aspect of the items and the shops displays) and get rid of .yml format. There are also other features, such as stock items or bundles/packages to make your shop unique.

## How does it work?
Every shop has an internal (customizable) timer. When it reachs 0, the shop cleans up the current items and replace them with random new items. Collected items can have weight, which means that some items are more likely to appear than others.

## Notes:
Although the plugin is open source, it uses a private lib repository internally, so you won't be able to compile the plugin yourself. The purpose of this open source is to collaborate or verify and learn from the code, which is something that has served me more than once and has allowed me to develop better programming skills. Making this repository public is an act of gratitude to that cause.
