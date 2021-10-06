package dev.eloo.mechanics.mechanic.tasks;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.core.database.DBHandler;
import dev.eloo.mechanics.mechanic.Manager;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.Map;

public class SaveMechanicsTask extends BukkitRunnable {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final Settings conf = mp.getSettings();
    private static final Connection con = mp.getDatabase().getCon();
    private static final Manager mh = mp.getManager();

    @Override
    public void run() {
        console("Saving mechanics...");
        Map<Location, Mechanic> save = mp.getMechanicsList();
        for(Map.Entry<Location, Mechanic> entry : save.entrySet()) {
            DBHandler.save(entry.getValue());
            save.remove(entry.getValue().getLocation());
        }
        console("Successfully saved " + save.size() + " mechanics.");
    }

    public void shutdownSave() {
        console("Saving mechanics...");
        Map<Location, Mechanic> save = mp.getMechanicsList();
        for(Map.Entry<Location, Mechanic> entry : save.entrySet()) {
            if(conf.HOLOGRAMS_ENABLED) {
                entry.getValue().getHolograms().delete();
            }
            DBHandler.save(entry.getValue());
            save.remove(entry.getValue().getLocation());
        }
        console("Successfully saved " + save.size() + " mechanics.");
    }

    /**
     * Will be sent to the console.
     * @param message - Will be sent to the console.
     */
    private void console(String message) {
        Logger.sendMessage("[SaveTask] " + message);
    }

}
