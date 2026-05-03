# Minecart Trains Fork

This mod was forked from [Minecart Trains](https://github.com/Larsens-Mods/minecart-trains).

- This mod allows you to build more realistic Minecart trains in Minecraft while trying to stay close to vanilla mechanics.
- The **server** is responsible for connecting the vehicles, and the **client** is responsible for rendering the connection effect. (This means that each client can customize their display.)

## Loader

[![Fabric](https://img.shields.io/badge/Available%20for-Fabric-dbd0b4)](https://fabricmc.net)
[![NeoForge](https://img.shields.io/badge/Available%20for-NeoForge-e68c37)](https://neoforged.net)

## Environment

![ServerAndClient](https://img.shields.io/badge/Side-Server%20and%20Client-3c8527)

## Releases

[![Github](https://img.shields.io/badge/Published%20in-GitHub-808284?logo=github&logoColor=white)](https://github.com/KSSJW/minecart-trains-fork/releases)
[![Modrinth](https://img.shields.io/badge/Published%20in-Modrinth-1bd96a?logo=modrinth&logoColor=white)](https://modrinth.com/mod/minecart-trains-fork)
[![CurseForge](https://img.shields.io/badge/Published%20in-CurseForge-f16436?logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/minecart-trains-fork)

## Compatibility

### Fabric

| Mod                                                           | Environments      | Relation      | Description |
| :------------------------------------------------------------ |:----------------- | :------------ | :--- |
| [Fabric API](https://github.com/FabricMC/fabric-api)          | Server and Client | **Required**  | - |
| [Cloth Config API](https://github.com/shedaniel/cloth-config) | Client            | Optional      | Custom configuration features |
| [Mod Menu](https://github.com/TerraformersMC/ModMenu)         | Client            | Optional      | Configuration entry |

### NeoForge

| Mod                                                           | Environments      | Relation      | Description |
| :------------------------------------------------------------ |:----------------- | :------------ | :--- |
| [Cloth Config API](https://github.com/shedaniel/cloth-config) | Client            | Optional      | Custom configuration features |

## Progress

Check out the latest development progress here. [Development Progress](https://www.windysky.top/docs/minecraft-java-edition/minecart-trains-fork/version)

![Two Minecarts chained together](./images/Big.png)

![Default Line](./images/DefaultLine.png)

## Features

> [!TIP]
> In short, connect with **Iron Chain** and untie with an **Axe**.
- Currently the mod only allow you to chain Minecarts together. This is done by sneak-clicking the first cart of your train with a **iron chain** in your hand and then on the next one down. This process can be repeated as often as desired to construct your train.

> [!NOTE]
> For example, if I need to connect from car A to car D, the order of clicking while stealthed is: **A, B, B, C, C, D**
- When you hold the iron chain, you enter grouping mode; switching to other items will exit grouping.
- You can use any **axe** to ungroup a Minecart while sneaking by right-clicking.

> [!NOTE]
> You can use the axe to remove any connection at any time.
- Only the front cart can be pushed by the player and is the only one to be affected by booster rails, all other cars just act as wagons. Chaining works with all types of Minecarts.
- The client can change the rendering effects. (Some complex particles are not supported.)

![Config1](./images/Config1.png)

![Config2](./images/Config2.png)

![CustomConfig](./images/CustomConfig.png)