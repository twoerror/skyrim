package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketUnitTraderInteract;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

public class TESGuiTradeUnitTradeInteract extends TESGuiTradeInteract {
	private GuiButton buttonHire;

	public TESGuiTradeUnitTradeInteract(TESEntityNPC entity) {
		super(entity);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == buttonHire) {
				IMessage packet = new TESPacketUnitTraderInteract(theEntity.getEntityId(), 1);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else {
				super.actionPerformed(button);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonHire = new TESGuiButton(-1, width / 2 - 65, height / 5 * 3 + 50, 130, 20, StatCollector.translateToLocal("tes.gui.npc.hire"));
		buttonList.add(buttonHire);
	}
}