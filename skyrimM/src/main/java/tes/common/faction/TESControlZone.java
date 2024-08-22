package tes.common.faction;

import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESWaypoint;

public class TESControlZone {
	private final long radiusCoordSq;
	private final int radiusCoord;

	private final int radius;
	private final int xCoord;
	private final int zCoord;

	public TESControlZone(double x, double y, int r) {
		radius = r;
		xCoord = TESWaypoint.mapToWorldX(x);
		zCoord = TESWaypoint.mapToWorldZ(y);
		radiusCoord = TESWaypoint.mapToWorldR(radius);
		radiusCoordSq = (long) radiusCoord * radiusCoord;
	}

	public TESControlZone(TESAbstractWaypoint wp, int r) {
		this(wp.getImgX(), wp.getImgY(), r);
	}

	public boolean inZone(double x, double z, int extraMapRange) {
		double dx = x - xCoord;
		double dz = z - zCoord;
		double distSq = dx * dx + dz * dz;
		if (extraMapRange == 0) {
			return distSq <= radiusCoordSq;
		}
		int checkRadius = TESWaypoint.mapToWorldR(radius + extraMapRange);
		long checkRadiusSq = (long) checkRadius * checkRadius;
		return distSq <= checkRadiusSq;
	}

	public int getRadius() {
		return radius;
	}

	public int getCoordX() {
		return xCoord;
	}

	public int getCoordZ() {
		return zCoord;
	}

	public int getRadiusCoord() {
		return radiusCoord;
	}
}