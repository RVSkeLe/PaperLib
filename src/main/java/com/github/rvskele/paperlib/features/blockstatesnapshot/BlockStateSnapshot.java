package com.github.rvskele.paperlib.features.blockstatesnapshot;

import org.bukkit.block.Block;

public interface BlockStateSnapshot {
    BlockStateSnapshotResult getBlockState(Block block, boolean useSnapshot);
}
