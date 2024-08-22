package tes.common;

import net.minecraft.util.StatCollector;

public enum TESGuiMessageTypes {
	ENCHANTING("enchanting"), FRIENDLY_FIRE("friendlyFire");

	private final String messageName;

	TESGuiMessageTypes(String s) {
		messageName = s;
	}

	public static TESGuiMessageTypes forSaveName(String name) {
		for (TESGuiMessageTypes message : values()) {
			if (message.messageName.equals(name)) {
				return message;
			}
		}
		return null;
	}

	public String getMessage() {
		return StatCollector.translateToLocal("tes.gui.message." + messageName);
	}

	public String getSaveName() {
		return messageName;
	}
}