package dev.eloo.mechanics.mechanic.objects;

import dev.eloo.mechanics.mechanic.Mechanic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Harvester extends Mechanic {

    protected boolean onlyTrees = true;
    protected boolean popLeaves = true;
    protected int leafRadius = 5;
    private boolean lootSpawned = false;
    private int breaked = 0;

    public Harvester(Location location, UUID owner) {
        super(location, owner);
    }

    @Override
    public String getMechanicName() {
        return "Harvester";
    }

    @Override
    public void run(BlockDispenseEvent event) {
        event.setCancelled(true);
        if(event.getItem().getType().name().endsWith("AXE")) {
            Block toHarvest = getTargetBlock(event.getBlock());
            if(isLogBlock(toHarvest.getType())) {
                if(doHarvest(toHarvest, getWorld(), toHarvest.getLocation())) {
                    if(Settings.DamageTools && breaked > 0) {
                        (new BukkitRunnable() {
                            public void run() {
                                ItemStack tool = event.getItem().clone();
                                Damageable d1 = (Damageable) tool.getItemMeta();
                                d1.setDamage(d1.getDamage() + breaked);
                                getDropper().getInventory().removeItem(event.getItem());
                                if(d1.getDamage() < getMaxDamage(tool.getType())) {
                                    tool.setItemMeta((ItemMeta) d1);
                                    getDropper().getInventory().addItem(tool);
                                }
                                breaked = 0;
                            }
                        }).runTask(dm);
                    }
                }
            }
        }
    }

    private boolean isLogBlock(Material material) {
        return material == Material.ACACIA_LOG ||
                material == Material.BIRCH_LOG ||
                material == Material.DARK_OAK_LOG ||
                material == Material.JUNGLE_LOG ||
                material == Material.OAK_LOG ||
                material == Material.SPRUCE_LOG ||
                material == Material.WARPED_STEM ||
                material == Material.CRIMSON_STEM;
    }

    private boolean isLeavesBlock(Material material) {
        return material == Material.OAK_LEAVES ||
                material == Material.ACACIA_LEAVES ||
                material == Material.BIRCH_LEAVES ||
                material == Material.DARK_OAK_LEAVES ||
                material == Material.JUNGLE_LEAVES ||
                material == Material.SPRUCE_LEAVES ||
                material == Material.NETHER_WART_BLOCK ||
                material == Material.WARPED_WART_BLOCK ||
                material == Material.SHROOMLIGHT;
    }

    public boolean doHarvest(Block block, World world, Location location) {
        List<Block> blocks = new LinkedList<>();
        Block highest = getHighestLog(block);
        if (isTree(highest, block)) {
            getBlocksToChop(block, highest, blocks);
            popLogs(blocks, world, location);
        } else {
            return false;
        }
        return true;
    }

    public void getBlocksToChop(Block block, Block highest, List<Block> blocks) {
        while (block.getY() <= highest.getY()) {
            if (!blocks.contains(block))
                blocks.add(block);
            getBranches(block, blocks, block.getRelative(BlockFace.NORTH));
            getBranches(block, blocks, block.getRelative(BlockFace.NORTH_EAST));
            getBranches(block, blocks, block.getRelative(BlockFace.EAST));
            getBranches(block, blocks, block.getRelative(BlockFace.SOUTH_EAST));
            getBranches(block, blocks, block.getRelative(BlockFace.SOUTH));
            getBranches(block, blocks, block.getRelative(BlockFace.SOUTH_WEST));
            getBranches(block, blocks, block.getRelative(BlockFace.WEST));
            getBranches(block, blocks, block.getRelative(BlockFace.NORTH_WEST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST));
            if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST)))
                getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST));
            if (block.getData() == 3 || block.getData() == 7 || block.getData() == 11 || block.getData() == 15) {
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST, 2));
                if (!blocks.contains(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST, 2)))
                    getBranches(block, blocks, block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST, 2));
            }
            if (blocks.contains(block.getRelative(BlockFace.UP)) || !isLogBlock(block.getRelative(BlockFace.UP).getType()))
                break;
            block = block.getRelative(BlockFace.UP);
        }
    }

    public void getBranches(Block block, List<Block> blocks, Block other) {
        if (!blocks.contains(other) && isLogBlock(other.getType()))
            getBlocksToChop(other, getHighestLog(other), blocks);
    }

    public Block getHighestLog(Block block) {
        boolean isLog = true;
        while (isLog) {
            if (isLogBlock(block.getRelative(BlockFace.UP).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST).getType()) ||
                    isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST).getType())) {
                if (isLogBlock(block.getRelative(BlockFace.UP).getType())) {
                    block = block.getRelative(BlockFace.UP);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_EAST);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH_WEST);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST).getType())) {
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_EAST);
                    continue;
                }
                if (isLogBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST).getType()))
                    block = block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH_WEST);
                continue;
            }
            isLog = false;
        }
        return block;
    }

    public boolean isTree(Block block, Block first) {
        if (!this.onlyTrees)
            return true;
        int counter = 0;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.DOWN).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.NORTH).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.EAST).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.SOUTH).getType()))
            counter++;
        if (isLeavesBlock(block.getRelative(BlockFace.WEST).getType()))
            counter++;
        if (counter >= 2)
            return true;
        if (block.getData() == 1) {
            block = block.getRelative(BlockFace.UP);
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.NORTH).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.EAST).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.SOUTH).getType()))
                counter++;
            if (isLeavesBlock(block.getRelative(BlockFace.WEST).getType()))
                counter++;
            if (counter >= 2)
                return true;
        }
        return false;
    }

    public void popLogs(List<Block> blocks, World world, Location location) {
        Location loc = location.add(0.5,0.5,0.5);
        for (Block value : blocks) {
            Material item = value.getType();
            if (this.popLeaves)
                popLeaves(value, world,  loc);
            value.setType(Material.AIR);
            if(value.getType().equals(Material.AIR)) {
                world.dropItem(loc, new ItemStack(item));
                breaked++;
            }
        }
    }

    public void popLeaves(Block block, World world, Location location) {
        int apple = (int) (Math.random() * 3);
        int sapling = (int) (Math.random() * 5);
        int stick = (int) (Math.random() * 7);
        for (int y = -this.leafRadius; y < this.leafRadius + 1; y++) {
            for (int x = -this.leafRadius; x < this.leafRadius + 1; x++) {
                for (int z = -this.leafRadius; z < this.leafRadius + 1; z++) {
                    Block target = block.getRelative(x, y, z);
                    if (isLeavesBlock(target.getType())) {
                        if(!target.getType().equals(Material.NETHER_WART_BLOCK) || !target.getType().equals(Material.WARPED_WART_BLOCK)) {
                            if (!lootSpawned) {
                                if(apple > 0) {
                                    world.dropItem(location, new ItemStack(Material.APPLE, apple));
                                }
                                if(sapling > 0) {
                                    if (target.getType() == Material.OAK_LEAVES) {
                                        world.dropItem(location, new ItemStack(Material.OAK_SAPLING, sapling));
                                    } else if (target.getType() == Material.ACACIA_LEAVES) {
                                        world.dropItem(location, new ItemStack(Material.ACACIA_SAPLING, sapling));
                                    } else if (target.getType() == Material.BIRCH_LEAVES) {
                                        world.dropItem(location, new ItemStack(Material.BIRCH_SAPLING, sapling));
                                    } else if (target.getType() == Material.DARK_OAK_LEAVES) {
                                        world.dropItem(location, new ItemStack(Material.DARK_OAK_SAPLING, sapling));
                                    } else if (target.getType() == Material.JUNGLE_LEAVES) {
                                        world.dropItem(location, new ItemStack(Material.JUNGLE_SAPLING, sapling));
                                    } else if (target.getType() == Material.SPRUCE_LEAVES) {
                                        world.dropItem(location, new ItemStack(Material.SPRUCE_SAPLING, sapling));
                                    }
                                }
                                if(stick > 0) {
                                    world.dropItem(location, new ItemStack(Material.STICK, stick));
                                }
                                lootSpawned = true;
                            }
                        } else if(target.getType().equals(Material.NETHER_WART_BLOCK)) {
                            world.dropItem(location, new ItemStack(Material.NETHER_WART_BLOCK));
                        } else if(target.getType().equals(Material.WARPED_WART_BLOCK)) {
                            world.dropItem(location, new ItemStack(Material.WARPED_WART_BLOCK));
                        } else if(target.getType().equals(Material.SHROOMLIGHT)) {
                            world.dropItem(location, new ItemStack(Material.SHROOMLIGHT));
                        }
                        target.setType(Material.AIR);
                    }
                }
            }
        }
        lootSpawned = false;
    }
}
