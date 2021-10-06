package dev.eloo.mechanics.mechanic;


import java.util.UUID;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.hologram.Holograms;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.event.block.BlockDispenseEvent;

public abstract class Mechanic {

    protected Location location;
    protected UUID owner;
    protected Holograms holograms;
    protected boolean isMechanicOn = false;
    protected static Mechanics dm = Mechanics.getMechanics();
    
    public Mechanic(Location location, UUID owner) {
        this.location = location;
        this.owner = owner;

    }

    public void enableHologram() {
        this.holograms = new Holograms(location, getMechanicName());
        this.holograms.build();
    }
    
    public Location getLocation() {
        return this.location;
    }

    public boolean isOwner(UUID uuid) {
        return owner == uuid;
    }

    public int getMaxDamage(Material material) {
        String name = material.name();
        if(name.endsWith("AXE") || name.endsWith("PICKAXE")) {
            return material.getMaxDurability();
        }
        throw new RuntimeException("A non-allowed item was detected as a tool.");
    }

    public UUID getOwner() {
        return this.owner;
    }

    public Dropper getDropper() {
        return (Dropper) getLocation().getBlock().getState();
    }

    public World getWorld() {
        return getLocation().getWorld();
    }

    public Block getTargetBlock(Block block) {
        return block.getRelative(((Dispenser) block.getBlockData()).getFacing());
    }

    public boolean isForbiddenBlock(Block block) {
        if(block.getType().isBlock()) {
            return block.getType().equals(Material.AIR) ||
                    block.getType().equals(Material.BEDROCK) ||
                    block.getType().equals(Material.WATER) ||
                    block.getType().equals(Material.LAVA);
        }
        return true;
    }

    public Holograms getHolograms() {
        return holograms;
    }

    public boolean isMechanicOn() {
        return this.isMechanicOn;
    }

    public void setMechanicOn(boolean status) {
        this.isMechanicOn = status;
    }

    public abstract String getMechanicName();

    public abstract void run(BlockDispenseEvent event);
}
