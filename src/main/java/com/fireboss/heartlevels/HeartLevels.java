package com.fireboss.heartlevels;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fireboss.heartlevels.handlers.PlayerHandler;
import com.fireboss.heartlevels.init.InitChestLoot;
import com.fireboss.heartlevels.init.InitItems;
import com.fireboss.heartlevels.proxy.CommonProxy;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MCVERSIONS)
public class HeartLevels {

	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

	@Instance(Reference.MOD_ID)
	public static HeartLevels instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static CommonProxy proxy;

	public static AttributeModifier healthMod;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.config = new Configuration(event.getSuggestedConfigurationFile());
		Config.config.load();
		Config.SetupConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		InitItems.Create();
		proxy.RenderItems();
		if (Config.heartItems.getBoolean()) {
			InitChestLoot.AddChestLoot();
		}
	}
	
	private static Object playerTracker;

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		playerTracker = new PlayerHandler();
		MinecraftForge.EVENT_BUS.register(playerTracker);
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equals(Reference.MOD_ID)) {
			Config.SetupConfig();
		}
	}

}
