package tes.client.render.other;

import net.minecraft.util.IIcon;

public class TESExtraLargeIconToken {
	private final String name;

	private IIcon icon;

	TESExtraLargeIconToken(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	public IIcon getIcon() {
		return icon;
	}

	public void setIcon(IIcon icon) {
		this.icon = icon;
	}
}