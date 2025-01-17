package igentuman.nc.setup.registration;

import igentuman.nc.content.ArmorMaterials;
import igentuman.nc.content.materials.*;
import igentuman.nc.item.*;
import igentuman.nc.item.Tiers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static igentuman.nc.setup.registration.NCSounds.SOUND_MAP;
import static igentuman.nc.setup.registration.Registries.ITEMS;
import static igentuman.nc.setup.registration.Registries.ITEM_REGISTRY;
import static igentuman.nc.setup.registration.Tags.*;

public class NCItems {

    public static HashMap<String, RegistryObject<Item>> NC_FOOD = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> ALL_NC_ITEMS = new HashMap<>();

    public static HashMap<String, RegistryObject<Item>> NC_RECORDS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_PARTS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_SHIELDING = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_ITEMS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_GEMS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_INGOTS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_CHUNKS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_NUGGETS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_PLATES = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_DUSTS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> ORE_BLOCK_ITEMS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_BLOCKS_ITEMS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_ELECTROMAGNETS_ITEMS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> NC_RF_AMPLIFIERS_ITEMS = new HashMap<>();
    public static HashMap<String, RegistryObject<Item>> MULTIBLOCK_ITEMS = new HashMap<>();
    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();
    public static final Item.Properties ONE_ITEM_PROPERTIES = new Item.Properties().stacksTo(1);
    public static final Item.Properties PAXEL_PROPS = new Item.Properties().stacksTo(1).durability(5000).fireResistant();
    public static final Item.Properties HAZMAT_PROPS = new Item.Properties().stacksTo(1).durability(250);
    public static final Item.Properties TOUGH_PROPS = new Item.Properties().stacksTo(1).durability(2500).fireResistant();
    public static final Item.Properties HEV_PROPS = new Item.Properties().stacksTo(1).durability(5500).fireResistant();

    public static final RegistryObject<Item> HAZMAT_MASK =
            ITEMS.register("hazmat_mask", () -> new HazmatItem(ArmorMaterials.HAZMAT, ArmorItem.Type.HELMET, HAZMAT_PROPS));
    public static final RegistryObject<Item> HAZMAT_CHEST =
            ITEMS.register("hazmat_chest", () -> new HazmatItem(ArmorMaterials.HAZMAT, ArmorItem.Type.CHESTPLATE, HAZMAT_PROPS));
    public static final RegistryObject<Item> HAZMAT_BOOTS =
            ITEMS.register("hazmat_boots", () -> new HazmatItem(ArmorMaterials.HAZMAT, ArmorItem.Type.BOOTS, HAZMAT_PROPS));
    public static final RegistryObject<Item> HAZMAT_PANTS =
            ITEMS.register("hazmat_pants", () -> new HazmatItem(ArmorMaterials.HAZMAT, ArmorItem.Type.LEGGINGS, HAZMAT_PROPS));


    public static final RegistryObject<Item> HEV_HELMET =
            ITEMS.register("hev_helmet", () -> new HEVItem(ArmorMaterials.HEV, ArmorItem.Type.HELMET, HEV_PROPS));
    public static final RegistryObject<Item> HEV_CHEST =
            ITEMS.register("hev_chest", () -> new HEVItem(ArmorMaterials.HEV, ArmorItem.Type.CHESTPLATE, HEV_PROPS));
    public static final RegistryObject<Item> HEV_BOOTS =
            ITEMS.register("hev_boots", () -> new HEVItem(ArmorMaterials.HEV, ArmorItem.Type.BOOTS, HEV_PROPS));
    public static final RegistryObject<Item> HEV_PANTS =
            ITEMS.register("hev_pants", () -> new HEVItem(ArmorMaterials.HEV, ArmorItem.Type.LEGGINGS, HEV_PROPS));

    public static final RegistryObject<Item> TOUGH_HELMET =
            ITEMS.register("tough_helmet", () -> new ArmorItem(ArmorMaterials.TOUGH, ArmorItem.Type.HELMET, TOUGH_PROPS));
    public static final RegistryObject<Item> TOUGH_CHEST =
            ITEMS.register("tough_chest", () -> new ArmorItem(ArmorMaterials.TOUGH, ArmorItem.Type.CHESTPLATE, TOUGH_PROPS));
    public static final RegistryObject<Item> TOUGH_BOOTS =
            ITEMS.register("tough_boots", () -> new ArmorItem(ArmorMaterials.TOUGH, ArmorItem.Type.BOOTS, TOUGH_PROPS));
    public static final RegistryObject<Item> TOUGH_PANTS =
            ITEMS.register("tough_pants", () -> new ArmorItem(ArmorMaterials.TOUGH, ArmorItem.Type.LEGGINGS, TOUGH_PROPS));

