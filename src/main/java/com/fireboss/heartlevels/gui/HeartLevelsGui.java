package com.fireboss.heartlevels.gui;

import java.awt.Color;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.PlayerStats;
import com.fireboss.heartlevels.handlers.PlayerHandlerHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class HeartLevelsGui extends GuiScreen {

	public static KeyBinding keyBinding;
	public static int id = 1020;

	public HeartLevelsGui() {

	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		PlayerStats stats = PlayerStats.getPlayerStats(mc.thePlayer.getUUID(mc.thePlayer.getGameProfile()).toString());
		ScaledResolution sr = new ScaledResolution(mc);
		drawCenteredString(fontRendererObj, "More Health Stats", sr.getScaledWidth()/2, sr.getScaledHeight()/2-32, 0xE0E0E0);
		drawCenteredString(fontRendererObj, "Current Hearts: " + (int) mc.thePlayer.getHealth() / 2.0,
				sr.getScaledWidth()/2, sr.getScaledHeight()/2-22, 0xE0E0E0);
		drawCenteredString(fontRendererObj, "Max Hearts: " + (int) mc.thePlayer.getMaxHealth() / 2.0,
				sr.getScaledWidth()/2, sr.getScaledHeight()/2-12, 0xE0E0E0);
		drawCenteredString(fontRendererObj, "Hearts from RPG: " + stats.count, sr.getScaledWidth()/2, sr.getScaledHeight()/2-2, 0xE0E0E0);
		int extraHearts = 0;
		for (int i = 0; i < stats.oldArmourSet.length; i++) {
			extraHearts += EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), stats.oldArmourSet[i]);
		}
		drawCenteredString(fontRendererObj, "Hearts from Enchantment: " + (extraHearts), sr.getScaledWidth()/2, sr.getScaledHeight()/2+8,
				0xE0E0E0);
		double health = PlayerHandlerHelper.calcDefaultHearts(mc.thePlayer, stats);
		double modAmount = Math.abs(20 + HeartLevels.healthMod.getAmount());
		double heartsFromContainers = stats.heartContainers;
		if (modAmount - health != 0) {
			// Some missing health not accounted for.
			heartsFromContainers += modAmount - health;
		}
		drawCenteredString(fontRendererObj, "Hearts from Heart Containers: " + (heartsFromContainers),
				sr.getScaledWidth()/2, sr.getScaledHeight()/2+18, 0xE0E0E0);
		IAttributeInstance aint = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		aint.removeModifier(HeartLevels.healthMod);
		drawCenteredString(fontRendererObj, "Health from other sources: " + (int) (mc.thePlayer.getMaxHealth() - 20),
				sr.getScaledWidth()/2, sr.getScaledHeight()/2+28, 0xE0E0E0);
		aint.applyModifier(HeartLevels.healthMod);
	}

}
