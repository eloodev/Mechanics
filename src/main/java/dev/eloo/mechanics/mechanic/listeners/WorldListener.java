package dev.eloo.mechanics.mechanic.listeners;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.core.database.DBHandler;
import dev.eloo.mechanics.exceptions.UnknownServerException;
import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Map;

public class WorldListener implements Listener {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Settings conf = mp.getSettings();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldLoadEvent event) {
        if(DBHandler.hasWorldMechanics(event.getWorld())) {
            World w = event.getWorld();
            Logger.warn(w.getName() + " has been loaded! Starting the mechanic loader for this world.");
            try {
                mp.getMechanicsList().putAll(DBHandler.getMechanics(conf.SERVER_NAME, w));
            } catch (UnknownServerException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinServer(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if(p.hasPermission("dm.mechanic.create")) {
            if(mp.getHologramList().contains(p)){
                for(Map.Entry<Location, Mechanic> entry: mp.getMechanicsList().entrySet()) {
                    Mechanic mechanic = entry.getValue();
                    mechanic.getHolograms().setVisiblePlayer(p);
                }
            }
        }
    }

}
