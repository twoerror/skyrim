package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESHireableBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TESPacketUnitTraderInteract implements IMessage {
	private int traderID;
	private int traderAction;

	@SuppressWarnings("unused")
	public TESPacketUnitTraderInteract() {
	}

	public TESPacketUnitTraderInteract(int idt, int a) {
		traderID = idt;
		traderAction = a;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		traderID = data.readInt();
		traderAction = data.readByte();
	}

	protected void openTradeGUI(EntityPlayer entityplayer, TESEntityNPC trader) {
		entityplayer.openGui(TES.instance, TESGuiId.UNIT_TRADE.ordinal(), entityplayer.worldObj, trader.getEntityId(), 0, 0);
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(traderID);
		data.writeByte(traderAction);
	}

	public static class Handler implements IMessageHandler<TESPacketUnitTraderInteract, IMessage> {
		@Override
		@SuppressWarnings("CastConflictsWithInstanceof")
		public IMessage onMessage(TESPacketUnitTraderInteract packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			Entity trader = world.getEntityByID(packet.traderID);
			if (trader instanceof TESHireableBase) {
				TESHireableBase tradeableTrader = (TESHireableBase) trader;
				TESEntityNPC livingTrader = (TESEntityNPC) trader;
				int action = packet.traderAction;
				boolean closeScreen = false;
				if (action == 0) {
					livingTrader.setNpcTalkTick(40);
					closeScreen = livingTrader.interactFirst(entityplayer);
				} else if (action == 1 && tradeableTrader.canTradeWith(entityplayer)) {
					packet.openTradeGUI(entityplayer, livingTrader);
				}
				if (closeScreen) {
					entityplayer.closeScreen();
				}
			}
			return null;
		}
	}
}