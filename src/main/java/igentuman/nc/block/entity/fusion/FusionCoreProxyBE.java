package igentuman.nc.block.entity.fusion;

import igentuman.nc.util.annotation.NBTField;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;

import static igentuman.nc.compat.oc2.NCFusionReactorDevice.DEVICE_CAPABILITY;
import static igentuman.nc.multiblock.fusion.FusionReactor.FUSION_CORE_PROXY_BE;
import static igentuman.nc.util.ModUtil.*;

public class FusionCoreProxyBE extends FusionBE {

    public FusionCoreProxyBE(BlockPos pPos, BlockState pBlockState) {
        super(FUSION_CORE_PROXY_BE.get(), pPos, pBlockState);
    }
    protected byte wasSignal = 0;
    protected void validateCore()
    {
        if(core != null)
        {
            if(!level.isLoaded(core.getBlockPos())) {
                return;
            }
            core = (FusionCoreBE<?>) level.getBlockEntity(core.getBlockPos());
            corePos = core.getBlockPos();
        } else {
            if(corePos == null) return;
            core = (FusionCoreBE<?>) level.getBlockEntity(corePos);
        }
    }

    public void tickServer()
    {
        if(getLevel().getGameTime() % 20 == 0) {
            validateCore();
        }
        if(!(core instanceof FusionCoreBE)) {
            level.removeBlock(worldPosition, false);
            return;
        }

        if(wasSignal != core.analogSignal) {
            wasSignal = core.analogSignal;
            setChanged();
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    public void setCore(FusionCoreBE<?> core) {
        FusionCoreBE<?> wasCore = this.core;
        this.core = core;
        corePos = core.getBlockPos();
        if(wasCore != core) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void destroyCore() {
        if(corePos != null) {
            BlockState st = level.getBlockState(corePos);
            if(st.equals(Blocks.AIR.defaultBlockState())) return;
            ItemStack core = new ItemStack(st.getBlock().asItem());
            level.removeBlock(corePos, false);
            Block.popResource(level, corePos, core);
        }
    }

    public FusionCoreBE<?> getCoreBE() {
        return core;
    }

    public BlockPos getCorePos() {
        return corePos;
    }

    protected <T> LazyOptional<T> fluidHandler(@Nullable Direction side)
    {
        return controller().contentHandler.getFluidCapability(side);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(controller() == null) return super.getCapability(cap, side);
        if(side == null || side.getAxis().isHorizontal()) {
            return LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler(side).cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return controller().getEnergy().cast();
        }
        if(isCcLoaded()) {
            if(cap == dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL) {
                return controller().getPeripheral(cap, side);
            }
        }

        if(isOC2Loaded()) {
            if(cap == DEVICE_CAPABILITY) {
                return controller().getOCDevice(cap, side);
            }
        }

        if(isMekanismLoadeed()) {
            if(cap == mekanism.common.capabilities.Capabilities.GAS_HANDLER) {
                if(controller().contentHandler.hasFluidCapability(side)) {
                    return LazyOptional.of(() -> controller().contentHandler.gasConverter(side));
                }
                return LazyOptional.empty();
            }
            if(cap == mekanism.common.capabilities.Capabilities.SLURRY_HANDLER) {
                if(controller().contentHandler.hasFluidCapability(side)) {
                    return LazyOptional.of(() -> controller().contentHandler.getSlurryConverter(side));
                }
                return LazyOptional.empty();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public boolean canInvalidateCache()
    {
        return false;
    }

    public void sendOutEnergy() {
        int required = getCoreBE().rfAmplifiersPower + getCoreBE().magnetsPower;
        for(Direction side: List.of(Direction.UP, Direction.DOWN)) {
            if(getCoreBE().energyStorage.getEnergyStored() > required) {
                BlockEntity be = getLevel().getBlockEntity(getBlockPos().relative(side));
                if(be instanceof BlockEntity && !(be instanceof FusionBE)) {
                    IEnergyStorage r = be.getCapability(ForgeCapabilities.ENERGY, side.getOpposite()).orElse(null);
                    if(r == null) break;
                    if(r.canReceive()) {
                        int recieved = r.receiveEnergy(getCoreBE().energyStorage.getEnergyStored()-required, false);
                        getCoreBE().energyStorage.setEnergy(getCoreBE().energyStorage.getEnergyStored()-recieved);
                    }
                }
            }
        }
    }

    public void forceTickServer(FusionCoreBE<?> core) {
        this.core = core;
        core.inputRedstoneSignal =  Math.max(getLevel().getBestNeighborSignal(getBlockPos()), core.inputRedstoneSignal);
        core.rfAmplificationRatio = (int)((double)core.inputRedstoneSignal / 0.15D);
    }

    public void toggleRedstoneMode() {
        getCoreBE().toggleRedstoneMode();
    }

    public int getAnalogSignal() {
        return getCoreBE().analogSignal;
    }
}
