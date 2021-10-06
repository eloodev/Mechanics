package dev.eloo.mechanics.core.hologram;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import dev.eloo.mechanics.Mechanics;
import dev.eloo.mechanics.utils.Chat;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Holograms {

    private static final Mechanics plugin = Mechanics.getMechanics();

    private final String text;

    private final Hologram hologram;

    private final VisibilityManager manager;

    public Holograms(Location location, String text) {
        this.text = text;
        this.hologram = HologramsAPI.createHologram(plugin, location.clone().add(0.5D, 1.7D, 0.5D));
        this.manager = this.hologram.getVisibilityManager();
    }

    public void build() {
        setVisibilityAllPlayer();
        if(!plugin.holoList.isEmpty()) {
            for(Player p : plugin.holoList) {
                setVisiblePlayer(p);
            }
        }
        hologram.appendTextLine(Chat.format("#E7AF3A" + text));
    }

    public void setVisibilityAllPlayer() {
        this.manager.setVisibleByDefault(false);
    }

    public void setVisiblePlayer(Player player) {
        this.manager.showTo(player);
        setVisibilityAllPlayer();
    }

    public void setInvisiblePlayer(Player player) {
        this.manager.hideTo(player);
        setVisibilityAllPlayer();
    }

    public void delete() {
        this.hologram.delete();
    }
}
