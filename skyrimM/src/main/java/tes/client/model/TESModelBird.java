package tes.client.model;

import tes.common.entity.animal.TESEntityBird;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class TESModelBird extends ModelBase {
	private final ModelRenderer head;
	private final ModelRenderer body = new ModelRenderer(this, 0, 7);
	private final ModelRenderer wingRight;
	private final ModelRenderer wingLeft;
	private final ModelRenderer legRight;
	private final ModelRenderer legLeft;

	public TESModelBird() {
		body.addBox(-1.5f, -2.0f, -2.0f, 3, 3, 5);
		body.setTextureOffset(8, 0).addBox(-1.0f, -1.5f, 3.0f, 2, 1, 3);
		body.setTextureOffset(8, 4).addBox(-1.0f, -0.5f, 3.0f, 2, 1, 2);
		body.setRotationPoint(0.0f, 21.0f, 0.0f);
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-1.0f, -1.5f, -1.5f, 2, 2, 2);
		head.setTextureOffset(0, 4).addBox(-0.5f, -0.5f, -2.5f, 1, 1, 1);
		head.setTextureOffset(15, 0).addBox(-0.5f, -0.5f, -3.5f, 1, 1, 2);
		head.setRotationPoint(0.0f, -2.0f, -2.0f);
		body.addChild(head);
		wingRight = new ModelRenderer(this, 16, 7);
		wingRight.addBox(0.0f, 0.0f, -2.0f, 0, 5, 4);
		wingRight.setRotationPoint(-1.5f, -1.5f, 0.5f);
		body.addChild(wingRight);
		wingLeft = new ModelRenderer(this, 16, 7);
		wingLeft.mirror = true;
		wingLeft.addBox(0.0f, 0.0f, -2.0f, 0, 5, 4);
		wingLeft.setRotationPoint(1.5f, -1.5f, 0.5f);
		body.addChild(wingLeft);
		legRight = new ModelRenderer(this, 0, 16);
		legRight.addBox(-1.0f, 0.0f, -1.5f, 1, 2, 2);
		legRight.setRotationPoint(-0.3f, 1.0f, 0.5f);
		body.addChild(legRight);
		legLeft = new ModelRenderer(this, 0, 16);
		legLeft.mirror = true;
		legLeft.addBox(0.0f, 0.0f, -1.5f, 1, 2, 2);
		legLeft.setRotationPoint(0.3f, 1.0f, 0.5f);
		body.addChild(legLeft);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		body.render(f5);
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
		TESEntityBird bird = (TESEntityBird) entity;
		if (bird.isBirdStill()) {
			body.rotateAngleX = -0.17453292519943295f;
			head.rotateAngleX = 0.3490658503988659f;
			wingRight.rotateAngleZ = bird.getFlapTime() > 0 ? 1.5707963267948966f + MathHelper.cos(f2 * 1.5f) * 0.5235987755982988f : 0.5235987755982988f;
			wingLeft.rotateAngleZ = -wingRight.rotateAngleZ;
			legRight.rotateAngleX = legLeft.rotateAngleX = -body.rotateAngleX;
			legRight.rotateAngleX += MathHelper.cos(f * 0.6662f) * f1;
			legLeft.rotateAngleX += MathHelper.cos(f * 0.6662f + 3.141593f) * f1;
			legRight.rotationPointY = 1.0f;
			legLeft.rotationPointY = 1.0f;
		} else {
			body.rotateAngleX = 0.0f;
			head.rotateAngleX = 0.0f;
			wingRight.rotateAngleZ = 1.5707963267948966f + MathHelper.cos(f2 * 1.5f) * 0.5235987755982988f;
			wingLeft.rotateAngleZ = -wingRight.rotateAngleZ;
			legLeft.rotateAngleX = 0.0f;
			legRight.rotateAngleX = 0.0f;
			legRight.rotationPointY = 0.0f;
			legLeft.rotationPointY = 0.0f;
		}
	}

	public ModelRenderer getHead() {
		return head;
	}
}