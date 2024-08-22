package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketUnitTraderInteract;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

public class TESGuiUnitTradeInteract extends TESGuiNPCInteract {
	public TESGuiUnitTradeInteract(TESEntityNPC entity) {
		super(entity);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			IMessage packet = new TESPacketUnitTraderInteract(theEntity.getEntityId(), button.id);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	@Override
	public void initGui() {
		GuiButton buttonTalk = new TESGuiButton(0, width / 2 - 65, height / 5 * 3, 60, 20, StatCollector.translateToLocal("tes.gui.npc.talk"));
		GuiButton buttonHire = new TESGuiButton(1, width / 2 + 5, height / 5 * 3, 60, 20, StatCollector.translateToLocal("tes.gui.npc.hire"));
		buttonList.add(buttonTalk);
		buttonList.add(buttonHire);
	}
}