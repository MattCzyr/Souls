package com.chaosthedude.souls.items;

import java.util.List;

import com.chaosthedude.souls.Souls;
import com.chaosthedude.souls.util.ItemUtils;
import com.chaosthedude.souls.util.StringUtils;
import com.chaosthedude.souls.util.Strings;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSoulIdentifier extends Item {

	public static final String NAME = "SoulIdentifier";

	public ItemSoulIdentifier() {
		setUnlocalizedName(Souls.MODID + "." + NAME);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.TOOLS);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean par4) {
		if (StringUtils.holdShiftForInfo(info)) {
			ItemUtils.addItemDesc(info, Strings.SOUL_IDENTIFIER);
		}
	}

}
