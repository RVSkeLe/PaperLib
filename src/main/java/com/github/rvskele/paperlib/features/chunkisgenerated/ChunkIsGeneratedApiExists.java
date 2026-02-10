package com.github.rvskele.paperlib.features.chunkisgenerated;

import org.bukkit.World;

public class ChunkIsGeneratedApiExists implements ChunkIsGenerated {
    @Override
    public boolean isChunkGenerated(World world, int x, int z) {
        return world.isChunkGenerated(x, z);
    }
}
