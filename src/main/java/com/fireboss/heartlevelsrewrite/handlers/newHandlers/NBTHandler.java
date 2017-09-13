package com.fireboss.heartlevelsrewrite.handlers.newHandlers;

import com.fireboss.heartlevelsrewrite.HeartLevels;
import com.fireboss.heartlevelsrewrite.Reference;

import net.minecraft.nbt.NBTTagCompound;

public class NBTHandler {

	public static NBTTagCompound createNewTag(NBTTagCompound tags) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("version", Reference.VERSION);
		tags.setTag(Reference.NBT_TAG, tag);
		HeartLevels.debug("A new NBT tag was created.");
		return tag;
	}

	public static NBTTagCompound getTag(NBTTagCompound tags, boolean isCompound) {
		NBTTagCompound tag;
		if (isCompound) {
			tag = (NBTTagCompound) tags.getCompoundTag(Reference.NBT_TAG);
		} else {
			tag = (NBTTagCompound) tags.getTag(Reference.NBT_TAG);
		}
		return tag;
	}

}
