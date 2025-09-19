package com.astrea.astreadesigntest;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.astrea.astreadesigntest.items.ControllerTestItem;
import com.astrea.astreadesigntest.items.LinkingToolItem;
import com.astrea.astreadesigntest.blocks.ControllerBlock;
import com.astrea.astreadesigntest.blockentity.ControllerBlockEntity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AstreaDesigntest.MODID)
public class AstreaDesigntest {
    public static final String MODID = "astreadesigntest";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    public static final DeferredBlock<ControllerBlock> CONTROLLER_BLOCK = BLOCKS.register("controller_block", 
            () -> new ControllerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0f, 6.0f).noOcclusion()));
    
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);
    public static final DeferredItem<BlockItem> CONTROLLER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("controller_block", CONTROLLER_BLOCK);

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredItem<ControllerTestItem> CONTROLLER_TEST_ITEM = ITEMS.register("controller_test_item", 
            () -> new ControllerTestItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<LinkingToolItem> LINKING_TOOL_ITEM = ITEMS.register("linking_tool", 
            () -> new LinkingToolItem(new Item.Properties().stacksTo(1)));
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ControllerBlockEntity>> CONTROLLER_BLOCK_ENTITY = 
            BLOCK_ENTITIES.register("controller_block_entity", () -> BlockEntityType.Builder.of(
                    ControllerBlockEntity::new, CONTROLLER_BLOCK.get()).build(null));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.astreadesigntest"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CONTROLLER_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get());
                output.accept(CONTROLLER_TEST_ITEM.get());
                output.accept(LINKING_TOOL_ITEM.get());
                output.accept(CONTROLLER_BLOCK_ITEM.get());
            }).build());

    public AstreaDesigntest(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}