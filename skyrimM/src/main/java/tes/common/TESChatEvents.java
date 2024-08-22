package tes.common;

import tes.common.util.TESReflection;
import net.minecraft.event.HoverEvent;
import net.minecraftforge.common.util.EnumHelper;

@SuppressWarnings({"WeakerAccess", "PublicField"})
public class TESChatEvents {
	private static final Class<?>[][] HOVER_PARAMS = new Class[][]{{HoverEvent.Action.class, String.class, Boolean.TYPE}};

	public static HoverEvent.Action showGotAchievement;

	private TESChatEvents() {
	}

	public static void onInit() {
		showGotAchievement = EnumHelper.addEnum(HOVER_PARAMS, HoverEvent.Action.class, "SHOW_TES_ACHIEVEMENT", "show_tes_achievement", true);
		TESReflection.getHoverEventMappings().put(showGotAchievement.getCanonicalName(), showGotAchievement);
	}
}