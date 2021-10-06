package dev.eloo.mechanics.mechanic.tasks;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.core.errors.System;
import dev.eloo.mechanics.mechanic.Manager;
import dev.eloo.mechanics.mechanic.objects.*;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoadMechanicsTask extends BukkitRunnable {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Settings conf = mp.getSettings();
    private static final Connection con = mp.getDatabase().getCon();
    private static final Manager mh = mp.getManager();
    private static final System sys = mp.getSystem();

    private final List<World> worldList;

    public LoadMechanicsTask(List<World> list) {
        this.worldList = list;
    }

    @Override
    public void run() {
        for(World world : worldList) {
            console("A world (" + world.getName() + ") was loaded, checking if the world has mechanics...");
            if(hasWorldMechanic(world)){
                console("World: " + world.getName() + ") the mechanic loader is starting...");
                Map<Location, Mechanic> loaded = loadWorldMechanics(world);
                if(!loaded.isEmpty()){
                    console("World: " + world.getName() + ") mechanics loaded: " + loaded.size());
                    for(Map.Entry<Location, Mechanic> entry : loaded.entrySet()) {
                        mh.initialize(entry.getValue());
                    }
                    continue;
                }
                throw new RuntimeException("The world has active mechanics but they cannot be loaded.");
            } else {
                console("World: " + world.getName() + ") No mechanics have been found.");
            }
        }
    }

    /**
     * Checks if the world has valid mechanics
     * @param world - The world to be checked whether mechanics exist.
     * @return - If exist mechanics it returns true otherwise false.
     */
    private boolean hasWorldMechanic(World world) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM mechanics WHERE world = ? AND server = ?")) {
            stmt.setString(1, world.getName().toLowerCase());
            stmt.setString(2, conf.SERVER_NAME);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Load all mechanics for the given world.
     * @param world - all mechanics in the given world.
     * @return - Map with all mechanics in the given world.
     */
    private Map<Location, Mechanic> loadWorldMechanics(World world) {
        Map<Location, Mechanic> mechanicMap = new HashMap<>();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM mechanics WHERE server = ? AND world = ?")) {
            stmt.setString(1, conf.SERVER_NAME.toLowerCase());
            stmt.setString(2, world.getName().toLowerCase());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Location loc = convertStringToLocation(rs.getString("location"), world);
                UUID owner = UUID.fromString(rs.getString("owner"));
                switch (rs.getString("type").toLowerCase()) {
                    case "blockbreaker":
                        mechanicMap.put(loc, new Breaker(loc, owner));
                        break;
                    case "blockplacer":
                        mechanicMap.put(loc, new Placer(loc, owner));
                        break;
                    case "trasher":
                        mechanicMap.put(loc, new Trasher(loc, owner));
                        break;
                    case "compactor":
                        mechanicMap.put(loc, new Compactor(loc, owner));
                        break;
                    case "harvester":
                        mechanicMap.put(loc, new Harvester(loc, owner));
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mechanicMap;
    }

    /**
     * Converts the location to a string.
     * @param l - Will be converted to a string.
     * @return - The location as string.
     */
    private static String convertLocationToString(@NotNull final Location l) {
        return l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
    }

    /**
     * Converts the string to a location.
     * @param s - Will be converted to a location.
     * @param world - The world where the location is.
     * @return - The String as valid location.
     */
    private static Location convertStringToLocation(@NotNull final String s, World world) {
        if (!s.trim().equals("")) {
            final String[] parts = s.split(":");
            if (parts.length == 3) {
                final int x = Integer.parseInt(parts[0]);
                final int y = Integer.parseInt(parts[1]);
                final int z = Integer.parseInt(parts[2]);
                return new Location(world, x, y, z);
            }
        }
        throw new NullPointerException();
    }

    /**
     * Will be sent to the console.
     * @param message - Will be sent to the console.
     */
    private void console(String message) {
        Logger.sendMessage("[MLoader] " + message);
    }
}
