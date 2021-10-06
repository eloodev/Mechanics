package dev.eloo.mechanics.mechanic.listeners;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.utils.Chat;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryListener implements Listener {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Settings conf = mp.getSettings();

    /**
     * Prevents editing of the inventory when the mechanics are working.
     *
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if(isDropper(event.getInventory().getHolder())){
            Mechanic m = getMechanic((Dropper) event.getInventory().getHolder());
            if(m != null && m.isMechanicOn()) {
                Player p = (Player) event.getWhoClicked();
                if(mp.hasPlayerClaimTrust(p, m.getLocation())){
                    event.setCancelled(true);
                    Chat cp = new Chat(p);
                    cp.sendWarningMessage("Schalte die Mechanic ab bevor du das Inventar bearbeiten möchtest.");
                }
            }
        }
    }

    /**
     * Prevents access to the inventory when the mechanics are working.
     *
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        if(isDropper(inv.getHolder())){
            Mechanic m = getMechanic((Dropper) inv.getHolder());
            if(m != null && m.isMechanicOn()) {
                event.setCancelled(true);
                Chat p = new Chat((Player) event.getPlayer());
                p.sendWarningMessage("Schalte die Mechanic ab bevor du auf das Inventar zugreifst.");
            }
        }
    }

    /**
     * Turns the mechanic off and on
     *
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwitchMechanic(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) && isMechanicTool(event.getItem())){
            if(isDropper(event.getClickedBlock())){
                Mechanic m = getMechanic((Dropper) event.getClickedBlock().getState());
                if(m != null) {
                    Chat cp = new Chat(event.getPlayer());
                    if(m.isMechanicOn()) {
                        m.setMechanicOn(false);
                        cp.sendWarningMessage("Mechanik ausgeschaltet!");
                    } else if(!m.isMechanicOn()) {
                        m.setMechanicOn(true);
                        cp.sendSuccessMessage("Mechanik eingeschaltet!");
                    }
                }
            }
        }
    }

    private boolean isMechanicTool(@Nullable ItemStack item) {
        if(item != null) {
            return item.getType().equals(conf.ITEM_TOOL);
        }
        return false;
    }
    private boolean isDropper(InventoryHolder holder) {
        return holder instanceof Dropper;
    }
    private boolean isDropper(Block block) {
        return block instanceof Dropper;
    }
    private Mechanic getMechanic(Dropper d) {
        return mp.getMechanicsList().get(d.getLocation());
    }
}
