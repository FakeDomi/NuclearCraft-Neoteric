package igentuman.nc.world;

import igentuman.nc.content.materials.Ores;
import igentuman.nc.world.ore.NCOre;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static igentuman.nc.NuclearCraft.rl;
import static igentuman.nc.setup.registration.NCBlocks.MUSHROOM_BLOCK;
import static net.minecraft.world.level.block.Blocks.*;

public class NCConfiguredFeatures {

    public static final HashMap<String, ResourceKey<ConfiguredFeature<?, ?>>> ORE_CONFIGURED_FEATURES = initFeatures();

    private static HashMap<String, ResourceKey<ConfiguredFeature<?,?>>> initFeatures() {
        HashMap<String, ResourceKey<ConfiguredFeature<?,?>>> features = new HashMap<>();
        for(String name: Ores.all().keySet()) {
            features.put(name, registerKey(name + "_ore"));
        }
        features.put("glowing_mushroom", registerKey("glowing_mushroom_feature"));
        return features;
    }

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        RuleTest netherrackReplacables = new BlockMatchTest(Blocks.NETHERRACK);
        RuleTest endReplaceables = new BlockMatchTest(Blocks.END_STONE);

        for(String name: Ores.registered().keySet()) {
            NCOre ore = Ores.all().get(name);

            if(ore.dimensions.contains(0)) {
                List<OreConfiguration.TargetBlockState> overworld = new ArrayList<>();
                if(ore.config().height[1] > 0) {
                    overworld.add(OreConfiguration.target(stoneReplaceable,
                            Ores.all().get(name).block().defaultBlockState()));
                }
                if(ore.config().height[0] < 0) {
                    overworld.add(OreConfiguration.target(deepslateReplaceables, ore.block("_deepslate").defaultBlockState()));
                }

                register(context, ORE_CONFIGURED_FEATURES.get(name), Feature.ORE, new OreConfiguration(overworld, 9));
            }
            if(ore.config().dimensions.contains(-1)) {
                register(context, ORE_CONFIGURED_FEATURES.get(name), Feature.ORE, new OreConfiguration(netherrackReplacables,
                        ore.block().defaultBlockState(), 9));
            }

            if(ore.config().dimensions.contains(1)) {
                register(context, ORE_CONFIGURED_FEATURES.get(name), Feature.ORE, new OreConfiguration(endReplaceables,
                        ore.block().defaultBlockState(), 9));
            }
        }
        register(context, ORE_CONFIGURED_FEATURES.get("glowing_mushroom"), Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(MUSHROOM_BLOCK.get())),
                        List.of(SOUL_SOIL, SOUL_SAND, GLOWSTONE)
                        )
        );
    }


    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, rl(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
