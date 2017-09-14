package com.fireboss.heartlevelsrewrite.handlers;

import java.util.concurrent.ConcurrentHashMap;

import com.fireboss.heartlevelsrewrite.Config;
import com.fireboss.heartlevelsrewrite.HeartLevels;
import com.fireboss.heartlevelsrewrite.Reference;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerHandler {

	public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<String, PlayerStats>();

	public static void addOrReloadHealthModifier(EntityPlayer player, double modifier) {
		HeartLevels.health_modifier = new AttributeModifier(Reference.HEART_LEVELS_UUID, "heartLevels.healthModifier",
				modifier, 0);
		IAttributeInstance attrInt = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		attrInt.removeModifier(HeartLevels.health_modifier);
		attrInt.applyModifier(HeartLevels.health_modifier);
		HeartLevels.debug("Added or reloaded health modifier.");
	}

	public static double calcDefHealth(EntityPlayer player, PlayerStats stats) {
		// Level Ramp
		int rpg = 0;
		int[] levelRamp = Config.levelRamp.getIntList();
		for (int i = 0; i < levelRamp.length; i++) {
			if (player.experienceLevel >= levelRamp[i]) {
				rpg += 2;
			} else {
				break;
			}
		}

		// Max Check
		int max = Config.maxHealth.getInt();
		if (max > 0 && rpg > max) {
			rpg = max;
		}

		// Heart Containers
		double containers = stats.heartContainers * 2;

		return stats.start + rpg + containers;
	}

	public static void updatePlayerHealth(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		NBTTagCompound hlt = NBTHandler.getTag(tags, false);
		if (stats.start != Config.startHealth.getInt()) {
			stats.start = Config.startHealth.getInt();
		}
		player.setHealth(player.getMaxHealth());
		double modifier = calcDefHealth(player, stats) - SharedMonsterAttributes.maxHealth.getDefaultValue();
		addOrReloadHealthModifier(player, modifier);
		hlt.setDouble("modifier", modifier);
	}

	public static void setupNewPlayer(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		stats.start = Config.startHealth.getInt();
		stats.currentLrPos = 0;
		stats.prevLevel = player.experienceLevel;
		updateModifier(player, stats.start - SharedMonsterAttributes.maxHealth.getDefaultValue());
		savePlayerData(player, false);
		updatePlayerHealth(player, tags, stats);
	}

	public static void loadPlayerData(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		if (!tags.hasKey(Reference.NBT_TAG)) {
			NBTHandler.createNewTag(tags);
			setupNewPlayer(player, tags, stats);
		}
		NBTTagCompound hlt = NBTHandler.getTag(tags, false);
		stats.start = hlt.getInteger("start");
		stats.currentLrPos = hlt.getInteger("currentLrPos");
		stats.prevLevel = hlt.getInteger("prevLevel");
		stats.modifier = hlt.getDouble("modifier");
		stats.heartContainers = hlt.getInteger("heartContainers");
	}

	public static void savePlayerData(EntityPlayer player, boolean loggedOut) {
		PlayerStats stats = getPlayerStats(player);
		if (stats == null)
			return;
		NBTTagCompound hlt = NBTHandler.getTag(player.getEntityData(), false);
		hlt.setInteger("start", stats.start);
		hlt.setInteger("currentLrPos", stats.currentLrPos);
		hlt.setInteger("prevLevel", stats.prevLevel);
		hlt.setDouble("modifier", stats.modifier);
		hlt.setInteger("heartContainers", stats.heartContainers);
		if (!loggedOut)
			return;
	}

	public static void saveHeartChange(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
		NBTTagCompound hlt = NBTHandler.getTag(tags, true);
		updateModifier(player, 0);
		hlt.setInteger("prevLevel", stats.prevLevel);
		playerStats.put(player.getUUID(player.getGameProfile()).toString(), stats);
	}

	public static double updateModifier(EntityPlayer player, double amountChanged) {
		PlayerStats stats = getPlayerStats(player);
		double newModifier = 0;
		try {
			newModifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(Reference.HEART_LEVELS_UUID).getAmount() + amountChanged;
			stats.modifier = newModifier;
		} catch (Exception e) {
			HeartLevels.debugErr(e);
		}
		return newModifier;
	}

	public static PlayerStats getPlayerStats(EntityPlayer player) {
		return PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
	}

	public static boolean hasLevelChanged(EntityPlayer player, PlayerStats stats) {
		boolean hlc = false;
		if (stats.prevLevel != player.experienceLevel) {
			stats.prevLevel = player.experienceLevel;
			hlc = true;
		}
		return hlc;
	}

	public static void calcHealthChange(EntityPlayer player, PlayerStats stats) {
		int max = Config.maxHealth.getInt();
		if (hasLevelChanged(player, stats)) {
			HeartLevels.debug("User's level has changed.");
			int[] level_ramp = Config.levelRamp.getIntList();
			if (Config.hardcoreMode.getBoolean()) {
				System.out.println("Pos below length: " + (stats.currentLrPos < level_ramp.length - 1));
				System.out.println("Pos > 0: " + (stats.currentLrPos > 0));
				System.out.println("XP < current val: " + (player.experienceLevel < level_ramp[stats.currentLrPos]));
				while (stats.currentLrPos < level_ramp.length && stats.currentLrPos > 0
						&& player.experienceLevel < level_ramp[stats.currentLrPos]) {
					HeartLevels.debug("The player now has too many hearts. Decreasing player health...");
					double newModifier = updateModifier(player, -2);
					if (!player.worldObj.isRemote) {
						PlayerHandler.addOrReloadHealthModifier(player, newModifier);
					}
					stats.currentLrPos--;
					player.setHealth(player.getMaxHealth());
					HeartLevels.debug("Player's health has decreased.");
				}
			}
			if (max > 0 && player.getMaxHealth() >= max)
				return;
			while (stats.currentLrPos < level_ramp.length
					&& player.experienceLevel >= level_ramp[stats.currentLrPos]) {
				HeartLevels.debug("XP matches next level ramp. Increasing player health...");
				double newModifier = updateModifier(player, 2);
				if (!player.worldObj.isRemote) {
					PlayerHandler.addOrReloadHealthModifier(player, newModifier);
				}
				stats.currentLrPos++;
				player.setHealth(player.getMaxHealth());
				HeartLevels.debug("Player's health has increased.");
			}
		}
	}

}
