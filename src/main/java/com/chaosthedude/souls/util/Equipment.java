package com.chaosthedude.souls.util;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

  /* * * * * * * * * * * * ARMOR INDEXES CHART * * * * * * * * * * * * * * * *\
  ** Player Armor Indexes:    * Armor Types:    * Equipment Armor Indexes:   **
  ** 0: Boots                 * 0: Helmet       * 1: Boots                   **
  ** 1: Leggings              * 1: Chestplate   * 2: Leggings                **
  ** 2: Chestplate            * 2: Leggings     * 3: Chestplate              **
  ** 3: Helmet                * 3: Boots        * 4: Helmet                  **
  \*                                                                         */

public class Equipment {

	public static final int MAINHAND = 0;
	public static final int BOOTS = 1;
	public static final int LEGGINGS = 2;
	public static final int CHESTPLATE = 3;
	public static final int HELMET = 4;
	public static final int OFFHAND = 5;

	public ItemStack mainhand;
	public ItemStack boots;
	public ItemStack leggings;
	public ItemStack chestplate;
	public ItemStack helmet;
	public ItemStack offhand;

	public Equipment() {
	}

	public void set(int index, ItemStack stack) {
		switch (index) {
		case MAINHAND:
			mainhand = stack;
		case BOOTS:
			boots = stack;
		case LEGGINGS:
			leggings = stack;
		case CHESTPLATE:
			chestplate = stack;
		case HELMET:
			helmet = stack;
		case OFFHAND:
			offhand = stack;
		}
	}

	public ItemStack getEquipmentFromIndex(int index) {
		switch (index) {
		case MAINHAND:
			return mainhand;
		case BOOTS:
			return boots;
		case LEGGINGS:
			return leggings;
		case CHESTPLATE:
			return chestplate;
		case HELMET:
			return helmet;
		case OFFHAND:
			return offhand;

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

	public static EntityEquipmentSlot getSlotFromEquipmentIndex(int index) {
		if (index == EntityEquipmentSlot.MAINHAND.getSlotIndex()) {
			return EntityEquipmentSlot.MAINHAND;
		} else if (index == EntityEquipmentSlot.FEET.getSlotIndex()) {
			return EntityEquipmentSlot.FEET;
		} else if (index == EntityEquipmentSlot.LEGS.getSlotIndex()) {
			return EntityEquipmentSlot.LEGS;
		} else if (index == EntityEquipmentSlot.CHEST.getSlotIndex()) {
			return EntityEquipmentSlot.CHEST;
		} else if (index == EntityEquipmentSlot.HEAD.getSlotIndex()) {
			return EntityEquipmentSlot.HEAD;
		} else if (index == EntityEquipmentSlot.OFFHAND.getSlotIndex()) {
			return EntityEquipmentSlot.OFFHAND;
		}

		return null;
	}

}
