package com.fireboss.heartlevels.handlers;

import java.util.Arrays;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.PlayerStats;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class PlayerHandlerHelper {

	private static void updateHealth(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
		NBTTagCompound hlt = (NBTTagCompound) tags.getTag("HeartLevels 1"); // Heart Levels Tag

		// Deals with cases of an existing user changing the config.
		if (stats.start != Config.startHearts.getInt()) {
			stats.start = Config.startHearts.getInt();
		}

		// When users start up the game, LevelArray gets reset (it's hard to save this
		// variable for now),
		// but nothing should really change.
		if (!Arrays.equals(stats.LevelArray, Config.levelRamp.getIntList())) {
			stats.LevelArray = Config.levelRamp.getIntList();
			player.addChatComponentMessage(new ChatComponentText("Level Ramp successfully changed!"));
			hlt.setIntArray("LevelArray", stats.LevelArray);
		}

		// Set Player Health
		player.setHealth(player.getMaxHealth());

		// If not in RPG mode
		if (!Config.rpgMode.getBoolean()) {
			player.addChatComponentMessage(new ChatComponentText("Heart Container mode enabled!"));
			return;
		}

		// NewMax calcualtes the player's starting health plus extra health from the XP
		// system.
		double newMax = PlayerHandlerHelper.calculateTotalHeartLevelsContrib(player, stats);

		// The mod's modifier should be based on the default of Minecraft (20) and not
		// accounting other mods.
		double healthModifier = newMax - 20;

		PlayerHandler.addHealthModifier(player, healthModifier);
		player.setHealth(player.getMaxHealth());

		// If we updated the player's health
		if (stats.count > 0) {
			player.addChatComponentMessage(new ChatComponentText(Config.onNewHeart.getString()));
		}

		// Enhanced Mode / RPG mode
		boolean rpgMode = Config.rpgMode.getBoolean();
		boolean heartItems = Config.heartItems.getBoolean();
		if (rpgMode == true && heartItems == true) {
			player.addChatComponentMessage(new ChatComponentText("Enhanced mode activated! (RPG + Heart Containers)"));
		} else if (rpgMode == true) {
			player.addChatComponentMessage(new ChatComponentText("RPG mode enabled!"));
		}
		hlt.setInteger("count", stats.count);

		try {

		} catch (Exception e) {
		}
		hlt.setDouble("healthModifier", stats.healthmod);
	}

	/*
	 * Calculated the max health. Example: Let's start at 3 hearts. 6 + 2 + 2 + 2 =
	 * 12; Default Health = 20; Modifier = 12-20 = -8; If the mod wants 22 max
	 * health. If the default is 20, and another mod gave 2 health, it shouldn't be
	 * doing 22-22, it should be doing 22-20.
	 */
	public static double calculateTotalHeartLevelsContrib(EntityPlayer player, PlayerStats stats) {
		int rpgHealth = 0;
		int maxHearts = Config.maxHearts.getInt();
		int[] levelRamp = Config.levelRamp.getIntList();
		for (int i = 0; i < levelRamp.length; i++) {
			if (player.experienceLevel >= levelRamp[i]) {
				rpgHealth += 2;
			} else {
				break;
			}
		}
		if (maxHearts != -1 && maxHearts != 0) {
			if (rpgHealth > maxHearts * 2) {
				rpgHealth = maxHearts * 2;
			}
		}
		int extraHearts = 0;
		for (int i = 0; i < stats.oldArmourSet.length; i++) {
			extraHearts += EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), stats.oldArmourSet[i]);
		}
		double armorHealth = extraHearts * 2;
		double heartContainerHealth = stats.heartContainers * 2;
		return stats.start * 2 + rpgHealth + armorHealth + heartContainerHealth;
	}

	public static void loadPlayerData(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		if (!tags.hasKey("HeartLevels 1")) {
			// THIS IS WHERE I LEFT OFF!!!!!
		}
	}

}
