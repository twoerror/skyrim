package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.inanimate.TESEntityBanner;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TESPacketBannerValidate implements IMessage {
	private int entityID;
	private int slot;
	private String prevText;
	private boolean valid;

	@SuppressWarnings("unused")
	public TESPacketBannerValidate() {
	}

	public TESPacketBannerValidate(int id, int s, String pre, boolean val) {
		entityID = id;
		slot = s;
		prevText = pre;
		valid = val;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		slot = data.readInt();
		byte length = data.readByte();
		ByteBuf textBytes = data.readBytes(length);
		prevText = textBytes.toString(Charsets.UTF_8);
		valid = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeInt(slot);
		byte[] textBytes = prevText.getBytes(Charsets.UTF_8);
		data.writeByte(textBytes.length);
		data.writeBytes(textBytes);
		data.writeBoolean(valid);
	}

	public static class Handler implements IMessageHandler<TESPacketBannerValidate, IMessage> {
		@Override
		public IMessage onMessage(TESPacketBannerValidate packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityBanner) {
				TESEntityBanner banner = (TESEntityBanner) entity;
				TES.proxy.validateBannerUsername(banner, packet.slot, packet.prevText, packet.valid);
			}
			return null;
		}
	}
}