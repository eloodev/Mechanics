package dev.eloo.mechanics.mechanic;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.mechanic.objects.*;
import dev.eloo.mechanics.core.database.DBHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.jetbrains.annotations.Nullable;

public class Manager {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Economy eco = mp.getEcoSystem();

    public Manager() { }

    public Mechanic setupMechanic(Mechanic mechanic) {
        DBHandler.save(mechanic);
        mp.getMechanicsList().put(mechanic.getLocation(), mechanic);
        Dropper d = mechanic.getDropper();
        d.setCustomName(mechanic.getMechanicName());
        d.getWorld().spawnParticle(Particle.PORTAL, d.getLocation().add(0.5, 0.5, 0.5), 100);
        d.getWorld().playSound(d.getLocation(), Sound.BLOCK_PISTON_EXTEND, 1, 1);
        d.update();
        return mechanic;
    }

    public Mechanic changeMechanic(Mechanic mechanic) {
        switch (mechanic.getMechanicName().toLowerCase()) {
            case "blockbreaker":
                mechanic.getHolograms().delete();
                remove(mechanic);
                return setupMechanic(new Placer(mechanic.getLocation(), mechanic.getOwner()));
            case "blockplacer":
                mechanic.getHolograms().delete();
                remove(mechanic);
                return setupMechanic(new Trasher(mechanic.getLocation(), mechanic.getOwner()));
            case "trasher":
                mechanic.getHolograms().delete();
                remove(mechanic);
                return setupMechanic(new Compactor(mechanic.getLocation(), mechanic.getOwner()));
            case "compactor":
                mechanic.getHolograms().delete();
                remove(mechanic);
                return setupMechanic(new Harvester(mechanic.getLocation(), mechanic.getOwner()));
            case "harvester":
            default:
                mechanic.getHolograms().delete();
                remove(mechanic);
                return setupMechanic(new Breaker(mechanic.getLocation(), mechanic.getOwner()));
        }
    }

    public Location getFrontBlockPosition(Block block) {
        return block.getRelative(((Dispenser) block.getBlockData()).getFacing()).getLocation();
    }

    public Boolean payMechanic(Player player) {
        if(eco != null) {
            if(eco.getBalance(player) >= 300) {
                EconomyResponse res = eco.withdrawPlayer(player, 300);
                return !res.type.equals(EconomyResponse.ResponseType.FAILURE);
            }
        }
        return true;
    }

    public boolean isDropperBlock(@Nullable Block block) {
        return block instanceof Dropper;
    }

    public Mechanic getMechanic(@Nullable Block block) {
        if(block != null) {
            return mp.getMechanicsList().get(block.getLocation());
        }
        return null;
    }

    public boolean isRightClickBlock(Action action) {
        return action.equals(Action.RIGHT_CLICK_BLOCK);
    }

    public boolean isLeftClickBlock(Action action) {
        return action.equals(Action.LEFT_CLICK_BLOCK);
    }

    public void remove(Mechanic mechanic) {
        mp.getMechanicsList().remove(mechanic.getLocation());
    }

}
