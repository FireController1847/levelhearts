package com.fireboss.heartlevelsrewrite.handlers;

import com.fireboss.heartlevelsrewrite.Config;
import com.fireboss.heartlevelsrewrite.HeartLevels;
import com.fireboss.heartlevelsrewrite.Reference;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerHandlerHelper {

	private static final String hlTagName = "HeartLevels";

	// Methods
	public static NBTTagCompound newHeartLevelsTag(NBTTagCompound tags) {
		NBTTagCompound hl = new NBTTagCompound();
		hl.setString("version", Reference.VERSION); // Mod Version
		tags.setTag(hlTagName, hl);
		HeartLevels.logger.info("New " + hlTagName + " Tag!");
		return hl;
	}

	public static NBTTagCompound getHeartLevelsTag(NBTTagCompound tags) {
		NBTTagCompound hl = (NBTTagCompound) tags.getTag(hlTagName);
		// OPTIONAL: In the future, we might want to only read tags that
		// are from a certain version, so we'd add that in this method,
		// instead of having to go through every instance of getting the tag.
		return hl;
	}
	
	public static NBTTagCompound getHeartLevelsCompoundTag(NBTTagCompound tags) {
		NBTTagCompound hl = (NBTTagCompound) tags.getCompoundTag(hlTagName);
		return hl;
	}

	public static double calcDefHealth(EntityPlayer player, PlayerStats stats) {
		int rpg = 0;
		
		// Max Check
		int max = Config.maxHealth.getInt()/2;
		if (max > 0 && rpg > max) {
			rpg = max;
		}
		
		return stats.start + rpg;
	}

	public static void updateHealth(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
		// Update if Config Changes
		NBTTagCompound hlt = getHeartLevelsTag(tags);
		if (stats.start != Config.startHealth.getInt()) {
			stats.start = Config.startHealth.getInt();
		}
		player.setHealth(player.getMaxHealth());

		// Update Modifier
		double newModifier = calcDefHealth(player, stats);
		double modifier = newModifier - SharedMonsterAttributes.maxHealth.getDefaultValue();
		PlayerHandler.addHealthModifier(player, modifier);
		hlt.setDouble("modifier", modifier);
	}

	public static void setupFirstTime(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		// Health Modifier
		stats.start = Config.startHealth.getInt();
		stats.prevLevel = player.experienceLevel;
		double modifier = stats.start - SharedMonsterAttributes.maxHealth.getDefaultValue();
		PlayerHandler.addHealthModifier(player, modifier);
		try {
			stats.modifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.HeartLevelsUUID).getAmount();
		} catch (Exception e) {
			HeartLevels.logger.error(e);
		}

		// Save
		NBTTagCompound hlt = getHeartLevelsTag(tags);
		hlt.setInteger("start", stats.start);
		hlt.setInteger("prevLevel", stats.prevLevel);
		hlt.setDouble("modifier", stats.modifier);
		updateHealth(player, stats, tags);
	}

	public static void loadPlayerData(EntityPlayer player, NBTTagCompound tags, PlayerStats stats) {
		// New User
		System.out.println("Loading player data!");
		System.out.println(tags);
		if (!tags.hasKey(hlTagName)) {
			System.out.println("Missing key! Making!");
			newHeartLevelsTag(tags);
			System.out.println("Setting up first time!");
			setupFirstTime(player, tags, stats);
		}

		// Get
		NBTTagCompound hlt = getHeartLevelsTag(tags);

		// Save
		stats.start = hlt.getInteger("start");
		stats.modifier = hlt.getDouble("modifier");
	}
	
	public static void savePlayerData(EntityPlayer player, boolean loggedOut) {
		PlayerStats stats = PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
		if (stats == null) return;
		NBTTagCompound hlt = getHeartLevelsTag(player.getEntityData());
		hlt.setInteger("start", stats.start);
		hlt.setInteger("prevLevel", stats.prevLevel);
		hlt.setDouble("modifier", stats.modifier);
		if (!loggedOut) return;
	}
	
	public static void updatePlayerData(EntityPlayer player) {
		PlayerStats stats = PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
		PlayerHandler.addHealthModifier(player, stats.modifier);
	}

}
