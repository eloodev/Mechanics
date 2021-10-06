package dev.eloo.mechanics.core.errors;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.utils.Chat;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.GregorianCalendar;

public class System {

    private final Mechanics mp = Mechanics.getMechanics();
    private final Connection con = mp.getDatabase().getCon();

    public System() {}

    public void addError(Ticket ticket) {
        Logger.warn("A new error ticket is about to create!");
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO errorsystem(errorID, player, server, world, location, active, type, message) VALUES (?,?,?,?,?,?,?,?)")) {
            stmt.setString(1, ticket.getId());
            stmt.setString(2, ticket.getPlayer().getName());
            stmt.setString(3, ticket.getServer());
            stmt.setString(4, ticket.getWorld());
            stmt.setString(5, ticket.getLocation());
            stmt.setInt(6, ticket.isActive());
            stmt.setString(7, ticket.getType());
            stmt.setString(8, ticket.getMessage());
            stmt.executeUpdate();
            if(ticket.getPlayer().isOnline()) {
                Chat cp = new Chat((Player) ticket.getPlayer());
                cp.sendErrorMessage("Es ist ein Fehler mit dieser Mechanic aufgetreten bitte melde dich bei einem Team-Mitglied.");
                cp.sendWarningMessage("Zeit/Datum: " + getTime());
                cp.sendWarningMessage("Error-ID: " + ticket.getId());
                cp.sendErrorMessage("Die Error-ID gibst du beim Support bekannt um schnell die Fehlerdetails zu finden.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Ticket getError(String id) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM errorsystem WHERE errorID = ?")) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(rs.getString("player"));
                return new Ticket(
                        rs.getString("errorID"),
                        op,
                        rs.getString("server"),
                        rs.getString("world"),
                        rs.getString("location"),
                        rs.getInt("active"),
                        rs.getString("type"),
                        rs.getString("message")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteError(String id) {

    }

    private String getTime() {
        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        return df.format(now.getTime());
    }

}
