package com.fireboss.heartlevels.handlers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.PlayerStats;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerHandler {

	// What identifies the mod
	public static final UUID HeartLevelsID = UUID.fromString("e3723b50-7cc6-11e3-baa7-0800200c9a66");
	// The map with all user's status stores for the session.
	public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<String, PlayerStats>();

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		NBTTagCompound tags = event.player.getEntityData();
		PlayerStats stats = new PlayerStats();
		
		// Loads a player from saved data
		PlayerHandlerHelper.loadPlayerData(event.player, tags, stats);
	}

	public static void addHealthModifier(EntityPlayer player, double healthModifier) {
		HeartLevels.healthMod = new AttributeModifier(HeartLevelsID, "Heart Levels Health Modifier", healthModifier, 0);
		IAttributeInstance attinstance = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		attinstance.removeModifier(HeartLevels.healthMod);
		attinstance.applyModifier(HeartLevels.healthMod);
	}
}
