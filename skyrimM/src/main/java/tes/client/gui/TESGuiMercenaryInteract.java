package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketMercenaryInteract;
import net.minecraft.client.gui.GuiButton;

public class TESGuiMercenaryInteract extends TESGuiUnitTradeInteract {
	public TESGuiMercenaryInteract(TESEntityNPC entity) {
		super(entity);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			IMessage packet = new TESPacketMercenaryInteract(theEntity.getEntityId(), button.id);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}
}