package dev.eloo.mechanics.events;

import dev.eloo.mechanics.mechanic.Mechanic;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class MechanicEvent extends Event implements Cancellable {

    public final Block block;
    public final Mechanic mechanic;
    public boolean isCancelled = false;


    public MechanicEvent(Block block, Mechanic mechanic) {
        this.block = block;
        this.mechanic = mechanic;
    }

    public Block getBlock() {
        return block;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

}
