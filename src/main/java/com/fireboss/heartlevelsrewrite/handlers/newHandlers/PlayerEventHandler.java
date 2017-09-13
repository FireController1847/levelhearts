package com.fireboss.heartlevelsrewrite.handlers.newHandlers;

import com.fireboss.heartlevelsrewrite.Config;
import com.fireboss.heartlevelsrewrite.HeartLevels;
import com.fireboss.heartlevelsrewrite.handlers.PlayerStats;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class PlayerEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerLivingUpdate(PlayerTickEvent event) {
		PlayerStats stats = PlayerHandler.getPlayerStats(event.player);
		if (stats.needsClientSideUpdate) {
			PlayerHandler.savePlayerData(event.player, false);
			PlayerHandler.addOrReloadHealthModifier(event.player, stats.modifier);
			event.player.setHealth(event.player.getHealth()+1); // Force the client to update
			event.player.setHealth(event.player.getHealth()-1); // the hearts
			stats.needsClientSideUpdate = false;
		}
		if (stats.justLoggedIn && stats.outHealth != 0) {
			event.player.setHealth(stats.outHealth);
			stats.justLoggedIn = false;
		}
		PlayerHandler.saveHeartChange(event.player, stats, event.player.getEntityData());
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		NBTTagCompound tags = event.player.getEntityData();
		PlayerStats stats = new PlayerStats();
		PlayerHandler.loadPlayerData(event.player, tags, stats);
		if (stats.start != Config.startHealth.getInt()) {
			PlayerHandler.updatePlayerHealth(event.player, tags, stats);
		}
		PlayerHandler.addOrReloadHealthModifier(event.player, stats.modifier);
		stats.player = event.player;
		stats.justLoggedIn = true;
		PlayerHandler.playerStats.put(event.player.getUUID(event.player.getGameProfile()).toString(), stats);
		HeartLevels.debug("Player logged in.");
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		PlayerStats stats = PlayerHandler.getPlayerStats(event.player);
		PlayerHandler.addOrReloadHealthModifier(event.player, stats.modifier);
		event.player.setHealth(event.player.getMaxHealth());
		NBTTagCompound tags = event.player.getEntityData();
		NBTTagCompound hlt = NBTHandler.createNewTag(tags);
		NBTTagCompound hltc = NBTHandler.getTag(tags, true);
		hlt.setInteger("start", stats.start);
		hlt.setInteger("prevLevel", stats.prevLevel);
		hltc.setDouble("modifier", stats.modifier);
		PlayerHandler.playerStats.put(event.player.getUUID(event.player.getGameProfile()).toString(), stats);
		HeartLevels.debug("Player respawned.");
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		PlayerHandler.updateModifier(event.player, 0, false);
		PlayerHandler.savePlayerData(event.player, false);
		PlayerStats stats = PlayerHandler.getPlayerStats(event.player);
		PlayerHandler.addOrReloadHealthModifier(event.player, stats.modifier);
		stats.needsClientSideUpdate = true;
		HeartLevels.debug("Player changed dimension.");
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		PlayerHandler.savePlayerData(event.player, true);
		HeartLevels.debug("Player logged out.");
	}

}
