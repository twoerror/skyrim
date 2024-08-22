package tes.common.faction;

import tes.TES;
import tes.common.TESAchievementRank;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.entity.other.utils.TESNPCSelectByFaction;
import tes.common.item.other.TESItemBanner;
import tes.common.world.TESWorldProvider;
import tes.common.world.map.TESWaypoint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

public enum TESFaction {
	EMPIRE(0x343A2C, TESDimension.DimensionRegion.TAMRIEL, new TESMapRegion(495, 271, 100)), HOSTILE(true, -1), UNALIGNED(false, 0);
	
	private final Collection<TESItemBanner.BannerType> factionBanners = new ArrayList<>();
	private final List<TESFactionRank> ranksSortedDescending = new ArrayList<>();
	private final List<TESControlZone> controlZones = new ArrayList<>();
	private final Map<Float, float[]> facRGBCache = new HashMap<>();
	private final Color factionColor;
	private final boolean allowEntityRegistry;
	private final int eggColor;

	private TESDimension.DimensionRegion factionRegion;
	private TESDimension factionDimension;
	private TESMapRegion factionMapInfo;
	private TESFactionRank pledgeRank;
	private boolean allowPlayer;
	private boolean hasFixedAlignment;
	private boolean approvesWarCrimes;
	private int fixedAlignment;

	TESFaction(boolean registry, int alignment) {
		this(0, null, null, false, registry, alignment, null);
	}

	TESFaction(int color, TESDimension.DimensionRegion region, TESMapRegion mapInfo) {
		this(color, TESDimension.GAME_OF_THRONES, region, mapInfo);
	}

	TESFaction(int color, TESDimension dim, TESDimension.DimensionRegion region, TESMapRegion mapInfo) {
		this(color, dim, region, true, true, Integer.MIN_VALUE, mapInfo);
	}

	TESFaction(int color, TESDimension dim, TESDimension.DimensionRegion region, boolean player, boolean registry, int alignment, TESMapRegion mapInfo) {
		allowPlayer = player;
		eggColor = color;
		allowEntityRegistry = registry;
		factionColor = new Color(color);
		factionDimension = dim;
		if (factionDimension != null) {
			factionDimension.getFactionList().add(this);
		}
		factionRegion = region;
		if (factionRegion != null) {
			factionRegion.getFactionList().add(this);
			if (factionRegion.getDimension() != factionDimension) {
				throw new IllegalArgumentException("Faction dimension region must agree with faction dimension!");
			}
		}
		if (alignment != Integer.MIN_VALUE) {
			setFixedAlignment(alignment);
		}
		if (mapInfo != null) {
			factionMapInfo = mapInfo;
		}
	}

	public static boolean controlZonesEnabled(World world) {
		return TESLevelData.isEnableAlignmentZones() && world.getWorldInfo().getTerrainType() != TES.worldTypeTESClassic;
	}

	public static TESFaction forID(int ID) {
		for (TESFaction f : values()) {
			if (f.ordinal() == ID) {
				return f;
			}
		}
		return null;
	}

	public static TESFaction forName(String name) {
		for (TESFaction f : values()) {
			if (f.matchesName(name)) {
				return f;
			}
		}
		return null;
	}

	public static List<String> getPlayableAlignmentFactionNames() {
		List<TESFaction> factions = getPlayableAlignmentFactions();
		List<String> names = new ArrayList<>();
		for (TESFaction f : factions) {
			names.add(f.codeName());
		}
		return names;
	}

	public static List<TESFaction> getPlayableAlignmentFactions() {
		List<TESFaction> factions = new ArrayList<>();
		for (TESFaction f : values()) {
			if (f.isPlayableAlignmentFaction()) {
				factions.add(f);
			}
		}
		return factions;
	}

	public static void onInit() {
	for (TESFaction fac : values()) {
			if (fac != UNALIGNED && fac != HOSTILE) {
				fac.addRank(10.0f, "guest").makeTitle().makeAchievement();
				fac.addRank(50.0f, "friend").makeTitle().makeAchievement();
				fac.addRank(100.0f, "defender").setPledgeRank().makeTitle().makeAchievement();
				fac.addRank(500.0f, "hero").makeTitle().makeAchievement();
				fac.addRank(1000.0f, "leader").makeTitle().makeAchievement();
			}
		}
	}

