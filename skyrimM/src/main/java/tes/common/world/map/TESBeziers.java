package tes.common.world.map;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static tes.common.world.map.TESCoordConverter.*;

public class TESBeziers {
	public static final Collection<TESBeziers> CONTENT = new ArrayList<>();

	private static BezierPointDatabase linkerPointDatabase = new BezierPointDatabase();
	private static BezierPointDatabase roadPointDatabase = new BezierPointDatabase();
	private static BezierPointDatabase wallPointDatabase = new BezierPointDatabase();

	private final Collection<BezierPoint> endpoints = new ArrayList<>();

	private BezierPoint[] bezierPoints;

	private TESBeziers(BezierPoint... ends) {
		Collections.addAll(endpoints, ends);
	}

	public static boolean isBezierAt(int x, int z, Type type) {
		return isBezierNear(x, z, 4, type) >= 0.0f;
	}

	public static float isBezierNear(int x, int z, int width, Type type) {
		double widthSq = width * width;
		float leastSqRatio = -1.0f;
		List<BezierPoint> points = null;
		switch (type) {
			case ROAD:
				points = roadPointDatabase.getPointsForCoords(x, z);
				break;
			case WALL:
				points = wallPointDatabase.getPointsForCoords(x, z);
				break;
			case LINKER:
				points = linkerPointDatabase.getPointsForCoords(x, z);
				break;
		}
		for (BezierPoint point : points) {
			double dx = point.getX() - x;
			double dz = point.getZ() - z;
			double distSq = dx * dx + dz * dz;
			if (distSq >= widthSq) {
				continue;
			}
			float f = (float) (distSq / widthSq);
			if (leastSqRatio == -1.0f) {
				leastSqRatio = f;
				continue;
			}
			if (f >= leastSqRatio) {
				continue;
			}
			leastSqRatio = f;
		}
		return leastSqRatio;
	}

	public static void onInit() {
		CONTENT.clear();

		roadPointDatabase = new BezierPointDatabase();
		wallPointDatabase = new BezierPointDatabase();
		linkerPointDatabase = new BezierPointDatabase();


		registerBezier(Type.WALL, new double[]{2847, 1273}, new double[]{2820, 1292}, new double[]{2771, 1308}, new double[]{2732, 1308});
		registerBezier(Type.WALL, new double[]{2732, 1308}, new double[]{2683, 1294}, new double[]{2628, 1294}, new double[]{2588, 1275});
		registerBezier(Type.WALL, new double[]{2708, 1230}, new double[]{2683, 1244}, new double[]{2656, 1253}, new double[]{2638, 1252});

		/* NORTH */
		double[] northRiverlandsCrossroads = {655, 1257};

		/* ARRYN */

		/* RIVERLANDS */
		double[] northRiverlandsBridge = {713, 1400};
		double[] antiCrossroadsInn = {732, 1447};
		double[] hillsExit = {519, 1476};

		registerBezier(Type.ROAD, northRiverlandsCrossroads, new double[]{664, 1310}, new double[]{683, 1360}, northRiverlandsBridge);

		/* CROWNLANDS */

		/* WESTERLANDS */
		double[] westerlandsCrossroads = {363, 1716};
		double[] westerlandsHillsHalfway = {489, 1577};
		double[] reachCrossroads = {574, 1623};

		registerBezier(Type.ROAD, westerlandsHillsHalfway, new double[]{518, 1592}, new double[]{543, 1612}, reachCrossroads);

		/* REACH */
		double[] kingswoodCrossroads = {775, 1666};


		/* DORNE */

		/* STORMLANDS */
		double[] stormlandsCrossroads = {741, 1840};


		/* SOTHORYOS */
		double[] sothoryosHalfway = {2176, 2793};


		/* GHISCAR & LHAZAR */
		double[] giscarLhazarCrossroads = {2373, 1919};


		/* DOTHRAKI */

		/* QARTH */
		double[] boneMountP1 = {2978, 2279};
		double[] qarthYiTiBeforeMount = {2928, 2260};
		double[] qarthYiTiAfterMount = {3078, 2240};

		registerBezier(Type.ROAD, boneMountP1, new double[]{3002, 2284}, new double[]{3036, 2247}, qarthYiTiAfterMount);
		registerBezier(Type.ROAD, qarthYiTiBeforeMount, new double[]{2948, 2260}, new double[]{2965, 2267}, boneMountP1);
		/* FREE CITIES */
		double[] unhabitedCrossroads = {1956, 2026};
		double[] norvosHalfway = {1423, 1552};
		double[] braavosHalfway1 = {1250, 1451};
		double[] braavosHalfway2 = {1210, 1339};

		registerBezier(Type.ROAD, braavosHalfway1, new double[]{1246, 1421}, new double[]{1223, 1376}, braavosHalfway2);

		double[] asshaiCrossroadsHalfway = {3674, 2408};
		double[] asshaiCrossroads = {3729, 2423};

			}

