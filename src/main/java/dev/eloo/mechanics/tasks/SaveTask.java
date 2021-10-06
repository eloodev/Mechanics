package dev.eloo.mechanics.tasks;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.database.DBHandler;
import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SaveTask extends BukkitRunnable {

    public static final List<Mechanic> mechanicList = new ArrayList<>();
    private static final Mechanics mp = Mechanics.getMechanics();

    @Override
    public void run() {
        for(Mechanic mechanic : mechanicList) {
            mechanic.getHolograms().delete();
            DBHandler.save(mp.serverName, mechanic);
        }
        Logger.info("Mechanics successfully saved.");
    }

    public void run(boolean delete) {
        for(Mechanic mechanic : mechanicList) {
            mechanic.getHolograms().delete();
            DBHandler.save(mp.serverName, mechanic);
        }
        Logger.info("Mechanics successfully saved.");
    }
}
