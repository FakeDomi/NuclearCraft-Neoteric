package igentuman.nc.world;

import com.mojang.serialization.Codec;
import igentuman.nc.setup.registration.WorldGeneration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static dev.latvian.mods.rhino.TopLevel.Builtins.Array;
import static igentuman.nc.handler.config.OreGenConfig.ORE_CONFIG;

public class OrePlacementModifier extends PlacementModifier {

    public static final Codec<OrePlacementModifier> CODEC = Codec.INT.fieldOf("count")
            .xmap(OrePlacementModifier::new, (modifier) -> modifier.count)
            .codec();

    private int count;
    private final HashMap<String, Integer> countMap;
    private final HashMap<String, Integer[]> heightMap;
    private final HashMap<String, List<Integer>> dimensionsMap;
    private String name;

    public OrePlacementModifier(int count) {
        this.countMap = new HashMap<>();
        this.heightMap = new HashMap<>();
        this.dimensionsMap = new HashMap<>();
        for(String name: ORE_CONFIG.ORES.keySet()) {
            int amount = ORE_CONFIG.ORES.get(name).veinSize.get();
            if(!ORE_CONFIG.ORES.get(name).register.get()) {
                amount = 0;
            }
            this.heightMap.put(name, new Integer[]{ORE_CONFIG.ORES.get(name).min_height.get(), ORE_CONFIG.ORES.get(name).max_height.get()});
            this.dimensionsMap.put(name, ORE_CONFIG.ORES.get(name).dimensions.get());
            this.countMap.put(name, amount);
        }

        this.count = count;
    }

    @Override
    public PlacementModifierType<?> type() {
        return WorldGeneration.NC_ORE_MODIFIER.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int actualCount = determinePlacementCount(context, random, pos);

        return Stream.generate(() -> new BlockPos(
                pos.getX() + random.nextInt(16),
                heightMap.get(name)[0] + random.nextInt(heightMap.get(name)[1] - heightMap.get(name)[0] + 1),
                pos.getZ() + random.nextInt(16)
        )).limit(actualCount);
    }

    private int determinePlacementCount(PlacementContext context, RandomSource random, BlockPos pos) {
        name = context.topFeature().get().feature().unwrapKey().get().location().getPath().replace("_ore", "");
        int dimensionId = context.getLevel().getServer().registryAccess().registry(Registries.DIMENSION_TYPE).get().getId(context.getLevel().getLevel().dimensionType());
        int veinSize = countMap.get(name);
        if(!dimensionsMap.get(name).contains(dimensionId)) {
            veinSize = 0;
        }
        if(veinSize == 0) return 0;
        return random.nextInt(veinSize);
    }
}