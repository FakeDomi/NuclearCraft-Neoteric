package igentuman.nc.setup.registration;

import igentuman.nc.block.ProcessorBlock;
import igentuman.nc.block.entity.processor.NCProcessorBE;
import igentuman.nc.container.NCProcessorContainer;
import igentuman.nc.content.processors.Processors;
import igentuman.nc.item.ProcessorBlockItem;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import static igentuman.nc.setup.registration.Registries.*;

public class NCProcessors {
    public static HashMap<String, RegistryObject<Block>> PROCESSORS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> PROCESSOR_BLOCKS_ITEMS = new HashMap<>();
    public static final Item.Properties PROCESSOR_ITEM_PROPERTIES = new Item.Properties();
    public static final BlockBehaviour.Properties PROCESSOR_BLOCK_PROPERTIES = BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(2f).requiresCorrectToolForDrops();
    public static HashMap<String, RegistryObject<MenuType<? extends NCProcessorContainer<?>>>> PROCESSORS_CONTAINERS = new HashMap<>();
    public static HashMap<String, RegistryObject<BlockEntityType<? extends NCProcessorBE<?>>>> PROCESSORS_BE = new HashMap<>();

    public static void init() {
        registerBlocks();
        registerBlockEntities();
        registerContainers();
    }

    @SuppressWarnings("unchecked")
    private static void registerContainers() {
        for(String name: Processors.all().keySet()) {
            PROCESSORS_CONTAINERS.put(name, CONTAINERS.register(name,
                    () -> IForgeMenuType.create((windowId, inv, data) -> {
                        NCProcessorContainer<?> o = null;
                        try {
                            o = (NCProcessorContainer<?>) Processors.all().get(name).getContainerConstructor()
                                    .newInstance(windowId, data.readBlockPos(), inv, inv.player, name);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
                        }
                        return o;
                    })));
        }
    }


    @SuppressWarnings("unchecked")
    private static void registerBlockEntities() {
        for(String name: Processors.all().keySet()) {
            PROCESSORS_BE.put(name, BLOCK_ENTITIES.register(name,
                    () -> BlockEntityType.Builder
                            .of(Processors.all().get(name).getBlockEntity(), PROCESSORS.get(name).get())
                            .build(null)));
        }
    }

    private static void registerBlocks() {
        for(String name: Processors.all().keySet()) {
            PROCESSORS.put(name, BLOCKS.register(name, () -> new ProcessorBlock(PROCESSOR_BLOCK_PROPERTIES)));
            PROCESSOR_BLOCKS_ITEMS.put(name, fromBlock(PROCESSORS.get(name)));
        }
    }

    public static <B extends Block> RegistryObject<Item> fromBlock(RegistryObject<B> block) {
        return ITEMS.register(block.getId().getPath(), () -> new ProcessorBlockItem(block.get(), PROCESSOR_ITEM_PROPERTIES));
    }
}