	private static void registerBezier(Type type, Object... waypoints) {
		ArrayList<BezierPoint> points = new ArrayList<>();
		for (Object obj : waypoints) {
			if (obj instanceof TESAbstractWaypoint) {
				TESAbstractWaypoint wp = (TESAbstractWaypoint) obj;
				points.add(new BezierPoint(wp.getCoordX(), wp.getCoordZ()));
			} else if (obj instanceof double[]) {
				double[] coords = (double[]) obj;
				if (coords.length == 2) {
					points.add(new BezierPoint(TESWaypoint.mapToWorldX(coords[0]), TESWaypoint.mapToWorldZ(coords[1])));
					continue;
				}
				throw new IllegalArgumentException("Coords length must be 2!");
			}
		}
		BezierPoint[] array = points.toArray(new BezierPoint[0]);
		TESBeziers[] beziers = BezierCurves.getSplines(array, type);
		if (type != Type.LINKER) {
			CONTENT.addAll(Arrays.asList(beziers));
		}
	}

	private static void registerLinker(TESAbstractWaypoint to) {
		registerBezier(Type.LINKER, to.getInstance(), to);
	}

	private static void registerLinker(TESAbstractWaypoint to, boolean xAxis) {
		TESWaypoint from = to.getInstance();
		double fromX = from.getImgX();
		double fromY = from.getImgY();
		double shiftX = to.getShiftX();
		double shiftY = to.getShiftY();
		double toX = to.getImgX();
		double toY = to.getImgY();
		if (xAxis) {
			double halfway = Math.min(Math.abs(shiftX / 2.0), 0.1) * (fromX < toX ? -1 : 1);
			registerBezier(Type.LINKER, from, from.info(shiftX + halfway, shiftY));
			registerBezier(Type.LINKER, from.info(shiftX + halfway, shiftY), to);
		} else {
			double halfway = Math.min(Math.abs(shiftY / 2.0), 0.1) * (fromY < toY ? -1 : 1);
			registerBezier(Type.LINKER, from, from.info(shiftX, shiftY + halfway));
			registerBezier(Type.LINKER, from.info(shiftX, shiftY + halfway), to);
		}
	}

	public static void setLinkerPointDatabase(BezierPointDatabase linkerPointDatabase) {
		TESBeziers.linkerPointDatabase = linkerPointDatabase;
	}

	public static void setRoadPointDatabase(BezierPointDatabase roadPointDatabase) {
		TESBeziers.roadPointDatabase = roadPointDatabase;
	}

	public static void setWallPointDatabase(BezierPointDatabase wallPointDatabase) {
		TESBeziers.wallPointDatabase = wallPointDatabase;
	}

	public BezierPoint[] getBezierPoints() {
		return bezierPoints;
	}

	public Collection<BezierPoint> getEndpoints() {
		return endpoints;
	}

	public enum Type {
		ROAD, WALL, LINKER
	}

	private static class BezierCurves {

		private static BezierPoint bezier(BezierPoint a, BezierPoint b, BezierPoint c, BezierPoint d, double t) {
			BezierPoint ab = lerp(a, b, t);
			BezierPoint bc = lerp(b, c, t);
			BezierPoint cd = lerp(c, d, t);
			BezierPoint abbc = lerp(ab, bc, t);
			BezierPoint bccd = lerp(bc, cd, t);
			return lerp(abbc, bccd, t);
		}

		private static double[][] getControlPoints(double[] src) {
			int i;
			int length = src.length - 1;
			double[] p1 = new double[length];
			double[] p2 = new double[length];
			double[] a = new double[length];
			double[] b = new double[length];
			double[] c = new double[length];
			double[] r = new double[length];
			a[0] = 0.0;
			b[0] = 2.0;
			c[0] = 1.0;
			r[0] = src[0] + 2.0 * src[1];
			for (i = 1; i < length - 1; ++i) {
				a[i] = 1.0;
				b[i] = 4.0;
				c[i] = 1.0;
				r[i] = 4.0 * src[i] + 2.0 * src[i + 1];
			}
			a[length - 1] = 2.0;
			b[length - 1] = 7.0;
			c[length - 1] = 0.0;
			r[length - 1] = 8.0 * src[length - 1] + src[length];
			for (i = 1; i < length; ++i) {
				double m = a[i] / b[i - 1];
				b[i] = b[i] - m * c[i - 1];
				r[i] = r[i] - m * r[i - 1];
			}
			p1[length - 1] = r[length - 1] / b[length - 1];
			for (i = length - 2; i >= 0; --i) {
				p1[i] = (r[i] - c[i] * p1[i + 1]) / b[i];
			}
			for (i = 0; i < length - 1; ++i) {
				p2[i] = 2.0 * src[i + 1] - p1[i + 1];
			}
			p2[length - 1] = 0.5 * (src[length] + p1[length - 1]);
			return new double[][]{p1, p2};
		}

