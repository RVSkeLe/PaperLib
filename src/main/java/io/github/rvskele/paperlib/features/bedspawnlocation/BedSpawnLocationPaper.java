package io.github.rvskele.paperlib.features.bedspawnlocation;

import io.github.rvskele.paperlib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class BedSpawnLocationPaper implements BedSpawnLocation {

    @Override
    public CompletableFuture<Location> getBedSpawnLocationAsync(Player player, boolean isUrgent) {
        Location bedLocation = player.getPotentialBedLocation();
        if (bedLocation == null || bedLocation.getWorld() == null) {
            return CompletableFuture.completedFuture(null);
        }
        return PaperLib.getChunkAtAsync(bedLocation.getWorld(), bedLocation.getBlockX() >> 4, bedLocation.getBlockZ() >> 4, false, isUrgent).thenCompose(chunk -> CompletableFuture.completedFuture(player.getBedSpawnLocation()));
    }
}
