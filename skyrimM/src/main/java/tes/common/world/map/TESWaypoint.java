package tes.common.world.map;

import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import tes.common.world.genlayer.TESGenLayerWorld;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tes.common.world.genlayer.TESGenLayerWorld;

import java.util.*;

public enum TESWaypoint implements TESAbstractWaypoint {

	ERM_WHAT_THE_SIGMA(Region.SKYRIM, TESFaction.UNALIGNED, 100, 100);

	private final List<Region> regions;
	private final double imgX;
	private final double imgY;
	private final int coordX;
	private final int coordZ;

	private TESFaction faction;
	private boolean isHidden;

	TESWaypoint(List<Region> regions, TESFaction f, double x, double y) {
		this(regions, f, x, y, false);
	}

	TESWaypoint(Region r, TESFaction f, double x, double y) {
		this(Collections.singletonList(r), f, x, y, false);
	}

	TESWaypoint(Region r, TESFaction f, double x, double y, boolean hide) {
		this(Collections.singletonList(r), f, x, y, hide);
	}

	TESWaypoint(List<Region> r, TESFaction f, double x, double y, boolean hide) {
		regions = r;
		for (Region region : regions) {
			region.getWaypoints().add(this);
		}
		faction = f;
		imgX = x;
		imgY = y;
		coordX = mapToWorldX(x);
		coordZ = mapToWorldZ(y);
		isHidden = hide;
	}

	public static List<TESAbstractWaypoint> listAllWaypoints() {
		return new ArrayList<>(Arrays.asList(values()));
	}

	public static int mapToWorldR(double r) {
		return (int) Math.round(r * TESGenLayerWorld.SCALE);
	}

	public static int mapToWorldX(double x) {
		return (int) Math.round((x - TESGenLayerWorld.ORIGIN_X + 0.5) * TESGenLayerWorld.SCALE);
	}

	public static int mapToWorldZ(double z) {
		return (int) Math.round((z - TESGenLayerWorld.ORIGIN_Z + 0.5) * TESGenLayerWorld.SCALE);
	}

	public static Region regionForID(int id) {
		for (Region waypointRegion : Region.values()) {
			if (waypointRegion.ordinal() != id) {
				continue;
			}
			return waypointRegion;
		}
		return null;
	}

	public static Region regionForName(String name) {
		for (Region waypointRegion : Region.values()) {
			if (!waypointRegion.name().equals(name)) {
				continue;
			}
			return waypointRegion;
		}
		return null;
	}

	public static TESWaypoint waypointForName(String name) {
		for (TESWaypoint wp : values()) {
			if (!wp.getCodeName().equals(name)) {
				continue;
			}
			return wp;
		}
		return null;
	}

	public static int worldToMapR(double r) {
		return (int) Math.round(r / TESGenLayerWorld.SCALE);
	}

	public static int worldToMapX(double x) {
		return (int) Math.round(x / TESGenLayerWorld.SCALE - 0.5 + TESGenLayerWorld.ORIGIN_X);
	}

	public static int worldToMapZ(double z) {
		return (int) Math.round(z / TESGenLayerWorld.SCALE - 0.5 + TESGenLayerWorld.ORIGIN_Z);
	}

	@Override
	public String getCodeName() {
		return name();
	}

	@Override
	public String getDisplayName() {
		return StatCollector.translateToLocal("TES.wp." + getCodeName());
	}

	public TESFaction getFaction() {
		return faction;
	}

	public void setFaction(TESFaction faction) {
		this.faction = faction;
	}

	@Override
	public int getID() {
		return ordinal();
	}

	@Override
	public TESWaypoint getInstance() {
		return this;
	}

	@Override
	public TESAbstractWaypoint.WaypointLockState getLockState(EntityPlayer entityplayer) {
		if (hasPlayerUnlocked(entityplayer)) {
			return isUnlockedByConquest(entityplayer) ? TESAbstractWaypoint.WaypointLockState.STANDARD_UNLOCKED_CONQUEST : TESAbstractWaypoint.WaypointLockState.STANDARD_UNLOCKED;
		}
		return TESAbstractWaypoint.WaypointLockState.STANDARD_LOCKED;
	}

	@Override
	public String getLoreText(EntityPlayer entityplayer) {
		return StatCollector.translateToLocal("TES.wp." + getCodeName() + ".info");
	}

	@Override
	public int getRotation() {
		return 0;
	}

	@Override
	public double getShiftX() {
		return 0;
	}

	@Override
	public double getShiftY() {
		return 0;
	}

	@Override
	public double getImgX() {
		return imgX;
	}

	@Override
	public int getCoordX() {
		return coordX;
	}

	@Override
	public double getImgY() {
		return imgY;
	}

	@Override
	public int getCoordY(World world, int i, int k) {
		return TES.getTrueTopBlock(world, i, k);
	}

	@Override
	public int getCoordYSaved() {
		return 64;
	}

	@Override
	public int getCoordZ() {
		return coordZ;
	}

	@Override
	public boolean hasPlayerUnlocked(EntityPlayer entityplayer) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		if (pd.isFTRegionUnlocked(regions)) {
			if (isCompatibleAlignment(entityplayer)) {
				return true;
			}
			if (isConquestUnlockable(entityplayer)) {
				return isConquered(entityplayer);
			}
		}
		return false;
	}

	public TESAbstractWaypoint info(double shiftX, double shiftY) {
		return info(shiftX, shiftY, TESFixer.Dir.NORTH);
	}

	public TESAbstractWaypoint info(double shiftX, double shiftY, TESFixer.Dir direction) {
		return new TESWaypointInfo(this, shiftX, shiftY, direction.ordinal());
	}

	public boolean isCompatibleAlignment(EntityPlayer entityplayer) {
		if (faction == TESFaction.UNALIGNED) {
			return true;
		}
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		return pd.getAlignment(faction) >= 0.0f;
	}

	private boolean isConquered(EntityPlayer entityplayer) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		World world = entityplayer.worldObj;
		TESConquestZone zone = TESConquestGrid.getZoneByWorldCoords(coordX, coordZ);
		TESFaction pledgeFac = pd.getPledgeFaction();
		return pledgeFac != null && zone.getConquestStrength(pledgeFac, world) >= 500.0f;
	}

	public boolean isConquestUnlockable(EntityPlayer entityplayer) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		World world = entityplayer.worldObj;
		TESConquestZone zone = TESConquestGrid.getZoneByWorldCoords(coordX, coordZ);
		TESFaction pledgeFac = pd.getPledgeFaction();
		return pledgeFac != null && pledgeFac.isBadRelation(faction) && TESConquestGrid.getConquestEffectIn(world, zone, pledgeFac) == TESConquestGrid.ConquestEffective.EFFECTIVE;
	}

	@Override
	public boolean isHidden() {
		return isHidden;
	}

	public void setHidden(boolean hidden) {
		isHidden = hidden;
	}

	private boolean isUnlockedByConquest(EntityPlayer entityplayer) {
		return !isCompatibleAlignment(entityplayer) && isConquestUnlockable(entityplayer) && isConquered(entityplayer);
	}

	@SuppressWarnings("InnerClassFieldHidesOuterClassField")
	public enum Region {
		SKYRIM,
		OCEAN;

		private final Collection<TESWaypoint> waypoints = new ArrayList<>();

		public Collection<TESWaypoint> getWaypoints() {
			return waypoints;
		}
	}
}