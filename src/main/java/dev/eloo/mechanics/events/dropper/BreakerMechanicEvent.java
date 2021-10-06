package dev.eloo.mechanics.events.dropper;

import dev.eloo.mechanics.mechanic.objects.Breaker;
import dev.eloo.mechanics.events.MechanicEvent;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;

public class BreakerMechanicEvent extends MechanicEvent {

    private final static HandlerList handlers = new HandlerList();

    public BreakerMechanicEvent(Block broken, Breaker blockBreaker) {
        super(broken, blockBreaker);
    }

    public Breaker getBreaker() {
        return (Breaker) getMechanic();
    }

    public Block getBrokenBlock() {
        return getBlock();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        super.isCancelled = cancel;
    }
}
