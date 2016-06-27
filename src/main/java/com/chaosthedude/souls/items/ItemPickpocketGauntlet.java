package com.chaosthedude.souls.items;

import java.util.List;

import com.chaosthedude.souls.Souls;
import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.SoulsSounds;
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
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemPickpocketGauntlet extends Item {

	public String name;

	private int successRate;
	private int maxCharges;

	public ItemPickpocketGauntlet(int maxCharges, double successRate, String name) {
		this.name = name;

		setUnlocalizedName(Souls.MODID + "." + name);
		setMaxStackSize(1);
		setNoRepair();
		setMaxCharges(maxCharges);
		setSuccessRate(successRate);
		setCreativeTab(CreativeTabs.TOOLS);
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		if (stack.getItem() == SoulsItems.creativePickpocketGauntlet) {
			return EnumRarity.EPIC;
		}

		return EnumRarity.RARE;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean par4) {
		info.add(TextFormatting.GRAY.toString() + StringUtils.localize(Strings.CHARGES) + ": " + getRarity(stack).rarityColor.toString() + getCharges(stack));
		if (StringUtils.holdShiftForInfo(info)) {
			if (stack.getItem() == SoulsItems.pickpocketGauntlet) {
				ItemUtils.addItemDesc(info, Strings.PICKPOCKET_GAUNTLET, MathHelper.floor_double(ConfigHandler.pickpocketSuccessRate) + "%");
			} else if (stack.getItem() == SoulsItems.creativePickpocketGauntlet) {
				ItemUtils.addItemDesc(info, Strings.CREATIVE_PICKPOCKET_GAUNTLET, "100%");
			}
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return recharge(player, stack);
	}

	public void pickpocket(EntityPlayer player, ItemStack stack, EntitySoul soul) {
		if (player == null || player.isSneaking() || soul == null || soul.items.isEmpty() || !(stack.getItem() instanceof ItemPickpocketGauntlet) || !soul.canInteract(player)) {
			return;
		}

		final ItemPickpocketGauntlet pickpocketGauntlet = (ItemPickpocketGauntlet) stack.getItem();
		if (!pickpocketGauntlet.hasCharges(stack)) {
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
				ItemStack soulStack = soul.items.get(i);
				if (soulStack != null && itemToGet == itemCount) {
					success = true;
					player.inventory.addItemStackToInventory(soulStack);
					soul.removeItemInSlot(i);
					PlayerUtils.playSoundAtPlayer(player, SoulsSounds.pickpocket);
					break;
				}

				itemCount++;
			}
		}

		if (!success && ConfigHandler.soulsAggro && ConfigHandler.pickpocketGauntletAggros && !player.capabilities.isCreativeMode) {
			soul.setAttackTarget(player);
		}

		pickpocketGauntlet.useCharge(player, stack);
	}

	public EnumActionResult recharge(EntityPlayer player, ItemStack stack) {
		if (player == null || !player.isSneaking() || player.capabilities.isCreativeMode || stack == null || stack.getItem() != SoulsItems.pickpocketGauntlet || getCharges(stack) == 16) {
			return EnumActionResult.PASS;
		}

		for (int i = 0; i < player.inventory.mainInventory.length; i++) {
			if (getCharges(stack) == maxCharges) {
				return EnumActionResult.SUCCESS;
			}

			ItemStack invStack = player.inventory.mainInventory[i];
			if (invStack != null && invStack.getItem() == Items.ENDER_PEARL) {
				if (getEmptyCharges(stack) < invStack.stackSize) {
					player.inventory.decrStackSize(i, getEmptyCharges(stack));
					addCharges(getEmptyCharges(stack), stack);
				} else {
					addCharges(invStack.stackSize, stack);
					player.inventory.setInventorySlotContents(i, null);
				}
			}
		}

		return EnumActionResult.SUCCESS;
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
