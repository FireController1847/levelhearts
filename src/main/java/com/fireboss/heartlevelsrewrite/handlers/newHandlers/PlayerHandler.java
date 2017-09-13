package com.fireboss.heartlevelsrewrite.handlers.newHandlers;

import java.util.concurrent.ConcurrentHashMap;

import com.fireboss.heartlevelsrewrite.Config;
import com.fireboss.heartlevelsrewrite.HeartLevels;
import com.fireboss.heartlevelsrewrite.Reference;
import com.fireboss.heartlevelsrewrite.handlers.PlayerStats;

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
		int rpg = 0;

		// Max Check
		int max = Config.maxHealth.getInt();
		if (max > 0 && rpg > max) {
			rpg = max;
		}

		return stats.start + rpg;
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
		stats.prevLevel = player.experienceLevel;
		updateModifier(player, stats.start - SharedMonsterAttributes.maxHealth.getDefaultValue(), false);
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
		stats.modifier = hlt.getDouble("modifier");
	}

	public static void savePlayerData(EntityPlayer player, boolean loggedOut) {
		PlayerStats stats = getPlayerStats(player);
		if (stats == null)
			return;
		NBTTagCompound hlt = NBTHandler.getTag(player.getEntityData(), false);
		hlt.setInteger("start", stats.start);
		hlt.setInteger("prevLevel", stats.prevLevel);
		hlt.setDouble("modifier", stats.modifier);
		if (!loggedOut)
			return;
	}

	public static void saveHeartChange(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
		NBTTagCompound hlt = NBTHandler.getTag(tags, true);
		updateModifier(player, 0, false);
		hlt.setInteger("prevLevel", stats.prevLevel);
		playerStats.put(player.getUUID(player.getGameProfile()).toString(), stats);
	}

	public static void updateModifier(EntityPlayer player, double amountChanged, boolean clientSideUpdate) {
		PlayerStats stats = getPlayerStats(player);
		try {
			stats.modifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(Reference.HEART_LEVELS_UUID).getAmount() + amountChanged;
		} catch (Exception e) {
			HeartLevels.debugErr(e);
		}
		if (amountChanged != 0 || clientSideUpdate)
			stats.needsClientSideUpdate = true;
	}

	public static PlayerStats getPlayerStats(EntityPlayer player) {
		return PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
	}

}
