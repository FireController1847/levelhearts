package com.fireboss.heartlevels.handlers;

import java.util.Arrays;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.PlayerStats;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

public class PlayerHandlerHelper {

	/**
	 * Update a user's health.
	 * 
	 * @param player
	 * @param stats
	 * @param tags
	 */
	public static void updateHealth(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
		NBTTagCompound hlt = (NBTTagCompound) tags.getTag("HeartLevels 1"); // Heart Levels Tag

		// Deals with cases of an existing user changing the config.
		if (stats.start != Config.startHearts.getInt()) {
			stats.start = Config.startHearts.getInt();
		}

		// When users start up the game, LevelArray gets reset (it's hard to save this
		// variable for now),
		// but nothing should really change.
		if (!Arrays.equals(stats.LevelArray, HeartLevels.LevelRampInt)) {
			stats.LevelArray = HeartLevels.LevelRampInt;
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
		double newMax = PlayerHandlerHelper.calcDefaultHearts(player, stats);

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
	
	/**
	 * Gets the default beginning hearts.
	 * @param player
	 * @param stats
	 * @return
	 */
	public static double calcDefaultHearts(EntityPlayer player, PlayerStats stats) {
		int rpgHealth = 0;
		int maxHearts = Config.maxHearts.getInt();
		int[] levelRamp = HeartLevels.LevelRampInt;
		for (int i = 0; i < levelRamp.length; i++) {
			if (player.experienceLevel >= levelRamp[i]) {
				rpgHealth += 2;
			} else {
				break;
			}
		}
		if (maxHearts != -1 && maxHearts != 0 && rpgHealth > maxHearts) { // OLD: maxHearts * 2
			rpgHealth = maxHearts; // OLD: maxHearts * 2
		}
		int extraHearts = 0;
		for (int i = 0; i < stats.oldArmourSet.length; i++) {
			extraHearts += EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), stats.oldArmourSet[i]);
		}
		double armorHealth = extraHearts; // OLD: extraHearts * 2
		double heartContainerHealth = stats.heartContainers; // OLD: heartContainers * 2
		return stats.start + rpgHealth + armorHealth + heartContainerHealth; // OLD: start * 2
	}
	
	/**
	 * Get the default beginning hearts WITHOUT including Heart Containers.
	 * @param player
	 * @param stats
	 * @return
	 */
	public static double calcDefaultHeartsNoHC(EntityPlayer player, PlayerStats stats) {
		double health = calcDefaultHearts(player, stats);
		health -= stats.heartContainers; // OLD: heartContainers * 2
		return health;
	}

	/**
	 * Setup a player for the first time
	 * 
	 * @param player
	 * @param tags
	 * @param stats
	 */
	static void setupFirstTime(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		if (Config.debug.getBoolean()) {
			HeartLevels.logger.info("Player not found!");
			HeartLevels.logger.info("Initiating a new player...");
		}
		stats.start = Config.startHearts.getInt();
		stats.LevelArray = HeartLevels.LevelRampInt;

		// 20 is default MC MaxHealth
		double healthModifier = stats.start - 20; // OLD: start * 2 -20

		// If config has starting hearts = 10, the healthModifier will be 0.
		PlayerHandler.addHealthModifier(player, healthModifier);
		stats.count = 0;
		stats.previousLevel = player.experienceLevel;
		try {
			stats.healthmod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.HeartLevelsID).getAmount();
		} catch (Exception e) {
		}

