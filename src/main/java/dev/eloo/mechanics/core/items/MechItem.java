package dev.eloo.mechanics.core.items;

import com.google.common.base.Splitter;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.items.builder.ItemBuilder;
import dev.eloo.mechanics.utils.Chat;
import dev.eloo.mechanics.utils.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MechItem {

    private static final Mechanics dm = Mechanics.getMechanics();
    private static final String itemName = Chat.format(dm.getConfig().getString("items.displayname", "Mechanic-Item"));
    private static final List<String> lore = dm.getConfig().getStringList("items.itemlore");

    public static ItemStack getNewItem(String playerName) {
        return getNewItem(playerName, getID(playerName));
    }

    public static ItemStack getNewItem(String playerName, String mechanicID) {
        Logger.info("Mechanic item was successfully generated. MechanicID: " + mechanicID);
        return new ItemBuilder(Material.DROPPER)
                .addEnchantGlow()
                .addLore(getLore(playerName, mechanicID))
                .setName(itemName)
                .build();
    }

    public static boolean isValid(String mechanicID) {
        Connection con = dm.getDatabase().getCon();
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM mechanic WHERE mechanicID = ?")) {
            stmt.setString(1, mechanicID);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    public static void removeItem(String mechanicID) {
        Connection con = dm.getDatabase().getCon();
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM mechanic WHERE mechanicID = ?")) {
            stmt.setString(1, mechanicID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getOwner(String mechanicID) {
        String ret = "";
        Connection con = dm.getDatabase().getCon();
        try (PreparedStatement stmt = con.prepareStatement("SELECT owner FROM mechanic WHERE mechanicID = ?")) {
            stmt.setString(1, mechanicID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                ret = rs.getString("owner");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getMechanicID(ItemStack item) {
        if(item.getType().equals(Material.DROPPER)){
            List<String> lore = item.getItemMeta().getLore();
            if(lore != null && !lore.isEmpty()) {
                List<String> id = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(lore.get(2));
                return id.get(1);
            }
            return null;
        }
        throw new RuntimeException("Wrong type of item for mechanics!");
    }

    private static List<String> getLore(String playerName) {
        return getLore(playerName, getID(playerName));
    }

    private static List<String> getLore(String playerName, String mechanicID) {
        List<String> ret = new ArrayList<>();
        for(String s : lore) {
            String text = Chat.format(s.replace("%player%", playerName).replace("%id%", mechanicID));
            ret.add(text);
        }
        return ret;
    }

    private static String getID(String playerName) {
        String mechanicID = RandomStringUtils.random(8, true, true);
        Connection con = dm.getDatabase().getCon();
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO mechanic(owner, mechanicID) VALUES (?,?)")) {
            stmt.setString(1, playerName);
            stmt.setString(2, mechanicID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mechanicID;
    }

}
