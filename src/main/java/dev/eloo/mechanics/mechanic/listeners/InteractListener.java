package dev.eloo.mechanics.mechanic.listeners;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.mechanic.Manager;
import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.mechanic.objects.Breaker;
import dev.eloo.mechanics.utils.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Manager ma = mp.getManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropperClick(PlayerInteractEvent event) {
        if(ma.isRightClickBlock(event.getAction()) && mp.isMechanicTool(event.getItem())) {
            if(mp.isBlockDropper(event.getClickedBlock()) && mp.hasPlayerClaimTrust(event.getPlayer(), event.getClickedBlock().getLocation())) {
                Player p = event.getPlayer();
                Chat cp = new Chat(p);
                if(p.hasPermission("dm.mechanic.create")) {
                    event.setCancelled(true);
                    Mechanic m = ma.getMechanic(event.getClickedBlock());
                    if(m != null) {
                        if(!m.isOwner(p.getUniqueId()) && !p.hasPermission("dm.mechanic.other")) {
                            cp.sendErrorMessage("Du hast keine Rechte diese Mechanik zu bearbeiten.");
                        } else {
                            m = ma.changeMechanic(m);
                            cp.sendSuccessMessage("Mechanik: " + m.getMechanicName());
                        }
                    } else {
                        boolean build = true;
                        if(!p.hasPermission("dm.mechanic.bypass")) {
                            if (!ma.payMechanic(p)) {
                                build = false;
                                cp.sendErrorMessage("Du hast nicht genug Geld für diese Mechanik.");
                            }
                        }
                        if(build) {
                            ma.setupMechanic(new Breaker(event.getClickedBlock().getLocation(), p.getUniqueId()));
                            cp.sendSuccessMessage("Mechanik erfolgreich erstellt.");
                            cp.sendWarningMessage("Mit Rechtsklick änderst du immer wieder die Mechanik.");
                        }
                    }
                }
            }
        }
    }

}
