package tes.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class TESModelWerewolf extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer nose;
	private final ModelRenderer snout;
	private final ModelRenderer teethU;
	private final ModelRenderer teethL;
	private final ModelRenderer mouth;
	private final ModelRenderer lEar;
	private final ModelRenderer rEar;
	private final ModelRenderer neck;
	private final ModelRenderer neck2;
	private final ModelRenderer sideburnL;
	private final ModelRenderer sideburnR;
	private final ModelRenderer chest;
	private final ModelRenderer abdomen;
	private final ModelRenderer tailA;
	private final ModelRenderer tailC;
	private final ModelRenderer tailB;
	private final ModelRenderer tailD;
	private final ModelRenderer rLegA;
	private final ModelRenderer rFoot;
	private final ModelRenderer rLegB;
	private final ModelRenderer rLegC;
	private final ModelRenderer lLegB;
	private final ModelRenderer lFoot;
	private final ModelRenderer lLegC;
	private final ModelRenderer lLegA;
	private final ModelRenderer rArmB;
	private final ModelRenderer rArmC;
	private final ModelRenderer lArmB;
	private final ModelRenderer rHand;
	private final ModelRenderer rArmA;
	private final ModelRenderer lArmA;
	private final ModelRenderer lArmC;
	private final ModelRenderer lHand;
	private final ModelRenderer rFinger2;
	private final ModelRenderer rFinger3;
	private final ModelRenderer rFinger4;
	private final ModelRenderer rFinger5;
	private final ModelRenderer lFinger1;
	private final ModelRenderer lFinger2;
	private final ModelRenderer lFinger3;
	private final ModelRenderer lFinger4;
	private final ModelRenderer lFinger5;

	private ModelRenderer rFinger1;

	public TESModelWerewolf() {
		textureWidth = 64;
		textureHeight = 128;
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4.0f, -3.0f, -6.0f, 8, 8, 6);
		head.setRotationPoint(0.0f, -8.0f, -6.0f);
		head.setTextureSize(64, 128);
		nose = new ModelRenderer(this, 44, 33);
		nose.addBox(-1.5f, -1.7f, -12.3f, 3, 2, 7);
		nose.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(nose, 0.2792527f, 0.0f, 0.0f);
		snout = new ModelRenderer(this, 0, 25);
		snout.addBox(-2.0f, 2.0f, -12.0f, 4, 2, 6);
		snout.setRotationPoint(0.0f, -8.0f, -6.0f);
		teethU = new ModelRenderer(this, 46, 18);
		teethU.addBox(-2.0f, 4.01f, -12.0f, 4, 2, 5);
		teethU.setRotationPoint(0.0f, -8.0f, -6.0f);
		teethL = new ModelRenderer(this, 20, 109);
		teethL.addBox(-1.5f, -12.5f, 2.01f, 3, 5, 2);
		teethL.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(teethL, 2.530727f, 0.0f, 0.0f);
		mouth = new ModelRenderer(this, 42, 69);
		mouth.addBox(-1.5f, -12.5f, 0.0f, 3, 9, 2);
		mouth.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(mouth, 2.530727f, 0.0f, 0.0f);
		lEar = new ModelRenderer(this, 13, 14);
		lEar.addBox(0.5f, -7.5f, -1.0f, 3, 5, 1);
		lEar.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(lEar, 0.0f, 0.0f, 0.1745329f);
		rEar = new ModelRenderer(this, 22, 0);
		rEar.addBox(-3.5f, -7.5f, -1.0f, 3, 5, 1);
		rEar.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(rEar, 0.0f, 0.0f, -0.1745329f);
		neck = new ModelRenderer(this, 28, 0);
		neck.addBox(-3.5f, -3.0f, -7.0f, 7, 8, 7);
		neck.setRotationPoint(0.0f, -5.0f, -2.0f);
		setRotation(neck, -0.6025001f, 0.0f, 0.0f);
		neck2 = new ModelRenderer(this, 0, 14);
		neck2.addBox(-1.5f, -2.0f, -5.0f, 3, 4, 7);
		neck2.setRotationPoint(0.0f, -1.0f, -6.0f);
		setRotation(neck2, -0.4537856f, 0.0f, 0.0f);
		sideburnL = new ModelRenderer(this, 28, 33);
		sideburnL.addBox(3.0f, 0.0f, -2.0f, 2, 6, 6);
		sideburnL.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(sideburnL, -0.2094395f, 0.418879f, -0.0872665f);
		sideburnR = new ModelRenderer(this, 28, 45);
		sideburnR.addBox(-5.0f, 0.0f, -2.0f, 2, 6, 6);
		sideburnR.setRotationPoint(0.0f, -8.0f, -6.0f);
		setRotation(sideburnR, -0.2094395f, -0.418879f, 0.0872665f);
		chest = new ModelRenderer(this, 20, 15);
		chest.addBox(-4.0f, 0.0f, -7.0f, 8, 8, 10);
		chest.setRotationPoint(0.0f, -6.0f, -2.5f);
		setRotation(chest, 0.641331f, 0.0f, 0.0f);
		abdomen = new ModelRenderer(this, 0, 40);
		abdomen.addBox(-3.0f, -8.0f, -8.0f, 6, 14, 8);
		abdomen.setRotationPoint(0.0f, 4.5f, 5.0f);
		setRotation(abdomen, 0.2695449f, 0.0f, 0.0f);
		tailA = new ModelRenderer(this, 52, 42);
		tailA.addBox(-1.5f, -1.0f, -2.0f, 3, 4, 3);
		tailA.setRotationPoint(0.0f, 9.5f, 6.0f);
		setRotation(tailA, 1.064651f, 0.0f, 0.0f);
		tailC = new ModelRenderer(this, 48, 59);
		tailC.addBox(-2.0f, 6.8f, -4.6f, 4, 6, 4);
		tailC.setRotationPoint(0.0f, 9.5f, 6.0f);
		setRotation(tailC, 1.099557f, 0.0f, 0.0f);
		tailB = new ModelRenderer(this, 48, 49);
		tailB.addBox(-2.0f, 2.0f, -2.0f, 4, 6, 4);
		tailB.setRotationPoint(0.0f, 9.5f, 6.0f);
		setRotation(tailB, 0.7504916f, 0.0f, 0.0f);
		tailD = new ModelRenderer(this, 52, 69);
		tailD.addBox(-1.5f, 9.8f, -4.1f, 3, 5, 3);
		tailD.setRotationPoint(0.0f, 9.5f, 6.0f);
		setRotation(tailD, 1.099557f, 0.0f, 0.0f);
		rLegA = new ModelRenderer(this, 12, 64);
		rLegA.addBox(-2.5f, -1.5f, -3.5f, 3, 8, 5);
		rLegA.setRotationPoint(-3.0f, 9.5f, 3.0f);
		setRotation(rLegA, -0.8126625f, 0.0f, 0.0f);
		rFoot = new ModelRenderer(this, 14, 93);
		rFoot.addBox(-2.506667f, 12.5f, -5.0f, 3, 2, 3);
		rFoot.setRotationPoint(-3.0f, 9.5f, 3.0f);
		rLegB = new ModelRenderer(this, 14, 76);
		rLegB.addBox(-1.9f, 4.2f, 0.5f, 2, 2, 5);
		rLegB.setRotationPoint(-3.0f, 9.5f, 3.0f);
		setRotation(rLegB, -0.8445741f, 0.0f, 0.0f);
		rLegC = new ModelRenderer(this, 14, 83);
		rLegC.addBox(-2.0f, 6.2f, 0.5f, 2, 8, 2);
		rLegC.setRotationPoint(-3.0f, 9.5f, 3.0f);
		setRotation(rLegC, -0.2860688f, 0.0f, 0.0f);
		lLegB = new ModelRenderer(this, 0, 76);
		lLegB.addBox(-0.1f, 4.2f, 0.5f, 2, 2, 5);
		lLegB.setRotationPoint(3.0f, 9.5f, 3.0f);
		setRotation(lLegB, -0.8445741f, 0.0f, 0.0f);
		lFoot = new ModelRenderer(this, 0, 93);
		lFoot.addBox(-0.5066667f, 12.5f, -5.0f, 3, 2, 3);
		lFoot.setRotationPoint(3.0f, 9.5f, 3.0f);
		lLegC = new ModelRenderer(this, 0, 83);
		lLegC.addBox(0.0f, 6.2f, 0.5f, 2, 8, 2);
		lLegC.setRotationPoint(3.0f, 9.5f, 3.0f);
		setRotation(lLegC, -0.2860688f, 0.0f, 0.0f);
		lLegA = new ModelRenderer(this, 0, 64);
		lLegA.addBox(-0.5f, -1.5f, -3.5f, 3, 8, 5);
		lLegA.setRotationPoint(3.0f, 9.5f, 3.0f);
		setRotation(lLegA, -0.8126625f, 0.0f, 0.0f);
		rArmB = new ModelRenderer(this, 48, 77);
		rArmB.addBox(-3.5f, 1.0f, -1.5f, 4, 8, 4);
		rArmB.setRotationPoint(-4.0f, -4.0f, -2.0f);
		setRotation(rArmB, 0.2617994f, 0.0f, 0.3490659f);
		rArmC = new ModelRenderer(this, 48, 112);
		rArmC.addBox(-6.0f, 5.0f, 3.0f, 4, 7, 4);
		rArmC.setRotationPoint(-4.0f, -4.0f, -2.0f);
		setRotation(rArmC, -0.3490659f, 0.0f, 0.0f);
		lArmB = new ModelRenderer(this, 48, 89);
		lArmB.addBox(-0.5f, 1.0f, -1.5f, 4, 8, 4);
		lArmB.setRotationPoint(4.0f, -4.0f, -2.0f);
		setRotation(lArmB, 0.2617994f, 0.0f, -0.3490659f);
		rHand = new ModelRenderer(this, 32, 118);
		rHand.addBox(-6.0f, 12.5f, -1.5f, 4, 3, 4);
		rHand.setRotationPoint(-4.0f, -4.0f, -2.0f);
		rArmA = new ModelRenderer(this, 0, 108);
		rArmA.addBox(-5.0f, -3.0f, -2.0f, 5, 5, 5);
		rArmA.setRotationPoint(-4.0f, -4.0f, -2.0f);
		setRotation(rArmA, 0.6320364f, 0.0f, 0.0f);
		lArmA = new ModelRenderer(this, 0, 98);
		lArmA.addBox(0.0f, -3.0f, -2.0f, 5, 5, 5);
		lArmA.setRotationPoint(4.0f, -4.0f, -2.0f);
		setRotation(lArmA, 0.6320364f, 0.0f, 0.0f);
		lArmC = new ModelRenderer(this, 48, 101);
		lArmC.addBox(2.0f, 5.0f, 3.0f, 4, 7, 4);
		lArmC.setRotationPoint(4.0f, -4.0f, -2.0f);
		setRotation(lArmC, -0.3490659f, 0.0f, 0.0f);
		lHand = new ModelRenderer(this, 32, 111);
		lHand.addBox(2.0f, 12.5f, -1.5f, 4, 3, 4);
		lHand.setRotationPoint(4.0f, -4.0f, -2.0f);
		rFinger1 = new ModelRenderer(this, 8, 120);
		rFinger1.addBox(-0.5f, 0.0f, -0.5f, 1, 3, 1);
		rFinger1.setRotationPoint(-6.5f, 11.5f, -0.5f);
		rFinger1 = new ModelRenderer(this, 8, 120);
		rFinger1.addBox(-3.0f, 15.5f, 1.0f, 1, 3, 1);
		rFinger1.setRotationPoint(-4.0f, -4.0f, -2.0f);
		rFinger2 = new ModelRenderer(this, 12, 124);
		rFinger2.addBox(-3.5f, 15.5f, -1.5f, 1, 3, 1);
		rFinger2.setRotationPoint(-4.0f, -4.0f, -2.0f);
		rFinger3 = new ModelRenderer(this, 12, 119);
		rFinger3.addBox(-4.8f, 15.5f, -1.5f, 1, 4, 1);
		rFinger3.setRotationPoint(-4.0f, -4.0f, -2.0f);
		rFinger4 = new ModelRenderer(this, 16, 119);
		rFinger4.addBox(-6.0f, 15.5f, -0.5f, 1, 4, 1);
		rFinger4.setRotationPoint(-4.0f, -4.0f, -2.0f);
		rFinger5 = new ModelRenderer(this, 16, 124);
		rFinger5.addBox(-6.0f, 15.5f, 1.0f, 1, 3, 1);
		rFinger5.setRotationPoint(-4.0f, -4.0f, -2.0f);
		lFinger1 = new ModelRenderer(this, 8, 124);
		lFinger1.addBox(2.0f, 15.5f, 1.0f, 1, 3, 1);
		lFinger1.setRotationPoint(4.0f, -4.0f, -2.0f);
		lFinger2 = new ModelRenderer(this, 0, 124);
		lFinger2.addBox(2.5f, 15.5f, -1.5f, 1, 3, 1);
		lFinger2.setRotationPoint(4.0f, -4.0f, -2.0f);
		lFinger3 = new ModelRenderer(this, 0, 119);
		lFinger3.addBox(3.8f, 15.5f, -1.5f, 1, 4, 1);
		lFinger3.setRotationPoint(4.0f, -4.0f, -2.0f);
		lFinger4 = new ModelRenderer(this, 4, 119);
		lFinger4.addBox(5.0f, 15.5f, -0.5f, 1, 4, 1);
		lFinger4.setRotationPoint(4.0f, -4.0f, -2.0f);
		lFinger5 = new ModelRenderer(this, 4, 124);
		lFinger5.addBox(5.0f, 15.5f, 1.0f, 1, 3, 1);
		lFinger5.setRotationPoint(4.0f, -4.0f, -2.0f);
	}

	private static void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4);
		head.render(f5);
		nose.render(f5);
		snout.render(f5);
		teethU.render(f5);
		teethL.render(f5);
		mouth.render(f5);
		lEar.render(f5);
		rEar.render(f5);
		neck.render(f5);
		neck2.render(f5);
		sideburnL.render(f5);
		sideburnR.render(f5);
		chest.render(f5);
		abdomen.render(f5);
		tailA.render(f5);
		tailC.render(f5);
		tailB.render(f5);
		tailD.render(f5);
		rLegA.render(f5);
		rFoot.render(f5);
		rLegB.render(f5);
		rLegC.render(f5);
		lLegB.render(f5);
		lFoot.render(f5);
		lLegC.render(f5);
		lLegA.render(f5);
		rArmB.render(f5);
		rArmC.render(f5);
		lArmB.render(f5);
		rHand.render(f5);
		rArmA.render(f5);
		lArmA.render(f5);
		lArmC.render(f5);
		lHand.render(f5);
		rFinger1.render(f5);
		rFinger2.render(f5);
		rFinger3.render(f5);
		rFinger4.render(f5);
		rFinger5.render(f5);
		lFinger1.render(f5);
		lFinger2.render(f5);
		lFinger3.render(f5);
		lFinger4.render(f5);
		lFinger5.render(f5);
	}

	private void setRotationAngles(float f, float f1, float f2, float f3, float f4) {
		float radianF = 57.29578f;
		float RLegXRot = MathHelper.cos(f * 0.6662f + 3.141593f) * 0.8f * f1;
		float LLegXRot = MathHelper.cos(f * 0.6662f) * 0.8f * f1;
		head.rotateAngleY = f3 / radianF;
		head.rotationPointY = -8.0f;
		head.rotationPointZ = -6.0f;
		head.rotateAngleX = f4 / radianF;
		neck.rotateAngleX = -34.0f / radianF;
		neck.rotationPointY = -5.0f;
		neck.rotationPointZ = -2.0f;
		neck2.rotationPointY = -1.0f;
		neck2.rotationPointZ = -6.0f;
		chest.rotationPointY = -6.0f;
		chest.rotationPointZ = -2.5f;
		chest.rotateAngleX = 36.0f / radianF;
		abdomen.rotateAngleX = 15.0f / radianF;
		lLegA.rotationPointZ = 3.0f;
		lArmA.rotationPointY = -4.0f;
		lArmA.rotationPointZ = -2.0f;
		tailA.rotationPointY = 9.5f;
		tailA.rotationPointZ = 6.0f;
		nose.rotationPointY = head.rotationPointY;
		snout.rotationPointY = head.rotationPointY;
		teethU.rotationPointY = head.rotationPointY;
		lEar.rotationPointY = head.rotationPointY;
		rEar.rotationPointY = head.rotationPointY;
		teethL.rotationPointY = head.rotationPointY;
		mouth.rotationPointY = head.rotationPointY;
		sideburnL.rotationPointY = head.rotationPointY;
		sideburnR.rotationPointY = head.rotationPointY;
		nose.rotationPointZ = head.rotationPointZ;
		snout.rotationPointZ = head.rotationPointZ;
		teethU.rotationPointZ = head.rotationPointZ;
		lEar.rotationPointZ = head.rotationPointZ;
		rEar.rotationPointZ = head.rotationPointZ;
		teethL.rotationPointZ = head.rotationPointZ;
		mouth.rotationPointZ = head.rotationPointZ;
		sideburnL.rotationPointZ = head.rotationPointZ;
		sideburnR.rotationPointZ = head.rotationPointZ;
		lArmB.rotationPointY = lArmA.rotationPointY;
		lArmC.rotationPointY = lArmA.rotationPointY;
		lHand.rotationPointY = lArmA.rotationPointY;
		lFinger1.rotationPointY = lArmA.rotationPointY;
		lFinger2.rotationPointY = lArmA.rotationPointY;
		lFinger3.rotationPointY = lArmA.rotationPointY;
		lFinger4.rotationPointY = lArmA.rotationPointY;
		lFinger5.rotationPointY = lArmA.rotationPointY;
		rArmA.rotationPointY = lArmA.rotationPointY;
		rArmB.rotationPointY = lArmA.rotationPointY;
		rArmC.rotationPointY = lArmA.rotationPointY;
		rHand.rotationPointY = lArmA.rotationPointY;
		rFinger1.rotationPointY = lArmA.rotationPointY;
		rFinger2.rotationPointY = lArmA.rotationPointY;
		rFinger3.rotationPointY = lArmA.rotationPointY;
		rFinger4.rotationPointY = lArmA.rotationPointY;
		rFinger5.rotationPointY = lArmA.rotationPointY;
		lArmB.rotationPointZ = lArmA.rotationPointZ;
		lArmC.rotationPointZ = lArmA.rotationPointZ;
		lHand.rotationPointZ = lArmA.rotationPointZ;
		lFinger1.rotationPointZ = lArmA.rotationPointZ;
		lFinger2.rotationPointZ = lArmA.rotationPointZ;
		lFinger3.rotationPointZ = lArmA.rotationPointZ;
		lFinger4.rotationPointZ = lArmA.rotationPointZ;
		lFinger5.rotationPointZ = lArmA.rotationPointZ;
		rArmA.rotationPointZ = lArmA.rotationPointZ;
		rArmB.rotationPointZ = lArmA.rotationPointZ;
		rArmC.rotationPointZ = lArmA.rotationPointZ;
		rHand.rotationPointZ = lArmA.rotationPointZ;
		rFinger1.rotationPointZ = lArmA.rotationPointZ;
		rFinger2.rotationPointZ = lArmA.rotationPointZ;
		rFinger3.rotationPointZ = lArmA.rotationPointZ;
		rFinger4.rotationPointZ = lArmA.rotationPointZ;
		rFinger5.rotationPointZ = lArmA.rotationPointZ;
		rLegA.rotationPointZ = lLegA.rotationPointZ;
		rLegB.rotationPointZ = lLegA.rotationPointZ;
		rLegC.rotationPointZ = lLegA.rotationPointZ;
		rFoot.rotationPointZ = lLegA.rotationPointZ;
		lLegB.rotationPointZ = lLegA.rotationPointZ;
		lLegC.rotationPointZ = lLegA.rotationPointZ;
		lFoot.rotationPointZ = lLegA.rotationPointZ;
		tailB.rotationPointY = tailA.rotationPointY;
		tailB.rotationPointZ = tailA.rotationPointZ;
		tailC.rotationPointY = tailA.rotationPointY;
		tailC.rotationPointZ = tailA.rotationPointZ;
		tailD.rotationPointY = tailA.rotationPointY;
		tailD.rotationPointZ = tailA.rotationPointZ;
		nose.rotateAngleY = head.rotateAngleY;
		snout.rotateAngleY = head.rotateAngleY;
		teethU.rotateAngleY = head.rotateAngleY;
		lEar.rotateAngleY = head.rotateAngleY;
		rEar.rotateAngleY = head.rotateAngleY;
		teethL.rotateAngleY = head.rotateAngleY;
		mouth.rotateAngleY = head.rotateAngleY;
		teethL.rotateAngleX = head.rotateAngleX + 2.530727f;
		mouth.rotateAngleX = head.rotateAngleX + 2.530727f;
		sideburnL.rotateAngleX = -0.2094395f + head.rotateAngleX;
		sideburnL.rotateAngleY = 0.418879f + head.rotateAngleY;
		sideburnR.rotateAngleX = -0.2094395f + head.rotateAngleX;
		sideburnR.rotateAngleY = -0.418879f + head.rotateAngleY;
		nose.rotateAngleX = 0.2792527f + head.rotateAngleX;
		snout.rotateAngleX = head.rotateAngleX;
		teethU.rotateAngleX = head.rotateAngleX;
		lEar.rotateAngleX = head.rotateAngleX;
		rEar.rotateAngleX = head.rotateAngleX;
		rLegA.rotateAngleX = -0.8126625f + RLegXRot;
		rLegB.rotateAngleX = -0.8445741f + RLegXRot;
		rLegC.rotateAngleX = -0.2860688f + RLegXRot;
		rFoot.rotateAngleX = RLegXRot;
		lLegA.rotateAngleX = -0.8126625f + LLegXRot;
		lLegB.rotateAngleX = -0.8445741f + LLegXRot;
		lLegC.rotateAngleX = -0.2860688f + LLegXRot;
		lFoot.rotateAngleX = LLegXRot;
		rArmA.rotateAngleZ = -(MathHelper.cos(f2 * 0.09f) * 0.05f) + 0.05f;
		lArmA.rotateAngleZ = MathHelper.cos(f2 * 0.09f) * 0.05f - 0.05f;
		rArmA.rotateAngleX = LLegXRot;
		lArmA.rotateAngleX = RLegXRot;
		rArmB.rotateAngleZ = 0.3490659f + rArmA.rotateAngleZ;
		lArmB.rotateAngleZ = -0.3490659f + lArmA.rotateAngleZ;
		rArmB.rotateAngleX = 0.2617994f + rArmA.rotateAngleX;
		lArmB.rotateAngleX = 0.2617994f + lArmA.rotateAngleX;
		rArmC.rotateAngleZ = rArmA.rotateAngleZ;
		lArmC.rotateAngleZ = lArmA.rotateAngleZ;
		rArmC.rotateAngleX = -0.3490659f + rArmA.rotateAngleX;
		lArmC.rotateAngleX = -0.3490659f + lArmA.rotateAngleX;
		rHand.rotateAngleZ = rArmA.rotateAngleZ;
		lHand.rotateAngleZ = lArmA.rotateAngleZ;
		rHand.rotateAngleX = rArmA.rotateAngleX;
		lHand.rotateAngleX = lArmA.rotateAngleX;
		rFinger1.rotateAngleX = rArmA.rotateAngleX;
		rFinger2.rotateAngleX = rArmA.rotateAngleX;
		rFinger3.rotateAngleX = rArmA.rotateAngleX;
		rFinger4.rotateAngleX = rArmA.rotateAngleX;
		rFinger5.rotateAngleX = rArmA.rotateAngleX;
		lFinger1.rotateAngleX = lArmA.rotateAngleX;
		lFinger2.rotateAngleX = lArmA.rotateAngleX;
		lFinger3.rotateAngleX = lArmA.rotateAngleX;
		lFinger4.rotateAngleX = lArmA.rotateAngleX;
		lFinger5.rotateAngleX = lArmA.rotateAngleX;
		rFinger1.rotateAngleZ = rArmA.rotateAngleZ;
		rFinger2.rotateAngleZ = rArmA.rotateAngleZ;
		rFinger3.rotateAngleZ = rArmA.rotateAngleZ;
		rFinger4.rotateAngleZ = rArmA.rotateAngleZ;
		rFinger5.rotateAngleZ = rArmA.rotateAngleZ;
		lFinger1.rotateAngleZ = lArmA.rotateAngleZ;
		lFinger2.rotateAngleZ = lArmA.rotateAngleZ;
		lFinger3.rotateAngleZ = lArmA.rotateAngleZ;
		lFinger4.rotateAngleZ = lArmA.rotateAngleZ;
		lFinger5.rotateAngleZ = lArmA.rotateAngleZ;
	}
}