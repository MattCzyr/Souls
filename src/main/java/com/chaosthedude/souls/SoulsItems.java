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

		registerItem(creativePickpocketGauntlet, creativePickpocketGauntlet.name);
		registerItem(pickpocketGauntlet, pickpocketGauntlet.name);
		registerItem(enderJewel, "ender_jewel");
		registerItem(soulIdentifier, soulIdentifier.NAME);
	}

	public static void registerItem(Item item, String name) {
		item.setRegistryName(name);
		GameRegistry.register(item);
	}

	private static void init() {
		creativePickpocketGauntlet = new ItemPickpocketGauntlet(9999, 100.0D, "creative_pickpocket_gauntlet");
		pickpocketGauntlet = new ItemPickpocketGauntlet(16, ConfigHandler.pickpocketSuccessRate, "pickpocket_gauntlet");
		enderJewel = new Item().setUnlocalizedName(Souls.MODID + ".ender_jewel").setCreativeTab(CreativeTabs.MATERIALS);
		soulIdentifier = new ItemSoulIdentifier();
	}

}
