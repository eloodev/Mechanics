package dev.eloo.mechanics.core;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Settings {

    public String SERVER_NAME;
    public Boolean DEBUG_MODE;
    public Material ITEM_TOOL;
    public Double MECHANIC_PRICE;
    public Boolean MECHANIC_TOOL_DURABILITY;
    public Boolean HOLOGRAMS_ENABLED;
    public Boolean HOLOGRAMS_DEFAULT_SHOWED;

    public String HOSTNAME;
    public String PORT;
    public String USERNAME;
    public String PASSWORD;
    public String DATABASE;

    public List<String> MYSQL_DATA;
    public List<String> COMPACTOR_ITEMS;
    public List<String> ITEM_LORE;

    private final FileConfiguration config;

    public Settings(FileConfiguration config) {
        this.config = config;
    }

}
