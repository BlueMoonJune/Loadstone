package bluemoonjune.loadstone.mixin;

import bluemoonjune.loadstone.Loadstone;
import bluemoonjune.loadstone.LoadstoneBlockEntity;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class LoadstoneCreation {

    @Inject(at = @At("HEAD"), method = "onUse")
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        if (state.isOf(Blocks.LODESTONE) && player.getStackInHand(hand).isOf(Items.RECOVERY_COMPASS)) {
            world.setBlockState(pos, Loadstone.LOADSTONE.getDefaultState());
            LoadstoneBlockEntity entity = world.getBlockEntity(pos, Loadstone.LOADSTONE_BLOCK_ENTITY).get();
            entity.setOwner(player.getUuid());
            player.swingHand(hand);
            if (!player.isCreative())
                player.getStackInHand(hand).decrement(1);
            world.playSound(null, pos, SoundEvent.of(Identifier.tryParse("minecraft:block.respawn_anchor.set_spawn")), SoundCategory.BLOCKS, 1, 1);
        }
    }
}