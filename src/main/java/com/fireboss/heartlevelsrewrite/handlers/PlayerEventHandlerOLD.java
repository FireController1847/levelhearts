//package com.fireboss.heartlevelsrewrite.handlers;
//
//import com.fireboss.heartlevelsrewrite.HeartLevels;
//
//import net.minecraft.entity.SharedMonsterAttributes;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.fml.common.eventhandler.EventPriority;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
//
//public class PlayerEventHandlerOLD {
//
//	// Methods
//	private void saveHeartChange(EntityPlayer player, PlayerStats stats, NBTTagCompound tags) {
//		NBTTagCompound hlt = PlayerHandlerHelperOLD.getHeartLevelsCompoundTag(tags);
//		try {
//			stats.modifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
//					.getModifier(PlayerHandlerOLD.HeartLevelsUUID).getAmount();
//			hlt.setDouble("modifier", stats.modifier);
//		} catch (Exception e) {
////			HeartLevels.logger.error(e);
//		}
//		hlt.setInteger("prevLevel", stats.prevLevel);
//		PlayerHandlerOLD.playerStats.put(player.getUUID(player.getGameProfile()).toString(), stats);
//	}
//
//	// Events
//	@SubscribeEvent(priority = EventPriority.HIGH)
//	public void onPlayerLivingUpdate(PlayerTickEvent event) {
//		PlayerStats stats = PlayerStats.getPlayerStats(event.player.getUUID(event.player.getGameProfile()).toString());
//		if (stats.needsClientSideUpdate) {
//			PlayerHandlerHelperOLD.savePlayerData(event.player, false);
//			PlayerHandlerHelperOLD.updatePlayerData(event.player);
//			event.player.setHealth(event.player.getHealth());
//			stats.needsClientSideUpdate = false;
//		}
//		if (stats.justLoggedIn && stats.outHealth != 0) {
//			event.player.setHealth(stats.outHealth);
//			stats.justLoggedIn = false;
//		}
//		saveHeartChange(event.player, stats, event.player.getEntityData());
//	}
//}
