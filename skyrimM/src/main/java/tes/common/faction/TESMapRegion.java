package tes.common.faction;

public class TESMapRegion {
	private final int mapX;
	private final int mapY;
	private final int radius;

	public TESMapRegion(int x, int y, int r) {
		mapX = x;
		mapY = y;
		radius = r;
	}

	public int getMapX() {
		return mapX;
	}

	public int getMapY() {
		return mapY;
	}

	public int getRadius() {
		return radius;
	}
}