		// Saving is a bit different for a new player
		NBTTagCompound hlt = (NBTTagCompound) tags.getTag("HeartLevels 1");
		hlt.setInteger("start", stats.start);
		if (Config.rpgMode.getBoolean()) {
			hlt.setIntArray("LevelArray", stats.LevelArray);
		} else {
			hlt.setIntArray("LevelArray", new int[] { -1 });
		}
		hlt.setInteger("count", stats.count);
		hlt.setInteger("previousLevel", stats.previousLevel);
		hlt.setDouble("healthModifier", stats.healthmod);
		hlt.setInteger("heartContainers", stats.heartContainers);
		updateHealth(player, stats, tags);
		if (Config.debug.getBoolean()) {
			HeartLevels.logger.info("New player initated successfully!");
		}
	}

	/**
	 * Update player data?
	 * 
	 * @param player
	 */
	static void updatePlayerData(EntityPlayer player) {
		PlayerStats stats = PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
		double healthModifier = stats.healthmod;
		PlayerHandler.addHealthModifier(player, healthModifier);
	}

	/**
	 * Updates the mod's data in NBT
	 * 
	 * @param player
	 * @param loggedOut
	 */
	public static void savePlayerData(EntityPlayer player, boolean loggedOut) {
		if (Config.debug.getBoolean()) {
			HeartLevels.logger.info("Saving player data...");
		}
		PlayerStats stats = PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
		if (stats == null) {
			return;
		}
		// Fixes issues in Singleplayer, as mc.thePlayer isn't actually the
		// EntityPlayer!
		EntityPlayer realPlayer = stats.player;
		// This has the same name, but not same NBTTagCompound.
		NBTTagCompound entityPlayerTag;
		if (realPlayer != null) {
			entityPlayerTag = realPlayer.getEntityData();
		} else {
			entityPlayerTag = player.getEntityData();
		}
		NBTTagCompound hlt = (NBTTagCompound) entityPlayerTag.getTag("HeartLevels 1");
		hlt.setInteger("start", stats.start);
		if (Config.rpgMode.getBoolean()) {
			hlt.setIntArray("LevelArray", stats.LevelArray);
		} else {
			hlt.setIntArray("LevelArray", new int[] { -1 });
		}
		hlt.setInteger("count", stats.count);
		hlt.setInteger("previousLevel", stats.previousLevel);
		hlt.setDouble("healthModifier", stats.healthmod);
		if (!loggedOut) {
			return;
		}
		// Remove health bonus from armour. On log in, stats.oldArmorSet will be an
		// array of nulls even if armour is equipped. Thus, there will be extra hearts
		// added (an extra set based on armour hearts)
		double currentMaxHealthMod = 0;
		try {
			currentMaxHealthMod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.HeartLevelsID).getAmount();
		} catch (Exception e) {
		}
		for (int i = 0; i < 4; i++) {
			ItemStack currArmor = stats.oldArmourSet[i];
			int extraHearts = EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), currArmor);
			if (extraHearts > 0) {
				int extraHealth = extraHearts; // OLD: extraHearts * 2
				currentMaxHealthMod -= extraHealth;
			}
		}
		hlt.setDouble("healthModifier", currentMaxHealthMod);
		hlt.setFloat("loggedOutHealth", player.getHealth());
		hlt.setInteger("heartContainers", stats.heartContainers);
		PlayerHandler.playerStats.remove(player.getUUID(player.getGameProfile()).toString());
		if (Config.debug.getBoolean()) {
			HeartLevels.logger.info("Player data saved successfully!");
		}
	}

	/**
	 * Loads a player's data
	 * 
	 * @param player
	 * @param tags
	 * @param stats
	 */
	public static void loadPlayerData(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		if (Config.debug.getBoolean()) {
			HeartLevels.logger.info("Loading player data...");
		}
		// On a new user
		if (!tags.hasKey("HeartLevels 1")) {
			tags.setTag("HeartLevels 1", new NBTTagCompound());
			NBTTagCompound temp = (NBTTagCompound) tags.getTag("MoreHealth 1");

			// Setup for a new player
			setupFirstTime(player, tags, stats);
		}

		// Tags can only be stored with settag, and are stored as NBTBase. It must be
		// cast before using.
		NBTTagCompound hlt = (NBTTagCompound) tags.getTag("HeartLevels 1");

		// Save start and level array.
		stats.start = hlt.getInteger("start");
		stats.LevelArray = hlt.getIntArray("LevelArray");

		stats.count = hlt.getInteger("count");
		stats.previousLevel = hlt.getInteger("previousLevel");
		stats.healthmod = hlt.getDouble("healthModifier");
		stats.loggedOutHealth = hlt.getFloat("loggedOutHealth");
		stats.heartContainers = hlt.getInteger("heartContainers");
		if (Config.debug.getBoolean()) {
			HeartLevels.logger.info("Player data loaded successfully!");
		}
	}

}
