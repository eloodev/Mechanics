package dev.eloo.mechanics.tasks;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class HologramTask extends BukkitRunnable {

    private static final Mechanics mp = Mechanics.getMechanics();

    @Override
    public void run() {
        Plugin holo = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
        if(holo != null && holo.isEnabled()) {
            mp.isHologramEnabled = true;
            for(Map.Entry<Location, Mechanic> entry : mp.getMechanics().entrySet()) {
                entry.getValue().enableHologram();
            }

            Logger.info("HolographicDisplays Plugin found, enabling features!");
        }
    }

}
