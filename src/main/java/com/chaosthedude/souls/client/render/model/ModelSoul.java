package com.chaosthedude.souls.client.render.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.EnumHandSide;

public class ModelSoul extends ModelBiped {

	public ModelSoul() {
		this(0.0F, false);
	}

	public ModelSoul(float modelSize, boolean par2) {
		super(modelSize, 0.0F, 64, 32);

		if (!par2) {
			bipedRightArm = new ModelRenderer(this, 40, 16);
			bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
			bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			bipedLeftArm = new ModelRenderer(this, 40, 16);
			bipedLeftArm.mirror = true;
			bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			bipedRightLeg = new ModelRenderer(this, 0, 16);
			bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
			bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
			bipedLeftLeg = new ModelRenderer(this, 0, 16);
			bipedLeftLeg.mirror = true;
			bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, modelSize);
			bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
		}
	}

	@Override
	public void postRenderArm(float scale, EnumHandSide side) {
		float f = side == EnumHandSide.RIGHT ? 1.0F : -1.0F;
		ModelRenderer modelrenderer = this.getArmForSide(side);
		modelrenderer.rotationPointX += f;
		modelrenderer.postRender(scale);
		modelrenderer.rotationPointX -= f;
	}

}
