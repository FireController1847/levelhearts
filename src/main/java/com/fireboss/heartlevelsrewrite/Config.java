package com.fireboss.heartlevelsrewrite;

import java.util.Arrays;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	// Categories
	private static String genCat = Configuration.CATEGORY_GENERAL;
	private static String healCat = "health";
	private static String guiCat = "gui";

	// General
	public static Property debug;

	// Health
	public static Property startHealth;
	public static Property maxHealth;
	public static Property hardcoreMode;

	// GUI
	public static Property customHud;
	public static Property minimalHud;
	public static Property minimalHudNumberPos;

	// Setup
	public static Configuration config;

	public static void setupConfig() {
		// General
		debug = config.get(genCat, "Debug Mode", false);
		debug.comment = "Set to true to get a bunch of extra output in console. Default: false";

		// Health
		startHealth = config.get(healCat, "Starting Health", 20);
		startHealth.comment = "The amount in half-hearts the user should start with. Default: 20";

		maxHealth = config.get(healCat, "Maximum Health", -1);
		maxHealth.comment = "The maximum health a user should have in half-hearts. Default: 1000";
		
		hardcoreMode = config.get(healCat, "Hardcore Mode", false);
		hardcoreMode.comment = "Resets your hearts back to the starting value on death. Default: false.";
		
		// GUI Stuff
		customHud = config.get(guiCat, "Custom HUD", true);
		customHud.comment = "Enables the mods Custom HUD rendering engine. Disabling this will use Minecraft's default HUD rendering engine. Default: true";
		
		minimalHud = config.get(guiCat, "Minimal HUD", false);
		minimalHud.comment = "Enables the Custom HUD, but instead of stacking hearts it will show a number next to them. Default: false";
		
		minimalHudNumberPos = config.get(guiCat, "Minimal HUD Number Position", "left");
		minimalHudNumberPos.setValidValues(new String[] {"right", "left"});
		minimalHudNumberPos.comment = "Positions the number in the Minimal HUD. Can only be set to 'left' or 'right'. Default: 'left'";
		
		// Save
		if (config.hasChanged()) {
			config.save();
		}
	}
	
	public static void verifyConfig() {
		if (!Arrays.asList(minimalHudNumberPos.getValidValues()).contains(minimalHudNumberPos.getString())) {
			HeartLevels.logger.error("Invalid value for Minimal HUD Number Position in config. Resetting to default value!");
//			minimalHudNumberPos.setToDefault(); // Doesn't set changed to true?
			minimalHudNumberPos.set(minimalHudNumberPos.getDefault());
		}
		
		// Save
		if (config.hasChanged()) {
			config.save();
			System.out.println("Saving config");
		}
	}
}
