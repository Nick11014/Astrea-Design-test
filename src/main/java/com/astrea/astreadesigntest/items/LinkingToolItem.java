package com.astrea.astreadesigntest.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.astrea.astreadesigntest.client.renderer.LinkingToolItemRenderer;

import java.util.List;
import java.util.function.Consumer;

public class LinkingToolItem extends Item implements GeoItem {
    private static final RawAnimation ACTIVATE_ANIM = RawAnimation.begin().thenPlay("side_button_activate");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public LinkingToolItem(Properties properties) {
        super(properties);
        
        // Register our item as server-side handled.
        // This enables both animation data syncing and server-side animation triggering
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    // Utilise our own render hook to define our custom renderer
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private LinkingToolItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new LinkingToolItemRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.STOP)
                .triggerableAnim("activate", ACTIVATE_ANIM));
                // We've marked the "activate" animation as being triggerable from the server
    }

    // Let's handle our use method so that we activate the animation when right-clicking while holding the item
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel)
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), serverLevel), "Activation", "activate");

        if (!level.isClientSide()) {
            // Send different messages based on player state
            if (player.isCrouching()) {
                player.sendSystemMessage(Component.literal("Device Link Established!").withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.literal("Scanning for linkable devices...").withStyle(ChatFormatting.YELLOW));
            }
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Right-click to scan for devices").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Sneak + Right-click to establish link").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Advanced linking technology").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}