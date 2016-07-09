package com.chaosthedude.souls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.event.SoulsEventHandler;
import com.chaosthedude.souls.proxy.CommonProxy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Souls.MODID, name = Souls.NAME, version = Souls.VERSION, acceptedMinecraftVersions = "[1.8,1.8.9]")

public class Souls {

	public static final String MODID = "Souls";
	public static final String NAME = "Souls";
	public static final String VERSION = "1.0.2";

	public static final Logger logger = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "com.chaosthedude.souls.client.ClientProxy", serverSide = "com.chaosthedude.souls.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void init(FMLPreInitializationEvent event) {
		SoulsItems.register();

		proxy.registerRenderers();
		proxy.registerModels();

		MinecraftForge.EVENT_BUS.register(new SoulsEventHandler());

		ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		EntityRegistry.registerModEntity(EntitySoul.class, "Soul", 0, this, 64, 2, true);

		registerRecipes();
	}

	public static void registerRecipes() {
		GameRegistry.addShapelessRecipe(new ItemStack(SoulsItems.soulIdentifier, 1), new ItemStack(Items.book, 1), new ItemStack(Items.ender_pearl));

		if (ConfigHandler.pickpocketGauntletLoot) {
			ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(new ItemStack(SoulsItems.pickpocketGauntlet, 1, 16), 1, 1, 5));
		} else {
			GameRegistry.addShapedRecipe(new ItemStack(SoulsItems.pickpocketGauntlet), "II ", "IJI", " IB", 'I', Items.iron_ingot, 'J', SoulsItems.enderJewel, 'B', Blocks.iron_block);
			GameRegistry.addShapedRecipe(new ItemStack(SoulsItems.enderJewel), " D ", "DPD", " D ", 'D', Items.diamond, 'P', Items.ender_pearl);
		}
	}	

}