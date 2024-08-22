package tes.common.fellowship;

import com.mojang.authlib.GameProfile;
import tes.TES;
import tes.common.TESLevelData;
import net.minecraft.util.StatCollector;

import java.util.Locale;
import java.util.UUID;

public class TESFellowshipProfile extends GameProfile {
	private static final String FELLOWSHIP_PREFIX = "f/";

	private final String fellowshipName;

	public TESFellowshipProfile(UUID fsID, String fsName) {
		super(fsID, fsName);
		fellowshipName = fsName;
	}

	public static String addFellowshipCode(String s) {
		return FELLOWSHIP_PREFIX + s;
	}

	public static String getFellowshipCodeHint() {
		return StatCollector.translateToLocalFormatted("tes.gui.bannerEdit.fellowshipHint", FELLOWSHIP_PREFIX);
	}

	public static boolean hasFellowshipCode(String s) {
		return s.toLowerCase(Locale.ROOT).startsWith(FELLOWSHIP_PREFIX.toLowerCase(Locale.ROOT));
	}

	public static String stripFellowshipCode(String s) {
		return s.substring(FELLOWSHIP_PREFIX.length());
	}

	public TESFellowship getFellowship() {
		return TESFellowshipData.getActiveFellowship(getId());
	}

	public TESFellowshipClient getFellowshipClient() {
		return TESLevelData.getData(TES.proxy.getClientPlayer()).getClientFellowshipByName(fellowshipName);
	}

	@Override
	public String getName() {
		return addFellowshipCode(super.getName());
	}
}