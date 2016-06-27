package com.chaosthedude.souls.client.renderer.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;

public class ModelSoul extends ModelBiped {

	public ModelSoul() {
		this(0.0F);
	}

	public ModelSoul(float par1) {
		super(par1, 0.0F, 64, 32);
		bipedRightArm = new ModelRenderer(this, 40, 16);
		bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, par1);
		bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

		bipedLeftArm = new ModelRenderer(this, 40, 16);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, par1);
		bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

		bipedRightLeg = new ModelRenderer(this, 0, 16);
		bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, par1);
		bipedRightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);

		bipedLeftLeg = new ModelRenderer(this, 0, 16);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, par1);
		bipedLeftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
	}

}
