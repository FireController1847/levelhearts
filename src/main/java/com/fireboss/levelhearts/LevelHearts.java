package com.fireboss.levelhearts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fireboss.levelhearts.Handlers.PlayerEventHandler;
import com.fireboss.levelhearts.Proxy.CommonProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class LevelHearts {

	public static final Logger logger = LogManager.getLogger(Reference.INITIALS);

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}

	public static void debug(String str) {
		logger.info(str);
	}

	public static void debugErr(Exception e) {
		logger.error(e);
	}

}
