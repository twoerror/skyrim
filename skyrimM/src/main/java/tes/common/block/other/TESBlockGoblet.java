package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public abstract class TESBlockGoblet extends TESBlockMug {
	protected TESBlockGoblet() {
		super(2.5f, 9.0f);
		setStepSound(soundTypeMetal);
	}

	public static class Copper extends TESBlockGoblet {
		@SideOnly(Side.CLIENT)
		@Override
		public IIcon getIcon(int i, int j) {
			return TESBlocks.blockMetal1.getIcon(i, 0);
		}
	}

	public static class Gold extends TESBlockGoblet {
		@SideOnly(Side.CLIENT)
		@Override
		public IIcon getIcon(int i, int j) {
			return Blocks.gold_block.getIcon(i, 0);
		}
	}

	public static class Silver extends TESBlockGoblet {
		@SideOnly(Side.CLIENT)
		@Override
		public IIcon getIcon(int i, int j) {
			return TESBlocks.blockMetal1.getIcon(i, 3);
		}
	}

	public static class Wood extends TESBlockGoblet {
		public Wood() {
			setStepSound(soundTypeWood);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public IIcon getIcon(int i, int j) {
			return Blocks.planks.getIcon(i, 0);
		}
	}
}