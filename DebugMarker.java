import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.MinecraftKey;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugMarker {
    private static final PacketContainer STOP_ALL_MARKERS = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
    private PacketDataSerializer data;
    private final PacketContainer marker;
    private org.bukkit.Location location;
    private Color color;
    private String name;
    private int duration;
    private int distanceSquared;
    private final List<Player> seen;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    public DebugMarker(org.bukkit.Location location, Color color, String name, int duration, List<Player> showTo) throws InvocationTargetException {
        this(location, color, name, duration);
        for (Player player : showTo) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, marker);
        }
        seen.addAll(showTo);
    }
    public DebugMarker(org.bukkit.Location location, Color color, String name, int duration) {
        this.location = location;
        this.color = color;
        this.name = name;
        this.duration = duration;
        seen = new ArrayList<>();
        marker = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        data = new PacketDataSerializer(Unpooled.buffer());
        data.a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())); // location
        data.writeInt(color.getRGB()); // color
        data.a(name); // name
        data.writeInt(duration); // lifetime of marker
        
        marker.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_add_marker"));
        marker.getSpecificModifier(PacketDataSerializer.class).write(0, data);
    }
    public void start(int distance) {
        start(distance, () -> {
        }); // do-nothing runnable
    }
    public void start(int distance, Runnable callback) {
        distanceSquared = distance < 0 ? -1 : distance * distance;
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis() + duration;
        executorService = Executors.newScheduledThreadPool(1);
        // probably not the most efficient way of doing this
        Runnable run = () -> {
            for (Player p : location.getWorld().getPlayers()) {
                if (System.currentTimeMillis() > endTime) {
                    executorService.shutdown();
                    callback.run();
                    return;
                }
                if (isCloseEnough(p.getLocation()) && !seen.contains(p)) {
                    setData(location, color, name, (int) (endTime - System.currentTimeMillis())); // make sure death time is the same for all players
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, marker);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    seen.add(p);
                }
            }
        };
        executorService.scheduleAtFixedRate(run, 0, 200, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        executorService.shutdownNow();
    }
    
    public void stopAll(int distance) throws InvocationTargetException {
        int distanceSquared = distance < 0 ? -1 : distance * distance;
        // probably not the most efficient way of doing this
        for (Player p : location.getWorld().getPlayers()) {
            if (distanceSquared == -1 || this.location.distanceSquared(p.getLocation()) <= distanceSquared) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, STOP_ALL_MARKERS);
            }
        }
    }
    public static void stopAll(List<Player> stopTo) throws InvocationTargetException {
        for (Player player : stopTo) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, STOP_ALL_MARKERS);
        }
    }
    public static void stopAll(org.bukkit.Location location, int distance) throws InvocationTargetException {
        int distanceSquared = distance < 0 ? -1 : distance * distance;
        // probably not the most efficient way of doing this
        for (Player p : location.getWorld().getPlayers()) {
            if (distanceSquared == -1 || location.distanceSquared(p.getLocation()) <= distanceSquared) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, STOP_ALL_MARKERS);
            }
        }
    }
    
    private void setData(org.bukkit.Location location, Color color, String name, int duration) {
        data = new PacketDataSerializer(Unpooled.buffer());
        data.a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        data.writeInt(color.getRGB());
        data.a(name);
        data.writeInt(duration);
        marker.getSpecificModifier(PacketDataSerializer.class).write(0, data);
    }
    
    public void setLocation(org.bukkit.Location location) {
        this.location = location;
        setData(this.location, this.color, this.name, this.duration);
    }
    public void setColor(Color color) {
        this.color = color;
        setData(this.location, this.color, this.name, this.duration);
    }
    public void setName(String name) {
        this.name = name;
        setData(this.location, this.color, this.name, this.duration);
    }
    public void setDuration(int duration) {
        this.duration = duration;
        setData(this.location, this.color, this.name, this.duration);
    }
    
    public org.bukkit.Location getLocation() {
        return location;
    }
    public Color getColor() {
        return color;
    }
    public String getName() {
        return name;
    }
    public int getDuration() {
        return duration;
    }
    
    private boolean isCloseEnough(org.bukkit.Location location) {
        return distanceSquared == -1 ||
                this.location.distanceSquared(location) <= distanceSquared;
    }
    static {
        STOP_ALL_MARKERS.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_clear"));
        STOP_ALL_MARKERS.getSpecificModifier(PacketDataSerializer.class).write(0, new PacketDataSerializer(Unpooled.buffer()));
    }
}