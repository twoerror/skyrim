package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockDoor extends BlockDoor {
	public TESBlockDoor() {
		super(Material.wood);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setStepSound(soundTypeWood);
		setHardness(3.0f);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World world, int i, int j, int k) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return (i & 8) != 0 ? null : Item.getItemFromBlock(this);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemIconName() {
		return getTextureName();
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getDoorRenderID();
	}
}