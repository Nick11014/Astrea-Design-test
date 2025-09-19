package com.astrea.astreadesigntest.client.renderer;

import com.astrea.astreadesigntest.AstreaDesigntest;
import com.astrea.astreadesigntest.items.LinkingToolItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LinkingToolItemRenderer extends GeoItemRenderer<LinkingToolItem> {

    public LinkingToolItemRenderer() {
        super(new LinkingToolModel());
    }

    public static class LinkingToolModel extends DefaultedItemGeoModel<LinkingToolItem> {
        public LinkingToolModel() {
            super(ResourceLocation.fromNamespaceAndPath(AstreaDesigntest.MODID, "linking_tool"));
        }
    }
}