package com.fireboss.heartlevels.init;

import com.fireboss.heartlevels.items.Heart_Container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class InitItems {

	public static Item heart_container;
	public static Item heart_piece;
	public static Item cursed_heart;

	public static void Create() {
		heart_container = new Heart_Container("heart_container");
		Register();
	}

	public static void Register() {
		GameRegistry.registerItem(heart_container);
	}

	public static void Render() {
		RenderItem(heart_container);
	}

	public static void RenderItem(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
