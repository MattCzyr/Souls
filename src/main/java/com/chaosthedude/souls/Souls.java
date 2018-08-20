package com.chaosthedude.souls;

import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.event.SoulsEventHandler;
import com.chaosthedude.souls.items.ItemPickpocketGauntlet;
import com.chaosthedude.souls.items.ItemSoulIdentifier;
import com.chaosthedude.souls.proxy.CommonProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Souls.MODID, name = Souls.NAME, version = Souls.VERSION, acceptedMinecraftVersions = "[1.12,1.12.2]")

public class Souls {

	public static final String MODID = "souls";
	public static final String NAME = "Souls";
	public static final String VERSION = "3.0.1";

	public static final Logger logger = LogManager.getLogger(MODID);

	@SidedProxy(clientSide = "com.chaosthedude.souls.client.ClientProxy", serverSide = "com.chaosthedude.souls.proxy.CommonProxy")
	public static CommonProxy proxy;

	public Souls() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event) {
		ConfigHandler.loadConfig(event.getSuggestedConfigurationFile());

		MinecraftForge.EVENT_BUS.register(new SoulsEventHandler());

		proxy.preInit();
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		Item enderJewel = new Item().setUnlocalizedName(Souls.MODID + ".ender_jewel")
				.setCreativeTab(CreativeTabs.MATERIALS);
		Item soulIdentifier = new ItemSoulIdentifier();
		ItemPickpocketGauntlet pickpocketGauntlet = new ItemPickpocketGauntlet(16, ConfigHandler.pickpocketSuccessRate,
				"pickpocket_gauntlet");
		ItemPickpocketGauntlet creativePicketpocketGauntlet = new ItemPickpocketGauntlet(9999, 100.0D,
				"creative_pickpocket_gauntlet");

		event.getRegistry().register(enderJewel.setRegistryName("ender_jewel"));
		event.getRegistry().register(soulIdentifier.setRegistryName(ItemSoulIdentifier.NAME));
		event.getRegistry().register(pickpocketGauntlet.setRegistryName(pickpocketGauntlet.name));
		event.getRegistry().register(creativePicketpocketGauntlet.setRegistryName(creativePicketpocketGauntlet.name));
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "Soul"), EntitySoul.class, "Soul", 0, this, 64, 2,
				true);
	}

	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		registerSound(event.getRegistry(), "identifier");
		registerSound(event.getRegistry(), "pickpocket");
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		registerModel(SoulsItems.CREATIVE_PICKPOCKET_GAUNTLET);
		registerModel(SoulsItems.PICKPOCKET_GAUNTLET);
		registerModel(SoulsItems.ENDER_JEWEL);
		registerModel(SoulsItems.SOUL_IDENTIFIER);
	}

	private static void registerSound(IForgeRegistry<SoundEvent> registry, String soundName) {
		final ResourceLocation soundId = new ResourceLocation("souls", soundName);
		final SoundEvent sound = new SoundEvent(soundId).setRegistryName(soundName);
		registry.register(sound);
	}

	public static void registerModel(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}