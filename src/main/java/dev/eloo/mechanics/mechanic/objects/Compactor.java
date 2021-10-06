package dev.eloo.mechanics.mechanic.objects;

import dev.eloo.mechanics.mechanic.Mechanic;
import dev.eloo.mechanics.utils.wrapper.DoubleWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Compactor extends Mechanic {

	public static Map<Material, DoubleWrapper<Integer, Material>> itemsToCompact;

	public Compactor(Location location, UUID owner){
		super(location, owner);
	}

	public boolean equals(Object obj){
		if(obj instanceof Compactor){
			Compactor other = (Compactor) obj;
			return (other.getOwner().equals(getOwner()) && other.getLocation().equals(getLocation()));
		}
		return false;
	}

	public String toString() {
		return "{Block placer, Location: +" + this.location.toString() + ", Owner: " + Bukkit.getOfflinePlayer(this.owner).getName() + "}";
	}

	@Override
	public void run(BlockDispenseEvent events) {
		if(events != null) {
			Block block = events.getBlock();
			events.setCancelled(true);
			Bukkit.getScheduler().runTaskLater(dm, () -> {
				final Inventory inv = ((Dropper) block.getState()).getInventory();
				List<ItemStack> invItems = Arrays.asList(inv.getContents());
				ItemStack[] items = condense(invItems).toArray(new ItemStack[0]);

				for (int i = 0; i<items.length;i++){
					if (items[i] == null) {
						continue;
					}
					DoubleWrapper<Integer, Material> wrapper = itemsToCompact.get(items[i].getType());
					if (wrapper == null) {
						continue;
					}
					int currentIndex = i;
					int totalAmount = items[i].getAmount();
					for (int j = i+1; j<items.length && items[i].getType() == items[j].getType() ;j++) {
						totalAmount += items[j].getAmount();
						i++;
					}
					if (totalAmount / wrapper.getValue1() > 0) {
						if (totalAmount % wrapper.getValue1() > 0 && i == currentIndex) {
							if (inv.firstEmpty() != -1 && items.length < inv.getSize()) {
								items = Arrays.copyOf(items, items.length+1);
								ItemStack compressedItem = new ItemStack(wrapper.getValue2());
								compressedItem.setAmount(totalAmount / wrapper.getValue1());
								items[items.length-1] = compressedItem;
								items[currentIndex].setAmount(totalAmount % wrapper.getValue1());
							}
							continue;
						}
						ItemStack compressedItem = new ItemStack(wrapper.getValue2());
						compressedItem.setAmount(totalAmount / wrapper.getValue1());
						for (int j = currentIndex+1 ; j<i+1;j++) {
							items[j] = null;
						}
						if (totalAmount % wrapper.getValue1() > 0) {
							items[currentIndex].setAmount(totalAmount % wrapper.getValue1());
							items[currentIndex+1] = compressedItem;
						} else {
							items[currentIndex] = compressedItem;
						}
					}
				}
				inv.setContents(items);
			}, 2);
		}
	}

	@Override
	public String getMechanicName() {
		return "Compactor";
	}

	private List<ItemStack> condense(List<ItemStack> items) {
		List<ItemStack> condensedItems = new ArrayList<ItemStack>();
		List<ItemStack> unchecked = new ArrayList<ItemStack>();
		for (ItemStack item:items) {
			if (item == null || item.getAmount() == 0 || item.getType() == Material.AIR) {
				continue;
			} else if (item.getAmount() == item.getMaxStackSize()) {
				condensedItems.add(item);
			} else {
				unchecked.add(item);
			}
		}
		while(!unchecked.isEmpty()) {
			ItemStack toTest = unchecked.get(0);
			for (ItemStack item:new ArrayList<>(unchecked.subList(1, unchecked.size()))) {
				if (toTest.getType() == item.getType()) {
					if (toTest.getAmount() + item.getAmount() > toTest.getMaxStackSize()) {
						toTest.setAmount((item.getAmount()+toTest.getAmount()) - toTest.getMaxStackSize());
						condensedItems.add(item);
						item.setAmount(item.getMaxStackSize());
						unchecked.remove(item);
					} else {
						toTest.setAmount(toTest.getAmount() + item.getAmount());
						unchecked.remove(item);
					}
				}
			}
			unchecked.remove(0);
			if (toTest.getType() != Material.AIR) {
				condensedItems.add(toTest);
			}
		}
		condensedItems.sort((a,b) -> a.getType().name().compareTo(b.getType().name()));
		return condensedItems;
	}
}
