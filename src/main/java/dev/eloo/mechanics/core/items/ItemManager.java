package dev.eloo.mechanics.core.items;

import com.google.common.base.Splitter;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.utils.Chat;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Settings conf = mp.getSettings();
    private static final Connection con = mp.getDatabase().getCon();

    public Boolean isValid(OfflinePlayer player, String itemID) {
        try (PreparedStatement stmt = con.prepareStatement("SELECT * FROM mechanic WHERE owner = ? AND mechanicID = ?")) {
            stmt.setString(1, player.getName());
            stmt.setString(2, itemID);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete(OfflinePlayer player, String itemID) {
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM mechanic WHERE owner = ? AND mechanicID = ?")) {
            stmt.setString(1, player.getName());
            stmt.setString(2, itemID);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getMechanicID(ItemStack item) {
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

    public List<String> getLore(@NotNull OfflinePlayer playerName) {
        return getLore(playerName, generateID(playerName));
    }

    public List<String> getLore(@NotNull OfflinePlayer player, String mechanicID) {
        List<String> ret = new ArrayList<>();
        for(String s : conf.ITEM_LORE) {
            String text = Chat.format(s.replace("%player%", player.getName()).replace("%id%", mechanicID));
            ret.add(text);
        }
        return ret;
    }

    /**
     * Generate the ID for the Items that the player will become.
     * @param player - With the name of the player will generate the ID.
     * @return - The new generated ID for the player.
     */
    public String generateID(@NotNull OfflinePlayer player) {
        String id = RandomStringUtils.random(8, true, true);
        try (PreparedStatement stmt = con.prepareStatement("INSERT INTO mechanic(owner, mechanicID) VALUES (?,?)")) {
            stmt.setString(1, player.getName());
            stmt.setString(2, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }


}
