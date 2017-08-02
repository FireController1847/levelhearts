package com.fireboss.heartlevels.gui;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.PlayerStats;
import com.fireboss.heartlevels.handlers.PlayerHandlerHelper;

import net.minecraft.client.gui.GuiScreen;
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
		PlayerStats stats = PlayerStats.getPlayerStats(mc.thePlayer.getCommandSenderEntity().getName());
		drawCenteredString(fontRendererObj, "More Health Stats", mc.displayWidth / 4, 2, 0xE0E0E0);
		drawCenteredString(fontRendererObj, "Current Hearts: " + (int) mc.thePlayer.getHealth() / 2.0,
				mc.displayWidth / 4, 12, 0xE0E0E0);
		drawCenteredString(fontRendererObj, "Max Hearts: " + (int) mc.thePlayer.getMaxHealth() / 2.0,
				mc.displayWidth / 4, 22, 0xE0E0E0);
		drawCenteredString(fontRendererObj, "Hearts from RPG: " + stats.count, mc.displayWidth / 4, 32, 0xE0E0E0);
		int extraHearts = 0;
		for (int i = 0; i < stats.oldArmourSet.length; i++) {
			extraHearts += EnchantmentHelper.getEnchantmentLevel(Config.armorEnchantID.getInt(), stats.oldArmourSet[i]);
		}
		drawCenteredString(fontRendererObj, "Hearts from Enchantment: " + (extraHearts), mc.displayWidth / 4, 42,
				0xE0E0E0);
		double health = PlayerHandlerHelper.calculateTotalHeartLevelsContrib(mc.thePlayer, stats);
		double modAmount = Math.abs(20 + HeartLevels.healthMod.getAmount());
		double heartsFromContainers = stats.heartContainers;
		if (modAmount - health != 0) {
			// Some missing health not accounted for.
			heartsFromContainers += modAmount - health;
		}
		drawCenteredString(fontRendererObj, "Hearts from Heart Containers: " + (heartsFromContainers),
				mc.displayWidth / 4, 52, 0xE0E0E0);
		IAttributeInstance aint = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		aint.removeModifier(HeartLevels.healthMod);
		drawCenteredString(fontRendererObj, "Health from other sources: " + (int) (mc.thePlayer.getMaxHealth() - 20),
				mc.displayWidth / 4, 62, 0xE0E0E0);
		aint.applyModifier(HeartLevels.healthMod);
	}

}
