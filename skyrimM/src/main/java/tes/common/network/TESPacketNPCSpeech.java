package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESConfig;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class TESPacketNPCSpeech implements IMessage {
	private int entityID;
	private String speech;
	private boolean forceChatMsg;

	@SuppressWarnings("unused")
	public TESPacketNPCSpeech() {
	}

	public TESPacketNPCSpeech(int id, String s, boolean forceChat) {
		entityID = id;
		speech = s;
		forceChatMsg = forceChat;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		int length = data.readInt();
		speech = data.readBytes(length).toString(Charsets.UTF_8);
		forceChatMsg = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		byte[] speechBytes = speech.getBytes(Charsets.UTF_8);
		data.writeInt(speechBytes.length);
		data.writeBytes(speechBytes);
		data.writeBoolean(forceChatMsg);
	}

	public static class Handler implements IMessageHandler<TESPacketNPCSpeech, IMessage> {
		@Override
		public IMessage onMessage(TESPacketNPCSpeech packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entity;
				if (TESConfig.immersiveSpeech) {
					TES.proxy.clientReceiveSpeech(npc, packet.speech);
				}
				if (!TESConfig.immersiveSpeech || TESConfig.immersiveSpeechChatLog || packet.forceChatMsg) {
					String name = npc.getCommandSenderName();
					String message = EnumChatFormatting.YELLOW + "<" + name + "> " + EnumChatFormatting.WHITE + packet.speech;
					entityplayer.addChatMessage(new ChatComponentText(message));
				}
			}
			return null;
		}
	}
}