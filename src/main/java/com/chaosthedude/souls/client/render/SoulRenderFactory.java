package com.chaosthedude.souls.client.render;

import com.chaosthedude.souls.entity.EntitySoul;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class SoulRenderFactory implements IRenderFactory<EntitySoul> {

	@Override
	public RenderSoul createRenderFor(RenderManager manager) {
		return new RenderSoul(manager);
	}

}
