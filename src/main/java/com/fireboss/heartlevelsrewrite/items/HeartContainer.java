package com.fireboss.heartlevelsrewrite.items;

import com.fireboss.heartlevelsrewrite.Config;
import com.fireboss.heartlevelsrewrite.Reference;
import com.fireboss.heartlevelsrewrite.handlers.PlayerHandler;
import com.fireboss.heartlevelsrewrite.handlers.PlayerStats;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class HeartContainer extends Item {

	public HeartContainer(String name) {
		this.setUnlocalizedName(name);
		this.setRegistryName(Reference.MOD_ID, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack items, World world, EntityPlayer player) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) return items;
		items.stackSize--;
		if (Config.maxHealth.getInt() > 0 && player.getMaxHealth() + 2 > Config.maxHealth.getInt()) {
			player.setHealth(player.getMaxHealth());
			return items;
		}
		double mod = PlayerHandler.updateModifier(player, 2);
		PlayerHandler.addOrReloadHealthModifier(player, mod);
		PlayerStats stats = PlayerHandler.getPlayerStats(player);
		stats.modifier = mod;
		stats.heartContainers++;
		player.setHealth(player.getMaxHealth());
		return items;
	}

}
