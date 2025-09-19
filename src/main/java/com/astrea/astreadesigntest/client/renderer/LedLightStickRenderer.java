package com.astrea.astreadesigntest.client.renderer;

import com.astrea.astreadesigntest.AstreaDesigntest;
import com.astrea.astreadesigntest.items.LedLightStickItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LedLightStickRenderer extends GeoItemRenderer<LedLightStickItem> {

    public LedLightStickRenderer() {
        super(new LedLightStickModel());
    }

    public static class LedLightStickModel extends DefaultedItemGeoModel<LedLightStickItem>{
        public LedLightStickModel() {
            super(ResourceLocation.fromNamespaceAndPath(AstreaDesigntest.MODID, "led_light_stick"));
        }
    }
}