package com.chaosthedude.souls;

import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.items.ItemPickpocketGauntlet;
import com.chaosthedude.souls.items.ItemSoulIdentifier;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoulsItems {

	public static ItemPickpocketGauntlet creativePickpocketGauntlet;
	public static ItemPickpocketGauntlet pickpocketGauntlet;
	public static Item enderJewel;
	public static ItemSoulIdentifier soulIdentifier;

	public static void register() {
		init();

		GameRegistry.registerItem(creativePickpocketGauntlet, creativePickpocketGauntlet.name);
		GameRegistry.registerItem(pickpocketGauntlet, pickpocketGauntlet.name);
		GameRegistry.registerItem(enderJewel, "EnderJewel");
		GameRegistry.registerItem(soulIdentifier, soulIdentifier.NAME);
	}

	private static void init() {
		creativePickpocketGauntlet = new ItemPickpocketGauntlet(9999, 100.0D, "CreativePickpocketGauntlet");
		pickpocketGauntlet = new ItemPickpocketGauntlet(16, ConfigHandler.pickpocketSuccessRate, "PickpocketGauntlet");
		enderJewel = new Item().setUnlocalizedName(Souls.MODID + ".EnderJewel").setCreativeTab(CreativeTabs.tabMaterials);
		soulIdentifier = new ItemSoulIdentifier();
	}

}
