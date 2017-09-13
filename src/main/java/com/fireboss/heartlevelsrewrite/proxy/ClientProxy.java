package com.fireboss.heartlevelsrewrite.proxy;

import com.fireboss.heartlevelsrewrite.gui.HeartLevelsHUD;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements ICommonProxy {

	@Override
	public void preInit() {

	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new HeartLevelsHUD(Minecraft.getMinecraft()));
	}

	@Override
	public void postInit() {

	}

}
