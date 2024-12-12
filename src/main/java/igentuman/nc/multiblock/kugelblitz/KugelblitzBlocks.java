package igentuman.nc.multiblock.kugelblitz;

import igentuman.nc.block.fusion.MultiblockConnectorBlock;
import igentuman.nc.block.kugelblitz.ChamberPortBlock;
import igentuman.nc.block.kugelblitz.ChamberTerminalBlock;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

import static igentuman.nc.setup.registration.NCItems.ALL_NC_ITEMS;
import static igentuman.nc.setup.registration.Registries.BLOCKS;
import static igentuman.nc.setup.registration.Registries.ITEMS;
import static igentuman.nc.setup.registration.Tags.blockTag;
import static igentuman.nc.setup.registration.Tags.itemTag;

public class KugelblitzBlocks {

    public static final Item.Properties KUGELBLITZ_ITEM_PROPERTIES = new Item.Properties();
    public static final Block.Properties KUGELBLITZ_BLOCK_PROPERTIES =  BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(4f).requiresCorrectToolForDrops();;
    public static HashMap<String, RegistryObject<Block>> KUGELBLITZ_BLOCKS = new HashMap<>();
    public static HashMap<String, RegistryObject<BlockEntityType<? extends BlockEntity>>> KUGELBLITZ_BE = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> KUGELBLITZ_ITEMS = new HashMap<>();
    public static TagKey<Block> CASING_BLOCKS = blockTag("kugelblitz_casing");
    public static TagKey<Item> CASING_ITEMS = itemTag("kugelblitz_casing");

    /*
    1. Photon Concentrator
    2. Event Horizon Stabilizer
    3. Neutronium Frame
    4. Chamber port
    5. Chamber terminal
    6. Quantum Flux Regulator
    7. Quantum transformer
    */
    public static void init() {
        registerSimpleBlock("neutronium_frame");
        registerSimpleBlock("event_horizon_stabilizer");
        registerSimpleBlock("photon_concentrator");
        registerSimpleBlock("quantum_flux_regulator");
        registerSimpleBlock("quantum_transformer");

        KUGELBLITZ_BLOCKS.put("chamber_port", BLOCKS.register("chamber_port", () -> new ChamberPortBlock(KUGELBLITZ_BLOCK_PROPERTIES)));
        KUGELBLITZ_ITEMS.put("chamber_port", fromMultiblock(KUGELBLITZ_BLOCKS.get("chamber_port")));
        ALL_NC_ITEMS.put("chamber_port", KUGELBLITZ_ITEMS.get("chamber_port"));

        KUGELBLITZ_BLOCKS.put("chamber_terminal", BLOCKS.register("chamber_terminal", () -> new ChamberTerminalBlock(KUGELBLITZ_BLOCK_PROPERTIES)));
        KUGELBLITZ_ITEMS.put("chamber_terminal", fromMultiblock(KUGELBLITZ_BLOCKS.get("chamber_terminal")));
        ALL_NC_ITEMS.put("chamber_terminal", KUGELBLITZ_ITEMS.get("chamber_terminal"));
    }

    private static void registerSimpleBlock(String key) {
        KUGELBLITZ_BLOCKS.put(key, BLOCKS.register(key, () -> new Block(KUGELBLITZ_BLOCK_PROPERTIES)));
        KUGELBLITZ_ITEMS.put(key, fromMultiblock(KUGELBLITZ_BLOCKS.get(key)));
        ALL_NC_ITEMS.put(key, KUGELBLITZ_ITEMS.get(key));
    }

    public static <B extends Block> RegistryObject<Item> fromMultiblock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), KUGELBLITZ_ITEM_PROPERTIES));
    }
}
