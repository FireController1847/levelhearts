//package com.fireboss.heartlevelsrewrite.handlers;
//
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//import com.fireboss.heartlevelsrewrite.Config;
//import com.fireboss.heartlevelsrewrite.HeartLevels;
//
//import net.minecraft.entity.SharedMonsterAttributes;
//import net.minecraft.entity.ai.attributes.AttributeModifier;
//import net.minecraft.entity.ai.attributes.IAttributeInstance;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.util.ChatComponentText;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
//import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
//import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
//import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
//
//public class PlayerHandlerOLD {
//
//	public static final UUID HeartLevelsUUID = UUID.fromString("10c19648-77cb-4e0a-a764-13992928e1ab");
//	public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<String, PlayerStats>();
//
//	// Methods
//	public static void addHealthModifier(EntityPlayer player, double healthModifier) {
//		HeartLevels.health_modifier = new AttributeModifier(HeartLevelsUUID, "heartLevels.healthModifier",
//				healthModifier, 0);
//		IAttributeInstance attrInt = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
//		attrInt.removeModifier(HeartLevels.health_modifier);
//		attrInt.applyModifier(HeartLevels.health_modifier);
//		HeartLevels.logger.info("Added health modifier.");
//	}
//
//	// Events
//	@SubscribeEvent
//	public void onPlayerLogin(PlayerLoggedInEvent event) {
//		NBTTagCompound tags = event.player.getEntityData();
//		PlayerStats stats = new PlayerStats();
//		PlayerHandlerHelperOLD.loadPlayerData(event.player, tags, stats);
//		// Config Change
//		if (stats.start != Config.startHealth.getInt()) {
//			PlayerHandlerHelperOLD.updateHealth(event.player, stats, tags);
//		}
//		// Modifier & Save
//		addHealthModifier(event.player, stats.modifier);
//		stats.player = event.player;
//		stats.justLoggedIn = true;
//		playerStats.put(event.player.getUUID(event.player.getGameProfile()).toString(), stats);
//		HeartLevels.logger.info("Player logged in.");
//	}
//
//	@SubscribeEvent
//	public void onPlayerRespawn(PlayerRespawnEvent event) {
//		PlayerStats stats = PlayerStats.getPlayerStats(event.player.getUUID(event.player.getGameProfile()).toString());
//		addHealthModifier(event.player, stats.modifier); // TODO: Update modifier to be modifier from stats.
//		event.player.setHealth(event.player.getMaxHealth());
//		NBTTagCompound tags = event.player.getEntityData();
//		NBTTagCompound hltt = PlayerHandlerHelperOLD.newHeartLevelsTag(tags);
//		NBTTagCompound hlttc = PlayerHandlerHelperOLD.getHeartLevelsCompoundTag(tags);
//		hltt.setInteger("start", stats.start);
//		hltt.setInteger("prevLevel", stats.prevLevel);
//		hlttc.setDouble("modifier", stats.modifier);
//		playerStats.put(event.player.getUUID(event.player.getGameProfile()).toString(), stats);
//		HeartLevels.logger.info("Player respawned.");
//	}
//
//	@SubscribeEvent
//	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
//		double newMod = 20;
//		try {
//			newMod = event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
//					.getModifier(PlayerHandlerOLD.HeartLevelsUUID).getAmount() + 10;
//		} catch (Exception e) {
//			HeartLevels.logger.error(e);
//		}
//		PlayerHandlerHelperOLD.savePlayerData(event.player, false);
//		PlayerHandlerHelperOLD.updatePlayerData(event.player);
//		PlayerStats stats = PlayerStats.getPlayerStats(event.player.getUUID(event.player.getGameProfile()).toString());
//		stats.modifier = newMod;
//		event.player.addChatComponentMessage(new ChatComponentText("You've gained a heart!"));
//		stats.needsClientSideUpdate = true;
//		HeartLevels.logger.info("Player switched dimension.");
//	}
//
//	@SubscribeEvent
//	public void onPlayerLogOut(PlayerLoggedOutEvent event) {
//		PlayerHandlerHelperOLD.savePlayerData(event.player, true);
//		HeartLevels.logger.info("Player logged out.");
//	}
//
//}
