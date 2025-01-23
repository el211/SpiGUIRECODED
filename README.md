# SpiGUI RECODED

SpiGUI is a robust and flexible inventory management library for Bukkit/Spigot/Paper Minecraft plugins. It simplifies creating custom GUIs with features like pagination, stickied slots, HEX color and gradient support, and more.

## Features

- **Easy-to-Use API**: Create and manage custom GUIs with minimal boilerplate.
- **Pagination**: Automatic pagination for GUIs with toolbar support.
- **Stickied Slots**: Persistent slots that remain across all pages.
- **HEX Colors and Gradients**: Supports HEX colors and gradients for inventory titles, item names, and lore.
- **Event Handling**: Includes `SGButtonClickEvent` for precise control over button interactions.
- **Customizable Toolbar**: Define toolbar buttons for pagination or custom actions.
- **Advanced Item Building**: Use `ItemBuilder` to create and customize items with support for colors, lore, enchantments, and more.

## Requirements

- **Minecraft Server**: Bukkit/Spigot/Paper
- **Java Version**: Java 8 or higher

## Installation

1. Clone or download the repository.
2. Add the JAR file to your server's `plugins` folder.
3. Restart your server.

## Getting Started
[![](https://jitpack.io/v/el211/SpiGUIRECODED.svg)](https://jitpack.io/#el211/SpiGUIRECODED)

### Setting Up a Basic GUI
```java
SpiGUI spiGUI = new SpiGUI(this);

// Create a new menu
SGMenu menu = spiGUI.create("&6&lMy Custom GUI", 3);

// Add buttons to the menu
menu.setButton(0, new SGButton(
    new ItemBuilder(Material.DIAMOND)
        .name("&bShiny Diamond")
        .lore("&7This is a special item!")
        .build()
).withListener(event -> {
    event.getWhoClicked().sendMessage("You clicked the diamond!");
}));

// Open the menu for a player
player.openInventory(menu.getInventory());

# SpiGUI
A comprehensive inventory menu API for Spigot with pages support. Supports Bukkit/Spigot 1.7 - 1.20 (see [Version Notes](#version-notes)) (Future versions ought to work just fine too!).
<p>
  <a target="_blank" href="https://github.com/SamJakob/SpiGUI/blob/master/LICENSE">
    <img alt="License" src="https://img.shields.io/github/license/SamJakob/SpiGUI?style=for-the-badge">
  </a>
  <a href="#">
    <img alt="No Dependencies" src="https://img.shields.io/badge/dependencies-none-green?color=orange&style=for-the-badge">
  </a>
</p>

<p>
  <a target="_blank" href="https://jitpack.io/#com.samjakob/SpiGUI">
    <img alt="JitPack" src="https://img.shields.io/badge/dynamic/json?color=red&label=JitPack&query=%24.version&url=https%3A%2F%2Fjitpack.io%2Fapi%2Fbuilds%2Fcom.samjakob%2FSpiGUI%2FlatestOk&style=for-the-badge">
  </a>
  <a target="_blank" href="https://jitpack.io/com/github/SamJakob/SpiGUI/latest/javadoc/">
    <img alt="JavaDoc" src="https://img.shields.io/badge/dynamic/json?color=blue&label=JavaDoc&query=%24.version&url=https%3A%2F%2Fjitpack.io%2Fapi%2Fbuilds%2Fcom.samjakob%2FSpiGUI%2FlatestOk&style=for-the-badge">
  </a>
</p>

<br><br>

<p align="center">
<img width="640" src="https://user-images.githubusercontent.com/37072691/91370390-2071d400-e806-11ea-86a8-57a60138e505.gif">
<br>
<small>The code for this example can be found in the library <a href="https://github.com/SamJakob/SpiGUI/blob/master/src/test/java/com/samjakob/spiguitest/SpiGUITest.java">test class</a>.</small>
</p>

<br><br>

## Version Notes
> _**IMPORTANT!**_ If you have an opinion on how backwards compatibility should be achieved with new versions, please
> feel free to [drop a reply to this open discussion](https://github.com/SamJakob/SpiGUI/issues/21).

- I don't see a reason it shouldn't work in Spigot 1.7 or any version of Bukkit from 1.8 - 1.20 but it hasn't been tested on each individual version.
- This library has been tested on Spigot 1.8, Spigot 1.16, PaperSpigot 1.19, Spigot 1.20 and is expected to work on every versions in-between for most, if not all, forks of Spigot.
- The [ItemBuilder](https://github.com/SamJakob/SpiGUI/blob/master/src/main/java/com/samjakob/spigui/item/ItemBuilder.java) API should work for all versions of Bukkit/Spigot unless you use the `ItemDataColor` (or `data` value) which relies on pre-1.13 item data values. (Though you can just use the relevant `Material` instead - e.g., instead of using `Material.WOOL` and `ItemDataColor.BLUE`, just use `Material.BLUE_WOOL`.)

<br>

## Installation

You can very easily install SpiGUI using [JitPack](https://jitpack.io/#com.samjakob/SpiGUI).
(The JitPack page contains instructions for Gradle, Maven, sbt, etc.)

<details>
<summary>Instructions for Gradle</summary>

Just add the following to your `build.gradle` file:
```groovy
repositories {
    // ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    // ...
    implementation 'com.samjakob:SpiGUI:<insert latest version here>'
}
```

<br>

For distribution, you can just shade the library into your plugin JAR. On
Gradle, this can be done by adding the following to the end of your
`build.gradle`:

```groovy
jar {
    duplicatesStrategy(DuplicatesStrategy.EXCLUDE)

    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```

</details>

If you aren't using a build system, you can just download the latest JAR and
add it to your project's classpath (just make sure the SpiGUI classes are
included in your JAR when you build it).

<br>

## Quick Start

**Step 1: Create an instance of the SpiGUI library in your plugin**
```java

class MyPlugin extends JavaPlugin {

  public static SpiGUI spiGUI;
  
  @Override
  public void onEnable() {
    // (IMPORTANT!) Registers SpiGUI event handlers (and stores plugin-wide settings for SpiGUI.)
    spiGUI = new SpiGUI(this);
  }
  
}

```

<br>

**Step 2: Use the library**
```java
public void openMyAwesomeMenu(Player player) {

  // Create a GUI with 3 rows (27 slots)
  SGMenu myAwesomeMenu = MyPlugin.spiGUI.create("&cMy Awesome Menu", 3);

  // Create a button
  SGButton myAwesomeButton = new SGButton(
    // Includes an ItemBuilder class with chainable methods to easily
    // create menu items.
    new ItemBuilder(Material.WOOD).build()
  ).withListener((InventoryClickEvent event) -> {
    // Events are cancelled automatically, unless you turn it off
    // for your plugin or for this inventory.
    event.getWhoClicked().sendMessage("Hello, world!");
  });
  
  // Add the button to your GUI
  myAwesomeMenu.addButton(myAwesomeButton);
  
  // Show the GUI
  player.openInventory(myAwesomeMenu.getInventory());

}
```

<br>

**Step 3: Profit!**

<br>

## Why?
Chest Inventory Menus (commonly referred to as GUIs) are the ubiquitous way to display menus, execute actions and even manage configuration in Spigot plugins.
However, the Inventory API leveraged to achieve this in Spigot is not designed for menus, making code far more verbose and far less maintainable than it needs to be.

SpiGUI is a rewrite of my outdated [SpigotPaginatedGUI](https://github.com/masterdoctor/SpigotPaginatedGUI) API including improvements and features I've
added whilst using the API in my own software that aims to provide a highly readable and concise API for menus.
