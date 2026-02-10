package com.github.rvskele.paperlib.features.tileentitiessnapshot;

import org.bukkit.Chunk;

public class TileEntitiesSnapshotNoOption implements TileEntitiesSnapshot {
    @Override
    public TileEntitiesSnapshotResult getTileEntities(Chunk chunk, boolean useSnapshot) {
        return new TileEntitiesSnapshotResult(true, chunk.getTileEntities());
    }
}
