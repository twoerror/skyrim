package tes.common.faction;

import tes.common.TESAchievementRank;
import tes.common.TESPlayerData;
import tes.common.database.TESTitle;
import net.minecraft.util.StatCollector;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class TESFactionRank implements Comparable<TESFactionRank> {
	public static final TESFactionRank RANK_NEUTRAL = new Dummy("tes.rank.neutral");
	public static final TESFactionRank RANK_ENEMY = new Dummy("tes.rank.enemy");

	protected final String name;

	private final TESFaction faction;
	private final float alignment;

	private TESAchievementRank rankAchievement;
	private boolean addFacName = true;

	public TESFactionRank(TESFaction f, float al, String s) {
		faction = f;
		alignment = al;
		name = s;
	}

	public TESFactionRank(TESFaction f, float al, String s, Boolean add) {
		faction = f;
		alignment = al;
		name = s;
		addFacName = add;
	}

	@Override
	public int compareTo(TESFactionRank other) {
		if (faction != other.faction) {
			throw new IllegalArgumentException("Cannot compare two ranks from different factions!");
		}
		float al1 = alignment;
		float al2 = other.alignment;
		if (al1 == al2) {
			throw new IllegalArgumentException("Two ranks cannot have the same alignment value!");
		}
		return -Float.compare(al1, al2);
	}

	public String getAffiliationCodeName() {
		if (faction != null) {
			return "tes.rank." + faction.codeName();
		}
		return "";
	}

	public String getCodeFullNameWithGender(TESPlayerData pd) {
		if (pd.getFeminineRanks()) {
			return getCodeNameFem();
		}
		return getCodeName();
	}

	public String getCodeName() {
		return "tes.rank." + name;
	}

	public String getCodeNameFem() {
		return getCodeName() + "_fm";
	}

	public String getDisplayFullName() {
		if (addFacName) {
			return StatCollector.translateToLocal(getCodeName()) + ' ' + StatCollector.translateToLocal(getAffiliationCodeName());
		}
		return StatCollector.translateToLocal(getCodeName());
	}

	public String getDisplayFullNameFem() {
		if (addFacName) {
			return StatCollector.translateToLocal(getCodeNameFem()) + ' ' + StatCollector.translateToLocal(getAffiliationCodeName());
		}
		return StatCollector.translateToLocal(getCodeNameFem());
	}

	public String getDisplayName() {
		return StatCollector.translateToLocal(getCodeName());
	}

	@SuppressWarnings("WeakerAccess")
	public String getDisplayNameFem() {
		return StatCollector.translateToLocal(getCodeNameFem());
	}

	public String getFullNameWithGender(TESPlayerData pd) {
		if (pd.getFeminineRanks()) {
			return getDisplayFullNameFem();
		}
		return getDisplayFullName();
	}

	public TESAchievementRank getRankAchievement() {
		return rankAchievement;
	}

	public String getShortNameWithGender(TESPlayerData pd) {
		if (pd.getFeminineRanks()) {
			return getDisplayNameFem();
		}
		return getDisplayName();
	}

	public boolean isAbovePledgeRank() {
		return alignment > faction.getPledgeAlignment();
	}

	public boolean isDummyRank() {
		return false;
	}

	public boolean isPledgeRank() {
		return this == faction.getPledgeRank();
	}

	public void makeAchievement() {
		rankAchievement = new TESAchievementRank(this);
	}

	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public TESFactionRank makeTitle() {
		new TESTitle(this, false);
		new TESTitle(this, true);
		return this;
	}

	public TESFactionRank setPledgeRank() {
		faction.setPledgeRank(this);
		return this;
	}

	public boolean isAddFacName() {
		return addFacName;
	}

	public float getAlignment() {
		return alignment;
	}

	public TESFaction getFaction() {
		return faction;
	}

	public static class Dummy extends TESFactionRank {
		private Dummy(String s) {
			super(null, 0.0f, s);
		}

		@Override
		public String getCodeName() {
			return name;
		}

		@Override
		public String getDisplayFullName() {
			return getDisplayName();
		}

		@Override
		public String getDisplayName() {
			return StatCollector.translateToLocal(name);
		}

		@Override
		public boolean isDummyRank() {
			return true;
		}
	}
}