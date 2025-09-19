package com.astrea.astreadesigntest.sounds;

import com.astrea.astreadesigntest.AstreaDesigntest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, AstreaDesigntest.MODID);

    // Exemplo de som customizado para o linking tool
    public static final DeferredHolder<SoundEvent, SoundEvent> LINKING_TOOL_ACTIVATE = SOUNDS.register("linking_tool_activate",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AstreaDesigntest.MODID, "linking_tool_activate")));
}