	private TESFactionRank addRank(float alignment, String name) {
		TESFactionRank rank = new TESFactionRank(this, alignment, name);
		ranksSortedDescending.add(rank);
		Collections.sort(ranksSortedDescending);
		return rank;
	}

	private TESFactionRank addSpecialRank(float alignment, String name) {
		TESFactionRank rank = new TESFactionRank(this, alignment, name, false);
		ranksSortedDescending.add(rank);
		Collections.sort(ranksSortedDescending);
		return rank;
	}

	public int[] calculateFullControlZoneWorldBorders() {
		int xMin = 0;
		int xMax = 0;
		int zMin = 0;
		int zMax = 0;
		boolean first = true;
		for (TESControlZone zone : controlZones) {
			int cxMin = zone.getCoordX() - zone.getRadiusCoord();
			int cxMax = zone.getCoordX() + zone.getRadiusCoord();
			int czMin = zone.getCoordZ() - zone.getRadiusCoord();
			int czMax = zone.getCoordZ() + zone.getRadiusCoord();
			if (first) {
				xMin = cxMin;
				xMax = cxMax;
				zMin = czMin;
				zMax = czMax;
				first = false;
				continue;
			}
			xMin = Math.min(xMin, cxMin);
			xMax = Math.max(xMax, cxMax);
			zMin = Math.min(zMin, czMin);
			zMax = Math.max(zMax, czMax);
		}
		return new int[]{xMin, xMax, zMin, zMax};
	}

	public void checkAlignmentAchievements(EntityPlayer entityplayer) {
		TESPlayerData playerData = TESLevelData.getData(entityplayer);
		for (TESFactionRank rank : ranksSortedDescending) {
			TESAchievementRank rankAch = rank.getRankAchievement();
			if (rankAch == null || !rankAch.isPlayerRequiredRank(entityplayer)) {
				continue;
			}
			playerData.addAchievement(rankAch);
		}
	}


	public String codeName() {
		return name();
	}

	public double distanceToNearestControlZoneInRange(World world, double x, double z, int mapRange) {
		double closestDist = -1.0;
		if (isFactionDimension(world)) {
			int coordRange = TESWaypoint.mapToWorldR(mapRange);
			for (TESControlZone zone : controlZones) {
				double dx = x - zone.getCoordX();
				double dz = z - zone.getCoordZ();
				double dSq = dx * dx + dz * dz;
				double dToEdge = Math.sqrt(dSq) - zone.getRadiusCoord();
				if (dToEdge > coordRange || closestDist >= 0.0 && dToEdge >= closestDist) {
					continue;
				}
				closestDist = dToEdge;
			}
		}
		return closestDist;
	}

	public String factionEntityName() {
		return StatCollector.translateToLocal("tes.faction." + codeName() + ".entity");
	}

	public String factionName() {
		return StatCollector.translateToLocal(untranslatedFactionName());
	}

	public String factionSubtitle() {
		return StatCollector.translateToLocal("tes.faction." + codeName() + ".subtitle");
	}

	public List<TESFaction> getBonusesForKilling() {
		List<TESFaction> list = new ArrayList<>();
		for (TESFaction f : values()) {
			if (f == this || !isBadRelation(f)) {
				continue;
			}
			list.add(f);
		}
		return list;
	}

	public List<TESFaction> getConquestBoostRelations() {
		List<TESFaction> list = new ArrayList<>();
		for (TESFaction f : values()) {
			if (f == this || !f.isPlayableAlignmentFaction() || TESFactionRelations.getRelations(this, f) != TESFactionRelations.Relation.ALLY) {
				continue;
			}
			list.add(f);
		}
		return list;
	}

	public float getControlZoneAlignmentMultiplier(EntityPlayer entityplayer) {
		int reducedRange;
		double dist;
		if (inControlZone(entityplayer)) {
			return 1.0f;
		}
		if (isFactionDimension(entityplayer.worldObj) && (dist = distanceToNearestControlZoneInRange(entityplayer.worldObj, entityplayer.posX, entityplayer.posZ, reducedRange = 50)) >= 0.0) {
			double mapDist = TESWaypoint.worldToMapR(dist);
			float frac = (float) mapDist / reducedRange;
			float mplier = 1.0f - frac;
			return MathHelper.clamp_float(mplier, 0.0f, 1.0f);
		}
		return 0.0f;
	}

