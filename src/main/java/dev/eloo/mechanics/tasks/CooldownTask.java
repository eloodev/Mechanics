package dev.eloo.mechanics.tasks;

import dev.eloo.mechanics.Mechanics;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Map;

public class CooldownTask extends BukkitRunnable {

    private static final Mechanics mp = Mechanics.getMechanics();

    @Override
    public void run() {
        for(Map.Entry<Location, Long> s : mp.cooldown.entrySet()) {
            if(s.getValue() != null) {
                if((s.getValue() + 5000) <= System.currentTimeMillis()) {
                    //mp.cooldown.remove(s.getKey());
                }
            }
        }
    }

}