    public static final RegistryObject<Item> GEIGER_COUNTER = ITEMS.register("geiger_counter", () -> new GeigerCounterItem(ONE_ITEM_PROPERTIES));
    public static final RegistryObject<Item> LITHIUM_ION_CELL = ITEMS.register("lithium_ion_cell", () -> new BatteryItem(ONE_ITEM_PROPERTIES));
    public static final RegistryObject<Item> SPAXELHOE_TOUGH = ITEMS.register("spaxelhoe_tough", () -> new PaxelItem(7, 2, Tiers.TOUGH, PAXEL_PROPS));
    public static final RegistryObject<Item> SPAXELHOE_THORIUM = ITEMS.register("spaxelhoe_thorium", () -> new PaxelItem(3, 1, Tiers.THORIUM, PAXEL_PROPS));
    public static final RegistryObject<Item> QNP = ITEMS.register("qnp", () -> new QNP(Tiers.QNP, 11, 2F, ONE_ITEM_PROPERTIES));
    public static final RegistryObject<Item> MULTITOOL = ITEMS.register("multitool", () -> new MultitoolItem(ONE_ITEM_PROPERTIES));

    public static RegistryObject<Item> registerItem(String name) {
        return ITEMS.register(name, () -> new Item(ITEM_PROPERTIES));
    }

    public static RegistryObject<Item> registerIngot(String name) {
        return ITEMS.register(name, () -> new NCIngotItem(ITEM_PROPERTIES));
    }

    public static RegistryObject<Item> registerChunk(String name) {
        return ITEMS.register(name, () -> new NCChunkItem(ITEM_PROPERTIES));
    }

    public static RegistryObject<Item> registerNugget(String name) {
        return ITEMS.register(name, () -> new NCNuggetItem(ITEM_PROPERTIES));
    }

    public static RegistryObject<Item> registerPlate(String name) {
        return ITEMS.register(name, () -> new NCPlateItem(ITEM_PROPERTIES));
    }

    public static RegistryObject<Item> registerDust(String name) {
        return ITEMS.register(name, () -> new NCDustItem(ITEM_PROPERTIES));
    }

    public static RegistryObject<Item> registerGem(String name) {
        return ITEMS.register(name, () -> new NCBGemItem(ITEM_PROPERTIES));
    }


    public static RegistryObject<Item> registerBlockItem(String name) {
        return ITEMS.register(name, () -> new NCBlockItem(ITEM_PROPERTIES));
    }


    public static void init() {
        registerChunks();
        registerNuggets();
        registerIngots();
        registerPlates();
        registerDusts();
        registerGems();
        registerParts();
        registerItems();
        registerFood();
        registerRecords();
        registerShielding();
    }



    private static void registerRecords() {
        List<String> items = Arrays.asList(
                "end_of_the_world",
                "hyperspace",
                "money_for_nothing",
                "wanderer"
        );
        for(String name: items) {
            NC_RECORDS.put(name, ITEMS.register(name, () -> new MusicDiscItem(15, SOUND_MAP.get(name), new Item.Properties(), 3600)));
            ALL_NC_ITEMS.put(name, NC_RECORDS.get(name));
        }
    }

