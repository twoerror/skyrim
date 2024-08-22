package tes.common.database;

import tes.TES;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionRank;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

import java.util.*;

public class TESTitle {
	public static final Collection<TESTitle> CONTENT = new ArrayList<>();

	private static int nextTitleID;

	private final String name;
	private final int titleID;

	private TitleType titleType = TitleType.STARTER;
	private TESAchievement titleAchievement;
	private TESFactionRank titleRank;
	private UUID[] uuids;

	private boolean isHidden;
	private boolean isFeminineRank;
	private boolean useAchievementName;

	@SuppressWarnings("WeakerAccess")
	public TESTitle(String s, TESAchievement ach) {
		this(s == null ? ach.getName() : s);
		titleType = TitleType.ACHIEVEMENT;
		titleAchievement = ach;
		if (s == null) {
			useAchievementName = true;
		}
	}

	@SuppressWarnings("WeakerAccess")
	public TESTitle(TESFactionRank rank, boolean fem) {
		this(fem ? rank.getCodeNameFem() : rank.getCodeName());
		titleType = TitleType.RANK;
		titleRank = rank;
		isFeminineRank = fem;
	}

	@SuppressWarnings("WeakerAccess")
	public TESTitle(String s) {
		titleID = nextTitleID;
		nextTitleID++;
		name = s;
		CONTENT.add(this);
	}

	public static Comparator<TESTitle> createTitleSorter(EntityPlayer entityplayer) {
		return Comparator.comparing(title -> title.getDisplayName(entityplayer));
	}

	public static TESTitle forID(int ID) {
		for (TESTitle title : CONTENT) {
			if (title.titleID == ID) {
				return title;
			}
		}
		return null;
	}

	public static void onInit() {
		new TESTitle("targaryenF").setPlayerExclusive(TES.DEVS);
		new TESTitle("targaryenM").setPlayerExclusive(TES.DEVS);
	}

	public boolean canDisplay(EntityPlayer entityplayer) {
		return !isHidden || canPlayerUse(entityplayer);
	}

	public boolean canPlayerUse(EntityPlayer entityplayer) {
		switch (titleType) {
			case ACHIEVEMENT:
				return TESLevelData.getData(entityplayer).hasAchievement(titleAchievement);
			case PLAYER_EXCLUSIVE:
				for (UUID player : uuids) {
					if (!entityplayer.getUniqueID().equals(player)) {
						continue;
					}
					return true;
				}
				return false;
			case RANK:
				TESPlayerData pd = TESLevelData.getData(entityplayer);
				TESFaction fac = titleRank.getFaction();
				float align = pd.getAlignment(fac);
				if (align >= titleRank.getAlignment()) {
					boolean requirePledge = titleRank.isAbovePledgeRank() || titleRank.isPledgeRank() && TESConfig.areStrictFactionTitleRequirementsEnabled(entityplayer.worldObj);
					return !requirePledge || pd.isPledgedTo(fac);
				}
				return false;
			default:
				return true;
		}
	}

	public String getDescription(EntityPlayer entityplayer) {
		switch (titleType) {
			case STARTER:
				return StatCollector.translateToLocal("tes.titles.unlock.starter");
			case PLAYER_EXCLUSIVE:
				return StatCollector.translateToLocal("tes.titles.unlock.exclusive");
			case ACHIEVEMENT:
				return titleAchievement.getDescription();
			case RANK:
				String alignS = TESAlignmentValues.formatAlignForDisplay(titleRank.getAlignment());
				boolean requirePledge = titleRank.isAbovePledgeRank() || titleRank.isPledgeRank() && TESConfig.areStrictFactionTitleRequirementsEnabled(entityplayer.worldObj);
				if (requirePledge) {
					return StatCollector.translateToLocalFormatted("tes.titles.unlock.alignment.pledge", titleRank.getFaction().factionName(), alignS);
				}
				return StatCollector.translateToLocalFormatted("tes.titles.unlock.alignment", titleRank.getFaction().factionName(), alignS);
			default:
				return "If you can read this, something has gone hideously wrong";
		}
	}

