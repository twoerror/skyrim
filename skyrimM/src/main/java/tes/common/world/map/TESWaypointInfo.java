package tes.common.world.map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TESWaypointInfo implements TESAbstractWaypoint {
	private final TESWaypoint waypoint;

	private final double shiftX;
	private final double shiftY;
	private final int rotation;

	public TESWaypointInfo(TESWaypoint waypoint, double shiftX, double shiftY, int rotation) {
		this.shiftX = shiftX;
		this.shiftY = shiftY;
		this.waypoint = waypoint;
		this.rotation = rotation;
	}

	@Override
	public String getCodeName() {
		return waypoint.getCodeName();
	}

	@Override
	public String getDisplayName() {
		return waypoint.getDisplayName();
	}

	@Override
	public int getID() {
		return waypoint.getID();
	}

	@Override
	public TESWaypoint getInstance() {
		return waypoint;
	}

	@Override
	public WaypointLockState getLockState(EntityPlayer entityplayer) {
		return waypoint.getLockState(entityplayer);
	}

	@Override
	public String getLoreText(EntityPlayer entityplayer) {
		return waypoint.getLoreText(entityplayer);
	}

	@Override
	public int getRotation() {
		return rotation;
	}

	@Override
	public double getShiftX() {
		return shiftX;
	}

	@Override
	public double getShiftY() {
		return shiftY;
	}

	@Override
	public double getImgX() {
		return waypoint.getImgX() + shiftX;
	}

	@Override
	public int getCoordX() {
		return TESWaypoint.mapToWorldX(waypoint.getImgX() + shiftX);
	}

	@Override
	public double getImgY() {
		return waypoint.getImgY() + shiftY;
	}

	@Override
	public int getCoordY(World world, int i, int k) {
		return waypoint.getCoordY(world, i, k);
	}

	@Override
	public int getCoordYSaved() {
		return waypoint.getCoordYSaved();
	}

	@Override
	public int getCoordZ() {
		return TESWaypoint.mapToWorldZ(waypoint.getImgY() + shiftY);
	}

	@Override
	public boolean hasPlayerUnlocked(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}