package dev.eloo.mechanics.mechanic.objects;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.events.dropper.BreakerMechanicEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class  Breaker extends Mechanic {

    public Breaker(Location location, UUID owner) {
        super(location, owner);
    }

    public boolean equals(Object obj) {
        if(obj instanceof Placer) {
            Placer other = (Placer) obj;
            return (other.getOwner().equals(getOwner()) && other.getLocation().equals(getLocation()));
        }
        return false;
    }

    public String toString() {
        return "{Block placer, Location: +" + this.location.toString() + ", Owner: " + Bukkit.getOfflinePlayer(this.owner).getName() + "}";
    }

    @Override
    public String getMechanicName() {
        return "BlockBreaker";
    }

	@Override
	public void run(BlockDispenseEvent event) {
        event.setCancelled(true);
        if(event.getItem().getType().name().endsWith("PICKAXE")) {
            Block toBreak = getTargetBlock(event.getBlock());
            if(!isForbiddenBlock(toBreak)) {
                Bukkit.getServer().getPluginManager().callEvent(new BreakerMechanicEvent(toBreak, this));
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        boolean blockBreak = true;
                        if(Settings.DamageTools) {
                            ItemStack tool = event.getItem().clone();
                            Damageable d1 = (Damageable) tool.getItemMeta();
                            d1.setDamage(d1.getDamage() + 1);
                            getDropper().getInventory().removeItem(event.getItem());
                            if(d1.getDamage() < getMaxDamage(tool.getType())) {
                                tool.setItemMeta((ItemMeta) d1);
                                getDropper().getInventory().addItem(tool);
                            } else {
                                blockBreak = false;
                            }
                        }
                        if (blockBreak) {
                            toBreak.getLocation().getWorld().playSound(toBreak.getLocation(), Sound.BLOCK_BONE_BLOCK_BREAK, 1, 1);
                            toBreak.breakNaturally();
                        }
                    }
                }).runTask(dm);
            }
        }
	}
}
