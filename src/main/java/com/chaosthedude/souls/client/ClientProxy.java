package com.chaosthedude.souls.client;

import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.client.render.SoulRenderFactory;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.proxy.CommonProxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySoul.class, new SoulRenderFactory());
	}

	@Override
	public void registerModels() {
		registerModel(SoulsItems.creativePickpocketGauntlet);
		registerModel(SoulsItems.pickpocketGauntlet);
		registerModel(SoulsItems.enderJewel);
		registerModel(SoulsItems.soulIdentifier);
	}

	public static void registerModel(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
