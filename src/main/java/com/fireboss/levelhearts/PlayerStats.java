package com.fireboss.levelhearts;

import com.fireboss.levelhearts.Handlers.PlayerHandler;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerStats {

	public EntityPlayer player;

	public int startingHealth;
	public int currentLevelPosition;
	public int previousExperienceLevel;
	public double healthModifier;
	public float loggedOutHealth;
	public boolean playerJustLoggedIn;
	public boolean needsClientSideUpdate;

	public static PlayerStats getPlayerStats(String uuid) {
		PlayerStats stats = PlayerHandler.playerStats.get(uuid);
		if (stats == null) {
			stats = new PlayerStats();
			PlayerHandler.playerStats.put(uuid, stats);
		}
		return stats;
	}

}
