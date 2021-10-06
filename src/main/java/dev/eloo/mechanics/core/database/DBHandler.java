package dev.eloo.mechanics.core.database;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.exceptions.UnknownServerException;
import dev.eloo.mechanics.mechanic.objects.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.*;

public class DBHandler {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Connection db = mp.getDatabase().getCon();

    public static void save(Mechanic mechanic) {
        if(exist(mp.serverName, mechanic)) {
            save(mp.serverName, mechanic);
        } else {
            add(mp.serverName, mechanic);
        }
    }

    public static void add(String server, Mechanic mechanic) {
        Bukkit.getScheduler().runTaskAsynchronously(mp, () -> {
            if(!exist(server, mechanic)){
                try (PreparedStatement stmt = db.prepareStatement("INSERT INTO mechanics(server, world, location, type, owner) VALUES (?,?,?,?,?)")) {
                    stmt.setString(1, server);
                    stmt.setString(2, mechanic.getLocation().getWorld().getName().toLowerCase());
                    stmt.setString(3, locationToString(mechanic.getLocation()));
                    stmt.setString(4, mechanic.getMechanicName().toLowerCase());
                    stmt.setString(5, mechanic.getOwner().toString());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    throw new Exception("Mechanic already exist!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void save(String server, Mechanic mechanic) {
        Bukkit.getScheduler().runTaskAsynchronously(mp, () -> {
            try (PreparedStatement stmt = db.prepareStatement("UPDATE mechanics SET type = ? WHERE location = ? AND server = ? AND world = ?")) {
                stmt.setString(1, mechanic.getMechanicName().toLowerCase());
                stmt.setString(2, locationToString(mechanic.getLocation()));
                stmt.setString(3, server.toLowerCase());
                stmt.setString(4, mechanic.getLocation().getWorld().getName().toLowerCase());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean exist(String server, Mechanic mechanic) {
        try {
            PreparedStatement stmt = db.prepareStatement("SELECT * FROM mechanics WHERE server = ? AND location = ?");
            stmt.setString(1, server);
            stmt.setString(2, locationToString(mechanic.getLocation()));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void remove(Mechanic mechanic) {
        Bukkit.getScheduler().runTaskAsynchronously(mp, () -> {
            try (PreparedStatement stmt = db.prepareStatement("DELETE FROM mechanics WHERE location = ?")) {
                stmt.setString(1, locationToString(mechanic.getLocation()));
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<Location, Mechanic> getMechanics(String server, World world) throws UnknownServerException{
        Map<Location, Mechanic> ret = new HashMap<>();
        try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM mechanics WHERE server = ? AND world = ?")) {
            stmt.setString(1, server.toLowerCase());
            stmt.setString(2, world.getName().toLowerCase());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Location loc = stringToLocation(rs.getString("location"), world);
                UUID owner = UUID.fromString(rs.getString("owner"));
                switch (rs.getString("type").toLowerCase()) {
                    case "blockbreaker":
                        ret.put(loc, new Breaker(loc, owner));
                        break;
                    case "blockplacer":
                        ret.put(loc, new Placer(loc, owner));
                        break;
                    case "trasher":
                        ret.put(loc, new Trasher(loc, owner));
                        break;
                    case "compactor":
                        ret.put(loc, new Compactor(loc, owner));
                        break;
                    case "harvester":
                        ret.put(loc, new Harvester(loc, owner));
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean hasWorldMechanics(World world) {
        try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM mechanics WHERE world = ? AND server = ?")) {
            stmt.setString(1, world.getName().toLowerCase());
            stmt.setString(2, mp.serverName);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getItems(Player player) {
        List<String> ret = new ArrayList<>();
        try (PreparedStatement stmt = db.prepareStatement("SELECT * FROM mechanic WHERE owner = ?")) {
            stmt.setString(1, player.getName());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ret.add(rs.getString("mechanicID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static String locationToString(final Location l) {
        if(l != null) {
            return l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ() ;
        }
        throw new NullPointerException();
    }

    private static Location stringToLocation(final String s, World world) {
        if (s != null && !s.trim().equals("")) {
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

}
