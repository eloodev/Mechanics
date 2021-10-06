package dev.eloo.mechanics.core.filesystem;

import dev.eloo.mechanics.Mechanics;
import jdk.jpackage.internal.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class Files extends File {

    private final static Mechanics mp = Mechanics.getMechanics();
    private final static File backupFolder = new File(mp.getDataFolder() + File.separator + "backups");

    public Files(@NotNull String pathname) {
        super(pathname);
    }

    public Files(@NotNull String pathname, @NotNull String child) {
        super(pathname, child);
    }

    public void create() {
        try {
            backupFolder.createNewFile();
            if(!exists()) {
                mp.saveResource("messages.yml", true);
            } else {

            }
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void update(InputStream stream) {

    }

    public File copyFile(InputStream stream) {
        File temp = new File(mp.getDataFolder(), "temp.yml");
        temp.deleteOnExit();
        try (OutputStream outputStream = new FileOutputStream(temp, false)) {
            int read;
            byte[] bytes = new byte[2048];
            while ((read = stream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
