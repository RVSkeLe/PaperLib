package com.github.rvskele.paperlib.features.tileentitiessnapshot;

import org.bukkit.Chunk;

public interface TileEntitiesSnapshot {
    TileEntitiesSnapshotResult getTileEntities(Chunk chunk, boolean useSnapshot);
}
