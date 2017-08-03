package com.fireboss.heartlevels.handlers;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.PlayerStats;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerHandler {

	// What identifies the mod
	public static final UUID HeartLevelsID = UUID.fromString("e3723b50-7cc6-11e3-baa7-0800200c9a66");
	// The map with all user's status stores for the session.
	public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<String, PlayerStats>();

	/**
	 * OnPlayerLogin Event
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		NBTTagCompound tags = event.player.getEntityData();
		PlayerStats stats = new PlayerStats();

		// Loads a player from saved data (or sets up a new user)
		PlayerHandlerHelper.loadPlayerData(event.player, tags, stats);

		// Update health (for changes in config)
		// Start gets set to default config. If the user changes it, heart values need
		// to be recalculated.
		if (stats.start != Config.startHearts.getInt()
				|| !Arrays.equals(stats.LevelArray, HeartLevels.LevelRampInt)) {
			PlayerHandlerHelper.updateHealth(event.player, stats, tags);
		}

		double healthModifier = stats.healthmod;
		addHealthModifier(event.player, healthModifier);

		stats.player = event.player;
		stats.justLoggedIn = true;
		playerStats.put(event.player.getUUID(event.player.getGameProfile()).toString(), stats);
	}

	/**
	 * Add health modifier. Simple.
	 * 
	 * @param player
	 * @param healthModifier
	 */
	public static void addHealthModifier(EntityPlayer player, double healthModifier) {
		HeartLevels.healthMod = new AttributeModifier(HeartLevelsID, "Heart Levels Health Modifier", healthModifier, 0);
		IAttributeInstance attinstance = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		attinstance.removeModifier(HeartLevels.healthMod);
		attinstance.applyModifier(HeartLevels.healthMod);
	}

	/**
	 * On the player logging out. - Not working for client log out, only server.
	 * Need a workaround for singleplayer.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerLogOut(PlayerLoggedOutEvent event) {
		PlayerHandlerHelper.savePlayerData(event.player, true);
	}

	/**
	 * On the server stopping. Fired at the client when a client disconnects.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onServerStop(ClientDisconnectionFromServerEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.CLIENT) {
			// Called only if you go into a SP world, then quit, world is unloaded.
			PlayerHandlerHelper.savePlayerData(FMLClientHandler.instance().getClient().thePlayer, true);
		}
	}

	/**
	 * When the player changes dimension.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerChangedDimention(PlayerChangedDimensionEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		PlayerHandlerHelper.savePlayerData(event.player, false);
		// Changing dimention also requires an update.
		PlayerHandlerHelper.updatePlayerData(event.player);
		// Set variable true so during OnLivingUpdate the user is forced to update the
		// client side health
		PlayerStats stats = PlayerStats.getPlayerStats(event.player.getUUID(event.player.getGameProfile()).toString());
		stats.needClientSideHealthUpdate = true;
	}

	/**
	 * When the player respawns.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		PlayerStats stats = PlayerStats.getPlayerStats(event.player.getUUID(event.player.getGameProfile()).toString());
		// Since everything needed is either saves in the stats or in
		// entityPlayer(maxhealth)
		// no need to save anything here (even in ntb)
		// NBTTagCompound tags = currentPlayer.getEntityData();
		// Death causes modifiers to reset, so replace it here.
		double healthModifier = stats.healthmod;
		addHealthModifier(event.player, healthModifier);
		if (Config.hardcoreMode.getBoolean()) {
			stats.count = 0; // Resets progress
			double baseHearts = event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
			healthModifier = (stats.start * 2) - baseHearts;
			addHealthModifier(event.player, healthModifier);
		}
		// Set health
		event.player.setHealth(event.player.getMaxHealth());
		// All players need update tag compound, as respawn creates a "new" player with
		// an unknown health tag compound
		NBTTagCompound tags = event.player.getEntityData();
		NBTTagCompound tagTemp = new NBTTagCompound();
		tagTemp.setInteger("count", stats.count);
		tagTemp.setInteger("start", stats.start);
		tagTemp.setInteger("previousLevel", stats.previousLevel);
		if (Config.rpgMode.getBoolean()) {
			tagTemp.setIntArray("LevelArray", stats.LevelArray);
		} else {
			tagTemp.setIntArray("LevelArray", new int[] { -1 });
		}
		tags.setTag("HeartLevels 1", tagTemp);
		try {
			stats.healthmod = event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(HeartLevelsID).getAmount();
		} catch (Exception e) {
			// TODO: Handle Exception
		}
		tags.getCompoundTag("HeartLevels 1").setDouble("healthModifier", stats.healthmod);
		playerStats.put(event.player.getUUID(event.player.getGameProfile()).toString(), stats);
	}

}
