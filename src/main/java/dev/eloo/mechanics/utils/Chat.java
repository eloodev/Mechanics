package dev.eloo.mechanics.utils;

import dev.eloo.mechanics.Mechanics;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {

    public static final String error = "#CBEF16";//"#FF5244";
    private final static Mechanics mp = Mechanics.getMechanics();
    private final static Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private final Player player;

    public Chat(Player player) {
        this.player = player;
    }

    public static String PREFIX = format("#4C93FF&l» ");
    public static String NO_PERM = PREFIX + format("§cDu hast keine Berechtigung für diesen Befehl.");

    public static String format(String msg) {
        Matcher match = pattern.matcher(msg);
        while (match.find()) {
            String color = msg.substring(match.start(), match.end());
            msg = msg.replace(color, ChatColor.of(color) + "");
            match = pattern.matcher(msg);
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void sendMessage(String message) {
        player.sendMessage (
                format(Chat.PREFIX + "#DADADA" + message)
        );
    }

    public void sendSuccessMessage(String message) {
        player.sendMessage (
                format(Chat.PREFIX + "#72CA5E" + message)
        );
    }

    public void sendWarningMessage(String message) {
        player.sendMessage (
                format(Chat.PREFIX + "#E7AF3A" + message)
        );
    }
    public void sendErrorMessage(String message) {
        player.sendMessage (
                format(Chat.PREFIX + "#C70039" + message)
        );
    }

}
