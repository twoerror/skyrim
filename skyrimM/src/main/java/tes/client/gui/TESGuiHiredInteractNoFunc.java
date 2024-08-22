package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketHiredUnitInteract;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TESGuiHiredInteractNoFunc extends TESGuiNPCInteract {
	public TESGuiHiredInteractNoFunc(TESEntityNPC entity) {
		super(entity);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button.id == 2) {
				mc.displayGuiScreen(new TESGuiHiredDismiss(theEntity));
				return;
			}
			IMessage packet = new TESPacketHiredUnitInteract(theEntity.getEntityId(), button.id);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	@Override
	public void initGui() {
		buttonList.add(new TESGuiButton(0, width / 2 - 65, height / 5 * 3, 130, 20, StatCollector.translateToLocal("tes.gui.npc.talk")));
		buttonList.add(new TESGuiButton(2, width / 2 - 65, height / 5 * 3 + 25, 130, 20, StatCollector.translateToLocal("tes.gui.npc.dismiss")));
		((List<GuiButton>) buttonList).get(0).enabled = theEntity.getSpeechBank(mc.thePlayer) != null;
	}
}