package com.fireboss.heartlevels.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.util.StatCollector;

public class ArmorHealthEnchantment extends Enchantment {

	public ArmorHealthEnchantment(int effectID, int weight) {
		super(effectID, null, weight, EnumEnchantmentType.ARMOR);
		this.setName("Hearts");
	}

	/**
	 * Returns the minimal value of enchantability needed on the enchantment level
	 * passed.
	 */
	@Override
	public int getMinEnchantability(int enchantLevel) {
		// I have enchant Levels of 1-5, so the min level of enchantability needed is 10
		// at I enchantment
		// and 30 at V enchantment
		return 5 + 5 * enchantLevel;
	}

	/**
	 * Returns the maximum value of enchantability needed on the enchantment level
	 * passed.
	 */
	@Override
	public int getMaxEnchantability(int enchantLevel) {
		return getMinEnchantability(enchantLevel) + 50;
	}

	/**
	 * Returns the maximum level that the enchantment can have.
	 */
	@Override
	public int getMaxLevel() {
		return 5;
	}

}
