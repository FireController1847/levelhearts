package com.fireboss.heartlevels.items;

import com.fireboss.heartlevels.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class Heart_Piece extends Item {
	
	public Heart_Piece(String name) {
		this.setUnlocalizedName(name);
		this.setRegistryName(Reference.MOD_ID, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

}
