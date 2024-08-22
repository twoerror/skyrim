package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketTraderInteract;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

public class TESGuiTradeInteract extends TESGuiNPCInteract {
	public TESGuiTradeInteract(TESEntityNPC entity) {
		super(entity);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			IMessage packet = new TESPacketTraderInteract(theEntity.getEntityId(), button.id);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	@Override
	public void initGui() {
		GuiButton buttonTalk = new TESGuiButton(0, width / 2 - 65, height / 5 * 3, 60, 20, StatCollector.translateToLocal("tes.gui.npc.talk"));
		GuiButton buttonTrade = new TESGuiButton(1, width / 2 + 5, height / 5 * 3, 60, 20, StatCollector.translateToLocal("tes.gui.npc.trade"));
		GuiButton buttonExchange = new TESGuiButton(2, width / 2 - 65, height / 5 * 3 + 25, 130, 20, StatCollector.translateToLocal("tes.gui.npc.exchange"));
		buttonList.add(buttonTalk);
		buttonList.add(buttonTrade);
		buttonList.add(buttonExchange);
		if (theEntity instanceof TESTradeable.Smith) {
			buttonTalk.xPosition -= 35;
			buttonTrade.xPosition -= 35;
			GuiButton buttonSmith = new TESGuiButton(3, width / 2 + 40, height / 5 * 3, 60, 20, StatCollector.translateToLocal("tes.gui.npc.smith"));
			buttonList.add(buttonSmith);
		}
	}
}