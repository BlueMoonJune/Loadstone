package bluemoonjune.loadstone;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Loadstone implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("loadstone");
	public static final LoadstoneBlock LOADSTONE = new LoadstoneBlock(FabricBlockSettings.create().mapColor(MapColor.IRON_GRAY).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LODESTONE).pistonBehavior(PistonBehavior.BLOCK));
	public static final BlockEntityType<LoadstoneBlockEntity> LOADSTONE_BLOCK_ENTITY = Registry.register(
			Registries.BLOCK_ENTITY_TYPE,
			new Identifier("loadstone", "loadstone"),
			FabricBlockEntityTypeBuilder.create(LoadstoneBlockEntity::new, LOADSTONE).build()
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		Registry.register(Registries.BLOCK, new Identifier("loadstone", "loadstone"), LOADSTONE);
		Registry.register(Registries.ITEM, new Identifier("loadstone", "loadstone"), new BlockItem(LOADSTONE, new FabricItemSettings()));
	}
}