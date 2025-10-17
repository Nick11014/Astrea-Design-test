package com.astrea.astreadesigntest.blocks;

import com.astrea.astreadesigntest.blockentity.LedTestBlockEntity;
import net.minecraft.core.BlockPos;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import com.astrea.astreadesigntest.AstreaDesigntest;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LedTestBlock extends BaseEntityBlock {
    public static final MapCodec<LedTestBlock> CODEC = simpleCodec(LedTestBlock::new);

    public LedTestBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // If the block at the clicked position has the expected BlockEntity, operate on it.
        if (level.getBlockEntity(pos) instanceof LedTestBlockEntity ledEntity) {
            // Log the click for debugging. `level.isClientSide()` differentiates
            // where the click handler is executing (client vs server).
            AstreaDesigntest.LOGGER.info("[LedTestBlock] Right-click at {} by {} (client={}) currentCharge={}", pos, player.getName().getString(), level.isClientSide(), ledEntity.getChargePercentage());

            // Only modify world state on the server side. The client call is used
            // only for visual feedback / prediction in most cases.
            if (!level.isClientSide()) {
                // Increase the stored charge by 10 percentage points and wrap around
                // so values cycle through 0..100. The arithmetic uses modulus to
                // temporarily compute a value in 0..109 then clamps >100 to 0.
                int newCharge = (ledEntity.getChargePercentage() + 10) % 110;
                if (newCharge > 100) newCharge = 0;

                // Update the BlockEntity value. Note: setChargePercentage only
                // sets the field locally; we must mark the entity as changed and
                // notify the chunk/world so the change is saved and sent to clients.
                ledEntity.setChargePercentage(newCharge);
                level.sendBlockUpdated(pos, state, state, 3); // notify clients and rerender
                ledEntity.setChanged(); // mark chunk dirty for save

                AstreaDesigntest.LOGGER.info("[LedTestBlock] Updated charge at {} to {}", pos, ledEntity.getChargePercentage());
            }

            // Return sided success: the side that handled the use (client or server)
            // will receive the success result. This helps avoid duplicate handling.
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            AstreaDesigntest.LOGGER.warn("[LedTestBlock] Right-clicked but no LedTestBlockEntity found at {}", pos);
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Placeholder caso queira agendar ticks futuramente
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LedTestBlockEntity(pos, state);
    }
}
