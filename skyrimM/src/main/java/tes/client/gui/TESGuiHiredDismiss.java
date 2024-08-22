package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketHiredUnitDismiss;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.util.StatCollector;

public class TESGuiHiredDismiss extends TESGuiNPCInteract {
	public TESGuiHiredDismiss(TESEntityNPC entity) {
		super(entity);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 1) {
				mc.displayGuiScreen(new TESGuiHiredInteract(theEntity));
				return;
			}
			IMessage packet = new TESPacketHiredUnitDismiss(theEntity.getEntityId(), button.id);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
		String s = StatCollector.translateToLocal("tes.gui.dismiss.warning1");
		int y = height / 5 * 3;
		fontRendererObj.drawString(s, (width - fontRendererObj.getStringWidth(s)) / 2, y, 16777215);
		s = StatCollector.translateToLocal("tes.gui.dismiss.warning2");
		fontRendererObj.drawString(s, (width - fontRendererObj.getStringWidth(s)) / 2, y += fontRendererObj.FONT_HEIGHT, 16777215);
		y += fontRendererObj.FONT_HEIGHT;
		Entity mount = theEntity.ridingEntity;
		Entity rider = theEntity.riddenByEntity;
		boolean hasMount = mount instanceof TESEntityNPC && ((TESEntityNPC) mount).getHireableInfo().getHiringPlayer() == mc.thePlayer;
		boolean hasRider = rider instanceof TESEntityNPC && ((TESEntityNPC) rider).getHireableInfo().getHiringPlayer() == mc.thePlayer;
		if (hasMount) {
			s = StatCollector.translateToLocal("tes.gui.dismiss.mount");
			fontRendererObj.drawString(s, (width - fontRendererObj.getStringWidth(s)) / 2, y, 11184810);
			y += fontRendererObj.FONT_HEIGHT;
		}
		if (hasRider) {
			s = StatCollector.translateToLocal("tes.gui.dismiss.rider");
			fontRendererObj.drawString(s, (width - fontRendererObj.getStringWidth(s)) / 2, y, 11184810);
		}
	}

	@Override
	public void initGui() {
		buttonList.add(new TESGuiButton(0, width / 2 - 65, height / 5 * 3 + 40, 60, 20, StatCollector.translateToLocal("tes.gui.dismiss.dismiss")));
		buttonList.add(new TESGuiButton(1, width / 2 + 5, height / 5 * 3 + 40, 60, 20, StatCollector.translateToLocal("tes.gui.dismiss.cancel")));
	}
}