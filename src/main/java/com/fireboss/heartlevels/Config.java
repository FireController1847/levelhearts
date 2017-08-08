package com.fireboss.heartlevels;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	private static String category = Configuration.CATEGORY_GENERAL;
	private static String healthCategory = "health";
	private static String guiCategory = "gui";

	// General
	public static Property heartItems;
	public static Property rpgMode;
	public static Property hardcoreMode;
	public static Property multiplier;
	public static Property enchantsEnabled;
	public static Property armorEnchantID;
	public static Property debug;
	
	// Health
	public static Property startHearts;
	public static Property maxHearts;
	public static Property levelRamp;
	public static Property heartGain;
	public static Property expMultiplier;

	// GUI
	public static Property customGui;
	public static Property minimalGui;
	public static Property guiKeyBinding;

	public static Configuration config;

	public static void SetupConfig() {

		// General Stuff
		
		debug = config.get(category, "Debug Mode", false);
		debug.comment = "Set to true to get a bunch of extra output in console. Please enable this when reporting bugs.";

		heartItems = config.get(category, "Heart Container And Pieces", true);
		heartItems.comment = "Set to false to disable all heart containers from even registering into game.";

		rpgMode = config.get(category, "RPG Mode", true);
		rpgMode.comment = "Set to false to completely disable level ramping.";

		hardcoreMode = config.get(category, "Hardcore Mode", false);
		hardcoreMode.comment = "Resets a user's heart level to starting hearts on death.";

		multiplier = config.get(category, "Heart Item Multiplier", 10);
		multiplier.comment = "The multiplier on how often you'll find heart containers in dungeons.";

		enchantsEnabled = config.get(category, "Enchantments Enabled", true);
		enchantsEnabled.comment = "Set to false to disable the Hearts enchantment on Armor";

		armorEnchantID = config.get(category, "Armor Enchantments ID", 120);
		armorEnchantID.comment = "The ID of the Hearts enchantment. Disabled if the above is set to false.";
		
		// Health
		
		startHearts = config.get(healthCategory, "Starting Hearts", 20);
		startHearts.comment = "The amount of half-hearts the user will start with. Default Minecraft hearts is 20 (or 10 full hearts).";

		maxHearts = config.get(healthCategory, "Maximum Hearts", -1);
		maxHearts.comment = "The maximum amount of half-hearts a user is allowed to have. Set to -1 to disable.";

		levelRamp = config.get(healthCategory, "Level Ramp",
				new int[] { 1, 5, 10, 15, 20, 25, 30, 34, 38, 42, 46, 50, 53, 56, 59, 62, 64, 66, 68, 70, 75, 80, 85,
						90, 95, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260,
						270, 280, 290, 300, 310, 320, 330, 340, 350, 360, 370, 380, 390, 400, 420, 440, 460, 500 });
		levelRamp.comment = "The levels in which the user 'levels up' (gains a heart).";
		
		heartGain = config.get(healthCategory, "Heart Gain", 2);
		heartGain.comment = "When leveling up, how many half-hearts does the user gets?";
		
		expMultiplier = config.get(healthCategory, "EXP Multiplier", 1.0);
		expMultiplier.comment = "Set to 2 to double the amount of EXP the user gets, 3 to triple, and so on. Decimals work too. Do not set below 0.";

		// GUI Stuff

		guiKeyBinding = config.get(guiCategory, "Heart Levels Stats Key", "H");
		guiKeyBinding.comment = "The key you'll press to get the Heart Levels menu.";

		customGui = config.get(guiCategory, "Heart Levels HUD", true);
		customGui.comment = "Disabling will only show 10 full hearts even if you have 20 full hearts.";

		minimalGui = config.get(guiCategory, "Minimal HUD", false);
		minimalGui.comment = "A number shows up next to the full hearts saying how many hearts you have by tens."
				+ " So if you have 25 full hearts, 5 hearts will show on the HUD and a 2 will show up next to it.";

		if (config.hasChanged()) {
			config.save();
		}
	}

}
