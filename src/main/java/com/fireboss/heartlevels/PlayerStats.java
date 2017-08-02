package com.fireboss.heartlevels;

import com.fireboss.heartlevels.handlers.PlayerHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerStats {
	
	public int[] LevelArray;
	public int start;
	public int maxhp;
	public int count; // Tracks the current position in LevelRamp
	public int previousLevel; // The player's previous level
	public double healthmod; // Used when a player's health modifier is lost (on respawn, login, or dim change)
	public EntityPlayer player; // The player we are referring to
	public boolean needClientSideHealthUpdate = false;
	public ItemStack[] oldArmourSet = new ItemStack[4]; // The armour equipped when stats are saved
	public boolean justLoggedIn;
	public float loggedOutHealth;
	public int heartContainers; // Tracks how many heart containers were used

	public static PlayerStats getPlayerStats(String username) {
		PlayerStats stats = PlayerHandler.playerStats.get(username);
		if (stats == null) {
			stats = new PlayerStats();
			PlayerHandler.playerStats.put(username, stats);
		}
		return stats;
	}
}
