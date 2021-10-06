package dev.eloo.mechanics.mechanic.objects;

import dev.eloo.mechanics.mechanic.Mechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Placer extends Mechanic {

	private static final ArrayList<Material> MINERAL_ORES = new ArrayList<>();
	private static final ArrayList<Material> CROPS = new ArrayList<>();
	private static final ArrayList<Material> SHULKER = new ArrayList<>();
	private static final ArrayList<Material> BONE = new ArrayList<>();

	static {
		CROPS.add(Material.BEETROOT_SEEDS);
		CROPS.add(Material.MELON_SEEDS);
		CROPS.add(Material.PUMPKIN_SEEDS);
		CROPS.add(Material.CARROT);
		CROPS.add(Material.POTATO);
		CROPS.add(Material.WHEAT_SEEDS);
		MINERAL_ORES.add(Material.DIAMOND_ORE);
        MINERAL_ORES.add(Material.REDSTONE_ORE);
        MINERAL_ORES.add(Material.LAPIS_ORE);
        MINERAL_ORES.add(Material.EMERALD_ORE);
        MINERAL_ORES.add(Material.COAL_ORE);
        MINERAL_ORES.add(Material.NETHER_QUARTZ_ORE);
        SHULKER.add(Material.SHULKER_BOX);
		SHULKER.add(Material.WHITE_SHULKER_BOX);
		SHULKER.add(Material.ORANGE_SHULKER_BOX);
		SHULKER.add(Material.MAGENTA_SHULKER_BOX);
		SHULKER.add(Material.LIGHT_BLUE_SHULKER_BOX);
		SHULKER.add(Material.YELLOW_SHULKER_BOX);
		SHULKER.add(Material.LIME_SHULKER_BOX);
		SHULKER.add(Material.PINK_SHULKER_BOX);
		SHULKER.add(Material.GRAY_SHULKER_BOX);
		SHULKER.add(Material.LIGHT_GRAY_SHULKER_BOX);
		SHULKER.add(Material.CYAN_SHULKER_BOX);
		SHULKER.add(Material.PURPLE_SHULKER_BOX);
		SHULKER.add(Material.BROWN_SHULKER_BOX);
		SHULKER.add(Material.GREEN_SHULKER_BOX);
		SHULKER.add(Material.RED_SHULKER_BOX);
		SHULKER.add(Material.BLUE_SHULKER_BOX);
		BONE.add(Material.WHEAT);
	}

	public Placer(Location location, UUID owner) {
		super(location, owner);
	}

	public boolean equals(Object obj) {
		if(obj instanceof Placer) {
			Placer other = (Placer) obj;
			return other.getOwner().equals(getOwner()) && other.getLocation().equals(getLocation());
		}
		return false;
	}

	public String toString() {
		return "{Block placer, Location: +" + location.toString() + ", Owner: " + Bukkit.getOfflinePlayer(owner).getName() + "}";
	}

	@Override
	public void run(BlockDispenseEvent events) {
		if(events != null) {
			events.setCancelled(true);
			final ItemStack dis = events.getItem();
			final Dropper d = (Dropper) events.getBlock().getState();
			org.bukkit.block.data.type.Dispenser disp = (org.bukkit.block.data.type.Dispenser) events.getBlock().getBlockData();
			BlockFace face = disp.getFacing();
			final Block b = events.getBlock().getRelative(face);
			List<Block> faceList = new ArrayList<>();
			faceList.add(b.getRelative(BlockFace.UP));
			faceList.add(b.getRelative(BlockFace.DOWN));
			faceList.add(b.getRelative(BlockFace.NORTH));
			faceList.add(b.getRelative(BlockFace.EAST));
			faceList.add(b.getRelative(BlockFace.SOUTH));
			faceList.add(b.getRelative(BlockFace.WEST));

			for(Block block : faceList) {
				if(block.getType().equals(Material.PISTON)){
					return;
				}
			}
			if(!b.getType().equals(Material.AIR)) {
				if(!b.getType().equals(Material.WATER)){
					return;
				}
			}
			if(SHULKER.contains(dis.getType())){
				return;
			}
			boolean place = false;
			Material dispensedType = dis.getType();
			if(dispensedType.isBlock()) {
				if (dispensedType.name().endsWith("CONCRETE_POWDER") && (b.getRelative(BlockFace.EAST).getType() == Material.WATER || b.getRelative(BlockFace.WEST).getType() == Material.WATER || b.getRelative(BlockFace.SOUTH).getType() == Material.WATER || b.getRelative(BlockFace.NORTH).getType() == Material.WATER))
					dispensedType = Material.getMaterial(dispensedType.name().replace("_POWDER", ""));
				place = true;
			} else if(CROPS.contains(dispensedType)) {
				switch(dispensedType) {
					case WHEAT_SEEDS:
						dispensedType = Material.WHEAT;
						break;
					case CARROT:
						dispensedType = Material.CARROTS;
						break;
					case POTATO:
						dispensedType = Material.POTATOES;
						break;
					case BEETROOT_SEEDS:
						dispensedType = Material.BEETROOTS;
						break;
					case PUMPKIN_SEEDS:
						dispensedType = Material.PUMPKIN_STEM;
						break;
					case MELON_SEEDS:
						dispensedType = Material.MELON_STEM;
						break;
				}
				place = true;
			}
			if(place) {
				events.setCancelled(true);
				final Material toPlace = dispensedType;
				/*PlacerEvent placerEvent = new PlacerEvent(b, this);
				Bukkit.getServer().getPluginManager().callEvent(placerEvent);*/
				(new BukkitRunnable() {
					@Override
					public void run() {
						b.setType(toPlace);
						for(ItemStack items : d.getInventory().getContents()) {
							if(items != null) {
								if(items.getType().equals(dis.getType())){
									int amount = items.getAmount();
									if(amount == 1) {
										d.getInventory().remove(items);
										break;
									}
									items.setAmount(amount - 1);
									break;
								}
							}
						}
					}
				}).runTask(dm);
			}
		}
	}

	@Override
	public String getMechanicName() {
		return "BlockPlacer";
	}
}
