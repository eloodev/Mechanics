package dev.eloo.mechanics.commands;

import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.core.errors.System;
import dev.eloo.mechanics.core.errors.Ticket;
import dev.eloo.mechanics.utils.Chat;
import dev.eloo.mechanics.utils.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class ErrorSystemCMD implements CommandExecutor {

    private static final Mechanics mp = Mechanics.getMechanics();
    private static final System er = mp.getSystem();
    private static final PluginCommand cmd = mp.getCommand("es");

    public ErrorSystemCMD() {
        if(cmd != null) {
            cmd.setExecutor(this);
        } else {
            Logger.warn("Command: amechanic was not loaded! Unknown command.");
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            Chat cp = new Chat(p);
            if(p.hasPermission("dm.mechanics.errorsystem")) {
                if(strings.length == 1) {
                    Ticket ticket = er.getError(strings[0]);
                    if(ticket != null) {
                        cp.sendMessage(" ");
                        cp.sendMessage("Fehlerbericht zur ID: " + ticket.getId());
                        cp.sendMessage("Spieler: " + ticket.getPlayer());
                        cp.sendMessage("Server: " + ticket.getServer());
                        cp.sendMessage("World: " + ticket.getWorld());
                        cp.sendMessage("Location: " + ticket.getLocation());
                        cp.sendMessage("Active: " + ticket.isActive());
                        cp.sendMessage("Type: " + ticket.getType());
                        cp.sendMessage("Nachricht: " + ticket.getMessage());
                        cp.sendMessage(" ");
                        return true;
                    } else {
                        cp.sendWarningMessage("Unter dieser ID ist kein Error eingetragen!");
                        return false;
                    }
                } else if(strings.length == 2) {

                } else {
                    cp.sendWarningMessage("Nutze: /es <id> - Fehler laden.");
                    cp.sendWarningMessage("Nutze: /es <id> delete - Fehler löschen.");
                    return false;
                }
            }
            cp.sendErrorMessage("Du hast keine Berechtigung für diesen Befehl.");
            return false;
        }
        sender.sendMessage("Command nur fuer ingame nicht fuer console!");
        return false;
    }
}
