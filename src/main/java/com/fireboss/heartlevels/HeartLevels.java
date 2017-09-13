//package com.fireboss.heartlevels;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import com.fireboss.heartlevels.commands.HeartLevelCommands;
//import com.fireboss.heartlevels.enchantments.ArmorHealthEnchantment;
//import com.fireboss.heartlevels.gui.HeartLevelsGui;
//import com.fireboss.heartlevels.gui.HeartLevelsGuiHandler;
//import com.fireboss.heartlevels.handlers.FMLEventHandler;
//import com.fireboss.heartlevels.handlers.ForgeEventHandler;
//import com.fireboss.heartlevels.handlers.PlayerHandler;
//import com.fireboss.heartlevels.init.InitChestLoot;
//import com.fireboss.heartlevels.init.InitItems;
//import com.fireboss.heartlevels.proxy.ICommonProxy;
//
//import net.minecraft.enchantment.Enchantment;
//import net.minecraft.entity.ai.attributes.AttributeModifier;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.common.config.Configuration;
//import net.minecraftforge.fml.client.FMLClientHandler;
//import net.minecraftforge.fml.client.event.ConfigChangedEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.common.Mod.EventHandler;
//import net.minecraftforge.fml.common.Mod.Instance;
//import net.minecraftforge.fml.common.SidedProxy;
//import net.minecraftforge.fml.common.event.FMLInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
//import net.minecraftforge.fml.common.network.NetworkRegistry;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MCVERSIONS)
//public class HeartLevels {
//
//	public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);
//
//	@Instance(Reference.MOD_ID)
//	public static HeartLevels instance;
//
//	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
//	public static ICommonProxy proxy;
//
//	public static int[] LevelRampInt;
//	public static Enchantment armorEnchantment;
//	public static AttributeModifier healthMod;
//
//	@EventHandler
//	public void preInit(FMLPreInitializationEvent event) {
//		Config.config = new Configuration(event.getSuggestedConfigurationFile());
//		Config.config.load();
//		Config.SetupConfig();
//		InitItems.Create();
//		proxy.preInit();
//	}
//
//	@EventHandler
//	public void init(FMLInitializationEvent event) {
//		MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
//		MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
//		MinecraftForge.EVENT_BUS.register(instance);
//		if (Config.heartItems.getBoolean()) {
//			InitChestLoot.AddChestLoot();
//		}
//		if (!Config.rpgMode.getBoolean()) {
//			LevelRampInt = new int[1];
//			LevelRampInt[0] = -1; // Stops RPG
//		} else {
//			LevelRampInt = Config.levelRamp.getIntList();
//		}
//		// What happens to users who had enchanted items then turn off enchantments?
//		if (Config.enchantsEnabled.getBoolean()) {
//			armorEnchantment = new ArmorHealthEnchantment(Config.armorEnchantID.getInt(), 4);
//		}
//		proxy.init();
//		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new HeartLevelsGuiHandler());
//	}
//
//	private static Object playerTracker;
//
//	@EventHandler
//	public void postInit(FMLPostInitializationEvent event) {
//		playerTracker = new PlayerHandler();
//		MinecraftForge.EVENT_BUS.register(playerTracker);
//		proxy.postInit();
//	}
//
//	@EventHandler
//	public void serverLoad(FMLServerStartingEvent event) {
//		event.registerServerCommand(new HeartLevelCommands());
//	}
//
//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent
//	public void onKeyEvent(KeyInputEvent event) {
//		if (HeartLevelsGui.keyBinding.isPressed()) {
//			FMLClientHandler.instance().getClient().thePlayer.openGui(instance, HeartLevelsGui.id,
//					FMLClientHandler.instance().getClient().theWorld, 0, 0, 0);
//		}
//	}
//
//	@SubscribeEvent
//	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
//		if (event.modID.equals(Reference.MOD_ID)) {
//			Config.SetupConfig();
//		}
//	}
//
//}
