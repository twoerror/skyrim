package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketSetOption implements IMessage {
	private int option;

	@SuppressWarnings("unused")
	public TESPacketSetOption() {
	}

	public TESPacketSetOption(int i) {
		option = i;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		option = data.readByte();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(option);
	}

	public static class Handler implements IMessageHandler<TESPacketSetOption, IMessage> {
		@Override
		public IMessage onMessage(TESPacketSetOption packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			int option = packet.option;
			boolean flag;
			switch (option) {
				case 0:
					flag = pd.getFriendlyFire();
					pd.setFriendlyFire(!flag);
					break;
				case 1:
					flag = pd.getEnableHiredDeathMessages();
					pd.setEnableHiredDeathMessages(!flag);
					break;
				case 2:
					flag = pd.getHideAlignment();
					pd.setHideAlignment(!flag);
					break;
				case 3:
					flag = pd.getHideMapLocation();
					pd.setHideMapLocation(!flag);
					break;
				case 4:
					flag = pd.getFeminineRanks();
					pd.setFeminineRanks(!flag);
					break;
				case 5:
					flag = pd.getEnableConquestKills();
					pd.setEnableConquestKills(!flag);
					break;
				case 9:
					flag = pd.getTableSwitched();
					pd.setTableSwitched(!flag);
					break;
			}
			return null;
		}
	}
}