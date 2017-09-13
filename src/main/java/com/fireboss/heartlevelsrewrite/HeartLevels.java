package com.fireboss.heartlevelsrewrite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fireboss.heartlevelsrewrite.handlers.PlayerHandler;
import com.fireboss.heartlevelsrewrite.proxy.ICommonProxy;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MCVERSIONS)
public class HeartLevels {

	// Logger
	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

	// Instance
	@Instance(Reference.MOD_ID)
	public static HeartLevels instance;

	// Proxy
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static ICommonProxy proxy;

	// Globals
	public static AttributeModifier health_modifier;

	// Events
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.config = new Configuration(event.getSuggestedConfigurationFile());
		Config.config.load();
		Config.setupConfig();
		Config.verifyConfig();
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new PlayerHandler());
		proxy.postInit();
	}
}
