/**
 * Sayuki — Stellar Meteor block putter helper (bedrock-safe block placement)
 * Compat: Goety-2 and IronsSpellbooks — utility class, no conflict
 */
package com.xiaoxue.sayuki.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class StellarMeteorBlockPutter {
    public boolean put(LevelAccessor level, BlockPos pos, BlockState blk) {
        BlockState original = level.getBlockState(pos);
        if (original.getBlock() == Blocks.BEDROCK || original == blk) {
            return false;
        }
        level.setBlock(pos, blk, Block.UPDATE_ALL);
        return true;
    }
}
