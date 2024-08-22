package tes.common;

import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionRank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;

public class TESAchievementRank extends TESAchievement {
	private final TESFactionRank theRank;
	private final TESFaction theFac;

	public TESAchievementRank(TESFactionRank rank) {
		super(TESAchievement.Category.TITLES, TESAchievement.Category.TITLES.getNextRankAchID(), TESItems.gregorCleganeSword, "alignment_" + rank.getFaction().codeName() + '_' + rank.getAlignment());
		theRank = rank;
		theFac = theRank.getFaction();
		isSpecial = true;
		setRequiresAlly(theFac);
	}

	@Override
	public boolean canPlayerEarn(EntityPlayer entityplayer) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		float align = pd.getAlignment(theFac);
		return !(align < 0.0f);
	}

	@Override
	public IChatComponent getAchievementChatComponent(EntityPlayer entityplayer) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		IChatComponent component = new ChatComponentTranslation(theRank.getCodeFullNameWithGender(pd)).appendText(" ").appendSibling(new ChatComponentTranslation(theRank.getAffiliationCodeName())).createCopy();
		component.getChatStyle().setColor(EnumChatFormatting.YELLOW);
		component.getChatStyle().setChatHoverEvent(new HoverEvent(TESChatEvents.showGotAchievement, new ChatComponentText(getCategory().name() + '$' + getId())));
		return component;
	}

	@Override
	public String getDescription() {
		return StatCollector.translateToLocalFormatted("TES.faction.achieveRank", TESAlignmentValues.formatAlignForDisplay(theRank.getAlignment()));
	}

	@Override
	public String getTitle(EntityPlayer entityplayer) {
		return theRank.getFullNameWithGender(TESLevelData.getData(entityplayer));
	}

	@Override
	public String getUntranslatedTitle(EntityPlayer entityplayer) {
		return theRank.getCodeFullNameWithGender(TESLevelData.getData(entityplayer));
	}

	public boolean isPlayerRequiredRank(EntityPlayer entityplayer) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		float align = pd.getAlignment(theFac);
		float rankAlign = theRank.getAlignment();
		return align >= rankAlign;
	}
}