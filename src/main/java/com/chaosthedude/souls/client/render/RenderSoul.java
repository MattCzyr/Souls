package com.chaosthedude.souls.client.render;

import com.chaosthedude.souls.client.renderer.model.ModelSoul;
import com.chaosthedude.souls.entity.EntitySoul;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.util.ResourceLocation;

public class RenderSoul extends RenderBiped<EntitySoul> {

	private static final ResourceLocation texture = new ResourceLocation("souls", "textures/models/Soul.png");

	public RenderSoul(RenderManager manager) {
		super(manager, new ModelSoul(), 0.5F);
		addLayer(new LayerHeldItem(this));
		addLayer(new LayerBipedArmor(this) {
			protected void initArmor() {
				field_177189_c = new ModelSoul(0.5F, true);
				field_177186_d = new ModelSoul(1.0F, true);
			}
		});
	}

	@Override
	public void transformHeldFull3DItemLayer() {
		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySoul soul) {
		return texture;
	}

}
