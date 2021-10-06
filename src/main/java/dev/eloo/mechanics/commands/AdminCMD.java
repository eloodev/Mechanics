package dev.eloo.mechanics.commands;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.utils.Logger;
import dev.eloo.mechanics.utils.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class AdminCMD implements CommandExecutor {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final PluginCommand cmd = mp.getCommand("amechanics");

    public AdminCMD() {
        if(cmd != null) {
            cmd.setExecutor(this);
        } else {
            Logger.warn("Command: amechanic was not loaded! Unknown command.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("dm.mechanic.admin")) {
            if(args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "reload":
                        mp.reload();
                        if(sender instanceof Player) {
                            Chat cp = new Chat((Player) sender);
                            cp.sendSuccessMessage("Config wurde erfolgreich neu geladen.");
                        }
                        Logger.info("config was successfully reloaded.");
                        return true;
                    case "save":
                        if(sender instanceof Player) {
                            Chat cp = new Chat((Player) sender);
                            cp.sendSuccessMessage("Mechanics wurden erfolgreich gespeichert.");
                        }
                        Logger.info("mechanics successfully saved.");
                        return true;
                }
            }
            sender.sendMessage(Chat.PREFIX + "§7Hilfe für Mechanics-Admin");
            sender.sendMessage(Chat.PREFIX + "§f/amech reload §7- Lade die Config neu.");
            sender.sendMessage(Chat.PREFIX + "§f/amech save §7- Speichere alle Mechanics.");
            return true;
        }
        sender.sendMessage(Chat.NO_PERM);
        return false;
    }
}
