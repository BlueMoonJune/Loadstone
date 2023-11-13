package bluemoonjune.loadstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.UUID;

public class LoadstoneBlockEntity extends BlockEntity {

    public static final int RANGE = 5;


    private UUID owner = null;

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        if (!owner.equals(this.owner)) {
            this.owner = owner;
            markDirty();
        }
    }

    private boolean isInit = false;

    private boolean isLoading = false;

    public boolean getIsLoading() {
        return isLoading;
    }

    private void setIsLoading(boolean value) {
        isLoading = value;
        BlockState newState = Loadstone.LOADSTONE.setActive(world, pos, value);
        markDirty(world, pos, newState);
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), newState, Block.NOTIFY_ALL);
    }



    public LoadstoneBlockEntity(BlockPos pos, BlockState state) {
        super(Loadstone.LOADSTONE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putUuid("Owner", owner);
        nbt.putBoolean("IsLoading", isLoading);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        owner = nbt.getUuid("Owner");
        isLoading = nbt.getBoolean("IsLoading");
    }

    public void setAreaForced(boolean state) {
        setIsLoading(state);
        for (int zo = -RANGE; zo <= RANGE; zo++) {
            for (int xo = -RANGE; xo <= RANGE; xo++) {
                ChunkPos cpos = new ChunkPos((pos.getX() >> 4) + xo, (pos.getZ() >> 4) + zo);
                world.getChunkManager().setChunkForced(cpos, state);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, LoadstoneBlockEntity be) {
        if (!be.isInit) {
            be.setAreaForced(be.isLoading);
            be.isInit = true;
        }
        if (world.isClient()) return;;
        if (be.owner == null) return;
        boolean ownerPresent;
        MinecraftServer server = world.getServer();
        if (server != null) {
            ownerPresent = server.getPlayerManager().getPlayer(be.owner) != null;
            if (be.isLoading == ownerPresent) return;
            be.setAreaForced(ownerPresent);
        }
    }
}