	public List<TESControlZone> getControlZones() {
		return controlZones;
	}

	public int getFactionColor() {
		return factionColor.getRGB();
	}

	public float[] getFactionRGB() {
		return getFactionRGB_MinBrightness(0.0f);
	}

	public float[] getFactionRGB_MinBrightness(float minBrightness) {
		float[] rgb = facRGBCache.get(minBrightness);
		if (rgb == null) {
			float[] hsb = Color.RGBtoHSB(factionColor.getRed(), factionColor.getGreen(), factionColor.getBlue(), null);
			hsb[2] = Math.max(hsb[2], minBrightness);
			int alteredColor = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
			rgb = new Color(alteredColor).getColorComponents(null);
			facRGBCache.put(minBrightness, rgb);
		}
		return rgb;
	}

	public TESFactionRank getFirstRank() {
		if (ranksSortedDescending.isEmpty()) {
			return TESFactionRank.RANK_NEUTRAL;
		}
		return ranksSortedDescending.get(ranksSortedDescending.size() - 1);
	}

	public List<TESFaction> getOthersOfRelation(TESFactionRelations.Relation rel) {
		List<TESFaction> list = new ArrayList<>();
		for (TESFaction f : values()) {
			if (f == this || !f.isPlayableAlignmentFaction() || TESFactionRelations.getRelations(this, f) != rel) {
				continue;
			}
			list.add(f);
		}
		return list;
	}

	public List<TESFaction> getPenaltiesForKilling() {
		List<TESFaction> list = new ArrayList<>();
		list.add(this);
		for (TESFaction f : values()) {
			if (f == this || !isGoodRelation(f)) {
				continue;
			}
			list.add(f);
		}
		return list;
	}

	public float getPledgeAlignment() {
		if (pledgeRank != null) {
			return pledgeRank.getAlignment();
		}
		return 0.0f;
	}

	public TESFactionRank getPledgeRank() {
		return pledgeRank;
	}

	public void setPledgeRank(TESFactionRank rank) {
		if (rank.getFaction() != this) {
			throw new IllegalArgumentException("Incompatible faction!");
		}
		if (pledgeRank != null) {
			throw new IllegalArgumentException("Faction already has a pledge rank!");
		}
		pledgeRank = rank;
	}

	public TESFactionRank getRank(float alignment) {
		for (TESFactionRank rank : ranksSortedDescending) {
			if (rank.isDummyRank() || alignment < rank.getAlignment()) {
				continue;
			}
			return rank;
		}
		if (alignment >= 0.0f) {
			return TESFactionRank.RANK_NEUTRAL;
		}
		return TESFactionRank.RANK_ENEMY;
	}

	public TESFactionRank getRank(TESPlayerData pd) {
		float alignment = pd.getAlignment(this);
		return getRank(alignment);
	}

	public TESFactionRank getRankAbove(TESFactionRank curRank) {
		return getRankNAbove(curRank, 1);
	}

	public TESFactionRank getRankBelow(TESFactionRank curRank) {
		return getRankNAbove(curRank, -1);
	}

	public TESFactionRank getRankNAbove(TESFactionRank curRank, int n) {
		if (ranksSortedDescending.isEmpty() || curRank == null) {
			return TESFactionRank.RANK_NEUTRAL;
		}
		int index = -1;
		if (curRank.isDummyRank()) {
			index = ranksSortedDescending.size();
		} else if (ranksSortedDescending.contains(curRank)) {
			index = ranksSortedDescending.indexOf(curRank);
		}
		if (index >= 0) {
			index -= n;
			if (index < 0) {
				return ranksSortedDescending.get(0);
			}
			if (index > ranksSortedDescending.size() - 1) {
				return TESFactionRank.RANK_NEUTRAL;
			}
			return ranksSortedDescending.get(index);
		}
		return TESFactionRank.RANK_NEUTRAL;
	}

	public boolean inControlZone(EntityPlayer entityplayer) {
		return inControlZone(entityplayer.worldObj, entityplayer.posX, entityplayer.boundingBox.minY, entityplayer.posZ);
	}

