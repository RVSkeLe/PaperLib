package com.github.rvskele.paperlib.environments;

import com.github.rvskele.paperlib.features.asyncchunks.AsyncChunksPaper_13;
import com.github.rvskele.paperlib.features.asyncchunks.AsyncChunksPaper_15;
import com.github.rvskele.paperlib.features.asyncchunks.AsyncChunksPaper_9_12;
import com.github.rvskele.paperlib.features.asyncteleport.AsyncTeleportPaper;
import com.github.rvskele.paperlib.features.asyncteleport.AsyncTeleportPaper_13;
import com.github.rvskele.paperlib.features.bedspawnlocation.BedSpawnLocationPaper;
import com.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotOptionalSnapshots;
import com.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import com.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotOptionalSnapshots;
import com.github.rvskele.paperlib.features.tileentitiessnapshot.TileEntitiesSnapshotOptionalSnapshots;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;

public class PaperEnvironment extends SpigotEnvironment {

    public PaperEnvironment() {
        super();

        if (isVersion(13, 1)) {
            asyncChunksHandler = new AsyncChunksPaper_13();
            asyncTeleportHandler = new AsyncTeleportPaper_13();
        } else if (isVersion(9) && !isVersion(13)) {
            asyncChunksHandler = new AsyncChunksPaper_9_12();
            asyncTeleportHandler = new AsyncTeleportPaper();
        }
        if (isVersion(12)) {
            // Paper added this API in 1.12 with same signature spigot did in 1.13
            isGeneratedHandler = new ChunkIsGeneratedApiExists();
            blockStateSnapshotHandler = new BlockStateSnapshotOptionalSnapshots();
        }

        if (isVersion(13)) {
            tileEntitiesSnapshotHandler = new TileEntitiesSnapshotOptionalSnapshots();
        }

        if (isVersion(15, 2)) {
            try {
                // Try for new Urgent API in 1.15.2+, Teleport will automatically benefit from this
                World.class.getDeclaredMethod("getChunkAtAsyncUrgently", Location.class);
                asyncChunksHandler = new AsyncChunksPaper_15();
                HumanEntity.class.getDeclaredMethod("getPotentialBedLocation");
                bedSpawnLocationHandler = new BedSpawnLocationPaper();
            } catch (NoSuchMethodException ignored) {}
        }
        if (isVersion(16)) {
            inventoryHolderSnapshotHandler = new InventoryHolderSnapshotOptionalSnapshots();
        }
    }

    @Override
    public String getName() {
        return "Paper";
    }

    @Override
    public boolean isPaper() {
        return true;
    }

}
