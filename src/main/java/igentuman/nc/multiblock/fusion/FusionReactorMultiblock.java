package igentuman.nc.multiblock.fusion;

import igentuman.nc.block.ElectromagnetBlock;
import igentuman.nc.block.RFAmplifierBlock;
import igentuman.nc.block.entity.fusion.FusionCoreBE;
import igentuman.nc.block.fusion.MultiblockConnectorBlock;
import igentuman.nc.multiblock.MultiblockHandler;
import igentuman.nc.multiblock.AbstractNCMultiblock;
import igentuman.nc.multiblock.ValidationResult;
import igentuman.nc.util.NCBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;

import static igentuman.nc.handler.config.FusionConfig.FUSION_CONFIG;
import static igentuman.nc.util.TagUtil.getBlocksByTagKey;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.Blocks.AIR;

public class FusionReactorMultiblock extends AbstractNCMultiblock {
    public int magnetsEfficiency = 0;
    public int rfEfficiency = 0;
    protected FusionCoreBE<?> controllerBE;
    protected int length = 0;
    public double magneticFieldStrength = 0;
    public int magnetsPower = 0;
    public int maxMagnetsTemp = 0;
    //KEV
    public int rfAmplification = 0;
    public int rfAmplifiersPower = 0;
    public int maxRFAmplifiersTemp = 0;
    protected boolean connectorsValid = false;
    protected boolean ringValid = false;
    protected boolean needToCollectFunctionalBlocks = true;
    public boolean needToRecalculateCharacteristics = true;

    public boolean isReadyToProcess()
    {
        return isFormed && outerValid && innerValid && !needToRecalculateCharacteristics && !needToCollectFunctionalBlocks;
    }

    protected HashMap<BlockPos, ElectromagnetBlock> electromagnets = new HashMap<>();
    public HashMap<BlockPos, RFAmplifierBlock> amplifiers = new HashMap<>();

    public FusionReactorMultiblock(FusionCoreBE<?> core) {
        super(
                getBlocksByTagKey(FusionReactor.CASING_BLOCKS.location().toString()),
                List.of(AIR));
        controllerBE = core;
        id = "fusion_reactor_"+controllerBE.getBlockPos().toShortString();
        MultiblockHandler.addMultiblock(this);
        controller = new FusionReactorController(controllerBE);
    }

    @Override
    public int height() {
        return 3;
    }

    @Override
    public int width() {
        return length;
    }

    @Override
    public int depth() {
        return length;
    }

    @Override
    public int maxHeight() {
        return 3;
    }

    @Override
    public int minHeight() {
        return 3;
    }

    @Override
    public int maxWidth() {
        return FUSION_CONFIG.MAX_SIZE.get();
    }

    @Override
    public int minWidth() {
        return FUSION_CONFIG.MIN_SIZE.get();
    }

    @Override
    public int maxDepth() {
        return FUSION_CONFIG.MAX_SIZE.get();
    }

    @Override
    public int minDepth() {
        return FUSION_CONFIG.MIN_SIZE.get();
    }

    @Override
    public final List<Block> validOuterBlocks() {
        return validOuterBlocks;
    }

    @Override
    public List<Block> validInnerBlocks() {
        return validInnerBlocks;
    }

    @Override
    public void validateOuter() {
        validateConnectors();
        validateRing();
        outerValid = ringValid && connectorsValid;
        if(outerValid) {
            validationResult =  ValidationResult.VALID;
        }
    }

    @Override
    public void validate() {
        super.validate();
        needToCollectFunctionalBlocks = isFormed;
    }

    @Override
    public void onBlockDestroyed(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if(controllerBE.plasmaTemperature > 100000) {
          /*  level.explode(null,
                    pos.getX(), pos.getY(), pos.getZ(),
                    1, true, Explosion.BlockInteraction.KEEP);*/
        }
        controller.clearStats();
    }

    private Block getBlock(BlockPos pos) {
        if(level() == null) {
            return AIR;
        }
        return level().getBlockState(pos).getBlock();
    }

