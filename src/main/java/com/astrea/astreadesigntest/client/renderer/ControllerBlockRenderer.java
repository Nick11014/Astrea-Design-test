package com.astrea.astreadesigntest.client.renderer;

import com.astrea.astreadesigntest.AstreaDesigntest;
import com.astrea.astreadesigntest.blockentity.ControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ControllerBlockRenderer extends GeoBlockRenderer<ControllerBlockEntity> {

    public ControllerBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new ControllerModel());
    }

    public static class ControllerModel extends DefaultedBlockGeoModel<ControllerBlockEntity> {
        public ControllerModel() {
            super(ResourceLocation.fromNamespaceAndPath(AstreaDesigntest.MODID, "controller_block"));
        }
    }

    // A assinatura do método foi corrigida para incluir o parâmetro "packedOverlay"
    @Override
    public void render(ControllerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();

        if (level != null) {
            // Obter a iluminação do mundo na posição do bloco
            packedLight = LevelRenderer.getLightColor(level, pos);
        }

        // Chamar o método de renderização original com os parâmetros corretos
        super.render(blockEntity, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}