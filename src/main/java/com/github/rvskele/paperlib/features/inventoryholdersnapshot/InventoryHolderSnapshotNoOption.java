package com.github.rvskele.paperlib.features.inventoryholdersnapshot;

import org.bukkit.inventory.Inventory;

public class InventoryHolderSnapshotNoOption implements InventoryHolderSnapshot {
    @Override
    public InventoryHolderSnapshotResult getHolder(Inventory inventory, boolean useSnapshot) {
        return new InventoryHolderSnapshotResult(true, inventory.getHolder());
    }
}
