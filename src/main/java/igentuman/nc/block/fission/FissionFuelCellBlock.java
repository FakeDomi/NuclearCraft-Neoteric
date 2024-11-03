package igentuman.nc.block.fission;

import igentuman.nc.multiblock.MultiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FissionFuelCellBlock extends FissionBlock {

    public FissionFuelCellBlock(Properties pProperties) {
        super(pProperties.sound(SoundType.METAL));
    }

    @Override
    @Deprecated
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentBlockState, @NotNull Direction side) {
        return true;
    }
}
