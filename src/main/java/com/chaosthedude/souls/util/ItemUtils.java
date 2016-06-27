package com.chaosthedude.souls.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class ItemUtils {

	public static ItemStack getRandomArmor(int armorType, Random rand, List<ItemStack> items) {
		if (items.isEmpty()) {
			return null;
		}

		final List<ItemStack> armors = new ArrayList<ItemStack>();
		for (ItemStack stack : items) {
			if (stack != null && stack.getItem() instanceof ItemArmor) {
				final ItemArmor armor = (ItemArmor) stack.getItem();
				if (armor.armorType == armorType) {
					armors.add(stack);
				}
			}
		}

		if (!armors.isEmpty()) {
			return (ItemStack) getRandomListItem(rand, armors);
		}

		return null;
	}

	public static ItemStack getHighestProtectionArmor(int armorType, List<ItemStack> items) {
		if (items.isEmpty()) {
			return null;
		}

		ItemStack bestArmor = null;
		int bestProtection = -1;
		for (ItemStack stack : items) {
			if (stack != null && stack.getItem() instanceof ItemArmor) {
				final ItemArmor armor = (ItemArmor) stack.getItem();
				if (armor.armorType == armorType) {
					if (bestArmor == null || armor.damageReduceAmount > bestProtection) {
						bestArmor = stack;
						bestProtection = armor.damageReduceAmount;
					}
				}
			}
		}

		return bestArmor;
	}

	public static ItemStack getRandomWeapon(Random rand, List<ItemStack> items) {
		if (items.isEmpty()) {
			return null;
		}

		List<ItemStack> weapons = new ArrayList<ItemStack>();
		for (ItemStack stack : items) {
			if (stack != null) {
				final double damage = getDamageDealtBy(stack);
				if (damage > 1) {
					weapons.add(stack);
				}
			}
		}

		if (!weapons.isEmpty()) {
			return (ItemStack) getRandomListItem(rand, weapons);
		}

		return (ItemStack) getRandomListItem(rand, items);
	}

	public static ItemStack getHighestDamageItem(List<ItemStack> items) {
		ItemStack highestDamageItem = null;
		double highestDamage = -1D;
		for (ItemStack stack : items) {
			if (stack != null) {
				if (highestDamageItem == null) {
					highestDamageItem = stack;
				} else {
					final double damage = getDamageDealtBy(stack);
					if (damage > highestDamage) {
						highestDamage = damage;
						highestDamageItem = stack;
					}
				}
			}
		}

		return highestDamageItem;
	}

	public static double getDamageDealtBy(ItemStack stack) {
		double damage = -1D;
		final Multimap attributes = stack.getAttributeModifiers();
		final Collection collection = attributes.get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
		for (Object o : collection) {
			if (o instanceof AttributeModifier) {
				final AttributeModifier modifier = (AttributeModifier) o;
				if (modifier.getName().equals(Strings.ATTACK_DAMAGE) || modifier.getName().equals(Strings.WEAPON_MODIFIER) || modifier.getName().equals(Strings.TOOL_MODIFIER)) {
					damage = modifier.getAmount();
				}
			}
		}

		return damage;
	}

	public static int getNumberOfItems(EntityPlayer player, Item item) {
		int amount = 0;
		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			final ItemStack stack = player.inventory.mainInventory[i];
			if (stack != null && stack.getItem() == item) {
				amount++;
			}
		}

		return amount;
	}

	public static void swapItems(List<ItemStack> items, int par1, int par2) {
		final ItemStack stack = items.get(par1);
		items.add(par1, items.get(par2));
		items.add(par2, stack);
	}

	public static Object getRandomListItem(List list) {
		return getRandomListItem(new Random(), list);
	}

	public static Object getRandomListItem(Random rand, List list) {
		int randInt = rand.nextInt(list.size());
		if (randInt > 0) {
			randInt--;
		}

		return list.get(randInt);
	}

	public static boolean slotIsEmpty(EntityPlayer player, int slot) {
		if (player.inventory.mainInventory[slot] == null || player.inventory.mainInventory[slot].stackSize == 0) {
			return true;
		}

		return false;
	}

	public static void addItemDesc(List info, String desc, Object... args) {
		for (String s : StringUtils.parseStringIntoLength(String.format(StringUtils.localize(desc), args), 25)) {
			info.add(EnumChatFormatting.GRAY.toString() + s);
		}
	}

}
