package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESSquadrons;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.entity.other.iface.TESMercenary;
import tes.common.entity.other.iface.TESUnitTradeable;
import tes.common.entity.other.utils.TESMercenaryTradeEntry;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import tes.common.inventory.TESContainerUnitTrade;
import tes.common.util.TESLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.StringUtils;

public class TESPacketBuyUnit implements IMessage {
	private int tradeIndex;
	private String squadron;

	@SuppressWarnings("unused")
	public TESPacketBuyUnit() {
	}

	public TESPacketBuyUnit(int tr, String s) {
		tradeIndex = tr;
		squadron = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		tradeIndex = data.readByte();
		int squadronLength = data.readInt();
		if (squadronLength > -1) {
			squadron = data.readBytes(squadronLength).toString(Charsets.UTF_8);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(tradeIndex);
		if (StringUtils.isNullOrEmpty(squadron)) {
			data.writeInt(-1);
		} else {
			byte[] squadronBytes = squadron.getBytes(Charsets.UTF_8);
			data.writeInt(squadronBytes.length);
			data.writeBytes(squadronBytes);
		}
	}

	public static class Handler implements IMessageHandler<TESPacketBuyUnit, IMessage> {
		@Override
		public IMessage onMessage(TESPacketBuyUnit packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerUnitTrade) {
				TESContainerUnitTrade tradeContainer = (TESContainerUnitTrade) container;
				TESHireableBase unitTrader = tradeContainer.getTheUnitTrader();
				int tradeIndex = packet.tradeIndex;
				TESUnitTradeEntry trade = null;
				if (unitTrader instanceof TESUnitTradeable) {
					TESUnitTradeEntry[] tradeList = ((TESUnitTradeable) unitTrader).getUnits().getTradeEntries();
					if (tradeIndex >= 0 && tradeIndex < tradeList.length) {
						trade = tradeList[tradeIndex];
					}
				} else if (unitTrader instanceof TESMercenary) {
					trade = TESMercenaryTradeEntry.createFor((TESMercenary) unitTrader);
				}
				String squadron = packet.squadron;
				squadron = TESSquadrons.checkAcceptableLength(squadron);
				if (trade != null) {
					trade.hireUnit(entityplayer, unitTrader, squadron);
					if (unitTrader instanceof TESMercenary) {
						((EntityPlayer) entityplayer).closeScreen();
					}
				} else {
					TESLog.getLogger().error("Hummel009: Error player {} trying to hire unit from {} - trade is null or bad index!", entityplayer.getCommandSenderName(), unitTrader.getNPCName());
				}
			}
			return null;
		}
	}
}