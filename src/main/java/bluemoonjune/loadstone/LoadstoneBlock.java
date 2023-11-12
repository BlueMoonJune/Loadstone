package bluemoonjune.loadstone;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LoadstoneBlock extends BlockWithEntity implements BlockEntityProvider {

    private static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public LoadstoneBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ACTIVE, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LoadstoneBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        LoadstoneBlockEntity entity = world.getBlockEntity(pos, Loadstone.LOADSTONE_BLOCK_ENTITY).get();
        entity.setOwner(placer.getUuid());
        //world.setBlockState(pos, state.with(ACTIVE, true));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Items.LODESTONE);
    }

    public boolean getActive(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.isOf(this)) return false;
        return world.getBlockState(pos).get(ACTIVE);
    }

    public void setActive(World world, BlockPos pos, boolean active) {
        world.setBlockState(pos, world.getBlockState(pos).with(ACTIVE, active));
        world.playSound(null, pos, SoundEvent.of(Identifier.tryParse(active ? "minecraft:block.respawn_anchor.charge" : "minecraft:block.respawn_anchor.deplete")), SoundCategory.BLOCKS, 1, 1);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Loadstone.LOADSTONE_BLOCK_ENTITY, (world1, pos, state1, be) -> LoadstoneBlockEntity.tick(world1, pos, state1, be));
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        LoadstoneBlockEntity entity = world.getBlockEntity(pos, Loadstone.LOADSTONE_BLOCK_ENTITY).get();
        entity.setAreaForced(false);
        super.onBreak(world, pos, state, player);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
}
