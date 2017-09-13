package com.fireboss.heartlevelsrewrite.handlers;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.fireboss.heartlevelsrewrite.HeartLevels;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class PlayerHandler {

	public static final UUID HeartLevelsUUID = UUID.fromString("10c19648-77cb-4e0a-a764-13992928e1ab");
	public static ConcurrentHashMap<String, PlayerStats> playerStats = new ConcurrentHashMap<String, PlayerStats>();

	// Methods
	public static void addHealthModifier(EntityPlayer player, double healthModifier) {
		HeartLevels.health_modifier = new AttributeModifier(HeartLevelsUUID, "heartLevels.healthModifier",
				healthModifier, 0);
		IAttributeInstance attrInt = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		attrInt.removeModifier(HeartLevels.health_modifier);
		attrInt.applyModifier(HeartLevels.health_modifier);
		System.out.println("Added modifier!");
	}

	// Events
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		event.player.setHealth(event.player.getMaxHealth());
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		addHealthModifier(event.player, 180);
		event.player.setHealth(event.player.getMaxHealth());
		System.out.println("Player respawned!");
		System.out.println("Modifier: " + event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getModifier(HeartLevelsUUID).getAmount());
		System.out.println("Max Health: " + event.player.getMaxHealth());
		System.out.println("Health: " + event.player.getHealth());
	}

}
