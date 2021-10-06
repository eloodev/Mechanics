package dev.eloo.mechanics.events.dropper;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.events.MechanicEvent;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

public class PlacerMechanicEvent extends MechanicEvent {


    public PlacerMechanicEvent(Block block, Mechanic mechanic) {
        super(block, mechanic);
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        super.isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
