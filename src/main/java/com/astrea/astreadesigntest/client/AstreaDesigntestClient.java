package com.astrea.astreadesigntest.client;

import com.astrea.astreadesigntest.AstreaDesigntest;
import com.astrea.astreadesigntest.client.renderer.ControllerBlockRenderer;

public class AstreaDesigntestClient {
    
    public static void registerRenderers() {
        AstreaDesigntest.LOGGER.info("Registering Block Entity Renderers for " + AstreaDesigntest.MODID);
        
        // This will be filled when the renderer is working
        // BlockEntityRendererFactories.register(AstreaDesigntest.CONTROLLER_BLOCK_ENTITY.get(), ControllerBlockRenderer::new);
    }
}