package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.dispense.TESDispenseWildFireJar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TESItemWildFireJar extends ItemBlock {
	public TESItemWildFireJar(Block block) {
		super(block);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseWildFireJar());
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		super.addInformation(itemstack, entityplayer, list, flag);
		list.add(StatCollector.translateToLocal("tile.tes.wild_fire.warning"));
	}
}