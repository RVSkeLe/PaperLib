package io.github.rvskele.paperlib.environments;

import io.github.rvskele.paperlib.features.asyncchunks.AsyncChunks;
import io.github.rvskele.paperlib.features.asyncchunks.AsyncChunksSync;
import io.github.rvskele.paperlib.features.asyncteleport.AsyncTeleport;
import io.github.rvskele.paperlib.features.asyncteleport.AsyncTeleportSync;
import io.github.rvskele.paperlib.features.bedspawnlocation.BedSpawnLocation;
import io.github.rvskele.paperlib.features.bedspawnlocation.BedSpawnLocationSync;
import io.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import io.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotBeforeSnapshots;
import io.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotNoOption;
import io.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import io.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import io.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import io.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGeneratedUnknown;
import io.github.rvskele.paperlib.features.issolid.IsSolid;
import io.github.rvskele.paperlib.features.issolid.IsSolidMaterial;
import java.util.Locale;

import io.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import io.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotBeforeSnapshots;
import io.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotNoOption;
import io.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
import io.github.rvskele.paperlib.features.tileentitiessnapshot.TileEntitiesSnapshot;
import io.github.rvskele.paperlib.features.tileentitiessnapshot.TileEntitiesSnapshotBeforeSnapshots;
import io.github.rvskele.paperlib.features.tileentitiessnapshot.TileEntitiesSnapshotNoOption;
import io.github.rvskele.paperlib.features.tileentitiessnapshot.TileEntitiesSnapshotResult;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;

import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public abstract class Environment {

    private final int minecraftVersion;
    private final int minecraftPatchVersion;
    private final int minecraftPreReleaseVersion;
    private final int minecraftReleaseCandidateVersion;

    protected AsyncChunks asyncChunksHandler = new AsyncChunksSync();
    protected AsyncTeleport asyncTeleportHandler = new AsyncTeleportSync();
    protected ChunkIsGenerated isGeneratedHandler = new ChunkIsGeneratedUnknown();
    protected BlockStateSnapshot blockStateSnapshotHandler;
    protected InventoryHolderSnapshot inventoryHolderSnapshotHandler;
    protected TileEntitiesSnapshot tileEntitiesSnapshotHandler;
    protected BedSpawnLocation bedSpawnLocationHandler = new BedSpawnLocationSync();
    protected IsSolid isSolidHandler = new IsSolidMaterial();

    public Environment() {
        this(Bukkit.getVersion());
    }

    Environment(final String bukkitVersion) {
        Pattern versionPattern = Pattern.compile("(?i)\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?(?: (Pre-Release|Release Candidate) )?(\\d)?\\)");
        Matcher matcher = versionPattern.matcher(bukkitVersion);
        int version = 0;
        int patchVersion = 0;
        int preReleaseVersion = -1;
        int releaseCandidateVersion = -1;
        if (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            try {
                version = Integer.parseInt(matchResult.group(2), 10);
            } catch (Exception ignored) {
            }
            if (matchResult.groupCount() >= 3) {
                try {
                    patchVersion = Integer.parseInt(matchResult.group(3), 10);
                } catch (Exception ignored) {
                }
            }
            if (matchResult.groupCount() >= 5) {
                try {
                    final int ver = Integer.parseInt(matcher.group(5));
                    if (matcher.group(4).toLowerCase(Locale.ENGLISH).contains("pre")) {
                        preReleaseVersion = ver;
                    } else {
                        releaseCandidateVersion = ver;
                    }
                } catch (Exception ignored) {
                }
            }
        }
        this.minecraftVersion = version;
        this.minecraftPatchVersion = patchVersion;
        this.minecraftPreReleaseVersion = preReleaseVersion;
        this.minecraftReleaseCandidateVersion = releaseCandidateVersion;

        // Common to all environments
        if (isVersion(13, 1)) {
            isGeneratedHandler = new ChunkIsGeneratedApiExists();
        } else {
            // TODO: Reflection based?
        }
        if (!isVersion(12)) {
            blockStateSnapshotHandler = new BlockStateSnapshotBeforeSnapshots();
            inventoryHolderSnapshotHandler = new InventoryHolderSnapshotBeforeSnapshots();
        } else {
            blockStateSnapshotHandler = new BlockStateSnapshotNoOption();
            inventoryHolderSnapshotHandler = new InventoryHolderSnapshotNoOption();
        }

        // https://jd.papermc.io/paper/1.13.0/org/bukkit/Chunk.html#getTileEntities(boolean)
        if(!isVersion(13)) {
            tileEntitiesSnapshotHandler = new TileEntitiesSnapshotBeforeSnapshots();
        } else {
            tileEntitiesSnapshotHandler = new TileEntitiesSnapshotNoOption();
        }
    }

    public abstract String getName();

    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen) {
        return asyncChunksHandler.getChunkAtAsync(world, x, z, gen, false);
    }

    public CompletableFuture<Chunk> getChunkAtAsync(World world, int x, int z, boolean gen, boolean isUrgent) {
        return asyncChunksHandler.getChunkAtAsync(world, x, z, gen, isUrgent);
    }

    public CompletableFuture<Chunk> getChunkAtAsyncUrgently(World world, int x, int z, boolean gen) {
        return asyncChunksHandler.getChunkAtAsync(world, x, z, gen, true);
    }

    public CompletableFuture<Boolean> teleport(Entity entity, Location location, TeleportCause cause) {
        return asyncTeleportHandler.teleportAsync(entity, location, cause);
    }

    public boolean isChunkGenerated(World world, int x, int z) {
        return isGeneratedHandler.isChunkGenerated(world, x, z);
    }

    public BlockStateSnapshotResult getBlockState(Block block, boolean useSnapshot) {
        return blockStateSnapshotHandler.getBlockState(block, useSnapshot);
    }

    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean useSnapshot) {
        return inventoryHolderSnapshotHandler.getHolder(inventory, useSnapshot);
    }

    public TileEntitiesSnapshotResult getTileEntities(Chunk chunk, boolean useSnapshot) {
        return tileEntitiesSnapshotHandler.getTileEntities(chunk, useSnapshot);
    }

    public boolean isSolid(Block block) {
        return isSolidHandler.isSolid(block);
    }

    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
        return bedSpawnLocationHandler.getBedSpawnLocationAsync(player, isUrgent);
    }

    public boolean isVersion(int minor) {
        return isVersion(minor, 0);
    }

    public boolean isVersion(int minor, int patch) {
        return minecraftVersion > minor || (minecraftVersion >= minor && minecraftPatchVersion >= patch);
    }

    public int getMinecraftVersion() {
        return minecraftVersion;
    }

    public int getMinecraftPatchVersion() {
        return minecraftPatchVersion;
    }

    public int getMinecraftPreReleaseVersion() {
        return minecraftPreReleaseVersion;
    }

    public int getMinecraftReleaseCandidateVersion() {
        return minecraftReleaseCandidateVersion;
    }

    public boolean isSpigot() {
        return false;
    }

    public boolean isPaper() {
        return false;
    }
}
