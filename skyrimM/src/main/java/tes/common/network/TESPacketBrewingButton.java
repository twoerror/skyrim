package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.inventory.TESContainerBarrel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public class TESPacketBrewingButton implements IMessage {
	@Override
	public void fromBytes(ByteBuf data) {
	}

	@Override
	public void toBytes(ByteBuf data) {
	}

	public static class Handler implements IMessageHandler<TESPacketBrewingButton, IMessage> {
		@Override
		public IMessage onMessage(TESPacketBrewingButton packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerBarrel) {
				((TESContainerBarrel) container).getTheBarrel().handleBrewingButtonPress();
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.brewDrinkInBarrel);
			}
			return null;
		}
	}
}