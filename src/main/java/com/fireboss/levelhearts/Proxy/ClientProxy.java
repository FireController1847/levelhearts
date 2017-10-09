package com.fireboss.levelhearts.Proxy;

import com.fireboss.levelhearts.GUI.HUDModifier;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy implements CommonProxy {

	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new HUDModifier());
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void postInit() {
		// TODO Auto-generated method stub

	}

}