    private void processFunctionalBlock(NCBlockPos pos)
    {
        if(getBlock(pos) instanceof ElectromagnetBlock magnet) {
            electromagnets.put(pos.copy(), magnet);
            allBlocks.add(pos.copy());
            updateDimensions(pos);
        } else if(getBlock(pos) instanceof RFAmplifierBlock amplifier) {
            amplifiers.put(pos.copy(), amplifier);
            allBlocks.add(pos.copy());
            updateDimensions(pos);
        }
    }

    public void collectFunctionalParts() {
        electromagnets.clear();
        amplifiers.clear();
        NCBlockPos pos = new NCBlockPos(controllerBE.getBlockPos());
        for(Direction side: List.of(NORTH, EAST, SOUTH, WEST)) {
            Direction dir = side;
            int steps = length*2+3;
            int shift = length+1;
            NCBlockPos startPosInnerWall = null;
            NCBlockPos startPosOuterWall = null;
            //position to left corner of the ring
            switch (side) {
                case NORTH -> {
                    dir = EAST;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(NORTH, shift).relative(WEST, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(NORTH, 2+shift).relative(WEST, 1+shift));
                }
                case SOUTH -> {
                    dir = WEST;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(SOUTH, shift).relative(EAST, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(SOUTH, 2+shift).relative(EAST, 1+shift));
                }
                case WEST -> {
                    dir = SOUTH;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(WEST, shift).relative(NORTH, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(WEST, 2+shift).relative(NORTH, 1+shift));
                }
                case EAST -> {
                    dir = NORTH;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(EAST, shift).relative(SOUTH, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(EAST, 2+shift).relative(SOUTH, 1+shift));
                }
            }
            if(startPosInnerWall == null || startPosOuterWall == null) {
                return;
            }
            //inner
            for(int i = 0; i < steps; i++) {
                processFunctionalBlock(startPosInnerWall.revert().relative(dir, i));
                processFunctionalBlock(startPosInnerWall.revert().relative(UP, 2).relative(dir, i));
            }
            //outer
            for(int i = 0; i < steps+2; i++) {
                processFunctionalBlock(startPosOuterWall.revert().relative(dir, i));
                processFunctionalBlock(startPosOuterWall.revert().relative(UP, 2).relative(dir, i));
            }
        }
    }

