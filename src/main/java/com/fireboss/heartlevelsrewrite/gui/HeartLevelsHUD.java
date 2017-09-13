package com.fireboss.heartlevelsrewrite.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fireboss.heartlevelsrewrite.Config;
import com.fireboss.heartlevelsrewrite.HeartLevels;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HeartLevelsHUD extends Gui {

	// Variables
	private Minecraft mc;
	protected final Random rand = new Random();

	// Constructor
	public HeartLevelsHUD(Minecraft mc) {
		this.mc = Minecraft.getMinecraft();
	}

	// Events
	@SubscribeEvent
	public void modifyAirHUD(Pre event) {
		if (event.type.equals(ElementType.AIR)) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("AIR");

			// Setup Resolution
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();

			// Set Position
			int left = width / 2 + 90;
			int top = height - 49;

			// Event
			if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
				// If armor, air bubbles must display a level above, otherwise it's above food.
				int breath = ForgeHooks.getTotalArmorValue(mc.thePlayer);
				if (breath > 0)
					top = top - 10;
				// Render Bubbles
				int air = mc.thePlayer.getAir();
				int full = MathHelper.ceiling_double_int((double) (air - 2) * 10.0D / 300.0D);
				int partial = MathHelper.ceiling_double_int((double) air * 10.0D / 300.0D) - full;
				for (int i = 0; i < full + partial; i++) {
					drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
				}
			}
			mc.mcProfiler.endSection();
		}
	}

	@SubscribeEvent
	public void modifyArmorHUD(Pre event) {
		if (event.type.equals(ElementType.ARMOR)) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("armor");

			// Setup Resolution
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();

			// Set Position
			int left = width / 2 - 91 + 100;
			int top = height - 49;

			// Render Armor
			int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
			for (int i = 1; level > 0 && i < 20; i += 2) {
				if (i < level) {
					drawTexturedModalRect(left, top, 34, 9, 9, 9);
				} else if (i == level) {
					drawTexturedModalRect(left, top, 25, 9, 9, 9);
				} else if (i > level) {
					drawTexturedModalRect(left, top, 16, 9, 9, 9);
				}
				left += 8;
			}
			mc.mcProfiler.endSection();
		}
	}

	@SubscribeEvent
	public void modifyHealthHUD(Pre event) {
		if (event.type.equals(ElementType.HEALTH) && Config.minimalHud.getBoolean()) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("health");
			
			// Minimal HUD
			boolean isMinimalHud = Config.minimalHud.getBoolean();
			String mhnp = Config.minimalHudNumberPos.getString();

			// Heart Border
			boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
			if (mc.thePlayer.hurtResistantTime < 10) {
				highlight = false;
			}

			// Setup Resolution
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();

			// Set Position
			int left = width / 2 - 91;
			int top = height - 39;

			// Render Health
			float health = mc.thePlayer.getHealth() / 2;
			int row = (int) (health*2 - 1) / 20;
			for (int i = row * 10; i < row * 10 + 10; i++) {
				if ((i + 1) * 2 > mc.thePlayer.getMaxHealth()) {
					continue; // Doesn't display empty extra hearts.
				}
				// Get Icons
				int iconX = 16;
				int iconY = 0;
				int regen = -1;
				if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
					regen = mc.ingameGUI.getUpdateCounter() % 25;
				}
				if (mc.thePlayer.isPotionActive(Potion.poison)) {
					iconX += 36;
				} else if (mc.thePlayer.isPotionActive(Potion.wither)) {
					iconX += 72;
				}
				
				// Heart Positions
				int heartPosLeft = left + i * 8 - (80 * row);
				int heartPosY = top;
				if (health <= 4) {
					heartPosY = top + rand.nextInt(2);
				}
				if (i == regen) {
					heartPosY -= 2;
				}
				

				// Render Heart Border
				drawTexturedModalRect(heartPosLeft, heartPosY, 16 + (highlight ? 9 : 0), 9 * iconY, 9, 9);
				
				// Render Hearts
				float idx = (float) (i + 0.5);
				if (idx < health) {
					drawTexturedModalRect(heartPosLeft, heartPosY, iconX + 36, 9 * iconY, 9, 9);
				} else if (idx == health) {
					drawTexturedModalRect(heartPosLeft, heartPosY, iconX + 45, 9 * iconY, 9, 9);
				}
				
				// Minimal HUD
				if (isMinimalHud) {
					// Text
					if (row >= Integer.MAX_VALUE) {
						row = Integer.MAX_VALUE - 1;
					}
					int disRow = row + 1;
					String text = "" + disRow;
					
					// Position
					int minPosLeft = 0;
					int minPosTop = top + 1;
					if (Integer.toString(disRow).length() >= 2) {
						minPosLeft -= 6 * Integer.toString(disRow).length()-6;
					}
					if (mhnp.equalsIgnoreCase("right")) {
						minPosLeft = left + 85;
						if (mc.thePlayer.experienceLevel > 0) {
							minPosTop = top - 5;
						}
					} else if (mhnp.equalsIgnoreCase("left")) {
						minPosLeft += left - 7;
					}
					
					// Render
					mc.fontRendererObj.drawString(text, minPosLeft, minPosTop, 0xFFFFFF);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					mc.getTextureManager().bindTexture(Gui.icons);
				}
			}
			mc.mcProfiler.endSection();
		}
	}

}
