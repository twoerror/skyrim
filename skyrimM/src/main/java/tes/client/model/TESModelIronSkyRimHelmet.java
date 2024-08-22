package tes.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class TESModelIronSkyRimHelmet extends TESModelBiped {

	public TESModelIronSkyRimHelmet() {
		bipedHead = new ModelRenderer((ModelBase) this, 0, 0);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 18, 20, -4.0F, -9.0F, 4.0F, 8, 9, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 20, -4.0F, -9.0F, -5.0F, 8, 9, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 36, 11, -5.0F, -10.0F, -5.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 26, 35, -1.0F, -14.0F, -2.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 16, 35, 0.0F, -14.0F, -2.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 34, 31, -2.0F, -13.0F, -2.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 34, 26, 1.0F, -13.0F, -2.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 42, 8, 3.0F, -11.0F, -4.0F, 1, 1, 3, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 10, 33, 4.0F, -10.0F, -5.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 5, 42, -4.0F, -11.0F, -4.0F, 1, 1, 3, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 30, -3.0F, -12.0F, -3.0F, 1, 1, 6, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 9, 2.0F, -12.0F, -3.0F, 1, 1, 6, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 28, 30, 4.0F, -10.0F, 1.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 41, 3.0F, -11.0F, 1.0F, 1, 1, 3, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 38, 40, -4.0F, -11.0F, 1.0F, 1, 1, 3, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 18, 30, -5.0F, -10.0F, 1.0F, 1, 1, 4, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 34, -1.0F, -13.0F, -3.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 31, -2.0F, -12.0F, -3.0F, 4, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 30, 9, -3.0F, -11.0F, -4.0F, 6, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 18, -4.0F, -10.0F, -5.0F, 8, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 16, 32, -1.0F, -11.0F, -5.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 8, 32, -1.0F, -12.0F, -4.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 32, -1.0F, -10.0F, -6.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 16, -4.0F, -10.0F, 4.0F, 8, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 8, 30, -3.0F, -11.0F, 3.0F, 6, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 28, -2.0F, -12.0F, 2.0F, 4, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 30, -1.0F, -13.0F, 2.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 13, -1.0F, -12.0F, 3.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 11, -1.0F, -11.0F, 4.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 21, 9, -1.0F, -10.0F, 5.0F, 2, 1, 1, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 30, 40, 3.0F, -11.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 24, -5.0F, -11.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 40, 5.0F, -11.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 16, -7.0F, -11.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 14, 40, -8.0F, -9.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 4, 6.0F, -9.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 40, 0, 7.0F, -10.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 8, 38, -9.0F, -10.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 37, 8.0F, -9.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 36, 36, -10.0F, -9.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 36, 20, -10.0F, -7.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 35, 8.0F, -7.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 44, 20, 8.0F, -6.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 19, 44, -10.0F, -6.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 11, 44, -10.0F, -5.0F, -1.0F, 1, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 44, 38, 9.0F, -5.0F, -1.0F, 1, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 24, 30, 7.0F, -5.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 30, 11, -9.0F, -5.0F, -1.0F, 2, 2, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 42, 34, -9.0F, -3.0F, -1.0F, 2, 1, 2, 0.0F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 42, 12, 7.0F, -3.0F, -1.0F, 2, 1, 2, 0.0F));

		bipedHead.cubeList.add(new ModelBox(bipedHead, 22, 0, -4.0F, -8.0F, -1.0F, 8, 8, 1, 0.0F));

		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 0, -5.0F, -9.0F, -1.0F, 10, 9, 1, 0.0F));

		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 10, -5.0F, -9.0F, -1.0F, 10, 9, 1, 0.0F));		bipedHeadwear.cubeList.clear();
		bipedBody.cubeList.clear();
		bipedRightArm.cubeList.clear();
		bipedLeftArm.cubeList.clear();
		bipedRightLeg.cubeList.clear();
		bipedLeftLeg.cubeList.clear();
	
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		bipedHead.render(f5);
	}

}