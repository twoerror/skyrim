package tes.common.block.other;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.database.TESGuiId;
import tes.common.database.TESItems;
import tes.common.item.other.TESItemCoin;
import tes.common.network.TESPacketClientsideGUI;
import tes.common.network.TESPacketHandler;
import tes.common.util.TESItemStackMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Map;

public class TESBlockIronBank extends TESBlockForgeBase {
	public static final Map<ItemStack, Integer> BUY = new TESItemStackMap<>();
	public static final Map<ItemStack, Integer> SELL = new TESItemStackMap<>();

	public static void preInit() {
		for (int i = 0; i < TESItemCoin.VALUES.length; ++i) {
			BUY.put(new ItemStack(TESItems.coin, 1, i), TESItemCoin.VALUES[i]);
			SELL.put(new ItemStack(TESItems.coin, 1, i), TESItemCoin.VALUES[i]);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		if (!world.isRemote) {
			IMessage packet = new TESPacketClientsideGUI(TESGuiId.IRON_BANK.ordinal(), i, j, k);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
		return true;
	}

	@Override
	public boolean useLargeSmoke() {
		return false;
	}
}