package io.github.rvskele.paperlib.features.tileentitiessnapshot;

import org.bukkit.Chunk;

public class TileEntitiesSnapshotOptionalSnapshots implements TileEntitiesSnapshot {
    @Override
    public TileEntitiesSnapshotResult getTileEntities(Chunk chunk, boolean useSnapshot) {
        return new TileEntitiesSnapshotResult(useSnapshot, chunk.getTileEntities(useSnapshot));
    }
}
