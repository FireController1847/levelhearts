package com.fireboss.heartlevels.gui;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.fireboss.heartlevels.Config;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HeartLevelsHUD extends Gui {

	private Minecraft mc;
	protected final Random rand = new Random();

	public HeartLevelsHUD(Minecraft mc) {
		this.mc = Minecraft.getMinecraft();
	}

	private void bind(ResourceLocation res) {
		mc.getTextureManager().bindTexture(res);
	}

	@SubscribeEvent
	public void modifyAirHUD(RenderGameOverlayEvent.Pre event) {
		if (event == null || event.type == null)
			return;
		if (event.type.equals(RenderGameOverlayEvent.ElementType.AIR)) {
			if (Config.customGui.getBoolean() && !Config.minimalGui.getBoolean()) {
				event.setCanceled(true);
				mc.mcProfiler.startSection("air");
				ScaledResolution res = new ScaledResolution(mc);
				int width = res.getScaledWidth();
				int height = res.getScaledHeight();
				int left = width / 2 + 91;
				int top = height - 49;
				if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
					// If the player has armour, the air bubbles will display a level above.
					// Otherwise, it will appear above food.
					int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
					if (level > 0) {
						top = top - 9;
					}
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
	}

	@SubscribeEvent
	public void modifyArmorHUD(RenderGameOverlayEvent.Pre event) {
		if (event == null || event.type == null)
			return;
		if (event.type.equals(RenderGameOverlayEvent.ElementType.ARMOR)) {
			if (Config.minimalGui.getBoolean())
				GuiIngameForge.left_height += 10;
		}
		if (Config.customGui.getBoolean() && !Config.minimalGui.getBoolean()) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("armor");
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();

			// Armour shifted right 100 units (most likely pixels)
			int left = width / 2 - 91 + 100;
			int top = height - 49;
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
	public void modifyHealthHUD(RenderGameOverlayEvent.Pre event) {
		if (event == null || event.type == null)
			return;
		if (event.type.equals(RenderGameOverlayEvent.ElementType.HEALTH) && Config.minimalGui.getBoolean()) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("health");
			boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
			if (mc.thePlayer.hurtResistantTime < 10) {
				highlight = false;
			}
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();
			int health = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
			// int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth); //
			// WTF DO I REPLACE PREVHEALTH WITH
			int left = width / 2 - 91;
			int top = height - 39;
			int colorX = left - 7;
			int colorY = top + 1; // A little lower.
			int regen = -1;
			if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
				// Edits
				regen = mc.ingameGUI.getUpdateCounter() % 25;
			}
			// 16/20 (8 hearts) truncates to 0 = row 0. [1,20] is included in row 0.
			int row = (health - 1) / 20;
			// This "row" is off by the display row by (-1)
			boolean isMinimalGui = Config.minimalGui.getBoolean();
			for (int i = row * 10; i < row * 10 + 10; i++) {
				if ((i + 1) * 2 > mc.thePlayer.getMaxHealth()) {
					continue; // Doesn't display empty extra hearts.
				}
				int idx = i * 2 + 1;
				int iconX = 16;
				if (mc.thePlayer.isPotionActive(Potion.poison)) {
					iconX += 36;
				} else if (mc.thePlayer.isPotionActive(Potion.wither)) {
					iconX += 72;
				}

				int x = left + i * 8 - (80 * row);
				int y = top;
				if (health <= 4) {
					y = top + rand.nextInt(2);
				}
				if (i == regen) {
					y -= 2;
				}

				byte iconY = 0;
				if (mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
					iconY = 5;
				}
				drawTexturedModalRect(x, y, 16 + (highlight ? 9 : 0), 9 * iconY, 9, 9);

				if (highlight) {
					// if (idx < healthLast) {
					// drawTexturedModalRect(x, y, iconX + 54, 9 * iconY, 9, 9);
					// } else if (idx == healthLast) {
					// drawTexturedModalRect(x, y, iconX + 63, 9 * iconY, 9, 9);
					// }
				}

				if (idx < health) {
					drawTexturedModalRect(x, y, iconX + 36, 9 * iconY, 9, 9);
				} else if (idx == health) {
					drawTexturedModalRect(x, y, iconX + 45, 9 * iconY, 9, 9);
				}

				if (isMinimalGui) {
					int displayedRow = row + 1;
					String text = "" + displayedRow;
					int adjustedColorX = colorX;
					if (displayedRow >= 10) {
						adjustedColorX -= 6;
					}
					// If it's greater than 100, it's also great than 10, so -8 will happen twice.
					if (displayedRow >= 100) {
						adjustedColorX -= 6;
					}
					if (displayedRow >= 1000) {
						adjustedColorX -= 6;
					}
					if (displayedRow >= 10000) {
						text = "9999+";
						adjustedColorX -= 6;
					}
					mc.fontRendererObj.drawString(text, adjustedColorX + 1, colorY, 0);
					mc.fontRendererObj.drawString(text, adjustedColorX - 1, colorY, 0);
					mc.fontRendererObj.drawString(text, adjustedColorX, 0xF00000, 0);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					bind(Gui.icons);
				}
			}
			mc.mcProfiler.endSection();
		}
	}
}
