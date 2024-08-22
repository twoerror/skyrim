package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketOptions implements IMessage {
	private int option;
	private boolean enable;

	@SuppressWarnings("unused")
	public TESPacketOptions() {
	}

	public TESPacketOptions(int i, boolean flag) {
		option = i;
		enable = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		option = data.readByte();
		enable = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(option);
		data.writeBoolean(enable);
	}

	public static class Handler implements IMessageHandler<TESPacketOptions, IMessage> {
		@Override
		public IMessage onMessage(TESPacketOptions packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				int option = packet.option;
				boolean enable = packet.enable;
				switch (option) {
					case 0:
						TESLevelData.getData(entityplayer).setFriendlyFire(enable);
						break;
					case 1:
						TESLevelData.getData(entityplayer).setEnableHiredDeathMessages(enable);
						break;
					case 3:
						TESLevelData.getData(entityplayer).setHideMapLocation(enable);
						break;
					case 4:
						TESLevelData.getData(entityplayer).setFeminineRanks(enable);
						break;
					case 5:
						TESLevelData.getData(entityplayer).setEnableConquestKills(enable);
						break;
					case 9:
						TESLevelData.getData(entityplayer).setTableSwitched(enable);
						break;
				}
			}
			return null;
		}
	}
}