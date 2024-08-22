package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.world.biome.variant.TESBiomeVariantStorage;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class TESPacketBiomeVariantsUnwatch implements IMessage {
	private int chunkX;
	private int chunkZ;

	@SuppressWarnings("unused")
	public TESPacketBiomeVariantsUnwatch() {
	}

	public TESPacketBiomeVariantsUnwatch(int x, int z) {
		chunkX = x;
		chunkZ = z;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		chunkX = data.readInt();
		chunkZ = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(chunkX);
		data.writeInt(chunkZ);
	}

	public static class Handler implements IMessageHandler<TESPacketBiomeVariantsUnwatch, IMessage> {
		@Override
		public IMessage onMessage(TESPacketBiomeVariantsUnwatch packet, MessageContext context) {
			int chunkX;
			int chunkZ;
			World world = TES.proxy.getClientWorld();
			if (world.blockExists((chunkX = packet.chunkX) << 4, 0, (chunkZ = packet.chunkZ) << 4)) {
				TESBiomeVariantStorage.clearChunkBiomeVariants(world, new ChunkCoordIntPair(chunkX, chunkZ));
			} else {
				FMLLog.severe("Client received TES biome variant unwatch packet for nonexistent chunk at %d, %d", chunkX << 4, chunkZ << 4);
			}
			return null;
		}
	}
}