    private static void registerFood() {
        List<String> items = Arrays.asList(
                "marshmallow",
                "milk_chocolate",
                "dark_chocolate",
                "graham_cracker",
                "smore",
                "moresmore",
                "foursmore",
                "dominos",
                "evenmoresmore"

        );
        int i = -18;
        for(String name: items) {
            i+=4;
            int finalI = Math.max(i, 1);

            NC_FOOD.put(name, ITEMS.register(name, () -> new Item(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(finalI)
                            .saturationMod(finalI).build()
            ))));
            ALL_NC_ITEMS.put(name, NC_FOOD.get(name));
        }
        for(String name: List.of("rad_x","radaway","radaway_slow")) {
            NC_FOOD.put(name, ITEMS.register(name, () -> new RadAwayItem(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(0)
                            .saturationMod(0).alwaysEat().build()
            ))));
            ALL_NC_ITEMS.put(name, NC_FOOD.get(name));
        }
    }

    private static void registerItems() {
        List<String> items = Arrays.asList(
                "cocoa_butter",
                "cocoa_solids",
                "roasted_cocoa_beans",
                "flour",
                "gelatin",
                "ground_cocoa_nibs",
                "salt",
                "sawdust",
                "dosimeter",
                "upgrade_energy",
                "upgrade_speed",
                "water_collector",
                "lava_collector",
                "compact_water_collector",
                "dense_water_collector",
                "helium_collector",
                "compact_helium_collector",
                "dense_helium_collector",
                "nitrogen_collector",
                "compact_nitrogen_collector",
                "dense_nitrogen_collector",
                "unsweetened_chocolate"
        );
        for(String name: items) {
            if(name.equals("dosimeter")) {
                NC_ITEMS.put(name, ITEMS.register(name, () -> new DosimiterItem(ONE_ITEM_PROPERTIES)));
            } else {
                NC_ITEMS.put(name, registerItem(name));
            }
            ALL_NC_ITEMS.put(name, NC_ITEMS.get(name));
        }
    }

    private static void registerShielding() {
        List<String> parts = Arrays.asList(
                "light",
                "medium",
                "heavy",
                "dps"
        );
        int i = 1;
        for(String name: parts) {
            int finalI = i;
            NC_SHIELDING.put(name, ITEMS.register(name, () -> new RadShieldingItem(ITEM_PROPERTIES, finalI)));
            i+=2;
            ALL_NC_ITEMS.put(name, NC_SHIELDING.get(name));
        }
    }


    private static void registerParts() {
        List<String> parts = Arrays.asList(
                "actuator",
                "research_paper",
                "basic_electric_circuit",
                "bioplastic",
                "chassis",
                "empty_frame",
                "empty_sink",
                "motor",
                "plate_advanced",
                "plate_basic",
                "plate_du",
                "plate_elite",
                "plate_extreme",
                "servo",
                "sic_fiber",
                "steel_frame",
                "coil_copper",
                "coil_magnesium_diboride",
                "coil_bscco"
        );
        for(String name: parts) {
            if(name.equals("research_paper")) {
                NC_PARTS.put(name, ITEMS.register(name, () -> new ResearchPaperItem(ONE_ITEM_PROPERTIES)));
            } else {
                NC_PARTS.put(name,registerItem(name));
            }
            ALL_NC_ITEMS.put(name, NC_PARTS.get(name));
        }
    }

    private static void registerGems() {
        for(String name: Gems.get().all().keySet()) {
            addGemTag(name);
            NC_GEMS.put(name, registerGem(name+"_gem"));
            ALL_NC_ITEMS.put(name+"_gem", NC_GEMS.get(name));
        }
    }

    private static void registerChunks() {
        for(String name: Chunks.get().all().keySet()) {
            addChunkTag(name);
            NC_CHUNKS.put(name, registerChunk(name+"_chunk"));
            ALL_NC_ITEMS.put(name+"_chunk", NC_CHUNKS.get(name));
        }
    }

    private static void registerNuggets() {
        for(String name: Nuggets.get().all().keySet()) {
            addNuggetTag(name);
            NC_NUGGETS.put(name, registerNugget(name+"_nugget"));
            ALL_NC_ITEMS.put(name+"_nugget", NC_NUGGETS.get(name));
        }
    }

    private static void registerIngots() {
        for(String name: Ingots.get().all().keySet()) {
            addIngotTag(name);
            NC_INGOTS.put(name,registerIngot(name+"_ingot"));
            ALL_NC_ITEMS.put(name+"_ingot", NC_INGOTS.get(name));
        }
    }

    private static void registerPlates() {
        for(String name: Plates.get().all().keySet()) {
            addPlateTag(name);
            NC_PLATES.put(name,registerPlate(name+"_plate"));
            ALL_NC_ITEMS.put(name+"_plate", NC_PLATES.get(name));
        }
    }

    private static void registerDusts() {
        for(String name: Dusts.get().all().keySet()) {
            addDustTag(name);
            NC_DUSTS.put(name, registerDust(name+"_dust"));
            ALL_NC_ITEMS.put(name+"_dust", NC_DUSTS.get(name));
        }
    }
}
