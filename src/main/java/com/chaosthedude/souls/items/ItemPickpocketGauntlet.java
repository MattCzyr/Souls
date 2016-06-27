package com.chaosthedude.souls.items;

import java.util.List;

import com.chaosthedude.souls.Souls;
import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.util.ItemUtils;
import com.chaosthedude.souls.util.PlayerUtils;
import com.chaosthedude.souls.util.StringUtils;
import com.chaosthedude.souls.util.Strings;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemPickpocketGauntlet extends Item {

	public String name;

	private int successRate;
	private int maxCharges;

	public ItemPickpocketGauntlet(int maxCharges, double successRate, String name) {
		this.name = name;

		setUnlocalizedName(Souls.MODID + "." + name);
		setTextureName(Souls.MODID + ":" + name);
		setMaxStackSize(1);
		setNoRepair();
		setMaxCharges(maxCharges);
		setSuccessRate(successRate);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if (stack.getItem() == SoulsItems.creativePickpocketGauntlet) {
			return EnumRarity.epic;
		}

		return EnumRarity.rare;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean par4) {
		info.add(EnumChatFormatting.GRAY.toString() + StringUtils.localize(Strings.CHARGES) + ": " + getRarity(stack).rarityColor.toString() + getCharges(stack));
		if (StringUtils.holdShiftForInfo(info)) {
			if (stack.getItem() == SoulsItems.pickpocketGauntlet) {
				ItemUtils.addItemDesc(info, Strings.PICKPOCKET_GAUNTLET, MathHelper.floor_double(ConfigHandler.pickpocketSuccessRate) + "%");
			} else if (stack.getItem() == SoulsItems.creativePickpocketGauntlet) {
				ItemUtils.addItemDesc(info, Strings.CREATIVE_PICKPOCKET_GAUNTLET, "100%");
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return recharge(player, stack);
		}

		return false;
	}

	public void pickpocket(EntityPlayer player, EntitySoul soul) {
		if (player == null || player.isSneaking() || soul == null || soul.items.isEmpty() || !(player.getHeldItem().getItem() instanceof ItemPickpocketGauntlet) || !soul.canInteract(player)) {
			return;
		}

		final ItemPickpocketGauntlet pickpocketGauntlet = (ItemPickpocketGauntlet) player.getHeldItem().getItem();
		if (!pickpocketGauntlet.hasCharges(player.getHeldItem())) {
			return;
		}

		int totalItems = 0;
		for (int i = 0; i < soul.items.size(); i++) {
			if (soul.items.get(i) != null) {
				totalItems++;
			}
		}

		int itemCount = 0;
		int itemToGet = player.worldObj.rand.nextInt(totalItems * successRate);
		boolean success = false;
		if (itemToGet < soul.items.size()) {
			for (int i = 0; i < soul.items.size(); i++) {
				ItemStack stack = soul.items.get(i);
				if (stack != null && itemToGet == itemCount) {
					success = true;
					player.inventory.addItemStackToInventory(stack);
					soul.removeItemInSlot(i);
					PlayerUtils.playSoundAtPlayer(player, "pickpocketGauntlet.success");
					break;
				}

				itemCount++;
			}
		}

		if (!success && ConfigHandler.soulsAggro && ConfigHandler.pickpocketGauntletAggros && !player.capabilities.isCreativeMode) {
			soul.setAttackTarget(player);
		}

		pickpocketGauntlet.useCharge(player, player.getHeldItem());
	}

	public boolean recharge(EntityPlayer player, ItemStack stack) {
		if (player == null || !player.isSneaking() || player.capabilities.isCreativeMode || stack == null || stack.getItem() != SoulsItems.pickpocketGauntlet || getCharges(stack) == 16) {
			return false;
		}

		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			if (getCharges(stack) == maxCharges) {
				return true;
			}

			final ItemStack invStack = player.inventory.mainInventory[i];
			if (invStack != null && invStack.getItem() == Items.ender_pearl) {
				if (getEmptyCharges(stack) < invStack.stackSize) {
					player.inventory.decrStackSize(i, getEmptyCharges(stack));
					addCharges(getEmptyCharges(stack), stack);
				} else {
					addCharges(invStack.stackSize, stack);
					player.inventory.setInventorySlotContents(i, null);
				}
			}
		}

		return true;
	}

	public void addCharges(int amount, ItemStack stack) {
		if (getCharges(stack) < maxCharges) {
			setDamage(stack, stack.getItemDamage() - amount);
		}
	}

	public void useCharge(EntityPlayer player, ItemStack stack) {
		if (stack.getItem() == SoulsItems.pickpocketGauntlet && hasCharges(stack) && !player.capabilities.isCreativeMode) {
			setDamage(stack, stack.getItemDamage() + 1);
		}
	}

	public int getCharges(ItemStack stack) {
		return maxCharges - stack.getItemDamage();
	}

	public int getEmptyCharges(ItemStack stack) {
		return maxCharges - getCharges(stack);
	}

	public boolean hasCharges(ItemStack stack) {
		return getCharges(stack) > 0;
	}

	public ItemPickpocketGauntlet setMaxCharges(int amount) {
		maxCharges = amount;
		setMaxDamage(amount);
		return this;
	}

	public ItemPickpocketGauntlet setSuccessRate(double rate) {
		successRate = MathHelper.floor_double(100 / rate);
		return this;
	}

}
