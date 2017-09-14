package com.fireboss.heartlevelsrewrite.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PlayerStats {

	public static PlayerStats getPlayerStats(String uuid) {
		PlayerStats stats = PlayerHandler.playerStats.get(uuid);
		if (stats == null) {
			stats = new PlayerStats();
			PlayerHandler.playerStats.put(uuid, stats);
		}
		return stats;
	}

	public EntityPlayer player;
	public int start;
	public double modifier;
	public float outHealth;
	public int currentLrPos;
	public int heartContainers;
	public boolean justLoggedIn;
	public boolean needsClientSideUpdate = false;

	public int prevLevel;
	public ItemStack[] prevArmor = new ItemStack[4];

}