    private void validateRing() {
        NCBlockPos pos = new NCBlockPos(controllerBE.getBlockPos().relative(UP));
        ringValid = true;
        for(Direction side: List.of(NORTH, EAST, SOUTH, WEST)) {
            Direction dir = side;
            int steps = length*2+3;
            int shift = length+1;
            NCBlockPos startPosInnerWall = null;
            NCBlockPos startPosOuterWall = null;
            NCBlockPos startPosBottomWall = null;
            NCBlockPos startPosTopWall = null;
            Level level = controllerBE.getLevel();
            //position to left corner of the ring
            switch (side) {
                case NORTH -> {
                    dir = EAST;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(NORTH, shift).relative(WEST, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(NORTH, 2+shift).relative(WEST, 1+shift));
                    startPosBottomWall = new NCBlockPos(pos.revert().relative(NORTH, 1+shift).relative(WEST, 1+shift).relative(DOWN));
                    startPosTopWall = new NCBlockPos(pos.revert().relative(NORTH, 1+shift).relative(WEST, 1+shift).relative(UP));
                }
                case SOUTH -> {
                    dir = WEST;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(SOUTH, shift).relative(EAST, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(SOUTH, 2+shift).relative(EAST, 1+shift));
                    startPosBottomWall = new NCBlockPos(pos.revert().relative(SOUTH, 1+shift).relative(EAST, 1+shift).relative(DOWN));
                    startPosTopWall = new NCBlockPos(pos.revert().relative(SOUTH, 1+shift).relative(EAST, 1+shift).relative(UP));
                }
                case WEST -> {
                    dir = SOUTH;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(WEST, shift).relative(NORTH, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(WEST, 2+shift).relative(NORTH, 1+shift));
                    startPosBottomWall = new NCBlockPos(pos.revert().relative(WEST, 1+shift).relative(NORTH, 1+shift).relative(DOWN));
                    startPosTopWall = new NCBlockPos(pos.revert().relative(WEST, 1+shift).relative(NORTH, 1+shift).relative(UP));
                }
                case EAST -> {
                    dir = NORTH;
                    startPosInnerWall = new NCBlockPos(pos.revert().relative(EAST, shift).relative(SOUTH, shift));
                    startPosOuterWall = new NCBlockPos(pos.revert().relative(EAST, 2+shift).relative(SOUTH, 1+shift));
                    startPosBottomWall = new NCBlockPos(pos.revert().relative(EAST, 1+shift).relative(SOUTH, 1+shift).relative(DOWN));
                    startPosTopWall = new NCBlockPos(pos.revert().relative(EAST, 1+shift).relative(SOUTH, 1+shift).relative(UP));
                }
            }
            //inner wall
            for(int i = 0; i < steps; i++) {
                assert startPosInnerWall != null;
                if(isValidForOuter(startPosInnerWall.revert().relative(dir, i))) {
                    allBlocks.add(new NCBlockPos(startPosInnerWall));
                } else {
                    ringValid = false;
                    validationResult = ValidationResult.WRONG_OUTER;
                    controller().addErroredBlock(startPosInnerWall);
                    return;
                }
            }
            //outer, bottom, top walls
            for(int i = 0; i < steps+2; i++) {
                assert startPosOuterWall != null;
                if(isValidForOuter(startPosOuterWall.revert().relative(dir, i))) {
                    allBlocks.add(new NCBlockPos(startPosOuterWall));
                } else {
                    ringValid = false;
                    validationResult = ValidationResult.WRONG_OUTER;
                    controller().addErroredBlock(startPosOuterWall);
                    return;
                }
                assert startPosBottomWall != null;
                if(isValidForOuter(startPosBottomWall.revert().relative(dir, i))) {
                    allBlocks.add(new NCBlockPos(startPosBottomWall));
                } else {
                    ringValid = false;
                    validationResult = ValidationResult.WRONG_OUTER;
                    controller().addErroredBlock(startPosBottomWall);
                    return;
                }
                assert startPosTopWall != null;
                if(isValidForOuter(startPosTopWall.revert().relative(dir, i))) {
                    allBlocks.add(new NCBlockPos(startPosTopWall));
                } else {
                    ringValid = false;
                    validationResult = ValidationResult.WRONG_OUTER;
                    controller().addErroredBlock(startPosTopWall);
                    return;
                }
            }
        }
    }

    private void validateConnectors() {
        NCBlockPos pos = new NCBlockPos(controllerBE.getBlockPos().above());

        length = 1;
        connectorsValid = true;
        for(int i = 2; i <= maxWidth()/2+1; i++) {
            int connectors = 0;
            for(Direction side: List.of(NORTH, EAST, Direction.SOUTH, Direction.WEST)) {
                if(getBlockState(pos.revert().relative(side, i)).getBlock() instanceof MultiblockConnectorBlock) {
                    allBlocks.add(new NCBlockPos(pos));
                    connectors++;
                }
            }
            if(connectors == 4) {
                length++;
            } else {
                if (connectors != 0) {
                    connectorsValid = false;
                } else {
                    connectorsValid = true;
                    validationResult = ValidationResult.INCOMPLETE;
                    controller().addErroredBlock(pos);
                }
                return;
            }
        }
    }

