package com.github.rvskele.paperlib.environments;

import com.github.rvskele.paperlib.features.asyncchunks.AsyncChunks;
import com.github.rvskele.paperlib.features.asyncchunks.AsyncChunksSync;
import com.github.rvskele.paperlib.features.asyncteleport.AsyncTeleport;
import com.github.rvskele.paperlib.features.asyncteleport.AsyncTeleportSync;
import com.github.rvskele.paperlib.features.bedspawnlocation.BedSpawnLocation;
import com.github.rvskele.paperlib.features.bedspawnlocation.BedSpawnLocationSync;
import com.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshot;
import com.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotBeforeSnapshots;
import com.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotNoOption;
import com.github.rvskele.paperlib.features.blockstatesnapshot.BlockStateSnapshotResult;
import com.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGenerated;
import com.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGeneratedApiExists;
import com.github.rvskele.paperlib.features.chunkisgenerated.ChunkIsGeneratedUnknown;
import java.util.Locale;

import com.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshot;
import com.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotBeforeSnapshots;
import com.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotNoOption;
import com.github.rvskele.paperlib.features.inventoryholdersnapshot.InventoryHolderSnapshotResult;
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
    protected BedSpawnLocation bedSpawnLocationHandler = new BedSpawnLocationSync();

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
