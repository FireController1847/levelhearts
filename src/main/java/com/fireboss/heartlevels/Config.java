package com.fireboss.heartlevels;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	private static String category = Configuration.CATEGORY_GENERAL;
	private static String guiCategory = "gui_options";

	private static Property startHearts;
	private static Property maxHearts;
	private static Property levelRamp;
	private static Property heartItems;
	private static Property rpgMode;
	private static Property hardcoreMode;
	private static Property multiplier;
	private static Property enchantsEnabled;
	private static Property customGui;
	private static Property minimalGui;
	private static Property armorEnchantID;
	private static Property guiKeyBinding;

	public static Configuration config;

	public static void SetupConfig() {
		startHearts = config.get(category, "Starting Hearts", 20);
		startHearts.comment = "";

		maxHearts = config.get(category, "Maximum Hearts", -1);
		maxHearts.comment = "";

		levelRamp = config.get(category, "Level Ramp",
				"1,5,10,15,20,25,30,34,38,42,46,50,53,56,59,62,64,66,68,70,75,80,85,90,95,100,110,120,130,140,150,160,170,180,190,200,210,220,230,240,250,260,270,280,290,300,310,320,330,340,350,360,370,380,390,400,420,440,460,500");
		levelRamp.comment = "";

		heartItems = config.get(category, "Heart Container And Pieces", true);
		heartItems.comment = "";

		rpgMode = config.get(category, "RPG Mode", true);
		rpgMode.comment = "";
		
		hardcoreMode = config.get(category, "Hardcore Mode", false);
		hardcoreMode.comment = "";

		multiplier = config.get(category, "Heart Item Multiplier", 10);
		multiplier.comment = "";
		
		enchantsEnabled = config.get(category, "Enchantments Enabled", true);
		enchantsEnabled.comment = "";
		
		customGui = config.get(guiCategory, "More Health HUD", true);
		customGui.comment = "";
		
		minimalGui = config.get(guiCategory, "Minimal HUD", true);
		minimalGui.comment = "";
		
		armorEnchantID = config.get(category, "Armor Enchantments ID", 120);
		armorEnchantID.comment = "";
		
		guiKeyBinding = config.get(category, "More Health Stats Key", "H");
		guiKeyBinding.comment = "";

		if (config.hasChanged()) {
			config.save();
		}
	}

}
