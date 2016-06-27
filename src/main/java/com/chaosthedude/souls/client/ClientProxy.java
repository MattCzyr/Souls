package com.chaosthedude.souls.client;

import com.chaosthedude.souls.client.renderer.RenderSoul;
import com.chaosthedude.souls.client.renderer.model.ModelSoul;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.proxy.CommonProxy;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySoul.class, new RenderSoul(new ModelSoul(), 0.5F));
	}

}
