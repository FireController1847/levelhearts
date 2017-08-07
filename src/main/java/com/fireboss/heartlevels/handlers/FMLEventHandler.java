package com.fireboss.heartlevels.handlers;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.PlayerStats;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class FMLEventHandler {

	/**
	 * This function will be called for players using this FML event. The main logic
	 * of the RPG side of the code resides in this function. It calculates what the
	 * player's health should be on every tick of the game (for the RPG system)
	 * 
	 * @param event
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onPlayerLivingUpdate(PlayerTickEvent event) {
		PlayerStats stats = PlayerStats.getPlayerStats(event.player.getUUID(event.player.getGameProfile()).toString());
		Side side = event.side;
		if (side == Side.CLIENT && stats.needClientSideHealthUpdate) {
			// Update the client side on a dimension change.
			PlayerHandlerHelper.savePlayerData(event.player, false);
			PlayerHandlerHelper.updatePlayerData(event.player);
			event.player.setHealth(event.player.getHealth());
			stats.needClientSideHealthUpdate = false;
			if (stats.justLoggedIn && stats.loggedOutHealth != 0) {
				event.player.setHealth(stats.loggedOutHealth);
				stats.justLoggedIn = false;
			}
		}
		// Main logic for RPG heart gain.
		if (Config.rpgMode.getBoolean()) {
			calculateHeartChange(event.player, stats);
		}
		// Check player's armour slots (1-4: Armour)
		if (side == Side.SERVER && Config.enchantsEnabled.getBoolean()) {
			calculateEnchantmentChanges(event.player, stats);
		}
		// Save all changes to NBT and the player's stats data structure
		saveHeartChange(event.player, stats);
	}

	/**
	 * Looks though a player's current inventory armour comparing it with the
	 * previous tick/event's armor. Updates the player's health modifiers. Need a
	 * client side update, so set stats.needClientSideHealthUpdate to be true.
	 * 
	 * @param player
	 * @param stats
	 */
	private void calculateEnchantmentChanges(EntityPlayer player, PlayerStats stats) {
		if (stats.loggedOutHealth == 0) {
			return;
		}
		int armourHealth = 0;
		for (int i = 1; i <= 4; i++) {
			ItemStack currentArmour = player.getEquipmentInSlot(i);
			ItemStack oldArmour = stats.oldArmourSet[i - 1];

			double currentMaxHealthMod = 0;
			try {
				currentMaxHealthMod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
						.getModifier(PlayerHandler.HeartLevelsID).getAmount();
			} catch (Exception e) {
				return;
			}
			if (currentArmour == null && oldArmour != null) {
				// Armour was unequipped.
				int extraHearts = EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), oldArmour);
				// 1 heart = 2 health (because reasons)
				if (extraHearts > 0) {
					int extraHealth = extraHearts; // OLD: * 2
					PlayerHandler.addHealthModifier(player, currentMaxHealthMod - extraHealth);
					player.addChatComponentMessage(new ChatComponentText(
							"Removing the armor causes the extra " + extraHearts + " enchanted hearts to fade away."));
					stats.needClientSideHealthUpdate = true;
				}
			} else if (currentArmour != null && oldArmour == null) {
				// Armour was equipped (with nothing before)
				int extraHearts = EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), currentArmour);
				if (extraHearts > 0) {
					int extraHealth = extraHearts; // OLD: * 2
					PlayerHandler.addHealthModifier(player, currentMaxHealthMod + extraHealth); // changes the health
					// modifier to this new
					// one
					if (!stats.justLoggedIn) {
						player.addChatComponentMessage(new ChatComponentText("Equipping the armor binds an extra "
								+ extraHearts + " enchanted hearts to your soul."));
					}
					stats.needClientSideHealthUpdate = true;
					armourHealth += extraHealth;
				}
			} else if (oldArmour == currentArmour) {
				// Do nothing, the armour hasn't changed.
			} else {
				// Both are not null, and they are not equal to each other.
				// Removed 2* because of half hearts are now accepted on both oldHealth and newHealth
				int oldHealth = EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), oldArmour);
				int newHealth = EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), currentArmour);
				int healthChange = newHealth - oldHealth;
				PlayerHandler.addHealthModifier(player, currentMaxHealthMod + healthChange);
				// Adds the change in health (can be positive and negative)
				if (healthChange > 0) {
					// Player overall gained hearts
					player.addChatComponentMessage(
							new ChatComponentText("Equipping the stronger new armor binds an extra " + healthChange
									+ " enchanted hearts to your soul."));
					stats.needClientSideHealthUpdate = true;

				}
				if (healthChange < 0) {
					// Player overall lost hearts
					player.addChatComponentMessage(new ChatComponentText(
							"Equipping the weaker new armor releases an extra " + healthChange + " enchanted hearts."));
					stats.needClientSideHealthUpdate = true;
				}
			}
			// Update old Armour piece to be the current one
			stats.oldArmourSet[i - 1] = currentArmour;
		}
		// After checking armour, if the player lost health, his max health is updated
		// but his current health is not.
		if (player.getHealth() > player.getMaxHealth()) {
			player.setHealth(player.getMaxHealth());
		}
		// If a player just logged in
		if (stats.justLoggedIn) {
			player.setHealth(stats.loggedOutHealth);
			stats.needClientSideHealthUpdate = true;
		}
	}

	/**
	 * CommandEnchant class processCommand method This is where an enchant is
	 * applied-- I need to grab the item and apply hearts based on the enchantment's
	 * enchant level itemstack.addEnchantment(enchantment, j); j = enchantment level
	 * I need to get the enchantment when an enchantment is applied, check if it's
	 * an armor health enchantment that is applied Then I have to update health
	 * based on enchantment level I can get the level from
	 * getEnchantmentLevel(enchantmentID, itemstack) (since an item(stack) can have
	 * more than one enchantment I need to update a players health when he EQUIPS or
	 * REMOVES a heart enchanted armor-- aka not on CommandEvent
	 * 
	 * @param player
	 * @param stats
	 */
	private void calculateHeartChange(EntityPlayer player, PlayerStats stats) {
		int maxHearts = Config.maxHearts.getInt();
		if (maxHearts != -1 && maxHearts != 0 && player.getMaxHealth() + 2 > maxHearts) { // OLD: maxHearts * 2
			// Player gets more health through heart container system, so RPG doesn't exceed
			// the cap.
			return;
		}
		if (levelIncreased(player, stats)) {
			if (Config.debug.getBoolean()) {
				HeartLevels.logger.info("The user's level has increased!");
			}
			/*
			 * While the player is still able to earn hearts (based on the config file's
			 * level ramp)-- aka stats.count hasn't reached the end of the ramp array AND
			 * the player's exp level meets the level required for the (count)th's heart
			 * Continue to update the players health. EG Count = 3 LevelRampInt =
			 * [0,10,20,30,40,50] LevelRampInt[3] = 30. So if player is level 40, then he
			 * gains one heart. The while loop iterates again, incrementing count.
			 * LevelRampInt[4] = 40. He still gets one heart. The while loop iterates once
			 * again, but now at 50 the condition is not satisfied
			 */
			while (stats.count < HeartLevels.LevelRampInt.length
					&& player.experienceLevel >= HeartLevels.LevelRampInt[stats.count]) {
				if (Config.debug.getBoolean()) {
					HeartLevels.logger.info("EXP matches next level ramp! Upping player level...");
				}
				player.addChatComponentMessage(
						new ChatComponentText("Your Life has increased by one and is also now fully replenished!"));
				double updatedModifier = 2;
				try {
					updatedModifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
							.getModifier(PlayerHandler.HeartLevelsID).getAmount() + Config.heartGain.getInt();
				} catch (Exception e) {
					if(Config.debug.getBoolean()) {
						HeartLevels.logger.catching(e);
					}
				}
				if(!player.worldObj.isRemote) {
					PlayerHandler.addHealthModifier(player, updatedModifier);
				}
				stats.healthmod = updatedModifier;
				stats.count++;
				player.setHealth(player.getMaxHealth());
				if (Config.debug.getBoolean()) {
					HeartLevels.logger.info("Player's hearts have been updated!");
				}
			}
		}
	}

	/**
	 * Saves the changes made to the NBTTagCompound (persistent storage) from the
	 * changes to the player stats (game session storage)
	 * 
	 * @param player
	 * @param stats
	 */
	private void saveHeartChange(EntityPlayer player, PlayerStats stats) {
		NBTTagCompound tags = player.getEntityData(); // saves changes made to nbt
		tags.getCompoundTag("HeartLevels 1").setInteger("count", stats.count);
		try {
			stats.healthmod = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.HeartLevelsID).getAmount();
		} catch (Exception e) {
			// TODO: handle exception
		}
		tags.getCompoundTag("HeartLevels 1").setDouble("healthModifier", stats.healthmod);
		tags.getCompoundTag("HeartLevels 1").setInteger("previousLevel", stats.previousLevel);
		PlayerHandler.playerStats.put(player.getUUID(player.getGameProfile()).toString(), stats);
	}

	/**
	 * Checks if the player has has a level increase since the last time his level
	 * was recorded into the stats data structure
	 * 
	 * @param player
	 * @param stats
	 * @return
	 */
	private boolean levelIncreased(EntityPlayer player, PlayerStats stats) {
		boolean levelIncreased = false;
		if (stats.previousLevel != player.experienceLevel) {
			stats.previousLevel = player.experienceLevel;
			levelIncreased = true;
		}
		return levelIncreased;
	}
}
