package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESBannerProtection;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class TESPacketCreateCWP implements IMessage {
	private String name;

	@SuppressWarnings("unused")
	public TESPacketCreateCWP() {
	}

	public TESPacketCreateCWP(String s) {
		name = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		short length = data.readShort();
		name = data.readBytes(length).toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		byte[] nameBytes = name.getBytes(Charsets.UTF_8);
		data.writeShort(nameBytes.length);
		data.writeBytes(nameBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketCreateCWP, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCreateCWP packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			int numWaypoints = pd.getCustomWaypoints().size();
			if (numWaypoints <= pd.getMaxCustomWaypoints()) {
				IChatComponent[] protectionMessage = new IChatComponent[1];
				boolean protection = TESBannerProtection.isProtected(world, entityplayer, TESBannerProtection.forPlayer_returnMessage(entityplayer, TESBannerProtection.Permission.FULL, protectionMessage), true);
				if (protection) {
					IChatComponent clientMessage = protectionMessage[0];
					IMessage packetMessage = new TESPacketCWPProtectionMessage(clientMessage);
					TESPacketHandler.NETWORK_WRAPPER.sendTo(packetMessage, entityplayer);
				} else {
					String wpName = TESCustomWaypoint.validateCustomName(packet.name);
					if (wpName != null) {
						TESCustomWaypoint.createForPlayer(wpName, entityplayer);
					}
				}
			}
			return null;
		}
	}
}