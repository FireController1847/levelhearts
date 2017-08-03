package com.fireboss.heartlevels.init;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.items.Heart_Container;
import com.fireboss.heartlevels.items.Heart_Piece;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class InitItems {

	public static Item heart_container;
	public static Item heart_piece;
	public static Item cursed_heart;

	public static void Create() {
		heart_container = new Heart_Container("heart_container");
		heart_piece = new Heart_Piece("heart_piece");
		Register();
	}

	public static void Register() {
		if (Config.heartItems.getBoolean()) {
			GameRegistry.registerItem(heart_container);
			GameRegistry.registerItem(heart_piece);
			Recipies();
		}
	}

	public static void Recipies() {
		GameRegistry.addRecipe(new ItemStack(heart_container), new Object[] { "XX", "XX", 'X', heart_piece });
	}

	public static void Render() {
		if (Config.heartItems.getBoolean()) {
			RenderItem(heart_container);
			RenderItem(heart_piece);
		}
	}

	public static void RenderItem(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
