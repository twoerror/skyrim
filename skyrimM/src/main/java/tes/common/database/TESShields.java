package tes.common.database;

import tes.TES;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public enum TESShields {
	GOLDENCOMPANY, ALCOHOLIC, ACHIEVEMENT_BRONZE, ACHIEVEMENT_SILVER, ACHIEVEMENT_GOLD, ACHIEVEMENT_VALYRIAN, TARGARYEN(false, TES.DEVS);

	private final int shieldID;
	private final ShieldType shieldType;
	private final ResourceLocation shieldTexture;
	private final UUID[] exclusiveUUIDs;

	private TESFaction alignmentFaction;
	private boolean isHidden;

	TESShields() {
		this(ShieldType.ACHIEVABLE, false, new ArrayList<>());
	}

	TESShields(boolean hidden, List<String> players) {
		this(ShieldType.EXCLUSIVE, hidden, players);
	}

	//TESShields(TESFaction faction) {
	//	this(ShieldType.ALIGNMENT, false, new ArrayList<>());
	//	alignmentFaction = faction;
	//}

	TESShields(ShieldType type, boolean hidden, List<String> players) {
		shieldType = type;
		shieldID = shieldType.getShields().size();
		shieldType.getShields().add(this);
		shieldTexture = new ResourceLocation("tes:textures/shield/" + name().toLowerCase(Locale.ROOT) + ".png");
		exclusiveUUIDs = new UUID[players.size()];
		for (int i = 0; i < players.size(); ++i) {
			String s = players.get(i);
			exclusiveUUIDs[i] = UUID.fromString(s);
		}
		isHidden = hidden;
	}

	@SuppressWarnings("EmptyMethod")
	public static void preInit() {
	}

	public static TESShields shieldForName(String shieldName) {
		for (TESShields shield : values()) {
			if (!shield.name().equals(shieldName)) {
				continue;
			}
			return shield;
		}
		return null;
	}

	public boolean canDisplay(EntityPlayer entityplayer) {
		return !isHidden || canPlayerWear(entityplayer);
	}

	public boolean canPlayerWear(EntityPlayer entityplayer) {
		if (this == ACHIEVEMENT_BRONZE) {
			return TESLevelData.getData(entityplayer).getEarnedAchievements(TESDimension.GAME_OF_THRONES).size() >= 75;
		}
		if (this == ACHIEVEMENT_SILVER) {
			return TESLevelData.getData(entityplayer).getEarnedAchievements(TESDimension.GAME_OF_THRONES).size() >= 150;
		}
		if (this == ACHIEVEMENT_GOLD) {
			return TESLevelData.getData(entityplayer).getEarnedAchievements(TESDimension.GAME_OF_THRONES).size() >= 225;
		}
		if (this == ACHIEVEMENT_VALYRIAN) {
			return TESLevelData.getData(entityplayer).getEarnedAchievements(TESDimension.GAME_OF_THRONES).size() >= 300;
		}
		if (this == ALCOHOLIC) {
			return TESLevelData.getData(entityplayer).hasAchievement(TESAchievement.gainHighAlcoholTolerance);
		}
		if (this == GOLDENCOMPANY) {
			return TESLevelData.getData(entityplayer).hasAchievement(TESAchievement.hireGoldenCompany);
		}
		if (shieldType == ShieldType.EXCLUSIVE) {
			for (UUID uuid : exclusiveUUIDs) {
				if (!uuid.equals(entityplayer.getUniqueID())) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	public String getShieldDesc() {
		return StatCollector.translateToLocal("tes.shields." + name() + ".desc");
	}

	public String getShieldName() {
		return StatCollector.translateToLocal("tes.shields." + name() + ".name");
	}

	public void setHidden(boolean hidden) {
		isHidden = hidden;
	}

	public TESFaction getAlignmentFaction() {
		return alignmentFaction;
	}

	public ShieldType getShieldType() {
		return shieldType;
	}

	public ResourceLocation getShieldTexture() {
		return shieldTexture;
	}

	public int getShieldID() {
		return shieldID;
	}

	public enum ShieldType {
		ACHIEVABLE, EXCLUSIVE;

		private final List<TESShields> shields = new ArrayList<>();

		public String getDisplayName() {
			return StatCollector.translateToLocal("tes.shields.category." + name());
		}

		public List<TESShields> getShields() {
			return shields;
		}
	}
}