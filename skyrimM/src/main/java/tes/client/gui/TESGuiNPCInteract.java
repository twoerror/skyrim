package tes.client.gui;

import tes.common.entity.other.TESEntityNPC;

public abstract class TESGuiNPCInteract extends TESGuiScreenBase {
	protected final TESEntityNPC theEntity;

	protected TESGuiNPCInteract(TESEntityNPC entity) {
		theEntity = entity;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		String s = theEntity.getCommandSenderName();
		fontRendererObj.drawString(s, (width - fontRendererObj.getStringWidth(s)) / 2, height / 5 * 3 - 20, 16777215);
		super.drawScreen(i, j, f);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (theEntity == null || !theEntity.isEntityAlive() || theEntity.getDistanceSqToEntity(mc.thePlayer) > 100.0) {
			mc.thePlayer.closeScreen();
		}
	}
}