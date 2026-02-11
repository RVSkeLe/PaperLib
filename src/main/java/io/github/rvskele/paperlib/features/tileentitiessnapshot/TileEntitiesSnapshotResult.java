package io.github.rvskele.paperlib.features.tileentitiessnapshot;

import org.bukkit.block.BlockState;

public class TileEntitiesSnapshotResult {
    private final boolean isSnapshot;
    private final BlockState[] tileEntities;

    public TileEntitiesSnapshotResult(boolean isSnapshot, BlockState[] tileEntities) {
        this.isSnapshot = isSnapshot;
        this.tileEntities = tileEntities;
    }

    public boolean isSnapshot() {
        return isSnapshot;
    }

    public BlockState[] getTileEntities() {
        return tileEntities;
    }
}
