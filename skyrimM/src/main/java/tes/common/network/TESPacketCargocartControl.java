package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.entity.other.inanimate.TESEntityCart;
import tes.common.util.TESVertex;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class TESPacketCargocartControl implements IMessage {
	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<TESPacketCargocartControl, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCargocartControl message, MessageContext ctx) {
			EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
			if (sender.isRiding() && sender.ridingEntity instanceof EntityHorse) {
				List<TESEntityCart> result = sender.getServerForPlayer().getEntitiesWithinAABB(TESEntityCart.class, sender.boundingBox.expand(3.0D, 3.0D, 3.0D));
				if (!result.isEmpty()) {
					TESEntityCart closest = result.get(0);
					for (TESEntityCart cart : result) {
						if (cart.getPulling() == sender.ridingEntity) {
							cart.setPulling(null);
							sender.getServerForPlayer().getEntityTracker().func_151247_a(cart, TESPacketHandler.NETWORK_WRAPPER.getPacketFrom(new TESPacketCargocartUpdate(-1, cart.getEntityId())));
							return null;
						}
						if (new TESVertex(cart.posX, cart.posY, cart.posZ).distanceTo(new TESVertex(sender.posX, sender.posY, sender.posZ)) < new TESVertex(closest.posX, closest.posY, closest.posZ).distanceTo(new TESVertex(sender.posX, sender.posY, sender.posZ))) {
							closest = cart;
						}
					}
					closest.setPulling(sender.ridingEntity);
					sender.getServerForPlayer().getEntityTracker().func_151247_a(closest, TESPacketHandler.NETWORK_WRAPPER.getPacketFrom(new TESPacketCargocartUpdate(closest.getPulling().getEntityId(), closest.getEntityId())));
				}
			}
			return null;
		}
	}
}