package tes.common.item.weapon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.block.other.TESBlockBomb;
import tes.common.dispense.TESDispenseBomb;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TESItemBomb extends ItemBlock {
	public TESItemBomb(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseBomb());
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		int meta = itemstack.getItemDamage();
		int strength = TESBlockBomb.getBombStrengthLevel(meta);
		if (strength == 1) {
			list.add(StatCollector.translateToLocal("tile.tes.bomb.double_strength"));
		}
		if (strength == 2) {
			list.add(StatCollector.translateToLocal("tile.tes.bomb.triple_strength"));
		}
		if (TESBlockBomb.isFireBomb(meta)) {
			list.add(StatCollector.translateToLocal("tile.tes.bomb.fire"));
		}
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}
}