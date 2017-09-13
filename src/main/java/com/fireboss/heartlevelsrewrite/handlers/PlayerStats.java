package com.fireboss.heartlevelsrewrite.handlers;

public class PlayerStats {

	public static PlayerStats getPlayerStats(String uuid) {
		PlayerStats stats = PlayerHandler.playerStats.get(uuid);
		if (stats == null) {
			stats = new PlayerStats();
			PlayerHandler.playerStats.put(uuid, stats);
		}
		return stats;
	}

}
