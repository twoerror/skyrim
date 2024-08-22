package tes.client.model;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.EntityBodyHelper;

public class TESModelDragonBodyHelper extends EntityBodyHelper {
	private static final int TURN_TICKS_LIMIT = 20;

	private final TESEntityDragon dragon;

	private float prevRotationYawHead;
	private int turnTicks;

	public TESModelDragonBodyHelper(TESEntityDragon dragon) {
		super(dragon);
		this.dragon = dragon;
	}

	@Override
	public void func_75664_a() {
		double deltaX = dragon.posX - dragon.prevPosX;
		double deltaY = dragon.posZ - dragon.prevPosZ;
		double dist = deltaX * deltaX + deltaY * deltaY;
		float yawSpeed = 90;
		if (dragon.isFlying() || dist > 0.0001) {
			dragon.renderYawOffset = dragon.rotationYaw;
			dragon.rotationYawHead = TESModelDragonAnimaton.updateRotation(dragon.renderYawOffset, dragon.rotationYawHead, yawSpeed);
			prevRotationYawHead = dragon.rotationYawHead;
			turnTicks = 0;
			return;
		}
		double yawDiff = Math.abs(dragon.rotationYawHead - prevRotationYawHead);
		if (dragon.isSitting() || yawDiff > 15) {
			turnTicks = 0;
			prevRotationYawHead = dragon.rotationYawHead;
		} else {
			turnTicks++;
			if (turnTicks > TURN_TICKS_LIMIT) {
				yawSpeed = Math.max(1 - (float) (turnTicks - TURN_TICKS_LIMIT) / TURN_TICKS_LIMIT, 0) * 75;
			}
		}
		dragon.renderYawOffset = TESModelDragonAnimaton.updateRotation(dragon.rotationYawHead, dragon.renderYawOffset, yawSpeed);
	}
}