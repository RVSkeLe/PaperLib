package io.github.rvskele.paperlib.features.issolid;

import org.bukkit.block.Block;

public class IsSolidMaterial implements IsSolid {
    @Override
    public boolean isSolid(Block block) {
        return block.getType().isSolid();
    }
}