	private boolean inControlZone(World world, double x, double y, double z) {
		if (inDefinedControlZone(world, x, z)) {
			return true;
		}
		double nearbyRange = 24.0;
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(nearbyRange, nearbyRange, nearbyRange);
		List<EntityLivingBase> nearbyNPCs = world.selectEntitiesWithinAABB(EntityLivingBase.class, aabb, new TESNPCSelectByFaction(this));
		return !nearbyNPCs.isEmpty();
	}

	public boolean inDefinedControlZone(EntityPlayer entityplayer) {
		return inDefinedControlZone(entityplayer, 0);
	}

	public boolean inDefinedControlZone(EntityPlayer entityplayer, int extraMapRange) {
		return inDefinedControlZone(entityplayer.worldObj, entityplayer.posX, entityplayer.posZ, extraMapRange);
	}

	private boolean inDefinedControlZone(World world, double x, double z) {
		return inDefinedControlZone(world, x, z, 0);
	}

	public boolean inDefinedControlZone(World world, double x, double z, int extraMapRange) {
		if (isFactionDimension(world)) {
			if (!controlZonesEnabled(world)) {
				return true;
			}
			for (TESControlZone zone : controlZones) {
				if (!zone.inZone(x, z, extraMapRange)) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	public boolean isAlly(TESFaction other) {
		TESFactionRelations.Relation rel = TESFactionRelations.getRelations(this, other);
		return rel == TESFactionRelations.Relation.ALLY;
	}

	public boolean isBadRelation(TESFaction other) {
		TESFactionRelations.Relation rel = TESFactionRelations.getRelations(this, other);
		return rel == TESFactionRelations.Relation.ENEMY || rel == TESFactionRelations.Relation.MORTAL_ENEMY;
	}

	private boolean isFactionDimension(World world) {
		return world.provider instanceof TESWorldProvider && factionDimension == TESDimension.GAME_OF_THRONES;
	}

	public boolean isGoodRelation(TESFaction other) {
		TESFactionRelations.Relation rel = TESFactionRelations.getRelations(this, other);
		return rel == TESFactionRelations.Relation.ALLY || rel == TESFactionRelations.Relation.FRIEND;
	}

	public boolean isMortalEnemy(TESFaction other) {
		TESFactionRelations.Relation rel = TESFactionRelations.getRelations(this, other);
		return rel == TESFactionRelations.Relation.MORTAL_ENEMY;
	}

	public boolean isNeutral(TESFaction other) {
		return TESFactionRelations.getRelations(this, other) == TESFactionRelations.Relation.NEUTRAL;
	}

	public boolean isPlayableAlignmentFaction() {
		return allowPlayer && !hasFixedAlignment;
	}

	private boolean matchesName(String name) {
		return codeName().equals(name);
	}

	private String untranslatedFactionName() {
		return "tes.faction." + codeName() + ".name";
	}

	public int getFixedAlignment() {
		return fixedAlignment;
	}

	public void setFixedAlignment(int alignment) {
		hasFixedAlignment = true;
		fixedAlignment = alignment;
	}

	public TESDimension getFactionDimension() {
		return factionDimension;
	}

	public void setFactionDimension(TESDimension factionDimension) {
		this.factionDimension = factionDimension;
	}

	public TESDimension.DimensionRegion getFactionRegion() {
		return factionRegion;
	}

	public void setFactionRegion(TESDimension.DimensionRegion factionRegion) {
		this.factionRegion = factionRegion;
	}

	public boolean isAllowPlayer() {
		return allowPlayer;
	}

	public void setAllowPlayer(boolean allowPlayer) {
		this.allowPlayer = allowPlayer;
	}

	public boolean isApprovesWarCrimes() {
		return approvesWarCrimes;
	}

	public TESMapRegion getFactionMapInfo() {
		return factionMapInfo;
	}

	public boolean isHasFixedAlignment() {
		return hasFixedAlignment;
	}

	public int getEggColor() {
		return eggColor;
	}

	public boolean isAllowEntityRegistry() {
		return allowEntityRegistry;
	}

	public List<TESFactionRank> getRanksSortedDescending() {
		return ranksSortedDescending;
	}

	public Collection<TESItemBanner.BannerType> getFactionBanners() {
		return factionBanners;
	}
}