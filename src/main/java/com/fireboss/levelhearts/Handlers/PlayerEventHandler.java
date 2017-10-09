package com.fireboss.levelhearts.Handlers;

import com.fireboss.levelhearts.LevelHearts;
import com.fireboss.levelhearts.PlayerStats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerEventHandler {

	@SubscribeEvent
	public void onPlayerLivingUpdate(PlayerTickEvent event) {
		PlayerStats stats = PlayerStats.getPlayerStats(PlayerHandler.getPlayerUUID(event.player));
		if (stats.needsClientSideUpdate) {
			PlayerHandler.savePlayerData(event.player, false);
			PlayerHandler.addOrReloadHealthModifier(event.player, stats.healthModifier);
			stats.needsClientSideUpdate = false;
		}
		if (stats.playerJustLoggedIn) {
			LevelHearts.debug("Logged out health: " + stats.loggedOutHealth);
			event.player.setHealth(stats.loggedOutHealth);
			stats.playerJustLoggedIn = false;
		}
		PlayerHandler.saveHealthChange(event.player, stats);
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		NBTTagCompound tags = event.player.getEntityData();
		PlayerStats stats = new PlayerStats();
		PlayerHandler.loadPlayerData(event.player, tags, stats);
		PlayerHandler.addOrReloadHealthModifier(event.player, stats.healthModifier);
		stats.player = event.player;
		stats.playerJustLoggedIn = true;
		PlayerHandler.playerStats.put(PlayerHandler.getPlayerUUID(event.player), stats);
		LevelHearts.debug("Player " + event.player.getName() + " has just logged in.");
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		PlayerHandler.savePlayerData(event.player, true);
		LevelHearts.debug("Player logged out.");
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		PlayerStats stats = PlayerStats.getPlayerStats(PlayerHandler.getPlayerUUID(event.player));
		PlayerHandler.addOrReloadHealthModifier(event.player, stats.healthModifier);
		System.out.println(event.player.getMaxHealth());
		event.player.setHealth(event.player.getMaxHealth());
		NBTTagCompound tags = event.player.getEntityData();
		NBTTagCompound lht = NBTHandler.createNewTag(tags);
		NBTTagCompound lhtc = NBTHandler.getTag(tags, true);
		lht.setInteger("startingHealth", stats.startingHealth);
		lht.setInteger("previousExperienceLevel", stats.previousExperienceLevel);
		lhtc.setDouble("healthModifier", stats.healthModifier);
		PlayerHandler.playerStats.put(PlayerHandler.getPlayerUUID(event.player), stats);
		LevelHearts.debug("Player respawned!");
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		PlayerHandler.updateModifier(event.player, 0);
		PlayerStats stats = PlayerStats.getPlayerStats(PlayerHandler.getPlayerUUID(event.player));
		stats.needsClientSideUpdate = true;
		LevelHearts.debug("Player switched dimension!");
	}

}
