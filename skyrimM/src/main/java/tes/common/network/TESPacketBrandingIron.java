package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.item.other.TESItemBrandingIron;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;

public class TESPacketBrandingIron implements IMessage {
	private String brandName;

	@SuppressWarnings("unused")
	public TESPacketBrandingIron() {
	}

	public TESPacketBrandingIron(String s) {
		brandName = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		int length = data.readInt();
		if (length > -1) {
			brandName = data.readBytes(length).toString(Charsets.UTF_8);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		if (StringUtils.isBlank(brandName)) {
			data.writeInt(-1);
		} else {
			byte[] brandBytes = brandName.getBytes(Charsets.UTF_8);
			data.writeInt(brandBytes.length);
			data.writeBytes(brandBytes);
		}
	}

	public static class Handler implements IMessageHandler<TESPacketBrandingIron, IMessage> {
		@Override
		public IMessage onMessage(TESPacketBrandingIron packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
			if (itemstack != null && itemstack.getItem() instanceof TESItemBrandingIron) {
				String brandName = packet.brandName;
				if (!StringUtils.isBlank(brandName = TESItemBrandingIron.trimAcceptableBrandName(brandName)) && !TESItemBrandingIron.hasBrandName(itemstack)) {
					TESItemBrandingIron.setBrandName(itemstack, brandName);
				}
			}
			return null;
		}
	}
}