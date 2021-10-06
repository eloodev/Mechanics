package dev.eloo.mechanics.mechanic.objects;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Dropper;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Trasher extends Mechanic {

    private static final Mechanics dm = Mechanics.getMechanics();

    public Trasher(Location location, UUID owner) {
    	super(location, owner);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Trasher) {
            Trasher trasher = (Trasher) obj;
            return (trasher.getOwner().equals(getOwner()) && trasher.getLocation().equals(getLocation()));
        }
        return false;
    }

	@Override
	public void run(BlockDispenseEvent events) {
        if(events != null) {
            events.setCancelled(true);
            Dropper d = (Dropper) events.getBlock().getState();
            (new BukkitRunnable() {
                @Override
                public void run() {
                    d.getLocation().getWorld().playSound(d.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                    d.getInventory().clear();
                }
            }).runTask(dm);
        }
	}

    @Override
    public String getMechanicName() {
        return "Trasher";
    }
}