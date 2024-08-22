package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketFTTimer implements IMessage {
	private int timer;

	@SuppressWarnings("unused")
	public TESPacketFTTimer() {
	}

	public TESPacketFTTimer(int i) {
		timer = i;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		timer = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(timer);
	}

	public static class Handler implements IMessageHandler<TESPacketFTTimer, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFTTimer packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESLevelData.getData(entityplayer).setTimeSinceFT(packet.timer);
			return null;
		}
	}
}