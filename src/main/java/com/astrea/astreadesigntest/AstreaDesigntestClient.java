package com.astrea.astreadesigntest;

import com.astrea.astreadesigntest.client.renderer.ControllerBlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// Esta classe não será carregada em servidores dedicados. Acessar código do lado do cliente a partir daqui é seguro.
@Mod(value = AstreaDesigntest.MODID, dist = Dist.CLIENT)
// Você pode usar EventBusSubscriber para registrar automaticamente todos os métodos estáticos na classe anotada com @SubscribeEvent
@EventBusSubscriber(modid = AstreaDesigntest.MODID, value = Dist.CLIENT)
public class AstreaDesigntestClient {
    public AstreaDesigntestClient(ModContainer container) {
        // Permite que o NeoForge crie uma tela de configuração para as configs deste mod.
        // A tela de configuração é acessada indo para a tela de Mods > clicando no seu mod > clicando em config.
        // Não se esqueça de adicionar as traduções para suas opções de configuração no arquivo en_us.json.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private static void registerRenderers() {
        AstreaDesigntest.LOGGER.info("Registering Block Entity Renderers for " + AstreaDesigntest.MODID);
        
        BlockEntityRenderers.register(AstreaDesigntest.CONTROLLER_BLOCK_ENTITY.get(), ControllerBlockRenderer::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Algum código de setup do cliente
        AstreaDesigntest.LOGGER.info("HELLO FROM CLIENT SETUP");
        AstreaDesigntest.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        
        // Registrar renderizadores
        registerRenderers();
    }
}
