package com.astrea.astreadesigntest.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ControllerTestItem extends Item {

    public ControllerTestItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            // Send different messages based on player state
            if (player.isCrouching()) {
                player.sendSystemMessage(Component.literal("Controller Link Successful! (GeckoLib Ready)").withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.literal("Attempting Controller Link... (GeckoLib Ready)").withStyle(ChatFormatting.YELLOW));
            }
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.literal("Right-click to test controller link").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("Sneak + Right-click for successful link").withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.literal("GeckoLib " + getGeckoLibVersion() + " integrated!").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private String getGeckoLibVersion() {
        // Try to get GeckoLib version at runtime
        try {
            Package geckoPackage = Class.forName("software.bernie.geckolib.GeckoLib").getPackage();
            String version = geckoPackage.getImplementationVersion();
            return version != null ? version : "4.7.5.1";
        } catch (ClassNotFoundException e) {
            return "Not Found";
        }
    }
}