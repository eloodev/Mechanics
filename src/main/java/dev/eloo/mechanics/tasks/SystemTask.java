package dev.eloo.mechanics.tasks;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

public class SystemTask extends BukkitRunnable {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final PluginManager pm = mp.getServer().getPluginManager();

    @Override
    public void run() {
        console("Checking if supported plugins are installed...");
        Plugin grief = pm.getPlugin("GriefPrevention");
        if(grief != null && grief.isEnabled()) {
            console("GriefPrevention found, plugin implementation now enabled.");
            mp.setGriefSystem(grief);
        }
        Plugin vault = pm.getPlugin("Vault");
        if(vault != null && vault.isEnabled()) {
            console("Vault found, plugin implementation now enabled.");
            RegisteredServiceProvider<Economy> rsp = mp.getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp != null) {
                Logger.sendMessage("§aVault functions enabled.");
                mp.setEcoSystem(rsp.getProvider());
            } else {
                Logger.info("Economy system not found!");
            }
        }
    }

    /**
     * Will be sent to the console.
     * @param message - Will be sent to the console.
     */
    private void console(String message) {
        Logger.sendMessage("[System] " + message);
    }
}
