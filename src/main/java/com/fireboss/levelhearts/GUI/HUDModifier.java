package com.fireboss.levelhearts.GUI;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUDModifier extends Gui {

	private Minecraft mc;
	protected final Random rand = new Random();

	public HUDModifier() {
		this.mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent
	public void modifyAirHUD(RenderGameOverlayEvent.Pre event) {
		EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.AIR) && player.getMaxHealth() > 20.0F) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("air");
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();
			GlStateManager.enableBlend();
			int left = width / 2 + 91;
			int top = height - 49;
			if (ForgeHooks.getTotalArmorValue(player) > 0) top = top - 10;
			if (player.isInsideOfMaterial(Material.WATER)) {
				int air = player.getAir();
				int full = MathHelper.ceil((double) (air - 2) * 10.0D / 300.0D);
				int partial = MathHelper.ceil((double) air * 10.0D / 300.0D) - full;
				for (int i = 0; i < full + partial; ++i) {
					drawTexturedModalRect(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
				}
			}
			GlStateManager.disableBlend();
			mc.mcProfiler.endSection();
		}
	}

	@SubscribeEvent
	public void modifyArmourHUD(RenderGameOverlayEvent.Pre event) {
		EntityPlayer player = (EntityPlayer) mc.getRenderViewEntity();
		if (event.getType().equals(RenderGameOverlayEvent.ElementType.ARMOR) && player.getMaxHealth() > 20.0F) {
			event.setCanceled(true);
			mc.mcProfiler.startSection("armor");
			ScaledResolution res = new ScaledResolution(mc);
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();
			GlStateManager.enableBlend();
			int left = width / 2 - 91 + 100;
			int top = height - 49;

			int level = ForgeHooks.getTotalArmorValue(mc.player);
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

			GlStateManager.disableBlend();
			mc.mcProfiler.endSection();
		}
	}

}
