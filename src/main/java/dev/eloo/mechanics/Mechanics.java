package dev.eloo.mechanics;

import dev.eloo.mechanics.commands.ErrorSystemCMD;
import dev.eloo.mechanics.commands.MechanicsCMD;
import dev.eloo.mechanics.core.chat.Messages;
import dev.eloo.mechanics.core.items.ItemManager;
import dev.eloo.mechanics.mechanic.Manager;
import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.mechanic.listeners.InteractListener;
import dev.eloo.mechanics.mechanic.listeners.InventoryListener;
import dev.eloo.mechanics.mechanic.listeners.WorldListener;
import dev.eloo.mechanics.mechanic.tasks.LoadMechanicsTask;
import dev.eloo.mechanics.mechanic.objects.Compactor;
import dev.eloo.mechanics.core.Settings;
import dev.eloo.mechanics.events.listeners.*;
import dev.eloo.mechanics.commands.AdminCMD;
import dev.eloo.mechanics.core.errors.System;
import dev.eloo.mechanics.mechanic.tasks.SaveMechanicsTask;
import dev.eloo.mechanics.tasks.PreparePluginTask;
import dev.eloo.mechanics.tasks.CooldownTask;
import dev.eloo.mechanics.utils.Database;
import dev.eloo.mechanics.utils.Logger;
import dev.eloo.mechanics.utils.wrapper.DoubleWrapper;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public final class Mechanics extends JavaPlugin implements Plugin {

	private static Mechanics mechanics;

	private final Map<Location, Mechanic> mechanicMap = new HashMap<>();
	private final List<Player> hologramList = new ArrayList<>();
	private final Map<Location, Long> actionCooldown = new HashMap<>();

	private final File messageFile = new File(getDataFolder(), "messages.yml");

	private Manager manager;
	private ItemManager itemManager;
	private System system;
	private Database database;
	private Settings settings;
	private Messages messages;
	public String serverName;

	private Economy ecosystem = null;
	private GriefPrevention griefsystem = null;

	public void onLoad() {
		mechanics = this;
	}

	@Override
	public void onEnable() {
		this.settings = prepareConfig();
		this.database = new Database(this.settings);
		this.manager = new Manager();
		this.itemManager = new ItemManager();
		this.messages = new Messages(messageFile);
		initFiles();
		initPlugin();
		this.system = new System();

		serverName = getConfig().getString("setting.servername");

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			setupCompactingItems();
			new PreparePluginTask();
			new LoadMechanicsTask(Bukkit.getWorlds()).runTaskAsynchronously(this);
			new SaveMechanicsTask().runTaskTimerAsynchronously(this, 0L, 20 * 60 * 10);
		});

		BukkitRunnable mechanicSecurity = new CooldownTask();
		mechanicSecurity.runTaskTimerAsynchronously(this, 0, 0);

		BukkitRunnable mechanicSaver = new SaveMechanicsTask();
		mechanicSaver.runTaskTimerAsynchronously(this, 60000L, 60000L);

	}

	@Override
	public void onDisable() {
		new SaveMechanicsTask().shutdownSave();
	}

	public static Mechanics getMechanics() {
		return mechanics;
	}
	public Map<Location, Mechanic> getMechanicsList() {
		return mechanicMap;
	}
	public List<Player> getHologramList() { return hologramList; }
	public Map<Location, Long> getActionCooldown() { return actionCooldown; }
	public Manager getManager() { return manager; }
	public ItemManager getItemManager() { return itemManager; }
	public System getSystem() { return system; }
	public Database getDatabase() {
		return database;
	}
	public Settings getSettings() { return settings; }
	public Messages getMessages() { return messages; }

	public void setGriefSystem(Plugin plugin) {
		this.griefsystem = (GriefPrevention) plugin;
	}
	public GriefPrevention getGriefSystem() { return griefsystem; }
	public void setEcoSystem(Economy economy) {
		this.ecosystem = economy;
	}
	public Economy getEcoSystem() {
		return ecosystem;
	}

	private void setupCompactingItems() {
		Compactor.itemsToCompact = new HashMap<>();
		for (String count : getConfig().getConfigurationSection("compactor").getKeys(false)) {
			int amount;
			try {
				amount = Integer.parseInt(count);
			} catch (Exception e) {
				getLogger().warning(count + " is not a number!");
				continue;
			}
			for (String material : getConfig().getConfigurationSection("compactor."+count).getKeys(false)) {
				Material from = Material.valueOf(material);
				Material to = Material.valueOf(getConfig().getString("compactor."+count + "." + material));

				Compactor.itemsToCompact.put(from, new DoubleWrapper<>(amount, to));
			}
		}
	}
	public void reload() {
		saveDefaultConfig();
		reloadConfig();
	}
	public boolean isMechanicTool(ItemStack item) {
		return item != null && item.getType().equals(settings.ITEM_TOOL);
	}
	public boolean isBlockDropper(Block block) {
		return block != null && block.getState() instanceof Dropper;
	}
	public boolean hasPlayerClaimTrust(Player player, Location location) {
		if(getGriefSystem() != null) {
			return getGriefSystem().allowBuild(player, location) == null;
		}
		return true;
	}
	public void hideAllHolograms(Player player) {
		for(Map.Entry<Location, Mechanic> m : getMechanicsList().entrySet()) {
			m.getValue().getHolograms().setInvisiblePlayer(player);
		}
	}
	public void showAllHolograms(Player player) {
		for(Map.Entry<Location, Mechanic> m : getMechanicsList().entrySet()) {
			m.getValue().getHolograms().setVisiblePlayer(player);
		}
	}

	private void registerCommandsAndListeners() {
		getServer().getPluginManager().registerEvents(new BlockListener(), this);
		getServer().getPluginManager().registerEvents(new InteractListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		new AdminCMD();
		new MechanicsCMD();
		new ErrorSystemCMD();
	}
	private Settings prepareConfig() {
		saveDefaultConfig();
		return new Settings(getConfig());
	}

	private void initFiles() {
		saveDefaultConfig();
	}
	private void initPlugin() {
		Logger.sendMessage("Checking if supported plugins are installed...");

		PluginManager pm = getServer().getPluginManager();

		if(pm.getPlugin("GriefPrevention") instanceof GriefPrevention) {
			Logger.sendMessage("§aGriefPrevention functions enabled.");
			griefsystem = (GriefPrevention) pm.getPlugin("GriefPrevention");
		}

		if(pm.getPlugin("Vault") != null && pm.getPlugin("Vault").isEnabled()) {
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			if(rsp != null) {
				Logger.sendMessage("§aVault functions enabled.");

				ecosystem = rsp.getProvider();
			} else {
				Logger.info("Economy system not found!");
			}
		}
	}
}
