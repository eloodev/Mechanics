package dev.eloo.mechanics.core.items;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.core.items.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Item {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final ItemManager im = mp.getItemManager();
    private static final Settings conf = mp.getSettings();

    private final OfflinePlayer owner;
    private final String id;
    private final ItemBuilder item = new ItemBuilder(Material.DROPPER);

    public Item(OfflinePlayer player) {
        this.owner = player;
        this.id = im.generateID(player);
    }

    public Item(OfflinePlayer player, String itemID) {
        this.owner = player;
        this.id = itemID;
    }

    public boolean isValid() {
        Boolean result = im.isValid(owner, id);
        if(result != null) {
            return result;
        }
        return false;
    }

    public void delete() {
        im.delete(owner, id);
    }

    public void drop() {
        if(owner.isOnline()) {
            Player p = (Player) owner;
            p.getWorld().dropItem(p.getLocation(), item.build());
        }
    }

    public void drop(OfflinePlayer player) {
        if (owner.isOnline()) {
            Player p = (Player) owner;
            p.getWorld().dropItem(p.getLocation(), item.build());
        }
    }
}
