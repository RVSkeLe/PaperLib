package io.github.rvskele.paperlib.features.tileentitiessnapshot;

import org.bukkit.Chunk;

/**
 * Block State Snapshots were added in 1.13, this will always be no snapshots.
 */
public class TileEntitiesSnapshotBeforeSnapshots implements TileEntitiesSnapshot {
    @Override
    public TileEntitiesSnapshotResult getTileEntities(Chunk chunk, boolean useSnapshot) {
        return new TileEntitiesSnapshotResult(false, chunk.getTileEntities());
    }
}
