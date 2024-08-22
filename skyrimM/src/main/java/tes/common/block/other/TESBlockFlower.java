package tes.common.block.other;

import tes.TES;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;

public class TESBlockFlower extends BlockBush {
	public TESBlockFlower() {
		super(Material.plants);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
		setHardness(0.0f);
		setStepSound(soundTypeGrass);
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getFlowerRenderID();
	}

	public Block setFlowerBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		return this;
	}
}