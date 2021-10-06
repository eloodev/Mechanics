package dev.eloo.mechanics.events.listeners;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.items.MechItem;
import dev.eloo.mechanics.utils.Logger;
import dev.eloo.mechanics.mechanic.objects.Breaker;
import dev.eloo.mechanics.utils.Chat;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class BlockListener implements Listener {

	private final Mechanics dm = Mechanics.getMechanics();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (dm.isBlockDropper(event.getBlock())) {
			BlockFace face = ((Dispenser) event.getBlock().getBlockData()).getFacing();
			Location relative = event.getBlock().getRelative(face).getLocation();
			if (dm.hasPlayerClaimTrust(event.getPlayer(), relative)) {
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
							//Handler.initialize(mechanic);
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