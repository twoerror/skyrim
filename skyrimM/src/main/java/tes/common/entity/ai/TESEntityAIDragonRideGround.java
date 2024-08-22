package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.init.Items;
import net.minecraft.util.Vec3;

public class TESEntityAIDragonRideGround extends TESEntityAIDragonRide {
	private final double speed;

	public TESEntityAIDragonRideGround(TESEntityDragon dragon, double speed) {
		super(dragon);
		this.speed = speed;
	}

	@Override
	public void startExecuting() {
		dragon.getNavigator().clearPathEntity();
	}

	@Override
	public void updateTask() {
		super.updateTask();

		float PLAYER_SPEED = 0.98f;
		float speedX = rider.moveForward / PLAYER_SPEED;
		float speedY = rider.moveStrafing / PLAYER_SPEED;

		if (TESEntityDragon.hasEquipped(rider, Items.carrot_on_a_stick)) {
			speedX = 1;
		}
		float speedPlayer = Math.max(Math.abs(speedX), Math.abs(speedY));

		Vec3 look = rider.getLookVec();
		float dir = Math.min(speedX, 0) * -1;
		dir += speedY / (speedX * 2 + (speedX < 0 ? -2 : 2));
		if (dir != 0) {
			look.rotateAroundY((float) Math.PI * dir);
		}
		if (speedPlayer > 0) {
			dragon.getMoveHelper().setMoveTo(dragon.posX + look.xCoord, dragon.posY, dragon.posZ + look.zCoord, speed * speedPlayer);
		}
		if (isFlyUp()) {
			dragon.liftOff();
		}
	}
}