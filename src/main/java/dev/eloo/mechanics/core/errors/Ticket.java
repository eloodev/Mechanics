package dev.eloo.mechanics.core.errors;

import org.bukkit.OfflinePlayer;

public class Ticket {

    private final String id;
    private final OfflinePlayer player;
    private final String message;
    private final String server;
    private final String world;
    private final String loc;
    private final int active;
    private final String type;

    public Ticket(String id, OfflinePlayer player, String server, String world, String loc, int active, String type, String message) {
        this.id = id;
        this.player = player;
        this.message = message;
        this.server = server;
        this.world = world;
        this.loc = loc;
        this.active = active;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public String getServer() {
        return server;
    }

    public String getWorld() {
        return world;
    }

    public String getLocation() {
        return loc;
    }

    public int isActive() {
        return active;
    }

    public String getType() {
        return type;
    }
}
