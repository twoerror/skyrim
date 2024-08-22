package tes.common.world.map;

public enum TESMountains {
	IBBEN_WALL_1(2847, 1273, 2.0F, 50), IBBEN_WALL_2(2588, 1275, 2.0F, 50), IBBEN_WALL_3(2708, 1230, 2.0F, 50), IBBEN_WALL_4(2638, 1252, 2.0F, 50), KING_SPEARS_01(793, 1646, 4.0F, 40), KING_SPEARS_02(795, 1650, 4.0F, 40), KING_SPEARS_03(798, 1646, 4.0F, 40), KING_SPEARS_04(800, 1648, 4.0F, 40), KING_SPEARS_05(800, 1652, 4.0F, 40), KING_SPEARS_06(805, 1646, 4.0F, 40), KING_SPEARS_07(805, 1650, 4.0F, 40), KING_SPEARS_08(804, 1654, 4.0F, 40), KING_SPEARS_09(811, 1648, 4.0F, 40), KING_SPEARS_10(810, 1651, 4.0F, 40), KING_SPEARS_11(817, 1649, 4.0F, 40), KING_SPEARS_12(814, 1652, 4.0F, 40), KING_SPEARS_13(819, 1652, 4.0F, 40), KING_SPEARS_14(822, 1649, 4.0F, 40), KING_SPEARS_15(828, 1648, 4.0F, 40), KING_SPEARS_16(833, 1647, 4.0F, 40), KING_SPEARS_17(837, 1644, 4.0F, 40), KING_SPEARS_18(842, 1644, 4.0F, 40), KING_SPEARS_19(846, 1652, 4.0F, 40), KING_SPEARS_20(851, 1643, 4.0F, 40), KING_SPEARS_21(856, 1641, 4.0F, 40), KING_SPEARS_22(860, 1642, 4.0F, 40), KING_SPEARS_23(864, 1642, 4.0F, 40), KING_SPEARS_24(869, 1639, 4.0F, 40), KING_SPEARS_25(872, 1635, 4.0F, 40), KING_SPEARS_26(877, 1633, 4.0F, 40), KING_SPEARS_27(882, 1634, 4.0F, 40), KING_SPEARS_28(873, 1651, 4.0F, 40), KING_SPEARS_29(877, 1649, 4.0F, 40), KING_SPEARS_30(880, 1644, 4.0F, 40), KING_SPEARS_31(882, 1647, 4.0F, 40), KING_SPEARS_32(881, 1650, 4.0F, 40), KING_SPEARS_33(882, 1638, 4.0F, 40), KING_SPEARS_34(884, 1640, 4.0F, 40), KING_SPEARS_35(885, 1643, 4.0F, 40), KING_SPEARS_36(885, 1648, 4.0F, 40), KING_SPEARS_37(886, 1635, 4.0F, 40), KING_SPEARS_38(912, 1639, 4.0F, 40), KING_SPEARS_39(908, 1639, 4.0F, 40), KING_SPEARS_40(904, 1638, 4.0F, 40), KING_SPEARS_41(904, 1641, 4.0F, 40), KING_SPEARS_42(902, 1644, 4.0F, 40), KING_SPEARS_43(900, 1641, 4.0F, 40), KING_SPEARS_44(899, 1646, 4.0F, 40), KING_SPEARS_45(899, 1636, 4.0F, 40), KING_SPEARS_46(895, 1636, 4.0F, 40), KING_SPEARS_47(896, 1639, 4.0F, 40), KING_SPEARS_48(895, 1644, 4.0F, 40), KING_SPEARS_49(894, 1648, 4.0F, 40), KING_SPEARS_50(892, 1642, 4.0F, 40), KING_SPEARS_51(892, 1633, 4.0F, 40), KING_SPEARS_52(890, 1636, 4.0F, 40), KING_SPEARS_53(889, 1650, 4.0F, 40), KING_SPEARS_54(889, 1640, 4.0F, 40), KING_SPEARS_55(889, 1645, 4.0F, 40), OLDTOWN_1(388, 1946, 1.0f, 70), OLDTOWN_2(389, 1944, 1.0f, 70), OLDTOWN_3(391, 1944, 1.0f, 70);
	private final float height;
	private final int xCoord;
	private final int zCoord;
	private final int range;
	private final int lavaRange;

	TESMountains(double x, double z, float h, int r) {
		this(x, z, h, r, 0);
	}

	TESMountains(TESAbstractWaypoint waypoint, float h, int r) {
		this(waypoint.getImgX(), waypoint.getImgY(), h, r, 0);
	}

	TESMountains(double x, double z, float h, int r, int l) {
		xCoord = TESWaypoint.mapToWorldX(x);
		zCoord = TESWaypoint.mapToWorldZ(z);
		height = h;
		range = r;
		lavaRange = l;
	}

	public static int getLavaHeight(int x, int z) {
		for (TESMountains m : values()) {
			double dx;
			double dz;
			if (m.lavaRange <= 0 || (dx = x - m.xCoord) * dx + (dz = z - m.zCoord) * dz >= (m.lavaRange + 6) * (m.lavaRange + 6)) {
				continue;
			}
			return Math.round(m.getLavaCraterHeight() * 110.0f);
		}
		return 0;
	}

	public static float getTotalHeightBoost(int x, int z) {
		float f = 0.0f;
		for (TESMountains m : values()) {
			f += m.getHeightBoost(x, z);
		}
		return f;
	}

	public static boolean mountainAt(int x, int z) {
		return getTotalHeightBoost(x, z) > 0.005f;
	}

	public static boolean mountainNear(int x, int z, int range) {
		for (TESMountains m : values()) {
			double dx = x - m.xCoord;
			double dz = z - m.zCoord;
			double distSq = dx * dx + dz * dz;
			double mtnRange = range + m.range;
			double rangeSq = mtnRange * mtnRange;
			if (distSq > rangeSq) {
				continue;
			}
			return true;
		}
		return false;
	}

	private float getHeightBoost(int x, int z) {
		double dx = x - xCoord;
		double dz = z - zCoord;
		double distSq = dx * dx + dz * dz;
		double rangeSq = range * range;
		if (distSq < rangeSq) {
			if (lavaRange > 0 && distSq < lavaRange * lavaRange) {
				return getLavaCraterHeight();
			}
			double dist = Math.sqrt(distSq);
			float f = (float) (dist / range);
			return (1.0f - f) * height;
		}
		return 0.0f;
	}

	private float getLavaCraterHeight() {
		return (1.0f - (float) lavaRange / range) * height * 0.4f;
	}
}