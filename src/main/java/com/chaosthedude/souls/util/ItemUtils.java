package com.chaosthedude.souls.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Multimap;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemUtils {

	public static ItemStack getRandomArmor(EntityEquipmentSlot armorType, Random rand, List<ItemStack> items) {
		final List<ItemStack> armors = new ArrayList<>();
		for (ItemStack stack : items) {
			if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) stack.getItem();
				if (armor.armorType == armorType) {
					armors.add(stack);
				}
			}
		}

		if (!armors.isEmpty()) {
			return (ItemStack) getRandomListItem(rand, armors);
		}

		return ItemStack.EMPTY;
	}

	public static ItemStack getHighestProtectionArmor(EntityEquipmentSlot armorType, List<ItemStack> items) {
		ItemStack bestArmor = ItemStack.EMPTY;
		int bestProtection = -1;
		for (ItemStack stack : items) {
			if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) stack.getItem();
				if (armor.armorType == armorType) {
					if (bestArmor.isEmpty() || armor.damageReduceAmount > bestProtection) {
						bestArmor = stack;
						bestProtection = armor.damageReduceAmount;
					}
				}
			}
		}

		return bestArmor;
	}

	public static ItemStack getRandomWeaponForHand(EntityEquipmentSlot hand, Random rand, List<ItemStack> items) {
		List<ItemStack> weapons = new ArrayList<ItemStack>();
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				double damage = getDamageDealtBy(stack, hand);
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

	public static ItemStack getHighestDamageItemForHand(EntityEquipmentSlot hand, List<ItemStack> items) {
		ItemStack highestDamageItem = ItemStack.EMPTY;
		double highestDamage = -1D;
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				if (highestDamageItem.isEmpty()) {
					highestDamageItem = stack;
				} else {
					double damage = getDamageDealtBy(stack, hand);
					if (damage > highestDamage) {
						highestDamage = damage;
						highestDamageItem = stack;
					}
				}
			}
		}

		return highestDamageItem;
	}

	public static double getDamageDealtBy(ItemStack stack, EntityEquipmentSlot hand) {
		Multimap<String, AttributeModifier> multimap = stack.getItem().getItemAttributeModifiers(hand);
		Collection<AttributeModifier> modifiers = multimap.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
		if (modifiers != null) {
			for (AttributeModifier mod : modifiers) {
				if (mod.getName().equals("Weapon modifier")) {
					return mod.getAmount();
				} else if (mod.getName().equals("Tool modifier")) {
					return mod.getAmount();
				}
			}
		}

		return 0D;
	}

	public static int getNumberOfItems(EntityPlayer player, Item item) {
		int amount = 0;
		for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
			final ItemStack stack = player.inventory.mainInventory.get(i);
			if (!stack.isEmpty() && stack.getItem() == item) {
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
		if (player.inventory.mainInventory.get(slot).isEmpty()
				|| player.inventory.mainInventory.get(slot).getCount() == 0) {
			return true;
		}

		return false;
	}

	public static void addItemDesc(List<String> info, String desc, Object... args) {
		for (String s : StringUtils.parseStringIntoLength(StringUtils.localize(desc, args), 25)) {
			info.add(TextFormatting.GRAY.toString() + s);
		}
	}

}
