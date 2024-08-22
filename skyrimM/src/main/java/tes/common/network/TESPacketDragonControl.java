package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import tes.common.entity.dragon.TESEntityDragon;
import tes.common.util.TESLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.BitSet;

public class TESPacketDragonControl implements IMessage {
	private final BitSet bits = new BitSet(Byte.SIZE);

	private int previous;

	@Override
	public void fromBytes(ByteBuf buf) {
		fromInteger(buf.readUnsignedByte());
	}

	private void fromInteger(int i) {
		int value = i;
		int index = 0;
		while (value != 0) {
			if (value % 2 != 0) {
				bits.set(index);
			}
			index++;
			value >>>= 1;
		}
	}

	public BitSet getFlags() {
		return bits;
	}

	public boolean hasChanged() {
		int current = toInteger();
		boolean changed = previous != current;
		previous = current;
		return changed;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(toInteger());
	}

	private int toInteger() {
		int value = 0;
		for (int i = 0; i < bits.length(); i++) {
			value += bits.get(i) ? 1 << i : 0;
		}
		return value;
	}

	public static class Handler implements IMessageHandler<TESPacketDragonControl, IMessage> {
		@Override
		public IMessage onMessage(TESPacketDragonControl message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {
				TESLog.getLogger().warn("Recieved unexpected control message from server!");
				return null;
			}
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			if (player.ridingEntity instanceof TESEntityDragon) {
				TESEntityDragon dragon = (TESEntityDragon) player.ridingEntity;
				dragon.setControlFlags(message.getFlags());
			}
			return null;
		}
	}
}