	public String getDisplayName(EntityPlayer entityplayer) {
		if (titleType == TitleType.RANK) {
			if (isFeminineRank) {
				return titleRank.getDisplayFullNameFem();
			}
			return titleRank.getDisplayFullName();
		}
		return StatCollector.translateToLocal(getUntranslatedName(entityplayer));
	}

	private String getUntranslatedName(EntityPlayer entityplayer) {
		if (useAchievementName && titleAchievement != null) {
			return titleAchievement.getUntranslatedTitle(entityplayer);
		}
		if (titleType == TitleType.RANK) {
			if (isFeminineRank) {
				return titleRank.getCodeNameFem();
			}
			return titleRank.getCodeName();
		}
		return "tes.title." + name;
	}

	private void setPlayerExclusive(List<String> devs) {
		UUID[] us = new UUID[devs.size()];
		for (int i = 0; i < devs.size(); ++i) {
			us[i] = UUID.fromString(devs.get(i));
		}
		titleType = TitleType.PLAYER_EXCLUSIVE;
		uuids = us;
		isHidden = true;
	}

	public int getTitleID() {
		return titleID;
	}

	public void setHidden(boolean hidden) {
		isHidden = hidden;
	}

	public TitleType getTitleType() {
		return titleType;
	}

	public TESAchievement getTitleAchievement() {
		return titleAchievement;
	}

	public void setTitleAchievement(TESAchievement titleAchievement) {
		this.titleAchievement = titleAchievement;
	}

	public enum TitleType {
		STARTER, PLAYER_EXCLUSIVE, ACHIEVEMENT, RANK

	}

	public static class PlayerTitle {
		protected final TESTitle title;
		protected final EnumChatFormatting theColor;

		public PlayerTitle(TESTitle title, EnumChatFormatting color) {
			EnumChatFormatting color1 = color;
			this.title = title;
			if (color1 == null || !color1.isColor()) {
				color1 = EnumChatFormatting.WHITE;
			}
			theColor = color1;
		}

		public static EnumChatFormatting colorForID(int ID) {
			for (EnumChatFormatting color : EnumChatFormatting.values()) {
				if (color.getFormattingCode() != ID) {
					continue;
				}
				return color;
			}
			return null;
		}

		public static PlayerTitle readNullableTitle(ByteBuf data) {
			short titleID = data.readShort();
			if (titleID >= 0) {
				byte colorID = data.readByte();
				TESTitle title = forID(titleID);
				EnumChatFormatting color = colorForID(colorID);
				if (title != null && color != null) {
					return new PlayerTitle(title, color);
				}
			}
			return null;
		}

		public static void writeNullableTitle(ByteBuf data, PlayerTitle title) {
			if (title != null) {
				data.writeShort(title.title.titleID);
				data.writeByte(title.theColor.getFormattingCode());
			} else {
				data.writeShort(-1);
			}
		}

		public EnumChatFormatting getColor() {
			return theColor;
		}

		public String getFormattedTitle(EntityPlayer entityplayer) {
			return getFullTitleComponent(entityplayer).getFormattedText();
		}

		public IChatComponent getFullTitleComponent(EntityPlayer entityplayer) {
			IChatComponent component;
			if (title.titleType != null && title.titleType == TitleType.RANK && title.titleRank.isAddFacName()) {
				component = new ChatComponentText("[").appendSibling(new ChatComponentTranslation(title.getUntranslatedName(entityplayer))).appendText(" ").appendSibling(new ChatComponentTranslation(title.titleRank.getAffiliationCodeName())).appendText("]").appendText(" ");
			} else {
				component = new ChatComponentText("[").appendSibling(new ChatComponentTranslation(title.getUntranslatedName(entityplayer))).appendText("]").appendText(" ");
			}
			component.getChatStyle().setColor(theColor);
			return component;
		}

		public TESTitle getTitle() {
			return title;
		}
	}
}