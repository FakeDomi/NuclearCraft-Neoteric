package igentuman.nc.block;

import igentuman.nc.NuclearCraft;
import igentuman.nc.block.entity.energy.BatteryBE;
import igentuman.nc.block.entity.energy.NCEnergy;
import igentuman.nc.block.entity.processor.NCProcessorBE;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.network.toServer.BatterySideConfig;
import igentuman.nc.network.toServer.PacketSideConfigToggle;
import igentuman.nc.setup.energy.BatteryBlocks;
import igentuman.nc.setup.energy.SolarPanels;
import igentuman.nc.setup.registration.NCEnergyBlocks;
import igentuman.nc.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static igentuman.nc.handler.config.CommonConfig.ENERGY_STORAGE_CONFIG;
import static igentuman.nc.setup.registration.NCTools.MULTITOOL;

public class BatteryBlock extends Block implements EntityBlock {
    public BatteryBlock() {
        this(Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(2.0f)
                .noOcclusion()
                .requiresCorrectToolForDrops());
    }
    public BatteryBlock(Properties pProperties) {
        super(pProperties.sound(SoundType.METAL));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BatteryBE batteryBE)  {
                if(player.getItemInHand(hand).getItem().equals(MULTITOOL.get())) {
                    NuclearCraft.packetHandler().sendToServer(new BatterySideConfig(pos, result.getDirection().ordinal()));
                } else {
                    player.sendSystemMessage(Component.translatable("tooltip.nc.energy_stored", formatEnergy(batteryBE.energyStorage.getEnergyStored()), formatEnergy(batteryBE.energyStorage.getMaxEnergyStored())).withStyle(ChatFormatting.BLUE));
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof BatteryBE) {

            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return NCEnergyBlocks.ENERGY_BE.get(code()).get().create(pPos, pState);
    }

    public String code()
    {
        return asItem().toString();
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof NCEnergy tile) {
                    tile.tickClient();
                }
            };
        }
        return (lvl, pos, blockState, t)-> {
            if (t instanceof NCEnergy tile) {
                tile.tickServer();
            }
        };

    }
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);

        if (stack.hasTag()) {
            BatteryBE tileEntity = (BatteryBE) world.getBlockEntity(pos);
            CompoundTag nbtData = stack.getTag();
            tileEntity.load(nbtData);
        }
    }

    @Override
    public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @javax.annotation.Nullable BlockEntity pBlockEntity, ItemStack pTool) {
        pPlayer.awardStat(Stats.BLOCK_MINED.get(this));
        pPlayer.causeFoodExhaustion(0.005F);
        BatteryBE batteryBE = (BatteryBE) pBlockEntity;
        CompoundTag data = batteryBE.getUpdateTag();

        ItemStack drop = new ItemStack(this);
        drop.setTag(data);
        if (!pLevel.isClientSide()) {
            ItemEntity itemEntity = new ItemEntity(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), drop);
            itemEntity.setDefaultPickUpDelay();
            pLevel.addFreshEntity(itemEntity);
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable BlockGetter world, List<Component> list, TooltipFlag flag)
    {
        int storage = ENERGY_STORAGE_CONFIG.getCapacityFor(asItem().toString());

        list.add(Component.translatable("tooltip.nc.energy_capacity", formatEnergy(storage)).withStyle(ChatFormatting.BLUE));
    }

    public String formatEnergy(int energy)
    {
        if(energy >= 1000000000) {
            return TextUtils.numberFormat(energy/1000000000)+" GFE";
        }
        if(energy >= 1000000) {
            return TextUtils.numberFormat(energy/1000000)+" MFE";
        }
        if(energy >= 1000) {
            return TextUtils.numberFormat(energy/1000)+" kFE";
        }
        return TextUtils.numberFormat(energy)+" FE";
    }

}