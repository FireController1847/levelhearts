package com.fireboss.levelhearts.Handlers;

import com.fireboss.levelhearts.LevelHearts;
import com.fireboss.levelhearts.Reference;

import net.minecraft.nbt.NBTTagCompound;

public class NBTHandler {

	public static NBTTagCompound createNewTag(NBTTagCompound tags) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("version", Reference.VERSION);
		tags.setTag(Reference.NBT_NAME, tag);
		LevelHearts.debug("A new NBT tag was created.");
		return tag;
	}

	public static NBTTagCompound getTag(NBTTagCompound tags, boolean isCompound) {
		NBTTagCompound tag;
		if (isCompound) {
			tag = (NBTTagCompound) tags.getCompoundTag(Reference.NBT_NAME);
		} else {
			tag = (NBTTagCompound) tags.getTag(Reference.NBT_NAME);
		}
		return tag;
	}

}
