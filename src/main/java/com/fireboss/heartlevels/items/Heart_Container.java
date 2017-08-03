package com.fireboss.heartlevels.items;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.PlayerStats;
import com.fireboss.heartlevels.Reference;
import com.fireboss.heartlevels.handlers.PlayerHandler;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class Heart_Container extends Item {

	public Heart_Container(String name) {
		this.setUnlocalizedName(name);
		this.setRegistryName(Reference.MOD_ID, name);
		this.setCreativeTab(CreativeTabs.tabMisc);
	}

	public ItemStack onItemRightClick(ItemStack items, World world, EntityPlayer player) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side.isClient()) {
			return items;
		}
		if (Config.heartItems.getBoolean() == false) {
			player.addChatComponentMessage(
					new ChatComponentText("This items are disabled in config! You cannot use it."));
			return items;
		}
		items.stackSize--;
		// Check -- In this case, heart container acts as a full heal item.
		if (Config.maxHearts.getInt() != -1 && Config.maxHearts.getInt() != 0
				&& player.getMaxHealth() + 2 > Config.maxHearts.getInt() * 2) {
			player.addChatComponentMessage(new ChatComponentText("Your Life is fully replenished!"));
			player.setHealth(player.getMaxHealth());
			return items;
		}
		// Otherwise, it acts normally.
		double updatedModifier = 2;
		try {
			updatedModifier = player.getEntityAttribute(SharedMonsterAttributes.maxHealth)
					.getModifier(PlayerHandler.HeartLevelsID).getAmount() + 2.0;
		} catch (Exception e) {
		}
		PlayerHandler.addHealthModifier(player, updatedModifier);
		player.addChatComponentMessage(
				new ChatComponentText("Your Life has increased by one and is also now fully replenished!"));
		PlayerStats stats = PlayerStats.getPlayerStats(player.getUUID(player.getGameProfile()).toString());
		stats.healthmod = updatedModifier;
		stats.heartContainers++;
		player.setHealth(player.getMaxHealth());
		return items;
	}

}
