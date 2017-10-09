package com.fireboss.levelhearts.Handlers;

import java.util.concurrent.ConcurrentHashMap;

import com.fireboss.levelhearts.LevelHearts;
import com.fireboss.levelhearts.PlayerStats;
import com.fireboss.levelhearts.Reference;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerHandler {

	public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<String, PlayerStats>();

	public static void saveHealthChange(EntityPlayer player, PlayerStats stats) {
		NBTTagCompound lht = NBTHandler.getTag(player.getEntityData(), true);
		PlayerHandler.updateModifier(player, 0);
		lht.setInteger("previousExperienceLevel", player.experienceLevel);
		playerStats.put(PlayerHandler.getPlayerUUID(player), stats);
	}

	public static void loadPlayerData(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		if (!tags.hasKey(Reference.NBT_NAME)) {
			NBTHandler.createNewTag(tags);
			PlayerHandler.setupNewPlayer(player, tags, stats);
		}
		NBTTagCompound lht = NBTHandler.getTag(tags, false);
		NBTTagCompound lhtc = NBTHandler.getTag(tags, true);
		LevelHearts.debug(lht.toString());
		LevelHearts.debug(lhtc.toString());
		stats.startingHealth = lht.getInteger("startingHealth");
		stats.currentLevelPosition = lht.getInteger("currentLevelPosition");
		stats.previousExperienceLevel = lht.getInteger("previousExperienceLevel");
		stats.healthModifier = lht.getDouble("healthModifier");
		stats.loggedOutHealth = lht.getFloat("loggedOutHealth");
		lht.removeTag("loggedOutHealth");
		LevelHearts.debug("Logged out health on load: " + lht.getFloat("loggedOutHealth"));
		LevelHearts.debug("Logged out health on load (stats): " + stats.loggedOutHealth);
	}

	public static void setupNewPlayer(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		stats.startingHealth = 20;
		stats.currentLevelPosition = 0;
		stats.previousExperienceLevel = player.experienceLevel;
		PlayerHandler.updateModifier(player,
				stats.startingHealth - SharedMonsterAttributes.MAX_HEALTH.getDefaultValue());
		PlayerHandler.savePlayerData(player, false);
		PlayerHandler.setupPlayerHealth(player, tags, stats);
	}

	public static void setupPlayerHealth(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		NBTTagCompound lht = NBTHandler.getTag(tags, false);
		player.setHealth(player.getMaxHealth());
		double healthModifier = calculateDefaultHealth(player, stats)
				- SharedMonsterAttributes.MAX_HEALTH.getDefaultValue();
		addOrReloadHealthModifier(player, healthModifier);
		lht.setDouble("healthModifier", healthModifier);
	}

	public static void addOrReloadHealthModifier(EntityPlayer player, double healthModifier) {
		AttributeModifier attrMod = new AttributeModifier(Reference.NBT_UUID, "levelHearts.healthModifier",
				healthModifier, 0);
		IAttributeInstance attrInt = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
		attrInt.removeModifier(attrMod);
		attrInt.applyModifier(attrMod);
		LevelHearts.debug("Added or reloaded health modifier.");
	}

	public static int calculateDefaultHealth(EntityPlayer player, PlayerStats stats) {
		int rpg = 0;
		return stats.startingHealth + rpg;
	}

	public static void savePlayerData(EntityPlayer player, boolean loggedOut) {
		PlayerStats stats = PlayerStats.getPlayerStats(PlayerHandler.getPlayerUUID(player));
		if (stats == null) {
			return;
		}
		NBTTagCompound lht = NBTHandler.getTag(player.getEntityData(), false);
		lht.setInteger("startingHealth", stats.startingHealth);
		lht.setInteger("currentLevelPosition", stats.currentLevelPosition);
		lht.setInteger("previousExperienceLevel", stats.previousExperienceLevel);
		lht.setDouble("healthModifier", stats.healthModifier);
		if (!loggedOut) {
			return;
		}
		LevelHearts.debug("Logged out health when saving: " + player.getHealth());
		lht.setFloat("loggedOutHealth", player.getHealth());
		PlayerHandler.playerStats.remove(PlayerHandler.getPlayerUUID(player));
	}

	public static double updateModifier(EntityPlayer player, double amountChanged) {
		PlayerStats stats = PlayerStats.getPlayerStats(PlayerHandler.getPlayerUUID(player));
		double newModifier = 0;
		try {
			newModifier = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getModifier(Reference.NBT_UUID)
					.getAmount() + amountChanged;
			stats.healthModifier = newModifier;
		} catch (NullPointerException e) {
			// Ignore
		} catch (Exception e) {
			LevelHearts.debugErr(e);
		}
		return newModifier;
	}

	public static String getPlayerUUID(EntityPlayer player) {
		return player.getUUID(player.getGameProfile()).toString();
	}

}
