package dev.eloo.mechanics.core.chat;

import dev.eloo.mechanics.Mechanics;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {

    private final static Mechanics mp = Mechanics.getMechanics();

    private final static Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private final FileConfiguration messageConf;
    private final String PREFIX;
    private final String WARN_PREFIX;
    private final String ERROR_PREFIX;

    public Messages(File file) {
        try {
            this.messageConf = YamlConfiguration.loadConfiguration(file);
            this.PREFIX = this.messageConf.getString("PREFIX_DEFAULT");
            this.WARN_PREFIX = this.messageConf.getString("PREFIX_WARN");
            this.ERROR_PREFIX = this.messageConf.getString("PREFIX_ERROR");
        } catch (IllegalArgumentException e) {
            throw new NullPointerException("Unable to load messages because the given file is null!");
        }
    }

    public Double getVersion() {
        String v = this.messageConf.getString("version");
        if (v != null) {
            try {
                return Double.valueOf(v);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void updateFile(InputStream stream) {
        if(stream != null) {
            File temp = new File(mp.getDataFolder(), "temp.yml");
            copyFile(stream, temp);
            try {
                Messages updated = new Messages(temp);
                if(temp.delete()) {
                    if(updated.getVersion() > getVersion()) {
                        mp.saveResource("messages.yml", true);
                    }
                } else {
                    throw new Exception("Unable to delete the temporary created file. Please check the file permissions and try again.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String get(Prefix type, String name) {
        String message = messageConf.getString(name.toUpperCase());
        if(message != null) {
            switch (type) {
                case WARN:
                    return format(WARN_PREFIX+message);
                case ERROR:
                    return format(ERROR_PREFIX+message);
                case DEFAULT:
                default:
                    return format(PREFIX+message);
            }
        }
        throw new RuntimeException("The requested message is not defined!");
    }

    private String format(String message) {
        Matcher match = pattern.matcher(message);
        while(match.find()) {
            String color = message.substring(match.start(), match.end());
            message = message.replace(color, ChatColor.valueOf(color) + "");
            match = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private boolean copyFile(InputStream inputStream, File temp) {
        try (FileOutputStream outputStream = new FileOutputStream(temp, false)){
            int read;
            byte[] bytes = new byte[2048];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
