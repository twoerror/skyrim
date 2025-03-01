package tes.common.entity.ai;

import tes.common.entity.other.TESEntitySpiderBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class TESEntityAIUntamedSpiderPanic extends EntityAIBase {
	private final TESEntitySpiderBase theMount;
	private final double speed;

	private double targetX;
	private double targetY;
	private double targetZ;

	public TESEntityAIUntamedSpiderPanic(TESEntitySpiderBase mount, double d) {
		theMount = mount;
		speed = d;
		setMutexBits(1);
	}

	@Override
	public boolean continueExecuting() {
		return !theMount.getNavigator().noPath() && theMount.riddenByEntity instanceof EntityPlayer && !theMount.isNPCTamed();
	}

	@Override
	public boolean shouldExecute() {
		if (!theMount.isNPCTamed() && theMount.riddenByEntity instanceof EntityPlayer) {
			Vec3 vec3 = RandomPositionGenerator.findRandomTarget(theMount, 5, 4);
			if (vec3 == null) {
				return false;
			}
			targetX = vec3.xCoord;
			targetY = vec3.yCoord;
			targetZ = vec3.zCoord;
			return true;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		theMount.getNavigator().tryMoveToXYZ(targetX, targetY, targetZ, speed);
	}

	@Override
	public void updateTask() {
		if (theMount.getRNG().nextInt(50) == 0) {
			if (theMount.riddenByEntity instanceof EntityPlayer) {
				int i = theMount.getNPCTemper();
				int j = 100;
				if (theMount.getRNG().nextInt(j) < i) {
					theMount.tameNPC((EntityPlayer) theMount.riddenByEntity);
					theMount.spawnHearts();
					return;
				}
				theMount.increaseNPCTemper(5);
			}
			theMount.riddenByEntity.mountEntity(null);
			theMount.riddenByEntity = null;
			theMount.angerNPC();
			theMount.spawnSmokes();
		}
	}
}