package com.github.rvskele.paperlib.features.inventoryholdersnapshot;

import org.bukkit.inventory.Inventory;

public interface InventoryHolderSnapshot {
    InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean useSnapshot);
}
