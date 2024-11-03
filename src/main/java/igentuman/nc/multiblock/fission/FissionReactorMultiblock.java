package igentuman.nc.multiblock.fission;

import igentuman.nc.block.entity.fission.*;
import igentuman.nc.block.fission.FissionFuelCellBlock;
import igentuman.nc.block.fission.HeatSinkBlock;
import igentuman.nc.block.fission.IrradiationChamberBlock;
import igentuman.nc.multiblock.MultiblockHandler;
import igentuman.nc.multiblock.AbstractNCMultiblock;
import igentuman.nc.util.NCBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.*;

import static igentuman.nc.handler.config.FissionConfig.FISSION_CONFIG;
import static igentuman.nc.util.TagUtil.getBlocksByTagKey;

public class FissionReactorMultiblock extends AbstractNCMultiblock {

    private int irradiationConnections = 0;
    private final List<Block> validModerators;
    public HashMap<BlockPos, HeatSinkBlock> activeHeatSinks = new HashMap<>();
    private List<BlockPos> moderators = new ArrayList<>();
    private List<BlockPos> irradiators = new ArrayList<>();
    public List<BlockPos> heatSinks = new ArrayList<>();
    public List<BlockPos> fuelCells = new ArrayList<>();
    private double heatSinkCooling = 0;
    private FissionControllerBE<?> controllerBe;
    private List<BlockPos> directFuelCellConnectionPos = new ArrayList<>();
    private List<BlockPos> secondFuelCellConnectionPos = new ArrayList<>();

    @Override
    public int maxHeight() {
        return FISSION_CONFIG.MAX_SIZE.get();
    }

    @Override
    public int maxWidth() {
        return FISSION_CONFIG.MAX_SIZE.get();
    }

    @Override
    public int maxDepth() {
        return FISSION_CONFIG.MAX_SIZE.get();
    }

    @Override
    public int minHeight() {
        return FISSION_CONFIG.MIN_SIZE.get();
    }

    @Override
    public int minWidth() {return FISSION_CONFIG.MIN_SIZE.get(); }

    @Override
    public int minDepth() { return FISSION_CONFIG.MIN_SIZE.get(); }

    public FissionReactorMultiblock(FissionControllerBE<?> fissionControllerBE) {
        super(
                getBlocksByTagKey(FissionBlocks.CASING_BLOCKS.location().toString()),
                getBlocksByTagKey(FissionBlocks.INNER_REACTOR_BLOCKS.location().toString())
        );
        id = "fission_reactor_"+fissionControllerBE.getBlockPos().toShortString();
        validModerators = getBlocksByTagKey(FissionBlocks.MODERATORS_BLOCKS.location().toString());
        MultiblockHandler.addMultiblock(this);
        controller = new FissionReactorController(fissionControllerBE);
        controllerBe = fissionControllerBE;
    }

    public Map<BlockPos, HeatSinkBlock> validHeatSinks() {
        if(activeHeatSinks.isEmpty()) {
            for(BlockPos hpos: heatSinks) {
                Block block = getBlockState(hpos).getBlock();
                if(block instanceof HeatSinkBlock hs) {
                    if(hs.isValid(getLevel(), hpos)) {
                        activeHeatSinks.put(hpos, hs);
                    }
                }
            }
        }
        controllerBE().heatSinksCount = activeHeatSinks.size();
        return activeHeatSinks;
    }

    public boolean isModerator(BlockPos pos, Level level) {
        return  validModerators.contains(level.getBlockState(pos).getBlock());
    }

    public boolean isModerator(BlockPos pos) {
        return  validModerators.contains(getBlockState(pos).getBlock());
    }

    public boolean isIrradiator(BlockPos pos) {
        return  getBlockState(pos).getBlock() instanceof IrradiationChamberBlock;
    }

    protected boolean isHeatSink(BlockPos pos) {
        return getBlockState(pos).getBlock() instanceof HeatSinkBlock;
    }

    protected boolean isFuelCell(BlockPos pos) {
        return getBlockState(pos).getBlock() instanceof FissionFuelCellBlock;
    }

