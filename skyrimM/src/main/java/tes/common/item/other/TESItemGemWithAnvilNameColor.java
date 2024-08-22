package tes.common.item.other;

import net.minecraft.util.EnumChatFormatting;

public class TESItemGemWithAnvilNameColor extends TESItemGem {
	private final EnumChatFormatting anvilNameColor;

	public TESItemGemWithAnvilNameColor(EnumChatFormatting color) {
		anvilNameColor = color;
	}

	public EnumChatFormatting getAnvilNameColor() {
		return anvilNameColor;
	}
}