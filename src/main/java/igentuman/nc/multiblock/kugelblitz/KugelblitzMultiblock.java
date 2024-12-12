package igentuman.nc.multiblock.kugelblitz;

import igentuman.nc.multiblock.AbstractNCMultiblock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class KugelblitzMultiblock extends AbstractNCMultiblock {

    protected KugelblitzMultiblock(List<Block> validOuterBlocks, List<Block> validInnerBlocks) {
        super(validOuterBlocks, validInnerBlocks);
    }

    @Override
    protected void invalidateStats() {

    }

    @Override
    protected Direction getFacing() {
        return null;
    }
}
