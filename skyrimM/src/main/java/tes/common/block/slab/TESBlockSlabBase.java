package tes.common.block.slab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public abstract class TESBlockSlabBase extends BlockSlab {
	private final int subtypes;

	private Block singleSlab;
	private Block doubleSlab;

	protected TESBlockSlabBase(boolean hidden, Material material, int n) {
		super(hidden, material);
		subtypes = n;
		setCreativeTab(hidden ? null : TESCreativeTabs.TAB_BLOCK);
		useNeighborBrightness = true;
		if (material == Material.wood) {
			setHardness(2.0f);
			setResistance(5.0f);
			setStepSound(soundTypeWood);
		}
	}

	public static void registerSlabs(Block block, Block block1) {
		((TESBlockSlabBase) block).singleSlab = block;
		((TESBlockSlabBase) block).doubleSlab = block1;
		((TESBlockSlabBase) block1).singleSlab = block;
		((TESBlockSlabBase) block1).doubleSlab = block1;
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return (meta & 8) == 8 || isOpaqueCube();
	}

	@Override
	public ItemStack createStackedBlock(int i) {
		return new ItemStack(singleSlab, 2, i & 7);
	}

	@Override
	public String func_150002_b(int i) {
		return getUnlocalizedName() + '.' + i;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World world, int i, int j, int k) {
		return Item.getItemFromBlock(singleSlab);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(singleSlab);
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		if (item != Item.getItemFromBlock(doubleSlab)) {
			for (int j = 0; j < subtypes; ++j) {
				list.add(new ItemStack(item, 1, j));
			}
		}
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, int x, int y, int z, int side) {
		return (world.getBlockMetadata(x, y, z) & 8) == 8 && side == 1 || isOpaqueCube();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int i, int j, int k, int l) {
		if (this == doubleSlab) {
			return super.shouldSideBeRendered(world, i, j, k, l);
		}
		if (l != 1 && l != 0 && !super.shouldSideBeRendered(world, i, j, k, l)) {
			return false;
		}
		int i1 = i + Facing.offsetsXForSide[Facing.oppositeSide[l]];
		int j1 = j + Facing.offsetsYForSide[Facing.oppositeSide[l]];
		int k1 = k + Facing.offsetsZForSide[Facing.oppositeSide[l]];
		boolean hidden = (world.getBlockMetadata(i1, j1, k1) & 8) != 0;
		return hidden ? l == 0 || l == 1 && super.shouldSideBeRendered(world, i, j, k, l) || world.getBlock(i, j, k) != singleSlab || (world.getBlockMetadata(i, j, k) & 8) == 0 : l == 1 || l == 0 && super.shouldSideBeRendered(world, i, j, k, l) || world.getBlock(i, j, k) != singleSlab || (world.getBlockMetadata(i, j, k) & 8) != 0;
	}

	@SuppressWarnings({"EmptyClass", "unused"})
	public static class SlabItems {
		public static class BoneDouble extends ItemSlab {
			public BoneDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabBoneSingle, (BlockSlab) TESBlocks.slabBoneDouble, true);
			}
		}

		public static class BoneSingle extends ItemSlab {
			public BoneSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabBoneSingle, (BlockSlab) TESBlocks.slabBoneDouble, false);
			}
		}

		public static class ClayTileDouble extends ItemSlab {
			public ClayTileDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabClayTileSingle, (BlockSlab) TESBlocks.slabClayTileDouble, true);
			}
		}

		public static class ClayTileDyed2Double extends ItemSlab {
			public ClayTileDyed2Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabClayTileDyedSingle2, (BlockSlab) TESBlocks.slabClayTileDyedDouble2, true);
			}
		}

		public static class ClayTileDyed2Single extends ItemSlab {
			public ClayTileDyed2Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabClayTileDyedSingle2, (BlockSlab) TESBlocks.slabClayTileDyedDouble2, false);
			}
		}

		public static class ClayTileDyedDouble extends ItemSlab {
			public ClayTileDyedDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabClayTileDyedSingle1, (BlockSlab) TESBlocks.slabClayTileDyedDouble1, true);
			}
		}

		public static class ClayTileDyedSingle extends ItemSlab {
			public ClayTileDyedSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabClayTileDyedSingle1, (BlockSlab) TESBlocks.slabClayTileDyedDouble1, false);
			}
		}

		public static class ClayTileSingle extends ItemSlab {
			public ClayTileSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabClayTileSingle, (BlockSlab) TESBlocks.slabClayTileDouble, false);
			}
		}

		public static class DirtDouble extends ItemSlab {
			public DirtDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleDirt, (BlockSlab) TESBlocks.slabDoubleDirt, true);
			}
		}

		public static class DirtSingle extends ItemSlab {
			public DirtSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleDirt, (BlockSlab) TESBlocks.slabDoubleDirt, false);
			}
		}

		public static class GravelDouble extends ItemSlab {
			public GravelDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleGravel, (BlockSlab) TESBlocks.slabDoubleGravel, true);
			}
		}

		public static class GravelSingle extends ItemSlab {
			public GravelSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleGravel, (BlockSlab) TESBlocks.slabDoubleGravel, false);
			}
		}

		public static class RottenSlabDouble extends ItemSlab {
			public RottenSlabDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.rottenSlabSingle, (BlockSlab) TESBlocks.rottenSlabDouble, true);
			}
		}

		public static class RottenSlabSingle extends ItemSlab {
			public RottenSlabSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.rottenSlabSingle, (BlockSlab) TESBlocks.rottenSlabDouble, false);
			}
		}

		public static class SandDouble extends ItemSlab {
			public SandDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleSand, (BlockSlab) TESBlocks.slabDoubleSand, true);
			}
		}

		public static class SandSingle extends ItemSlab {
			public SandSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleSand, (BlockSlab) TESBlocks.slabDoubleSand, false);
			}
		}

		public static class ScorchedDouble extends ItemSlab {
			public ScorchedDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.scorchedSlabSingle, (BlockSlab) TESBlocks.scorchedSlabDouble, true);
			}
		}

		public static class ScorchedSingle extends ItemSlab {
			public ScorchedSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.scorchedSlabSingle, (BlockSlab) TESBlocks.scorchedSlabDouble, false);
			}
		}

		public static class Slab10Double extends ItemSlab {
			public Slab10Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle10, (BlockSlab) TESBlocks.slabDouble10, true);
			}
		}

		public static class Slab10Single extends ItemSlab {
			public Slab10Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle10, (BlockSlab) TESBlocks.slabDouble10, false);
			}
		}

		public static class Slab11Double extends ItemSlab {
			public Slab11Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle11, (BlockSlab) TESBlocks.slabDouble11, true);
			}
		}

		public static class Slab11Single extends ItemSlab {
			public Slab11Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle11, (BlockSlab) TESBlocks.slabDouble11, false);
			}
		}

		public static class Slab12Double extends ItemSlab {
			public Slab12Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle12, (BlockSlab) TESBlocks.slabDouble12, true);
			}
		}

		public static class Slab12Single extends ItemSlab {
			public Slab12Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle12, (BlockSlab) TESBlocks.slabDouble12, false);
			}
		}

		public static class Slab13Double extends ItemSlab {
			public Slab13Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle6, (BlockSlab) TESBlocks.slabDouble6, true);
			}
		}

		public static class Slab13Single extends ItemSlab {
			public Slab13Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle6, (BlockSlab) TESBlocks.slabDouble6, false);
			}
		}

		public static class Slab1Double extends ItemSlab {
			public Slab1Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle1, (BlockSlab) TESBlocks.slabDouble1, true);
			}
		}

		public static class Slab1Single extends ItemSlab {
			public Slab1Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle1, (BlockSlab) TESBlocks.slabDouble1, false);
			}
		}

		public static class Slab2Double extends ItemSlab {
			public Slab2Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle2, (BlockSlab) TESBlocks.slabDouble2, true);
			}
		}

		public static class Slab2Single extends ItemSlab {
			public Slab2Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle2, (BlockSlab) TESBlocks.slabDouble2, false);
			}
		}

		public static class Slab3Double extends ItemSlab {
			public Slab3Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle3, (BlockSlab) TESBlocks.slabDouble3, true);
			}
		}

		public static class Slab3Single extends ItemSlab {
			public Slab3Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle3, (BlockSlab) TESBlocks.slabDouble3, false);
			}
		}

		public static class Slab4Double extends ItemSlab {
			public Slab4Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle4, (BlockSlab) TESBlocks.slabDouble4, true);
			}
		}

		public static class Slab4Single extends ItemSlab {
			public Slab4Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle4, (BlockSlab) TESBlocks.slabDouble4, false);
			}
		}

		public static class Slab5Double extends ItemSlab {
			public Slab5Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle5, (BlockSlab) TESBlocks.slabDouble5, true);
			}
		}

		public static class Slab5Single extends ItemSlab {
			public Slab5Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle5, (BlockSlab) TESBlocks.slabDouble5, false);
			}
		}

		public static class Slab7Double extends ItemSlab {
			public Slab7Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle7, (BlockSlab) TESBlocks.slabDouble7, true);
			}
		}

		public static class Slab7Single extends ItemSlab {
			public Slab7Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle7, (BlockSlab) TESBlocks.slabDouble7, false);
			}
		}

		public static class Slab8Double extends ItemSlab {
			public Slab8Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle8, (BlockSlab) TESBlocks.slabDouble8, true);
			}
		}

		public static class Slab8Single extends ItemSlab {
			public Slab8Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle8, (BlockSlab) TESBlocks.slabDouble8, false);
			}
		}

		public static class Slab9Double extends ItemSlab {
			public Slab9Double(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle9, (BlockSlab) TESBlocks.slabDouble9, true);
			}
		}

		public static class Slab9Single extends ItemSlab {
			public Slab9Single(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingle9, (BlockSlab) TESBlocks.slabDouble9, false);
			}
		}

		public static class SlabVDouble extends ItemSlab {
			public SlabVDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleV, (BlockSlab) TESBlocks.slabDoubleV, true);
			}
		}

		public static class SlabVSingle extends ItemSlab {
			public SlabVSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleV, (BlockSlab) TESBlocks.slabDoubleV, false);
			}
		}

		public static class ThatchDouble extends ItemSlab {
			public ThatchDouble(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleThatch, (BlockSlab) TESBlocks.slabDoubleThatch, true);
			}
		}

		public static class ThatchSingle extends ItemSlab {
			public ThatchSingle(Block block) {
				super(block, (BlockSlab) TESBlocks.slabSingleThatch, (BlockSlab) TESBlocks.slabDoubleThatch, false);
			}
		}

		public static class WoodSlab1Double extends ItemSlab {
			public WoodSlab1Double(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle1, (BlockSlab) TESBlocks.woodSlabDouble1, true);
			}
		}

		public static class WoodSlab1Single extends ItemSlab {
			public WoodSlab1Single(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle1, (BlockSlab) TESBlocks.woodSlabDouble1, false);
			}
		}

		public static class WoodSlab2Double extends ItemSlab {
			public WoodSlab2Double(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle2, (BlockSlab) TESBlocks.woodSlabDouble2, true);
			}
		}

		public static class WoodSlab2Single extends ItemSlab {
			public WoodSlab2Single(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle2, (BlockSlab) TESBlocks.woodSlabDouble2, false);
			}
		}

		public static class WoodSlab3Double extends ItemSlab {
			public WoodSlab3Double(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle3, (BlockSlab) TESBlocks.woodSlabDouble3, true);
			}
		}

		public static class WoodSlab3Single extends ItemSlab {
			public WoodSlab3Single(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle3, (BlockSlab) TESBlocks.woodSlabDouble3, false);
			}
		}

		public static class WoodSlab4Double extends ItemSlab {
			public WoodSlab4Double(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle4, (BlockSlab) TESBlocks.woodSlabDouble4, true);
			}
		}

		public static class WoodSlab4Single extends ItemSlab {
			public WoodSlab4Single(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle4, (BlockSlab) TESBlocks.woodSlabDouble4, false);
			}
		}

		public static class WoodSlab5Double extends ItemSlab {
			public WoodSlab5Double(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle5, (BlockSlab) TESBlocks.woodSlabDouble5, true);
			}
		}

		public static class WoodSlab5Single extends ItemSlab {
			public WoodSlab5Single(Block block) {
				super(block, (BlockSlab) TESBlocks.woodSlabSingle5, (BlockSlab) TESBlocks.woodSlabDouble5, false);
			}
		}
	}
}