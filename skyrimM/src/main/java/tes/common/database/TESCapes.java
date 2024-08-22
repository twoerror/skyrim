package tes.common.database;

import tes.TES;
import tes.common.TESLevelData;
import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public enum TESCapes {
	
	TARGARYEN(false, TES.DEVS);

	private final int capeID;
	private final CapeType capeType;
	private final ResourceLocation capeTexture;
	private final UUID[] exclusiveUUIDs;

	private TESFaction alignmentFaction;
	private boolean isHidden;

	TESCapes(boolean hidden, List<String> players) {
		this(CapeType.EXCLUSIVE, hidden, players);
	}

	//TESCapes(TESFaction faction) {
	//	this(CapeType.ALIGNMENT, false, new ArrayList<>());
	//	alignmentFaction = faction;
	//}

	TESCapes(CapeType type, boolean hidden, List<String> players) {
		capeType = type;
		capeID = capeType.getCapes().size();
		capeType.getCapes().add(this);
		capeTexture = new ResourceLocation("tes:textures/cape/" + name().toLowerCase(Locale.ROOT) + ".png");
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

	public static TESCapes capeForName(String capeName) {
		for (TESCapes cape : values()) {
			if (!cape.name().equals(capeName)) {
				continue;
			}
			return cape;
		}
		return null;
	}

	public boolean canDisplay(EntityPlayer entityplayer) {
		return !isHidden || canPlayerWear(entityplayer);
	}

	public boolean canPlayerWear(EntityPlayer entityplayer) {
	//	if (capeType == CapeType.ALIGNMENT) {
	//		return TESLevelData.getData(entityplayer).getAlignment(alignmentFaction) >= 100.0f;
		//}
		if (capeType == CapeType.EXCLUSIVE) {
			for (UUID uuid : exclusiveUUIDs) {
				if (!uuid.equals(entityplayer.getUniqueID())) {
					continue;
				}
				return true;
			}
		}
		return false;
	}

	public String getCapeDesc() {
		//if (capeType == CapeType.ALIGNMENT) {
		//	return StatCollector.translateToLocal("tes.attribute.desc");
	//	}
		return StatCollector.translateToLocal("tes.capes." + name() + ".desc");
	}

	public String getCapeName() {
		return StatCollector.translateToLocal("tes.capes." + name() + ".name");
	}

	public void setHidden(boolean hidden) {
		isHidden = hidden;
	}

	public TESFaction getAlignmentFaction() {
		return alignmentFaction;
	}

	public CapeType getCapeType() {
		return capeType;
	}

	public ResourceLocation getCapeTexture() {
		return capeTexture;
	}

	public int getCapeID() {
		return capeID;
	}

	public enum CapeType {
		EXCLUSIVE;

		private final List<TESCapes> capes = new ArrayList<>();

		public String getDisplayName() {
			return StatCollector.translateToLocal("tes.capes.category." + name());
		}

		public List<TESCapes> getCapes() {
			return capes;
		}
	}
}