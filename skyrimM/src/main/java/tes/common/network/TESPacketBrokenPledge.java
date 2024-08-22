package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketBrokenPledge implements IMessage {
	private int cooldown;
	private int cooldownStart;
	private TESFaction brokenFac;

	@SuppressWarnings("unused")
	public TESPacketBrokenPledge() {
	}

	public TESPacketBrokenPledge(int cd, int start, TESFaction f) {
		cooldown = cd;
		cooldownStart = start;
		brokenFac = f;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cooldown = data.readInt();
		cooldownStart = data.readInt();
		byte facID = data.readByte();
		if (facID >= 0) {
			brokenFac = TESFaction.forID(facID);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cooldown);
		data.writeInt(cooldownStart);
		if (brokenFac == null) {
			data.writeByte(-1);
		} else {
			data.writeByte(brokenFac.ordinal());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketBrokenPledge, IMessage> {
		@Override
		public IMessage onMessage(TESPacketBrokenPledge packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			pd.setPledgeBreakCooldown(packet.cooldown);
			pd.setPledgeBreakCooldownStart(packet.cooldownStart);
			pd.setBrokenPledgeFaction(packet.brokenFac);
			return null;
		}
	}
}