package com.astrea.astreadesigntest.blockentity;

import com.astrea.astreadesigntest.AstreaDesigntest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

public class ControllerBlockEntity extends BlockEntity implements GeoBlockEntity {
    
    // Enum para controlar o estado da animação de forma segura
    private enum AnimState { IDLE, ATTEMPT, SUCCESS }
    private AnimState animState = AnimState.IDLE;

    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation LINK_ATTEMPT_ANIM = RawAnimation.begin().thenPlayAndHold("controller_linkattempt_animation");
    private static final RawAnimation LINK_SUCCESS_ANIM = RawAnimation.begin().thenPlay("controller_linksucessfull_connected_animation");
    
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    public ControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(AstreaDesigntest.CONTROLLER_BLOCK_ENTITY.get(), pos, blockState);
    }

    // Lógica executada quando o bloco é clicado
    public void onRightClick() {
        if (level != null && !level.isClientSide()) {
            if (animState == AnimState.IDLE) {
                // Do estado IDLE vai para ATTEMPT
                animState = AnimState.ATTEMPT;
                level.players().forEach(player -> player.sendSystemMessage(Component.literal("§eAttempting Controller Link...")));
            } else if (animState == AnimState.ATTEMPT) {
                // Do estado ATTEMPT vai para SUCCESS
                animState = AnimState.SUCCESS;
                level.players().forEach(player -> player.sendSystemMessage(Component.literal("§aController Link Successful!")));
                // Agenda uma atualização no bloco para resetar para IDLE depois de 3 segundos
                level.scheduleTick(getBlockPos(), this.getBlockState().getBlock(), 60); // 60 ticks = 3 segundos
            }
            // Sincroniza o novo estado com o cliente para que a animação mude
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }
    
    // Chamado pelo tick agendado para voltar ao estado idle
    public void resetToIdle() {
        if (level != null && !level.isClientSide()) {
            animState = AnimState.IDLE;
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            setChanged();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationPredicate));
    }

    // O "coração" da animação. Decide qual animação tocar com base no estado.
    private <E extends GeoBlockEntity> PlayState animationPredicate(AnimationState<E> event) {
        switch (animState) {
            case ATTEMPT:
                event.getController().setAnimation(LINK_ATTEMPT_ANIM);
                break;
            case SUCCESS:
                event.getController().setAnimation(LINK_SUCCESS_ANIM);
                break;
            default: // IDLE
                event.getController().setAnimation(IDLE_ANIM);
                break;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    // --- Sincronização e Salvamento de Dados ---
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("animationState", this.animState.ordinal());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.animState = AnimState.values()[tag.getInt("animationState")];
    }
}

