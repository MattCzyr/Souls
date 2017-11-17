package com.chaosthedude.souls.client;

import com.chaosthedude.souls.client.render.SoulRenderFactory;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.proxy.CommonProxy;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		RenderingRegistry.registerEntityRenderingHandler(EntitySoul.class, new SoulRenderFactory());
	}

}
