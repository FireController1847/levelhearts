package com.fireboss.heartlevels.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class HeartLevelsGuiFactory implements IModGuiFactory {
	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ModConfigGUI.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class ModConfigGUI extends GuiConfig {

		public ModConfigGUI(GuiScreen parent) {
			super(parent, getConfigElements(), Reference.MOD_ID, false, false,
					GuiConfig.getAbridgedConfigPath(Config.config.toString()));
		}

		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.addAll(
					new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
			list.addAll(new ConfigElement(Config.config.getCategory("gui")).getChildElements());
			return list;
		}
	}
}