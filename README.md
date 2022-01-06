# debugmarker
1.16.5-1.18.1 Debug Marker API
Just clone the DebugMarker.java class into your project, resolve dependencies and you should be good to go!

## Requires Netty and ProtocolLib dependencies
Netty is included within minecraft, so if you are using maven, you can use the following code in your pom.xml:
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

At any time, you can call setLocation(), setColor(), setName(), or setDuration() to set these variables to anthing you want. Keep in mind that due to minecrafts limitations, these changes *will only show to players seeing the marker for the first time, or after using the start(int distance) function*

Also due to minecrafts limitations, a debug marker can not be individually stopped after starting it. You can, however, stop the debug marker from showing to any new players with stop(). 
You can also call stopAll() to stop **all** debug markers in the world for 

a) a List of Player's 

b) Player's within a distance of the DebugMarker

c) Or you can use the static method stopAll(Location location, int distance) to stop all DebugMarker's for any player within a set distance of a specified location.

Note: You can use -1 as the distance for "unlimited" distance, aka stopping all DebugMarker's in the world for all players.
