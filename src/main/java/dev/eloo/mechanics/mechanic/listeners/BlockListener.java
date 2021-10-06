package dev.eloo.mechanics.mechanic.listeners;

import dev.eloo.mechanics.core.items.ItemManager;
import dev.eloo.mechanics.core.items.MechItem;
import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.items.Item;
import dev.eloo.mechanics.mechanic.Manager;
import dev.eloo.mechanics.mechanic.objects.Breaker;
import dev.eloo.mechanics.utils.Chat;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Manager ma = mp.getManager();
    private static final ItemManager im = mp.getItemManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMechanicWork(final BlockDispenseEvent event) {
        if(ma.isDropperBlock(event.getBlock())){
            Mechanic m = ma.getMechanic(event.getBlock());
            if(m != null && m.isMechanicOn()) {
                m.run(event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDestroy(BlockBreakEvent event) {
        if(ma.isDropperBlock(event.getBlock())) {
            Mechanic m = ma.getMechanic(event.getBlock());
            if(m != null) {
                if(mp.hasPlayerClaimTrust(event.getPlayer(), m.getLocation())) {
                    Chat cp = new Chat(event.getPlayer());
                    if(m.isOwner(event.getPlayer().getUniqueId())) {
                        event.setDropItems(false);
                        ma.remove(m);
                        if(!event.getPlayer().hasPermission("dm.mechanic.bypass")) {
                            new Item(event.getPlayer()).drop();
                        }
                        cp.sendSuccessMessage("Du hast die Mechanik abgebaut!");
                    } else {
                        if(event.getPlayer().hasPermission("dm.mechanic.other")) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(m.getOwner());
                            cp.sendWarningMessage("Du hast die Mechanik von " + op.getName() + " abgebaut!");
                            new Item(op);
                        }
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (mp.isBlockDropper(event.getBlock())) {
            BlockFace face = ((Dispenser) event.getBlock().getBlockData()).getFacing();
            Location relative = event.getBlock().getRelative(face).getLocation();
            if (mp.hasPlayerClaimTrust(event.getPlayer(), relative)) {
                Player player = event.getPlayer();
                Chat cp = new Chat(player);
                String mechanicID = MechItem.getMechanicID(event.getItemInHand());
                if (mechanicID != null) {
                    if (MechItem.isValid(mechanicID)) {
                        boolean build = true;
                        String owner = MechItem.getOwner(mechanicID);
                        if (!owner.equalsIgnoreCase(player.getName())) {
                            if (!player.hasPermission("dm.mechanic.other")) {
                                cp.sendErrorMessage("Diese Mechanik gehört dir nicht!");
                                build = false;
                            } else {
                                cp.sendWarningMessage("Achtung dieser Mechanik block gehört nun dir!");
                            }
                        }
                        if (build) {
                            MechItem.removeItem(mechanicID);
                            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                                player.getInventory().removeItem(event.getItemInHand());
                            }
                            Mechanic mechanic = new Breaker(event.getBlock().getLocation(), player.getUniqueId());
                            ma.setupMechanic(mechanic);
                            cp.sendSuccessMessage("Mechanik erfolgreich platziert.");
                        } else {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                        player.getInventory().remove(event.getItemInHand());
                        cp.sendErrorMessage("Dieser Mechanik-Block ist ungültig und wurde gelöscht!");
                        if (!player.hasPermission("dm.mechanic.bypass")) {
                            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                                    "cheatnotify Mechanics Ein ungültiger Mechanik-Block wurde bei " + player.getName() + " beim Versuch zu platzieren gelöscht!");
                            Logger.warn("Invalid mechanic tried to place: MechanicID: " + mechanicID + ", Player: " + player.getName() + ", World: " + player.getWorld().getName());
                        }
                    }
                }
            }
        }
    }
}
