package dev.eloo.mechanics.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class Logger {

    private static final String PREFIX = "[Mechanics] ";

    private static void log(Level level, String msg) {
        Bukkit.getLogger().log(level, PREFIX + msg);
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        log(Level.WARNING, msg);
    }

    public static void error(String msg) {
        log(Level.SEVERE, msg);
    }

    public static void sendMessage(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage("[Mechanics] " + ChatColor.translateAlternateColorCodes('&', msg));
    }

}
