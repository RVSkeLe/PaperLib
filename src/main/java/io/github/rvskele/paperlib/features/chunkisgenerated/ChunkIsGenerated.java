package io.github.rvskele.paperlib.features.chunkisgenerated;

import org.bukkit.World;

public interface ChunkIsGenerated {
    boolean isChunkGenerated(World world, int x, int z);
}
