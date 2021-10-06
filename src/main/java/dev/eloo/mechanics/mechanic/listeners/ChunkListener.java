package dev.eloo.mechanics.mechanic.listeners;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;


public class ChunkListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        if(!event.isNewChunk()) {
            List<Dropper> chunkDropper = getAllDropper(event.getChunk());
            if(!chunkDropper.isEmpty()) {
                for(Dropper d : chunkDropper) {

                }
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkLoad(ChunkUnloadEvent event) {

    }

    private List<Dropper> getAllDropper(Chunk chunk) {
        List<Dropper> dropperList = new ArrayList<>();
        final int minX = chunk.getX() << 4;
        final int minZ = chunk.getZ() << 4;
        final int maxX = minX | 15;
        final int maxY = chunk.getWorld().getMaxHeight();
        final int maxZ = minZ | 15;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = 0; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = chunk.getBlock(x,y,z);
                    if(block instanceof Dropper) {
                        dropperList.add((Dropper) block.getState());
                    }
                }
            }
        }
        return dropperList;
    }
}
