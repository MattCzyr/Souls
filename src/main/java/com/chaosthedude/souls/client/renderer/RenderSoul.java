package com.chaosthedude.souls.client.renderer;

import com.chaosthedude.souls.client.renderer.model.ModelSoul;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderSoul extends RenderBiped {

	private static final ResourceLocation texture = new ResourceLocation("souls", "textures/models/Soul.png");

	public RenderSoul(ModelSoul par1, float par2) {
		super(par1, par2);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
