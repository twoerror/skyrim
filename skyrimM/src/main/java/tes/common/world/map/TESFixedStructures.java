package tes.common.world.map;

import tes.TES;
import tes.common.TESConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public enum TESFixedStructures {
	NIGHT_KING(613, 314);

	private final int xCoord;
	private final int zCoord;

	TESFixedStructures(double x, double z) {
		xCoord = TESWaypoint.mapToWorldX(x);
		zCoord = TESWaypoint.mapToWorldZ(z);
	}

	public static boolean[] isMountainOrStructureNear(World world, int x, int z) {
		boolean mountainNear = false;
		boolean structureNear = false;
		if (hasMapFeatures(world)) {
			if (TESMountains.mountainAt(x, z)) {
				mountainNear = true;
			}
			structureNear = structureNear(x, z, 256);
			if (!structureNear) {
				for (TESWaypoint wp : TESWaypoint.values()) {
					double dz;
					double dx = x - wp.getCoordX();
					double distSq = dx * dx + (dz = z - wp.getCoordZ()) * dz;
					double range = 256.0;
					if (distSq >= range * range) {
						continue;
					}
					structureNear = true;
					break;
				}
			}
			boolean roadNear = TESBeziers.isBezierNear(x, z, 32, TESBeziers.Type.ROAD) >= 0.0f;
			boolean wallNear = TESBeziers.isBezierNear(x, z, 32, TESBeziers.Type.WALL) >= 0.0f;
			boolean linkerNear = TESBeziers.isBezierNear(x, z, 32, TESBeziers.Type.LINKER) >= 0.0f;

			if (!structureNear && (roadNear || wallNear || linkerNear)) {
				structureNear = true;
			}
		}
		return new boolean[]{mountainNear, structureNear};
	}

	public static boolean fixedAt(int i, int k, TESAbstractWaypoint waypoint) {
		int x = TESWaypoint.mapToWorldX(waypoint.getImgX());
		int z = TESWaypoint.mapToWorldZ(waypoint.getImgY());
		return fixedAtfixedAtMapImageCoords(i, k, x, z);
	}

	private static boolean fixedAtfixedAtMapImageCoords(int i, int k, int x, int z) {
		return i >> 4 == x >> 4 && k >> 4 == z >> 4;
	}

	public static boolean hasMapFeatures(World world) {
		return TESConfig.generateMapFeatures && world.getWorldInfo().getTerrainType() != TES.worldTypeTESClassic;
	}

	public static boolean structureNear(int x, int z, int range) {
		for (TESFixedStructures str : values()) {
			double dx = x - str.xCoord;
			double dz = z - str.zCoord;
			double distSq = dx * dx + dz * dz;
			double rangeSq = range * range;
			if (distSq >= rangeSq) {
				continue;
			}
			return true;
		}
		return false;
	}

	public double distanceSqTo(EntityLivingBase entity) {
		double dx = entity.posX - (xCoord + 0.5);
		double dz = entity.posZ - (zCoord + 0.5);
		return dx * dx + dz * dz;
	}
}