		private static TESBeziers[] getSplines(BezierPoint[] waypoints, Type type) {
			int bezierLengthFactor = 1;
			if (waypoints.length == 2) {
				BezierPoint p1 = waypoints[0];
				BezierPoint p2 = waypoints[1];
				TESBeziers bezier = new TESBeziers(p1, p2);
				double dx = p2.getX() - p1.getX();
				double dz = p2.getZ() - p1.getZ();
				int bezierLength = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
				int points = bezierLength * bezierLengthFactor;
				bezier.bezierPoints = new BezierPoint[points];
				for (int l = 0; l < points; ++l) {
					BezierPoint point;
					double t = (double) l / points;
					bezier.getBezierPoints()[l] = point = new BezierPoint(p1.getX() + dx * t, p1.getZ() + dz * t);
					switch (type) {
						case ROAD:
							roadPointDatabase.add(point);
							break;
						case WALL:
							wallPointDatabase.add(point);
							break;
						case LINKER:
							linkerPointDatabase.add(point);
							break;
					}
				}
				return new TESBeziers[]{bezier};
			}
			int length = waypoints.length;
			double[] x = new double[length];
			double[] z = new double[length];
			for (int i = 0; i < length; ++i) {
				x[i] = waypoints[i].getX();
				z[i] = waypoints[i].getZ();
			}
			double[][] controlX = getControlPoints(x);
			double[][] controlZ = getControlPoints(z);
			int controlPoints = controlX[0].length;
			BezierPoint[] controlPoints1 = new BezierPoint[controlPoints];
			BezierPoint[] controlPoints2 = new BezierPoint[controlPoints];
			for (int i = 0; i < controlPoints; ++i) {
				BezierPoint p1 = new BezierPoint(controlX[0][i], controlZ[0][i]);
				BezierPoint p2 = new BezierPoint(controlX[1][i], controlZ[1][i]);
				controlPoints1[i] = p1;
				controlPoints2[i] = p2;
			}
			TESBeziers[] beziers = new TESBeziers[length - 1];
			for (int i = 0; i < beziers.length; ++i) {
				TESBeziers bezier;
				BezierPoint p1 = waypoints[i];
				BezierPoint p2 = waypoints[i + 1];
				BezierPoint cp1 = controlPoints1[i];
				BezierPoint cp2 = controlPoints2[i];
				beziers[i] = bezier = new TESBeziers(p1, p2);
				double dx = p2.getX() - p1.getX();
				double dz = p2.getZ() - p1.getZ();
				int bezierLength = (int) Math.round(Math.sqrt(dx * dx + dz * dz));
				int points = bezierLength * bezierLengthFactor;
				bezier.bezierPoints = new BezierPoint[points];
				for (int l = 0; l < points; ++l) {
					BezierPoint point;
					double t = (double) l / points;
					bezier.getBezierPoints()[l] = point = bezier(p1, cp1, cp2, p2, t);
					switch (type) {
						case ROAD:
							roadPointDatabase.add(point);
							break;
						case WALL:
							wallPointDatabase.add(point);
							break;
						case LINKER:
							linkerPointDatabase.add(point);
							break;
					}
				}
			}
			return beziers;
		}

		private static BezierPoint lerp(BezierPoint a, BezierPoint b, double t) {
			double x = a.getX() + (b.getX() - a.getX()) * t;
			double z = a.getZ() + (b.getZ() - a.getZ()) * t;
			return new BezierPoint(x, z);
		}
	}

	public static class BezierPoint {
		private final double x;
		private final double z;

		private BezierPoint(double i, double j) {
			x = i;
			z = j;
		}

		public double getX() {
			return x;
		}

		public double getZ() {
			return z;
		}
	}

	public static class BezierPointDatabase {
		private final Map<Pair<Integer, Integer>, List<BezierPoint>> pointMap = new HashMap<>();

		private void add(BezierPoint point) {
			int x = (int) Math.round(point.getX() / 1000.0);
			int z = (int) Math.round(point.getZ() / 1000.0);
			int overlap = 1;
			for (int i = -overlap; i <= overlap; ++i) {
				for (int k = -overlap; k <= overlap; ++k) {
					int xKey = x + i;
					int zKey = z + k;
					getBezierList(xKey, zKey, true).add(point);
				}
			}
		}

		private List<BezierPoint> getBezierList(int xKey, int zKey, boolean addToMap) {
			Pair<Integer, Integer> key = Pair.of(xKey, zKey);
			List<BezierPoint> list = pointMap.get(key);
			if (list == null) {
				list = new ArrayList<>();
				if (addToMap) {
					pointMap.put(key, list);
				}
			}
			return list;
		}

		private List<BezierPoint> getPointsForCoords(int x, int z) {
			int x1 = x / 1000;
			int z1 = z / 1000;
			return getBezierList(x1, z1, false);
		}
	}
}