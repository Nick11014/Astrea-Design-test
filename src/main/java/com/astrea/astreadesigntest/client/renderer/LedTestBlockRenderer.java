package com.astrea.astreadesigntest.client.renderer;

import com.astrea.astreadesigntest.AstreaDesigntest;
import com.astrea.astreadesigntest.blockentity.LedTestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import com.astrea.astreadesigntest.AstreaDesigntest;

public class LedTestBlockRenderer extends GeoBlockRenderer<LedTestBlockEntity> {

    public LedTestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new LedTestModel());
    }

    public static class LedTestModel extends DefaultedBlockGeoModel<LedTestBlockEntity> {
        public LedTestModel() {
            super(ResourceLocation.fromNamespaceAndPath(AstreaDesigntest.MODID, "led_test"));
        }
    }

    @Override
    public void render(LedTestBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        // Renderer entry: intentionally silent in production to avoid log spam.
        // If you need to debug rendering once, temporarily re-enable the log above.

        // The level and position are used to determine the correct packed light
        // value for the renderer; when rendering in-world we query the level for
        // lighting at the block position to make the model respond to environment lighting.
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        // Avoid logging the charge each frame; Geckolib/Render thread is high-frequency
        // and repeated logging can flood the debug log. Use a breakpoint or a
        // temporary log if needed for debugging.

        if (level != null) {
            packedLight = LevelRenderer.getLightColor(level, pos);
        }

        // Delegate rendering to GeoBlockRenderer which will load the model,
        // run Geckolib animation controllers, and apply transforms. The
        // registered Molang query (registered in client setup) ensures the
        // animation JSON can read the BlockEntity's charge value.
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
