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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		InitItems.Render();
		MinecraftForge.EVENT_BUS.register(new HeartLevelsHUD(Minecraft.getMinecraft()));
		HeartLevelsGui.keyBinding = new KeyBinding("key.hud.desc",
				Keyboard.getKeyIndex(Config.guiKeyBinding.getString().toUpperCase()), "key.heartlevels.category");
		ClientRegistry.registerKeyBinding(HeartLevelsGui.keyBinding);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

}
