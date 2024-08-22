package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESTradeable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TESPacketTraderInteract implements IMessage {
	private int traderID;
	private int traderAction;

	@SuppressWarnings("unused")
	public TESPacketTraderInteract() {
	}

	public TESPacketTraderInteract(int idt, int a) {
		traderID = idt;
		traderAction = a;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		traderID = data.readInt();
		traderAction = data.readByte();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(traderID);
		data.writeByte(traderAction);
	}

	public static class Handler implements IMessageHandler<TESPacketTraderInteract, IMessage> {
		@Override
		@SuppressWarnings("CastConflictsWithInstanceof")
		public IMessage onMessage(TESPacketTraderInteract packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			Entity trader = world.getEntityByID(packet.traderID);
			if (trader instanceof TESTradeable) {
				TESTradeable tradeableTrader = (TESTradeable) trader;
				TESEntityNPC livingTrader = (TESEntityNPC) trader;
				int action = packet.traderAction;
				boolean closeScreen = false;
				if (action == 0) {
					livingTrader.setNpcTalkTick(40);
					closeScreen = livingTrader.interactFirst(entityplayer);
				} else if (action == 1 && tradeableTrader.canTradeWith(entityplayer)) {
					entityplayer.openGui(TES.instance, TESGuiId.TRADE.ordinal(), world, livingTrader.getEntityId(), 0, 0);
				} else if (action == 2 && tradeableTrader.canTradeWith(entityplayer)) {
					entityplayer.openGui(TES.instance, TESGuiId.COIN_EXCHANGE.ordinal(), world, livingTrader.getEntityId(), 0, 0);
				} else if (action == 3 && tradeableTrader.canTradeWith(entityplayer) && tradeableTrader instanceof TESTradeable.Smith) {
					entityplayer.openGui(TES.instance, TESGuiId.ANVIL_NPC.ordinal(), world, livingTrader.getEntityId(), 0, 0);
				}
				if (closeScreen) {
					entityplayer.closeScreen();
				}
			}
			return null;
		}
	}
}