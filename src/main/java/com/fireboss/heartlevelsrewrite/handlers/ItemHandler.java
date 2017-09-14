package com.fireboss.heartlevelsrewrite.handlers;

import com.fireboss.heartlevelsrewrite.items.HeartContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemHandler {

	public static Item heart_container;

	public static void createItems() {
		heart_container = new HeartContainer("heart_container");
		registerItems();
	}

	public static void registerItems() {
		GameRegistry.registerItem(heart_container);
		registerRecipies();
	}

	public static void registerRecipies() {

	}

	public static void renderItems() {
		renderItem(heart_container);
	}

	private static void renderItem(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
