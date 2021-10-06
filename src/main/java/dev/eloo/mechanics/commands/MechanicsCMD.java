package dev.eloo.mechanics.commands;

import dev.eloo.mechanics.utils.Logger;
import dev.eloo.mechanics.utils.Chat;
import dev.eloo.mechanics.utils.text.TextComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MechanicsCMD implements CommandExecutor {

    private static final dev.eloo.mechanics.Mechanics dm = dev.eloo.mechanics.Mechanics.getMechanics();
    private static final PluginCommand cmd = dm.getCommand("mechanics");
    //private static final InventoryMenuManager inv = InventoryMenuManager.getInstance();
    private static final List<String> completer = new ArrayList<>();

    public MechanicsCMD() {
        if(cmd != null) {
            cmd.setExecutor(this);
        } else {
            Logger.warn("Command: mechanic was not loaded! Unknown command.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            Chat cp = new Chat(player);
            if(player.hasPermission("dm.mechanic.create")) {
                if(!(args.length > 0)) {
                    sendHelp(player);
                    return true;
                } else {
                    if(args[0].equalsIgnoreCase("hologram")) {
                        if(args.length == 2) {
                            switch (args[1].toLowerCase()) {
                                case "off":
                                    dm.hideAllHolograms(player);
                                    //dm.holoList.remove(player);
                                    cp.sendSuccessMessage("Mechanic: Holograme deaktiviert");
                                    return true;
                                case "on":
                                    dm.showAllHolograms(player);
                                    ///dm.holoList.add(player);
                                    cp.sendSuccessMessage("Mechanic: Holograme aktiviert");
                                    return true;
                            }
                        }
                        cp.sendWarningMessage("Nutze: /mechanics hologram <on/off>");
                        return false;
                    }
                    cp.sendErrorMessage("Unbekannter Befehl, nutze /mechanics um Hilfe zu erhalten.");
                    return false;
                }
            }
            player.sendMessage(Chat.NO_PERM);
            return false;
        }
        Logger.warn("This command is only for players.");
        return false;
    }

    private void sendHelp(Player player) {
        TextComponent component = new TextComponentBuilder("Klicke hier").addClickEvent(ClickEvent.Action.OPEN_URL, "https://bit.ly/3bLiwZp").build();
        TextComponent text = new TextComponent(Chat.PREFIX + "§eEine genaue Erklärung findest du im Lexikon: ");
        text.addExtra(component);
        player.sendMessage(text.getText());
    }
}