    @Override
    public void validateInner() {
        if(!outerValid) return;
        NCBlockPos pos = new NCBlockPos(controllerBE.getBlockPos().relative(UP));
        innerValid = true;
        for(Direction side: List.of(NORTH, EAST, SOUTH, WEST)) {
            Direction dir = side;
            int steps = length*2+3;
            int shift = length+2;
            NCBlockPos innerRingStartPos = null;
            Level level = controllerBE.getLevel();
            //position to left corner of the ring
            switch (side) {
                case NORTH -> {
                    dir = EAST;
                    innerRingStartPos = new NCBlockPos(pos.revert().relative(NORTH, shift).relative(WEST, shift));
                }
                case SOUTH -> {
                    dir = WEST;
                    innerRingStartPos = new NCBlockPos(pos.revert().relative(SOUTH, shift).relative(EAST, shift));
                }
                case WEST -> {
                    dir = SOUTH;
                    innerRingStartPos = new NCBlockPos(pos.revert().relative(WEST, shift).relative(NORTH, shift));
                }
                case EAST -> {
                    dir = NORTH;
                    innerRingStartPos = new NCBlockPos(pos.revert().relative(EAST, shift).relative(SOUTH, shift));
                }
            }
            for(int i = 0; i < steps; i++) {
                assert innerRingStartPos != null;
                if(!processInnerBlock(innerRingStartPos.revert().relative(dir, i))) {
                    innerValid = false;
                    validationResult = ValidationResult.WRONG_INNER;
                    controller().addErroredBlock(innerRingStartPos);
                    return;
                }
            }
        }
        validationResult =  ValidationResult.VALID;
    }

    @Override
    protected boolean processInnerBlock(BlockPos toCheck) {
        return validInnerBlocks.contains(level().getBlockState(toCheck).getBlock());
    }

    private Level level() {
        if(controllerBE == null) {
            return null;
        }
        return controllerBE.getLevel();
    }

    @Override
    public void invalidateStats() {
        length = 0;
        controller().clearStats();
        isFormed = false;
        hasToRefresh = true;
    }

    @Override
    protected Direction getFacing() {
        return UP;
    }

    public void tick() {
        super.tick();
        if(isFormed) {
            if(needToCollectFunctionalBlocks) {
                collectFunctionalParts();
                needToCollectFunctionalBlocks = false;
                needToRecalculateCharacteristics = true;
            } else if(needToRecalculateCharacteristics){
                recalculateCharacteristics();
                needToRecalculateCharacteristics = false;
            }
        }
    }

    public void recalculateCharacteristics() {
        magneticFieldStrength = 0;
        magnetsPower = 0;
        maxMagnetsTemp = 1000000;
        rfAmplification = 0;
        rfAmplifiersPower = 0;
        maxRFAmplifiersTemp = 1000000;
        double mEfficiency = 0;
        double rEfficiency = 0;
        for(ElectromagnetBlock magnet: electromagnets.values()) {
            magneticFieldStrength += magnet.getStrength();
            mEfficiency += (int) magnet.getEfficiency();
            magnetsPower += magnet.getPower();
            if(magnet.getMaxTemperature() < maxMagnetsTemp) {
                maxMagnetsTemp = magnet.getMaxTemperature();
            }
        }
        magnetsEfficiency = (int) (mEfficiency / electromagnets.size());
        for(RFAmplifierBlock amplifier: amplifiers.values()) {
            rfAmplification += amplifier.getAmplification();
            rfAmplifiersPower += amplifier.getPower();
            rEfficiency += (int) amplifier.getEfficiency();
            if(amplifier.getMaxTemperature() < maxRFAmplifiersTemp) {
                maxRFAmplifiersTemp = amplifier.getMaxTemperature();
            }
        }
        rfEfficiency = (int) (rEfficiency / amplifiers.size());
    }

    @Override
    public void onNeighborChange(BlockState state, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, pos, neighbor);
        if(!hasToRefresh && componentChanged(neighbor)) {
            needToCollectFunctionalBlocks = true;
        }
    }

    private boolean componentChanged(BlockPos neighbor) {
        //known component changed
        if(electromagnets.containsKey(neighbor) || amplifiers.containsKey(neighbor)) {
            return true;
        }
        Block changedBlock = level().getBlockState(neighbor).getBlock();
        //new added
        if(
                changedBlock instanceof ElectromagnetBlock
                || changedBlock instanceof RFAmplifierBlock
        ) {
            return true;
        }
        return false;
    }
}
