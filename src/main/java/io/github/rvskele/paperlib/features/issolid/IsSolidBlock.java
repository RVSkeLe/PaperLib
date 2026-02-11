package io.github.rvskele.paperlib.features.issolid;

import org.bukkit.block.Block;

public class IsSolidBlock implements IsSolid {
    @Override
    public boolean isSolid(Block block) {
        return block.isSolid();
    }
}
