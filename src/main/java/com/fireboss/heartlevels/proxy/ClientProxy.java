package com.fireboss.heartlevels.proxy;

import org.lwjgl.input.Keyboard;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.gui.HeartLevelsGui;
import com.fireboss.heartlevels.gui.HeartLevelsHUD;
import com.fireboss.heartlevels.init.InitItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ClientProxy implements ICommonProxy {

	public void preInit() {

	}

	public void init() {
		InitItems.Render();
		MinecraftForge.EVENT_BUS.register(new HeartLevelsHUD(Minecraft.getMinecraft()));
		HeartLevelsGui.keyBinding = new KeyBinding("HUD",
				Keyboard.getKeyIndex(Config.guiKeyBinding.getString().toUpperCase()), "Heart Levels");
		ClientRegistry.registerKeyBinding(HeartLevelsGui.keyBinding);
	}

	public void postInit() {

	}

}