    private boolean isAttachedToFuelCell(BlockPos toCheck) {
        if (directFuelCellConnectionPos.contains(toCheck)) {
            return true;
        }
        for(Direction d : Direction.values()) {
            if(isFuelCell(toCheck.relative(d))) {
                addDirectFuelCellConnection((toCheck.relative(d)));
                return true;
            }
        }
        return false;
    }

    public void validateInner()
    {
        super.validateInner();
        heatSinkCooling = countCooling(true);
        controllerBE().fuelCellsCount = fuelCells.size();
        controllerBE().moderatorsCount = moderators.size();
        controllerBE().irradiationConnections = irradiationConnections;
    }

    private FissionControllerBE<?> controllerBE() {
        if (controllerBe == null) {
            controllerBe = (FissionControllerBE<?>) controller().controllerBE();
        }
        return controllerBe;
    }

    @Override
    protected boolean processInnerBlock(BlockPos toCheck) {
        if(isFuelCell(toCheck)) {
            if(toCheck instanceof NCBlockPos) {
                toCheck = new BlockPos(toCheck);
            }
            fuelCells.add(toCheck);
            addDirectFuelCellConnection(toCheck);
            int moderatorAttachments = countAttachedModeratorsToFuelCell(toCheck);
            controllerBE().fuelCellMultiplier += countAdjacentFuelCells(NCBlockPos.of(toCheck), 3);
            controllerBE().moderatorCellMultiplier += (countAdjacentFuelCells(NCBlockPos.of(toCheck), 1)+1)*moderatorAttachments;
            controllerBE().moderatorAttachments += moderatorAttachments;
        }
        if(isModerator(toCheck)) {
            if(isAttachedToFuelCell(toCheck)) {
                moderators.add(toCheck);
            }
        }
        if(isHeatSink(toCheck)) {
            heatSinks.add(toCheck);
        }
        if(isIrradiator(toCheck)) {
            irradiators.add(toCheck);
        }
        return true;
    }

    private void addDirectFuelCellConnection(BlockPos toCheck) {
        directFuelCellConnectionPos.add(toCheck.relative(Direction.UP));
        directFuelCellConnectionPos.add(toCheck.relative(Direction.DOWN));
        directFuelCellConnectionPos.add(toCheck.relative(Direction.NORTH));
        directFuelCellConnectionPos.add(toCheck.relative(Direction.SOUTH));
        directFuelCellConnectionPos.add(toCheck.relative(Direction.WEST));
        directFuelCellConnectionPos.add(toCheck.relative(Direction.EAST));
    }

    private int countAttachedModeratorsToFuelCell(BlockPos toCheck) {
        int count = 0;
        for(Direction d : Direction.values()) {
            if(isModerator(toCheck.relative(d))) {
                count++;
            }
        }
        return count;
    }

    private void countIrradiationConnections(BlockPos toCheck) {
        for(Direction d: Direction.values()) {
            if(isModerator(toCheck.relative(d))) {
                Block bs = getBlockState(toCheck.relative(d, 2)).getBlock();
                if(bs instanceof FissionFuelCellBlock) {
                    irradiationConnections++;
                }
            }
        }
    }

    private int countAdjacentFuelCells(NCBlockPos toCheck, int step) {
        int count = 0;
        for (Direction d : Direction.values()) {
            if (isFuelCell(toCheck.revert().relative(d))) {
                count+=step;
                continue;
            }
            if(isModerator(toCheck.revert().relative(d))) {
                if(isFuelCell(toCheck.revert().relative(d, 2))) {
                    count += step;
                }
            }
        }
        return count;
    }

    public void invalidateStats()
    {
        controller().clearStats();
        moderators.clear();
        irradiators.clear();
        fuelCells.clear();
        heatSinks.clear();
        activeHeatSinks.clear();
        directFuelCellConnectionPos.clear();
        secondFuelCellConnectionPos.clear();
        irradiationConnections = 0;
    }

    protected Direction getFacing() {
        return controllerBE().getFacing();
    }

    public double countCooling(boolean forceCheck) {
        if(refreshInnerCacheFlag || forceCheck) {
            heatSinkCooling = 0;
            for (HeatSinkBlock hs : validHeatSinks().values()) {
                heatSinkCooling += hs.heat;
            }
        }
        return heatSinkCooling;
    }

}
