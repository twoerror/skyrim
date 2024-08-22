package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.block.other.TESBlockIronBank;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.io.IOException;
import java.util.UUID;

public class TESPacketMoneyGive extends TESPacketMoney {
	private static final UUID HUMMEL_UUID = UUID.fromString("9aee5b32-8e19-4d4b-a2d6-1318af62733d");

	private ItemStack item;

	@SuppressWarnings("unused")
	public TESPacketMoneyGive() {
	}

	public TESPacketMoneyGive(ItemStack items) {
		item = items;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		try {
			item = new PacketBuffer(data).readItemStackFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error writing money data");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		try {
			new PacketBuffer(data).writeItemStackToBuffer(item);
		} catch (IOException e) {
			FMLLog.severe("Error reading money data");
			e.printStackTrace();
		}
	}

	public static class Handler implements IMessageHandler<TESPacketMoneyGive, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMoneyGive packet, MessageContext context) {
			ItemStack item = packet.item;
			if (TESBlockIronBank.BUY.containsKey(item)) {
				EntityPlayerMP player = context.getServerHandler().playerEntity;
				TESPlayerData pd = TESLevelData.getData(player);
				int cost = TESBlockIronBank.BUY.get(item);
				if (HUMMEL_UUID.equals(player.getUniqueID())) {
					player.inventory.addItemStackToInventory(item);
				} else if (pd.getBalance() >= cost && player.inventory.addItemStackToInventory(item)) {
					int money = pd.getBalance();
					money -= cost;
					pd.setBalance(money);
					TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, player);
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocalFormatted("tes.gui.money.give", item.getDisplayName())));
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + StatCollector.translateToLocalFormatted("tes.gui.money.notGive", item.getDisplayName())));
				}
			}
			return null;
		}
	}
}