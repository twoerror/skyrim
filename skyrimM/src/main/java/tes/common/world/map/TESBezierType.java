package tes.common.world.map;

import tes.common.database.TESBlocks;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public abstract class TESBezierType {
	public static final TESBezierType WALL_IBBEN = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes = {new BezierBlock(TESBlocks.woodBeamV1, 1), new BezierBlock(TESBlocks.wood4, 2), new BezierBlock(TESBlocks.woodBeam4, 2)};
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType WALL_ICE = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes = {new BezierBlock(TESBlocks.brickIce, 0), new BezierBlock(TESBlocks.brickIce, 0), new BezierBlock(TESBlocks.brickIce, 0), new BezierBlock(TESBlocks.brickIce, 0), new BezierBlock(Blocks.packed_ice, 0), new BezierBlock(Blocks.packed_ice, 0), new BezierBlock(Blocks.snow, 0)};
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType WALL_YITI = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			return new BezierBlock(TESBlocks.cobblebrick, 0);
		}
	};

	public static final TESBezierType PATH_ASSHAI = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			if (slab) {
				return new BezierBlock(TESBlocks.slabSingleDirt, 3);
			}
			return new BezierBlock(TESBlocks.asshaiDirt, 0);
		}
	};

	public static final TESBezierType PATH_COBBLE = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes;
			if (slab) {
				blockTypes = new BezierBlock[]{new BezierBlock(Blocks.stone_slab, 5), new BezierBlock(Blocks.stone_slab, 3), new BezierBlock(Blocks.stone_slab, 3), new BezierBlock(TESBlocks.slabSingleV, 4)};
			} else {
				blockTypes = new BezierBlock[]{new BezierBlock(Blocks.stonebrick, 0), new BezierBlock(Blocks.cobblestone, 0), new BezierBlock(Blocks.cobblestone, 0), new BezierBlock(Blocks.mossy_cobblestone, 0)};
			}
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType PATH_DIRTY = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes;
			if (slab) {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.slabSingleDirt, 1), new BezierBlock(TESBlocks.slabSingleDirt, 0), new BezierBlock(TESBlocks.slabSingleGravel, 0)};
			} else {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.dirtPath, 0), new BezierBlock(Blocks.dirt, 1), new BezierBlock(Blocks.gravel, 0)};
			}
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType PATH_PAVING = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			if (slab) {
				return new BezierBlock(TESBlocks.slabSingleDirt, 5);
			}
			return new BezierBlock(TESBlocks.dirtPath, 2);
		}
	};

	public static final TESBezierType PATH_SANDY = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes;
			if (slab) {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.slabSingleDirt, 0), new BezierBlock(TESBlocks.slabSingleDirt, 1), new BezierBlock(TESBlocks.slabSingleSand, 0), new BezierBlock(TESBlocks.slabSingle4, 0), new BezierBlock(TESBlocks.slabSingle7, 1), new BezierBlock(TESBlocks.slabSingle4, 7)};
			} else {
				blockTypes = new BezierBlock[]{new BezierBlock(Blocks.dirt, 1), new BezierBlock(TESBlocks.dirtPath, 0), new BezierBlock(top ? Blocks.sand : Blocks.sandstone, 0), new BezierBlock(TESBlocks.brick1, 15), new BezierBlock(TESBlocks.brick3, 11), new BezierBlock(TESBlocks.pillar1, 5)};
			}
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType PATH_SNOWY = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			return new BezierBlock(Blocks.snow, 0);
		}
	};

	public static final TESBezierType PATH_SOTHORYOS = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes;
			if (slab) {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.slabSingle8, 0), new BezierBlock(TESBlocks.slabSingle8, 1), new BezierBlock(TESBlocks.slabSingle8, 2), new BezierBlock(TESBlocks.slabSingle8, rand.nextBoolean() ? 1 : 2)};
			} else {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.brick4, 0), new BezierBlock(TESBlocks.brick4, 1), new BezierBlock(TESBlocks.brick4, 2), new BezierBlock(TESBlocks.brick4, rand.nextBoolean() ? 1 : 2)};
			}
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType TOWN_ASSHAI = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes;
			if (slab) {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.slabSingleDirt, 3)};
			} else {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.basaltGravel, 0), new BezierBlock(TESBlocks.asshaiDirt, 0)};
			}
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	public static final TESBezierType TOWN_YITI = new TESBezierType() {
		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			BezierBlock[] blockTypes;
			if (slab) {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.slabSingle12, 0), new BezierBlock(TESBlocks.slabSingle12, 0), new BezierBlock(TESBlocks.slabSingle12, 0), new BezierBlock(TESBlocks.slabSingle12, 1), new BezierBlock(TESBlocks.slabSingle12, 2)};
			} else {
				blockTypes = new BezierBlock[]{new BezierBlock(TESBlocks.brick5, 11), new BezierBlock(TESBlocks.brick5, 11), new BezierBlock(TESBlocks.brick5, 11), new BezierBlock(TESBlocks.brick5, 13), new BezierBlock(TESBlocks.brick5, 14)};
			}
			return blockTypes[rand.nextInt(blockTypes.length)];
		}
	};

	@SuppressWarnings("WeakerAccess")
	protected TESBezierType() {
	}

	public abstract BezierBlock getBlock(Random var1, BiomeGenBase var2, boolean var3, boolean var4);

	public float getRepair() {
		return 1.0f;
	}

	public boolean hasFlowers() {
		return false;
	}

	public TESBezierType setHasFlowers(boolean flag) {
		TESBezierType baseRoad = this;
		return new CustomBezierType(baseRoad, flag);
	}

	@SuppressWarnings("AbstractClassWithOnlyOneDirectInheritor")
	public abstract static class BridgeType {
		public static final BridgeType DEFAULT = new BridgeType() {

			@Override
			public BezierBlock getBlock(boolean slab) {
				if (slab) {
					return new BezierBlock(Blocks.wooden_slab, 0);
				}
				return new BezierBlock(Blocks.planks, 0);
			}

			@Override
			public BezierBlock getEdge() {
				return new BezierBlock(TESBlocks.woodBeamV1, 0);
			}

			@Override
			public BezierBlock getFence() {
				return new BezierBlock(Blocks.fence, 0);
			}
		};

		abstract BezierBlock getBlock(boolean var2);

		abstract BezierBlock getEdge();

		abstract BezierBlock getFence();
	}

	public static class BezierBlock {
		private final Block block;
		private final int meta;

		@SuppressWarnings("WeakerAccess")
		public BezierBlock(Block b, int i) {
			block = b;
			meta = i;
		}

		public Block getBlock() {
			return block;
		}

		public int getMeta() {
			return meta;
		}
	}

	private static class CustomBezierType extends TESBezierType {
		private final TESBezierType baseRoad;
		private final boolean flag;

		private CustomBezierType(TESBezierType baseRoad, boolean flag) {
			this.baseRoad = baseRoad;
			this.flag = flag;
		}

		@Override
		public BezierBlock getBlock(Random rand, BiomeGenBase biome, boolean top, boolean slab) {
			return baseRoad.getBlock(rand, biome, top, slab);
		}

		@Override
		public float getRepair() {
			return baseRoad.getRepair();
		}

		@Override
		public boolean hasFlowers() {
			return flag;
		}
	}
}