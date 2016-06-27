package com.chaosthedude.souls.util;

import net.minecraft.item.ItemStack;

  /* * * * * * * * * * * * ARMOR INDEXES CHART * * * * * * * * * * * * * * * *\
  ** Player Armor Indexes:    * Armor Types:    * Equipment Armor Indexes:   **
  ** 0: Boots                 * 0: Helmet       * 1: Boots                   **
  ** 1: Leggings              * 1: Chestplate   * 2: Leggings                **
  ** 2: Chestplate            * 2: Leggings     * 3: Chestplate              **
  ** 3: Helmet                * 3: Boots        * 4: Helmet                  **
  \*                                                                         */

public class Equipment {

	public static final int WEAPON = 0;
	public static final int BOOTS = 1;
	public static final int LEGGINGS = 2;
	public static final int CHESTPLATE = 3;
	public static final int HELMET = 4;

	public ItemStack weapon;
	public ItemStack boots;
	public ItemStack leggings;
	public ItemStack chestplate;
	public ItemStack helmet;

	public Equipment() {
	}

	public void set(int index, ItemStack stack) {
		switch (index) {
		case WEAPON:
			weapon = stack;
		case BOOTS:
			boots = stack;
		case LEGGINGS:
			leggings = stack;
		case CHESTPLATE:
			chestplate = stack;
		case HELMET:
			helmet = stack;
		}
	}

	public ItemStack getEquipmentFromIndex(int index) {
		switch (index) {
		case WEAPON:
			return weapon;
		case BOOTS:
			return boots;
		case LEGGINGS:
			return leggings;
		case CHESTPLATE:
			return chestplate;
		case HELMET:
			return helmet;

		default:
			return null;
		}
	}

	public static int getPlayerArmorIndexFromEquipmentIndex(int index) {
		return index - 1;
	}

	public static int getEquipmentIndexFromPlayerArmorIndex(int index) {
		return index + 1;
	}

	public static int getEquipmentIndexFromArmorType(int armorType) {
		return 4 - armorType;
	}

}
