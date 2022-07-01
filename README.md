# debugmarker
1.16.5-1.19 Debug Marker API, allowing developers to create their own debug markers, which are slightly-bigger-than-blocks that can be walked through and can have any custom color (including alpha) and name!

In newer versions, you must use a resource pack shader if you want any other colors than black or green, as newer versions (1.17+) limits debug markers to only show the green channel. An example of a resource pack which would accomplish this is here: https://github.com/mworzala/canary/tree/448fa848c43370371d955692ed5d8fba71f76155/resourcepack/src/assets/minecraft/shaders/core
(Personally, i would recommend changing position_color.fsh to have `if (ColorModulator == vec4(0, 1, 0, 0.75)) { fragColor = color * vec4(1, 1, 1, 1);}` as this allows for full usage of the alpha spectrum, instead of being limited at ~204 alpha)

Just clone the DebugMarker.java class into your project, resolve dependencies and you should be good to go!

https://user-images.githubusercontent.com/96733109/148328044-96de7b38-12c3-4ce3-83c6-25faf037be27.mp4

## Requires Netty and ProtocolLib dependencies
Netty is included within minecraft, so if you are using maven, just use the following in your pom.xml: (assuming you have built spigot with buildtools before)
```
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot</artifactId>
            <version>1.18.1-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
```

## Usage
Instantiate a new DebugMarker with either `DebugMarker(Location location, java.awt.Color color, String name, int duration)` or `DebugMarker(Location location, java.awt.Color color, String name, int duration, List<Player> showTo)` which automatically shows the debug marker to the provided list of players.

At any time, you can call setLocation(), setColor(), setName(), or setDuration() to set these variables to anthing you want. You will have to call start() again to send these changes to players.

You can call either stop() to stop an individual marker, or stopAll() to stop all markers in the world for

a) a List of Player's 

b) Player's within a distance of the DebugMarker

c) Or you can use the static method stopAll(Location location, int distance) to stop all DebugMarker's for any player within a set distance of a specified location.

Note: You can use -1 as the distance for "unlimited" distance, aka stopping all DebugMarker's in the world for all players.

If you run into any issues or overall have any questions, my discord is here: https://discord.gg/3tFg7RPvJr
