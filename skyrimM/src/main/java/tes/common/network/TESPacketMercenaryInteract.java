package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketMercenaryInteract extends TESPacketUnitTraderInteract {
	@SuppressWarnings("unused")
	public TESPacketMercenaryInteract() {
	}

	public TESPacketMercenaryInteract(int idt, int a) {
		super(idt, a);
	}

	@Override
	public void openTradeGUI(EntityPlayer entityplayer, TESEntityNPC trader) {
		entityplayer.openGui(TES.instance, TESGuiId.MERCENARY_HIRE.ordinal(), entityplayer.worldObj, trader.getEntityId(), 0, 0);
	}

	public static class Handler implements IMessageHandler<TESPacketMercenaryInteract, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMercenaryInteract message, MessageContext ctx) {
			return new TESPacketUnitTraderInteract.Handler().onMessage(message, ctx);
		}
	}
}