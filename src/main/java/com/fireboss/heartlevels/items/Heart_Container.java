package com.fireboss.heartlevels.items;

import com.fireboss.heartlevels.HeartLevels;
import com.fireboss.heartlevels.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class Heart_Container extends Item {

	public Heart_Container(String name) {
		this.setUnlocalizedName(name);
		this.setRegistryName(Reference.MOD_ID, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

}
