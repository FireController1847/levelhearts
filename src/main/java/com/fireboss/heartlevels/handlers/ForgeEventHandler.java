package com.fireboss.heartlevels.handlers;

import com.fireboss.heartlevels.Config;
import com.fireboss.heartlevels.init.InitItems;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandler {

	/**
	 * When an entity dies, this gets called with the event that occured. If the
	 * entities happen to be Dragon or Wither, they will drop heart containers.
	 * 
	 * @param event
	 */

	@SubscribeEvent
	public void onEntityLivingDeath(LivingDeathEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			// Bosses drop 1 hear container each.
			// Must be manually tested.
			if (Config.heartItems.getBoolean()) {
				if (event.entity instanceof EntityDragon || event.entity instanceof EntityWither) {
					event.entity.entityDropItem(new ItemStack(InitItems.heart_container), 0.0F);
				}
			}
		}
	}

}
