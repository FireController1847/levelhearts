package com.fireboss.heartlevels.init;

import com.fireboss.heartlevels.Config;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class InitChestLoot {

	private static final ChestGenHooks dungeon = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
	private static final ChestGenHooks desert = ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST);
	private static final ChestGenHooks jungle = ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST);
	private static final ChestGenHooks library = ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_LIBRARY);
	private static final ChestGenHooks corridor = ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CORRIDOR);
	private static final ChestGenHooks blacksmith = ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH);
	private static final ChestGenHooks crossing = ChestGenHooks.getInfo(ChestGenHooks.STRONGHOLD_CROSSING);
	private static final ChestGenHooks mineshaft = ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR);

	private static final ItemStack HeartContainer = new ItemStack(InitItems.heart_container);
	private static final ItemStack HeartPiece = new ItemStack(InitItems.heart_piece);

	private static final Double multiplier = Config.multiplier.getDouble();

	public static void AddChestLoot() {
		AddContainer(HeartContainer, dungeon, 1, 1, 6);
		AddContainer(HeartPiece, dungeon, 1, 3, 8);
		AddContainer(HeartContainer, desert, 2, 3, 9);
		AddContainer(HeartPiece, desert, 1, 1, 5);
		AddContainer(HeartContainer, jungle, 2, 3, 8);
		AddContainer(HeartPiece, jungle, 1, 1, 5);
		AddContainer(HeartPiece, library, 1, 2, 5);
		AddContainer(HeartContainer, corridor, 1, 1, 6);
		AddContainer(HeartPiece, corridor, 1, 3, 8);
		AddContainer(HeartContainer, blacksmith, 1, 1, 8);
		AddContainer(HeartPiece, crossing, 1, 2, 7);
		AddContainer(HeartPiece, mineshaft, 1, 2, 7);
	}

	private static void AddContainer(ItemStack item, ChestGenHooks theDungeon, int min, int max, int secondMultiplier) {
		theDungeon.addItem(new WeightedRandomChestContent(item, min, max, (int) (secondMultiplier * multiplier)));
	}

}
