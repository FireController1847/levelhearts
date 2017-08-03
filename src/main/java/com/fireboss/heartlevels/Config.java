package com.fireboss.heartlevels;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	private static String category = Configuration.CATEGORY_GENERAL;
	private static String guiCategory = "gui";
	private static String msgCategory = "messages";

	// General
	public static Property startHearts;
	public static Property maxHearts;
	public static Property levelRamp;
	public static Property heartItems;
	public static Property rpgMode;
	public static Property hardcoreMode;
	public static Property multiplier;
	public static Property enchantsEnabled;
	public static Property armorEnchantID;

	// GUI
	public static Property customGui;
	public static Property minimalGui;
	public static Property guiKeyBinding;

	// Messages
	public static Property onNewHeart;

	public static Configuration config;

	public static void SetupConfig() {

		// General Stuff

		startHearts = config.get(category, "Starting Hearts", 10);
		startHearts.comment = "The amount of hearts the user will start with.";

		maxHearts = config.get(category, "Maximum Hearts", -1);
		maxHearts.comment = "The maximum amount of hearts a user is allowed to have. Set to -1 to disable.";

		levelRamp = config.get(category, "Level Ramp",
				new int[] { 1, 5, 10, 15, 20, 25, 30, 34, 38, 42, 46, 50, 53, 56, 59, 62, 64, 66, 68, 70, 75, 80, 85,
						90, 95, 100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260,
						270, 280, 290, 300, 310, 320, 330, 340, 350, 360, 370, 380, 390, 400, 420, 440, 460, 500 });
		levelRamp.comment = "The levels in which the user 'levels up' (gains a heart).";

		heartItems = config.get(category, "Heart Container And Pieces", true);
		heartItems.comment = "Set to false to disable all heart containers from even registering into game.";

		rpgMode = config.get(category, "RPG Mode", true);
		rpgMode.comment = "Set to false to completely disable level ramping.";

		hardcoreMode = config.get(category, "Hardcore Mode", false);
		hardcoreMode.comment = "Resets a user's heart level to starting hearts on death.";

		multiplier = config.get(category, "Heart Item Multiplier", 10);
		multiplier.comment = "The multiplier on how many hears the heart container gives you.";

		enchantsEnabled = config.get(category, "Enchantments Enabled", true);
		enchantsEnabled.comment = "Set to false to disable the Hearts enchantment on Armor";

		armorEnchantID = config.get(category, "Armor Enchantments ID", 120);
		armorEnchantID.comment = "The ID of the Hearts enchantment. Disabled if the above is set to false.";

		// GUI Stuff

		guiKeyBinding = config.get(guiCategory, "More Health Stats Key", "H");
		guiKeyBinding.comment = "";

		customGui = config.get(guiCategory, "More Health HUD", true);
		customGui.comment = "";

		minimalGui = config.get(guiCategory, "Minimal HUD", true);
		minimalGui.comment = "";

		// Message Stuff

		onNewHeart = config.get(msgCategory, "On New Heart",
				"Your life has increased by one and is also now fully replenished!");

		if (config.hasChanged()) {
			config.save();
		}
	}

}
