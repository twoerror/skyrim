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

public class TESPacketMoneyGet extends TESPacketMoney {
	private ItemStack item;

	@SuppressWarnings("unused")
	public TESPacketMoneyGet() {
	}

	public TESPacketMoneyGet(ItemStack items) {
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

	public static class Handler implements IMessageHandler<TESPacketMoneyGet, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMoneyGet packet, MessageContext context) {
			ItemStack item = packet.item;
			if (TESBlockIronBank.SELL.containsKey(item)) {
				EntityPlayerMP player = context.getServerHandler().playerEntity;
				TESPlayerData pd = TESLevelData.getData(player);
				int cost = TESBlockIronBank.SELL.get(item);
				int index = -1;
				for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
					if (player.inventory.mainInventory[i] == null || player.inventory.mainInventory[i].getItem() != item.getItem() || player.inventory.mainInventory[i].getItemDamage() != item.getItemDamage()) {
						continue;
					}
					index = i;
					break;
				}
				if (index >= 0) {
					--player.inventory.mainInventory[index].stackSize;
					if (player.inventory.mainInventory[index].stackSize <= 0) {
						player.inventory.mainInventory[index] = null;
					}
					int money = pd.getBalance();
					money += cost;
					pd.setBalance(money);
					TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, player);
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocalFormatted("tes.gui.money.get", item.getDisplayName())));
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + StatCollector.translateToLocalFormatted("tes.gui.money.notGet", item.getDisplayName())));
				}
			}
			return null;
		}
	}
}