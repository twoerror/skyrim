package tes.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class TESModelDirewolf extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer mouthB;
	private final ModelRenderer nose2;
	private final ModelRenderer neck;
	private final ModelRenderer neck2;
	private final ModelRenderer lSide;
	private final ModelRenderer rSide;
	private final ModelRenderer nose;
	private final ModelRenderer mouth;
	private final ModelRenderer mouthOpen;
	private final ModelRenderer rEar;
	private final ModelRenderer lEar;
	private final ModelRenderer chest;
	private final ModelRenderer body;
	private final ModelRenderer tailA;
	private final ModelRenderer tailB;
	private final ModelRenderer tailC;
	private final ModelRenderer tailD;
	private final ModelRenderer leg4A;
	private final ModelRenderer leg4D;
	private final ModelRenderer leg4B;
	private final ModelRenderer leg4C;
	private final ModelRenderer leg3B;
	private final ModelRenderer leg2A;
	private final ModelRenderer leg2B;
	private final ModelRenderer leg2C;
	private final ModelRenderer leg3D;
	private final ModelRenderer leg3C;
	private final ModelRenderer leg3A;
	private final ModelRenderer leg1A;
	private final ModelRenderer leg1B;
	private final ModelRenderer leg1C;

	public TESModelDirewolf() {
		textureWidth = 64;
		textureHeight = 128;
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4.0F, -3.0F, -6.0F, 8, 8, 6);
		head.setRotationPoint(0.0F, 7.0F, -10.0F);
		mouthB = new ModelRenderer(this, 16, 33);
		mouthB.addBox(-2.0F, 4.0F, -7.0F, 4, 1, 2);
		mouthB.setRotationPoint(0.0F, 7.0F, -10.0F);
		nose2 = new ModelRenderer(this, 0, 25);
		nose2.addBox(-2.0F, 2.0F, -12.0F, 4, 2, 6);
		nose2.setRotationPoint(0.0F, 7.0F, -10.0F);
		neck = new ModelRenderer(this, 28, 0);
		neck.addBox(-3.5F, -3.0F, -7.0F, 7, 8, 7);
		neck.setRotationPoint(0.0F, 10.0F, -6.0F);
		setRotation(neck, -0.4537856F, 0.0F, 0.0F);
		neck2 = new ModelRenderer(this, 0, 14);
		neck2.addBox(-1.5F, -2.0F, -5.0F, 3, 4, 7);
		neck2.setRotationPoint(0.0F, 14.0F, -10.0F);
		setRotation(neck2, -0.4537856F, 0.0F, 0.0F);
		lSide = new ModelRenderer(this, 28, 33);
		lSide.addBox(3.0F, -0.5F, -2.0F, 2, 6, 6);
		lSide.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotation(lSide, -0.2094395F, 0.418879F, -0.0872665F);
		rSide = new ModelRenderer(this, 28, 45);
		rSide.addBox(-5.0F, -0.5F, -2.0F, 2, 6, 6);
		rSide.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotation(rSide, -0.2094395F, -0.418879F, 0.0872665F);
		nose = new ModelRenderer(this, 44, 33);
		nose.addBox(-1.5F, -1.8F, -12.4F, 3, 2, 7);
		nose.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotation(nose, 0.2792527F, 0.0F, 0.0F);
		mouth = new ModelRenderer(this, 1, 34);
		mouth.addBox(-2.0F, 4.0F, -11.5F, 4, 1, 5);
		mouth.setRotationPoint(0.0F, 7.0F, -10.0F);
		mouthOpen = new ModelRenderer(this, 1, 34);
		mouthOpen.addBox(-2.0F, 0.0F, -12.5F, 4, 1, 5);
		mouthOpen.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotation(mouthOpen, 0.6108652F, 0.0F, 0.0F);
		rEar = new ModelRenderer(this, 22, 0);
		rEar.addBox(-3.5F, -7.0F, -1.5F, 3, 5, 1);
		rEar.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotation(rEar, 0.0F, 0.0F, -0.1745329F);
		lEar = new ModelRenderer(this, 13, 14);
		lEar.addBox(0.5F, -7.0F, -1.5F, 3, 5, 1);
		lEar.setRotationPoint(0.0F, 7.0F, -10.0F);
		setRotation(lEar, 0.0F, 0.0F, 0.1745329F);
		chest = new ModelRenderer(this, 20, 15);
		chest.addBox(-4.0F, -11.0F, -12.0F, 8, 8, 10);
		chest.setRotationPoint(0.0F, 5.0F, 2.0F);
		setRotation(chest, 1.570796F, 0.0F, 0.0F);
		body = new ModelRenderer(this, 0, 40);
		body.addBox(-3.0F, -8.0F, -9.0F, 6, 16, 8);
		body.setRotationPoint(0.0F, 6.5F, 2.0F);
		setRotation(body, 1.570796F, 0.0F, 0.0F);
		tailA = new ModelRenderer(this, 52, 42);
		tailA.addBox(-1.5F, 0.0F, -1.5F, 3, 4, 3);
		tailA.setRotationPoint(0.0F, 8.5F, 9.0F);
		setRotation(tailA, 1.064651F, 0.0F, 0.0F);
		tailB = new ModelRenderer(this, 48, 49);
		tailB.addBox(-2.0F, 3.0F, -1.0F, 4, 6, 4);
		tailB.setRotationPoint(0.0F, 8.5F, 9.0F);
		setRotation(tailB, 0.7504916F, 0.0F, 0.0F);
		tailC = new ModelRenderer(this, 48, 59);
		tailC.addBox(-2.0F, 7.8F, -4.1F, 4, 6, 4);
		tailC.setRotationPoint(0.0F, 8.5F, 9.0F);
		setRotation(tailC, 1.099557F, 0.0F, 0.0F);
		tailD = new ModelRenderer(this, 52, 69);
		tailD.addBox(-1.5F, 9.8F, -3.6F, 3, 5, 3);
		tailD.setRotationPoint(0.0F, 8.5F, 9.0F);
		setRotation(tailD, 1.099557F, 0.0F, 0.0F);
		leg1A = new ModelRenderer(this, 28, 57);
		leg1A.addBox(0.01F, -4.0F, -2.5F, 2, 8, 4);
		leg1A.setRotationPoint(4.0F, 12.5F, -5.5F);
		setRotation(leg1A, 0.2617994F, 0.0F, 0.0F);
		leg1B = new ModelRenderer(this, 28, 69);
		leg1B.addBox(0.0F, 3.2F, 0.5F, 2, 8, 2);
		leg1B.setRotationPoint(4.0F, 12.5F, -5.5F);
		setRotation(leg1B, -0.1745329F, 0.0F, 0.0F);
		leg1C = new ModelRenderer(this, 28, 79);
		leg1C.addBox(-0.5066667F, 9.5F, -2.5F, 3, 2, 3);
		leg1C.setRotationPoint(4.0F, 12.5F, -5.5F);
		leg2A = new ModelRenderer(this, 28, 84);
		leg2A.addBox(-2.01F, -4.0F, -2.5F, 2, 8, 4);
		leg2A.setRotationPoint(-4.0F, 12.5F, -5.5F);
		setRotation(leg2A, 0.2617994F, 0.0F, 0.0F);
		leg2B = new ModelRenderer(this, 28, 96);
		leg2B.addBox(-2.0F, 3.2F, 0.5F, 2, 8, 2);
		leg2B.setRotationPoint(-4.0F, 12.5F, -5.5F);
		setRotation(leg2B, -0.1745329F, 0.0F, 0.0F);
		leg2C = new ModelRenderer(this, 28, 106);
		leg2C.addBox(-2.506667F, 9.5F, -2.5F, 3, 2, 3);
		leg2C.setRotationPoint(-4.0F, 12.5F, -5.5F);
		leg3A = new ModelRenderer(this, 0, 64);
		leg3A.addBox(0.0F, -3.8F, -3.5F, 2, 7, 5);
		leg3A.setRotationPoint(3.0F, 12.5F, 7.0F);
		setRotation(leg3A, -0.3665191F, 0.0F, 0.0F);
		leg3B = new ModelRenderer(this, 0, 76);
		leg3B.addBox(-0.1F, 1.9F, -1.8F, 2, 2, 5);
		leg3B.setRotationPoint(3.0F, 12.5F, 7.0F);
		setRotation(leg3B, -0.7330383F, 0.0F, 0.0F);
		leg3C = new ModelRenderer(this, 0, 83);
		leg3C.addBox(0.0F, 3.2F, 0.0F, 2, 8, 2);
		leg3C.setRotationPoint(3.0F, 12.5F, 7.0F);
		setRotation(leg3C, -0.1745329F, 0.0F, 0.0F);
		leg3D = new ModelRenderer(this, 0, 93);
		leg3D.addBox(-0.5066667F, 9.5F, -3.0F, 3, 2, 3);
		leg3D.setRotationPoint(3.0F, 12.5F, 7.0F);
		leg4A = new ModelRenderer(this, 14, 64);
		leg4A.addBox(-2.0F, -3.8F, -3.5F, 2, 7, 5);
		leg4A.setRotationPoint(-3.0F, 12.5F, 7.0F);
		setRotation(leg4A, -0.3665191F, 0.0F, 0.0F);
		leg4B = new ModelRenderer(this, 14, 76);
		leg4B.addBox(-1.9F, 1.9F, -1.8F, 2, 2, 5);
		leg4B.setRotationPoint(-3.0F, 12.5F, 7.0F);
		setRotation(leg4B, -0.7330383F, 0.0F, 0.0F);
		leg4C = new ModelRenderer(this, 14, 83);
		leg4C.addBox(-2.0F, 3.2F, 0.0F, 2, 8, 2);
		leg4C.setRotationPoint(-3.0F, 12.5F, 7.0F);
		setRotation(leg4C, -0.1745329F, 0.0F, 0.0F);
		leg4D = new ModelRenderer(this, 14, 93);
		leg4D.addBox(-2.506667F, 9.5F, -3.0F, 3, 2, 3);
		leg4D.setRotationPoint(-3.0F, 12.5F, 7.0F);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f3, f4);
		head.render(f5);
		nose2.render(f5);
		neck.render(f5);
		neck2.render(f5);
		lSide.render(f5);
		rSide.render(f5);
		nose.render(f5);
		mouth.render(f5);
		mouthB.render(f5);
		rEar.render(f5);
		lEar.render(f5);
		chest.render(f5);
		body.render(f5);
		tailA.render(f5);
		tailB.render(f5);
		tailC.render(f5);
		tailD.render(f5);
		leg4A.render(f5);
		leg4D.render(f5);
		leg4B.render(f5);
		leg4C.render(f5);
		leg3B.render(f5);
		leg2A.render(f5);
		leg2B.render(f5);
		leg2C.render(f5);
		leg3D.render(f5);
		leg3C.render(f5);
		leg3A.render(f5);
		leg1A.render(f5);
		leg1B.render(f5);
		leg1C.render(f5);
	}

	private void setRotationAngles(float f, float f1, float f3, float f4) {
		head.rotateAngleX = f4 / 57.29578F;
		head.rotateAngleY = f3 / 57.29578F;
		float LLegX = MathHelper.cos(f * 0.6662F) * 1.4F * f1;
		float RLegX = MathHelper.cos(f * 0.6662F + 3.141593F) * 1.4F * f1;
		mouth.rotateAngleX = head.rotateAngleX;
		mouth.rotateAngleY = head.rotateAngleY;
		mouthB.rotateAngleX = head.rotateAngleX;
		mouthB.rotateAngleY = head.rotateAngleY;
		mouthOpen.rotateAngleX = 35 / 57.29578F + head.rotateAngleX;
		mouthOpen.rotateAngleY = head.rotateAngleY;
		nose.rotateAngleX = 16 / 57.29578F + head.rotateAngleX;
		nose.rotateAngleY = head.rotateAngleY;
		nose2.rotateAngleX = head.rotateAngleX;
		nose2.rotateAngleY = head.rotateAngleY;
		lSide.rotateAngleX = -12 / 57.29578F + head.rotateAngleX;
		lSide.rotateAngleY = 24 / 57.29578F + head.rotateAngleY;
		rSide.rotateAngleX = -12 / 57.29578F + head.rotateAngleX;
		rSide.rotateAngleY = -24 / 57.29578F + head.rotateAngleY;
		rEar.rotateAngleX = head.rotateAngleX;
		rEar.rotateAngleY = head.rotateAngleY;
		lEar.rotateAngleX = head.rotateAngleX;
		lEar.rotateAngleY = head.rotateAngleY;
		leg1A.rotateAngleX = 15 / 57.29578F + LLegX;
		leg1B.rotateAngleX = -10 / 57.29578F + LLegX;
		leg1C.rotateAngleX = LLegX;
		leg2A.rotateAngleX = 15 / 57.29578F + RLegX;
		leg2B.rotateAngleX = -10 / 57.29578F + RLegX;
		leg2C.rotateAngleX = RLegX;
		leg3A.rotateAngleX = -21 / 57.29578F + RLegX;
		leg3B.rotateAngleX = -42 / 57.29578F + RLegX;
		leg3C.rotateAngleX = -10 / 57.29578F + RLegX;
		leg3D.rotateAngleX = RLegX;
		leg4A.rotateAngleX = -21 / 57.29578F + LLegX;
		leg4B.rotateAngleX = -42 / 57.29578F + LLegX;
		leg4C.rotateAngleX = -10 / 57.29578F + LLegX;
		leg4D.rotateAngleX = LLegX;
		tailA.rotateAngleY = 0.0F;
		tailA.rotateAngleX = 61 / 57.29F;
		tailB.rotateAngleX = 43 / 57.29F;
		tailC.rotateAngleX = 63 / 57.29F;
		tailD.rotateAngleX = 63 / 57.29F;
		tailB.rotateAngleY = tailA.rotateAngleY;
		tailC.rotateAngleY = tailA.rotateAngleY;
		tailD.rotateAngleY = tailA.rotateAngleY;
	}
}