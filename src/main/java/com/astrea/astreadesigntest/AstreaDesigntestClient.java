package com.astrea.astreadesigntest;

import com.astrea.astreadesigntest.client.renderer.ControllerBlockRenderer;
import com.astrea.astreadesigntest.client.renderer.LedTestBlockRenderer;
import net.minecraft.client.Minecraft;
import software.bernie.geckolib.loading.math.MolangQueries;
import com.astrea.astreadesigntest.blockentity.LedTestBlockEntity;
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
        BlockEntityRenderers.register(AstreaDesigntest.LED_TEST_BLOCK_ENTITY.get(), LedTestBlockRenderer::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Algum código de setup do cliente
        AstreaDesigntest.LOGGER.info("HELLO FROM CLIENT SETUP");
        AstreaDesigntest.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        
        // Registrar renderizadores
        registerRenderers();

        // Here we register our custom Molang query
        // The query name should be unique, ideally prefixed with your modid
        String queryName = "query.astreadesigntest.charge_percentage";

        // Use MolangQueries.setActorVariable
        // 1. Specify our Animatable type: <LedTestBlockEntity>
        // 2. Provide the query name
        // 3. Give a lambda that, given an actor wrapper, returns the desired value.
        //    The actor is a wrapper that contains our animatable.
        MolangQueries.<LedTestBlockEntity>setActorVariable(queryName, actor -> actor.animatable().getChargePercentage());
        AstreaDesigntest.LOGGER.info("Registered Molang query '{}' successfully", queryName);
    }
}
