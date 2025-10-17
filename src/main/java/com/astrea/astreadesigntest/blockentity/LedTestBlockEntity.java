package com.astrea.astreadesigntest.blockentity;

import com.astrea.astreadesigntest.AstreaDesigntest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;

/**
 * BlockEntity used by the LedTestBlock.
 *
 * <p>This class stores a simple integer value representing the LED "charge" as a percentage
 * (0..100). The value is persisted to NBT, synchronized to clients via the standard
 * BlockEntity data packet, and exposed to Geckolib animation controllers so the model
 * animation can react to the charge level.</p>
 *
 * Key responsibilities:
 * - Hold and validate the charge percentage value.
 * - Persist and restore the value via NBT (saveAdditional/loadAdditional).
 * - Provide a network update packet so clients receive changes (getUpdatePacket/getUpdateTag).
 * - Register Geckolib animation controllers and start the model animation.
 */
public class LedTestBlockEntity extends BlockEntity implements GeoBlockEntity {
    /**
     * Charge stored as an integer percentage in range [0, 100].
     * Default is 100 (fully charged).
     */
    private int chargePercentage = 100;

    /**
     * GeckoLib requires an AnimatableInstanceCache per animatable instance to manage
     * animation state. We create and keep one here.
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * Construct a new BlockEntity instance.
     *
     * @param pos   block position
     * @param state current block state
     */
    public LedTestBlockEntity(BlockPos pos, BlockState state) {
        super(AstreaDesigntest.LED_TEST_BLOCK_ENTITY.get(), pos, state);
    }

    /**
     * Returns the current stored charge percentage.
     *
     * @return integer in range [0,100]
     */
    public int getChargePercentage() {
        return this.chargePercentage;
    }

    /**
     * Update the charge percentage value.
     * The value is clamped to the inclusive interval [0, 100].
     * We also log changes to help debugging and tooling.
     *
     * Note: calling this method does NOT automatically persist or sync the BlockEntity.
     * Callers should mark the block entity as changed and request a block update when
     * they want to save or broadcast the change.
     *
     * @param value requested new percentage (may be outside [0,100])
     */
    public void setChargePercentage(int value) {
        int newVal = Math.max(0, Math.min(100, value));
        AstreaDesigntest.LOGGER.info("[LedTestBlockEntity] setChargePercentage: {} -> {} at {}", this.chargePercentage, newVal, this.worldPosition);
        this.chargePercentage = newVal;
    }

    /**
     * Register Geckolib animation controllers for this animatable.
     *
     * <p>The controller created here uses {@link #animationPredicate(AnimationState)} which
     * starts the model animation and can be extended to expose animation variables to
     * the animation system (depending on the Geckolib version).</p>
     *
     * @param controllers controller registrar provided by Geckolib
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::animationPredicate));
    }

    /**
     * Animation predicate used by Geckolib's {@link AnimationController}.
     *
     * <p>In this project we use the predicate to read the current charge and start
     * the animation loop named "animation.model.new" (this name must match the
     * file created by Blockbench in your animation json). Depending on the Geckolib
     * version you could also expose the value to Molang via controller variables; here
     * we simply log the value and rely on a query registered in the client setup
     * to expose the block entity value to Molang queries.</p>
     *
     * @param event the animation state event
     * @param <E>   animatable type (GeoBlockEntity)
     * @return PlayState.CONTINUE to keep the controller active
     */
    private <E extends GeoBlockEntity> PlayState animationPredicate(AnimationState<E> event) {
        // The animation predicate is invoked frequently on the render thread.
        // Avoid reading and logging the value here to prevent per-frame overhead
        // and log spam. The value is exposed to Molang via the client-side
        // Molang query registered in the client setup, so the animation JSON
        // can still read the BlockEntity's charge when rendering.

        // Start (or ensure) the named animation is running. The name must match
        // the animation defined in `led_test.animation.json` (Blockbench default
        // often uses "animation.model.new"). We loop it to allow continuous
        // sampling by the renderer / Molang query.
        event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.model.new"));

        return PlayState.CONTINUE;
    }

    /**
     * GeckoLib animatable cache getter.
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * Provide the packet used to sync BlockEntity data to clients. This method
     * uses the vanilla helper which sends the full NBT of the BlockEntity to
     * connected clients when requested by the client.
     */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * Returns the NBT tag that will be sent to clients when they request
     * the block entity sync. This delegates to the vanilla helper to avoid
     * duplicating serialization logic.
     */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    /**
     * Save the block entity data to NBT. Called when the world is saved.
     *
     * @param tag        tag to write to
     * @param registries provider used by the base save logic
     */
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("chargePercentage", this.chargePercentage);
        AstreaDesigntest.LOGGER.debug("[LedTestBlockEntity] saveAdditional at {} charge={}", this.worldPosition, this.chargePercentage);
    }

    /**
     * Load block entity data from NBT. Called when the chunk is read from disk
     * or when the client receives an update packet.
     *
     * @param tag        tag to read from
     * @param registries provider used by the base load logic
     */
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("chargePercentage")) {
            this.chargePercentage = tag.getInt("chargePercentage");
            AstreaDesigntest.LOGGER.debug("[LedTestBlockEntity] loadAdditional at {} charge={}", this.worldPosition, this.chargePercentage);
        }
    }
}
