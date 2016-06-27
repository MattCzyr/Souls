package com.chaosthedude.souls.client.render;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class SoulRenderFactory implements IRenderFactory {

	@Override
	public RenderSoul createRenderFor(RenderManager manager) {
		return new RenderSoul(manager);
	}

}
