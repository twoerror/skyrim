package tes.common.world.feature;

import tes.common.database.TESBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.gen.feature.*;

import java.util.Random;

public enum TESTreeType {
	OAK((flag, rand) -> {
		if (rand.nextInt(4) == 0) {
			return new TESWorldGenGnarledOak(flag);
		}
		return new TESWorldGenSimpleTrees(flag, 4, 6, Blocks.log, 0, Blocks.leaves, 0);
	}), OAK_TALL((flag, rand) -> {
		if (rand.nextInt(4) == 0) {
			return new TESWorldGenGnarledOak(flag).setMinMaxHeight(6, 10);
		}
		return new TESWorldGenSimpleTrees(flag, 8, 12, Blocks.log, 0, Blocks.leaves, 0);
	}), OAK_TALLER((flag, rand) -> new TESWorldGenSimpleTrees(flag, 12, 16, Blocks.log, 0, Blocks.leaves, 0)), OAK_LARGE((flag, rand) -> new TESWorldGenBigTrees(flag, Blocks.log, 0, Blocks.leaves)), OAK_PARTY((flag, rand) -> new TESWorldGenPartyTrees(Blocks.log, 0, Blocks.leaves, 0)), OAK_GIANT((flag, rand) -> new TESWorldGenGiantTrees(flag, Blocks.log, 0, Blocks.leaves, 0)), WEIRWOOD((flag, rand) -> new TESWorldGenPartyTrees(TESBlocks.wood9, 2, TESBlocks.leaves9, 2)), OAK_SWAMP((flag, rand) -> new WorldGenSwamp()), OAK_DEAD((flag, rand) -> new TESWorldGenDeadTrees(Blocks.log, 0)), OAK_DESERT((flag, rand) -> new TESWorldGenDesertTrees(flag, Blocks.log, 0, Blocks.leaves, 0)), BIRCH((flag, rand) -> {
		if (rand.nextInt(3) != 0) {
			return new TESWorldGenAspen(flag).setBlocks(Blocks.log, 2, Blocks.leaves, 2).setMinMaxHeight(8, 16);
		}
		return new TESWorldGenSimpleTrees(flag, 5, 7, Blocks.log, 2, Blocks.leaves, 2);
	}), BIRCH_TALL((flag, rand) -> new TESWorldGenSimpleTrees(flag, 8, 11, Blocks.log, 2, Blocks.leaves, 2)), BIRCH_LARGE((flag, rand) -> new TESWorldGenBigTrees(flag, Blocks.log, 2, Blocks.leaves)), BIRCH_PARTY((flag, rand) -> new TESWorldGenPartyTrees(Blocks.log, 2, Blocks.leaves, 2)), BIRCH_DEAD((flag, rand) -> new TESWorldGenDeadTrees(Blocks.log, 2)), SPRUCE((flag, rand) -> new WorldGenTaiga2(flag)), ARAMANT((flag, rand) -> new TESWorldGenSimpleTrees(flag, 5, 9, TESBlocks.wood2, 0, TESBlocks.leaves2, 0)), SPRUCE_THIN((flag, rand) -> new WorldGenTaiga1()), SPRUCE_MEGA((flag, rand) -> new WorldGenMegaPineTree(flag, true)), SPRUCE_MEGA_THIN((flag, rand) -> new WorldGenMegaPineTree(flag, false)), SPRUCE_DEAD((flag, rand) -> new TESWorldGenDeadTrees(Blocks.log, 1)), JUNGLE((flag, rand) -> new TESWorldGenTropical(flag)), JUNGLE_LARGE((flag, rand) -> new TESWorldGenTropical(flag).setExtraTrunkWidth(1)), JUNGLE_SHRUB((flag, rand) -> new TESWorldGenShrub(Blocks.log, 3, Blocks.leaves, 3)), ACACIA((flag, rand) -> new WorldGenSavannaTree(flag)), ACACIA_DEAD((flag, rand) -> new TESWorldGenDeadTrees(Blocks.log2, 0)), DARK_OAK((flag, rand) -> new WorldGenCanopyTree(flag)), DARK_OAK_PARTY((flag, rand) -> new TESWorldGenPartyTrees(Blocks.log2, 1, Blocks.leaves2, 1)), CATALPA((flag, rand) -> new TESWorldGenSimpleTrees(flag, 6, 9, TESBlocks.wood1, 1, TESBlocks.leaves1, 1)), CATALPA_BOUGHS((flag, rand) -> new TESWorldGenCatalpa(flag)), CATALPA_PARTY((flag, rand) -> new TESWorldGenPartyTrees(TESBlocks.wood1, 1, TESBlocks.leaves1, 1)), ULTHOS_OAK((flag, rand) -> new TESWorldGenUlthosOak(flag, 4, 7, 0, true)), ULTHOS_OAK_LARGE((flag, rand) -> new TESWorldGenUlthosOak(flag, 12, 16, 1, true)), ULTHOS_OAK_DEAD((flag, rand) -> new TESWorldGenUlthosOak(flag, 4, 7, 0, true).setDead()), ULTHOS_RED_OAK((flag, rand) -> new TESWorldGenUlthosOak(flag, 6, 9, 0, false).setRedOak()), ULTHOS_RED_OAK_LARGE((flag, rand) -> new TESWorldGenUlthosOak(flag, 12, 17, 1, false).setRedOak()), CHARRED((flag, rand) -> new TESWorldGenCharredTrees()), APPLE((flag, rand) -> new TESWorldGenSimpleTrees(flag, 4, 7, TESBlocks.fruitWood, 0, TESBlocks.fruitLeaves, 0)), PEAR((flag, rand) -> new TESWorldGenSimpleTrees(flag, 4, 5, TESBlocks.fruitWood, 1, TESBlocks.fruitLeaves, 1)), CHERRY((flag, rand) -> new TESWorldGenSimpleTrees(flag, 4, 8, TESBlocks.fruitWood, 2, TESBlocks.fruitLeaves, 2)), MANGO((flag, rand) -> {
		if (rand.nextInt(3) == 0) {
			return new TESWorldGenOlive(flag).setBlocks(TESBlocks.fruitWood, 3, TESBlocks.fruitLeaves, 3);
		}
		return new TESWorldGenDesertTrees(flag, TESBlocks.fruitWood, 3, TESBlocks.fruitLeaves, 3);
	}), BEECH((flag, rand) -> new TESWorldGenSimpleTrees(flag, 5, 9, TESBlocks.wood2, 1, TESBlocks.leaves2, 1)), BEECH_LARGE((flag, rand) -> new TESWorldGenBigTrees(flag, TESBlocks.wood2, 1, TESBlocks.leaves2)), BEECH_PARTY((flag, rand) -> new TESWorldGenPartyTrees(TESBlocks.wood2, 1, TESBlocks.leaves2, 1)), BEECH_DEAD((flag, rand) -> new TESWorldGenDeadTrees(TESBlocks.wood2, 1)), HOLLY((flag, rand) -> new TESWorldGenHolly(flag)), HOLLY_LARGE((flag, rand) -> new TESWorldGenHolly(flag).setLarge()), BANANA((flag, rand) -> new TESWorldGenBanana(flag)), MAPLE((flag, rand) -> new TESWorldGenSimpleTrees(flag, 4, 8, TESBlocks.wood3, 0, TESBlocks.leaves3, 0)), MAPLE_LARGE((flag, rand) -> new TESWorldGenBigTrees(flag, TESBlocks.wood3, 0, TESBlocks.leaves3)), MAPLE_PARTY((flag, rand) -> new TESWorldGenPartyTrees(TESBlocks.wood3, 0, TESBlocks.leaves3, 0)), LARCH((flag, rand) -> new TESWorldGenLarch(flag)), DATE_PALM((flag, rand) -> new TESWorldGenPalm(flag, TESBlocks.wood3, 2, TESBlocks.leaves3, 2).setMinMaxHeight(5, 8).setDates()), MANGROVE((flag, rand) -> new TESWorldGenMangrove(flag)), CHESTNUT((flag, rand) -> new TESWorldGenSimpleTrees(flag, 5, 7, TESBlocks.wood4, 0, TESBlocks.leaves4, 0)), CHESTNUT_LARGE((flag, rand) -> new TESWorldGenBigTrees(flag, TESBlocks.wood4, 0, TESBlocks.leaves4)), CHESTNUT_PARTY((flag, rand) -> new TESWorldGenPartyTrees(TESBlocks.wood4, 0, TESBlocks.leaves4, 0)), BAOBAB((flag, rand) -> new TESWorldGenBaobab(flag)), CEDAR((flag, rand) -> new TESWorldGenCedar(flag)), CEDAR_LARGE((flag, rand) -> new TESWorldGenCedar(flag).setMinMaxHeight(15, 30)), FIR((flag, rand) -> new TESWorldGenFir(flag)), PINE((flag, rand) -> new TESWorldGenPine(flag)), IBBEN_PINE((flag, rand) -> new TESWorldGenIbbenPine(flag)), FOTINIA((flag, rand) -> new TESWorldGenFotinia(flag)), LEMON((flag, rand) -> new TESWorldGenSimpleTrees(flag, 6, 9, TESBlocks.wood5, 1, TESBlocks.leaves5, 1)), ORANGE((flag, rand) -> new TESWorldGenSimpleTrees(flag, 6, 9, TESBlocks.wood5, 2, TESBlocks.leaves5, 2)), LIME((flag, rand) -> new TESWorldGenSimpleTrees(flag, 6, 9, TESBlocks.wood5, 3, TESBlocks.leaves5, 3)), MAHOGANY((flag, rand) -> new TESWorldGenCedar(flag).setBlocks(TESBlocks.wood6, 0, TESBlocks.leaves6, 0).setHangingLeaves()), WILLOW((flag, rand) -> new TESWorldGenWillow(flag)), WILLOW_WATER((flag, rand) -> new TESWorldGenWillow(flag).setNeedsWater()), CYPRESS((flag, rand) -> new TESWorldGenCypress(flag)), CYPRESS_LARGE((flag, rand) -> new TESWorldGenCypress(flag).setLarge()), OLIVE((flag, rand) -> new TESWorldGenOlive(flag)), OLIVE_LARGE((flag, rand) -> new TESWorldGenOlive(flag).setMinMaxHeight(5, 8).setExtraTrunkWidth(1)), ASPEN((flag, rand) -> new TESWorldGenAspen(flag)), ASPEN_LARGE((flag, rand) -> new TESWorldGenAspen(flag).setExtraTrunkWidth(1).setMinMaxHeight(14, 25)), ULTHOS_GREEN_OAK((flag, rand) -> new TESWorldGenUlthosOak(flag, 4, 7, 0, false).setGreenOak()), ULTHOS_GREEN_OAK_LARGE((flag, rand) -> new TESWorldGenUlthosOak(flag, 12, 16, 1, false).setGreenOak()), ALMOND((flag, rand) -> new TESWorldGenAlmond(flag)), PLUM((flag, rand) -> new TESWorldGenSimpleTrees(flag, 4, 6, TESBlocks.wood8, 0, TESBlocks.leaves8, 0)), REDWOOD((flag, rand) -> new TESWorldGenRedwood(flag)), REDWOOD_2((flag, rand) -> new TESWorldGenRedwood(flag).setExtraTrunkWidth(1)), REDWOOD_3((flag, rand) -> new TESWorldGenRedwood(flag).setTrunkWidth(1)), REDWOOD_4((flag, rand) -> new TESWorldGenRedwood(flag).setTrunkWidth(1).setExtraTrunkWidth(1)), REDWOOD_5((flag, rand) -> new TESWorldGenRedwood(flag).setTrunkWidth(2)), POMEGRANATE((flag, rand) -> {
		if (rand.nextInt(3) == 0) {
			return new TESWorldGenOlive(flag).setBlocks(TESBlocks.wood8, 2, TESBlocks.leaves8, 2);
		}
		return new TESWorldGenDesertTrees(flag, TESBlocks.wood8, 2, TESBlocks.leaves8, 2);
	}), PALM((flag, rand) -> new TESWorldGenPalm(flag, TESBlocks.wood8, 3, TESBlocks.leaves8, 3).setMinMaxHeight(6, 11)), DRAGONBLOOD((flag, rand) -> new TESWorldGenDragonblood(flag, 3, 7, 0)), DRAGONBLOOD_LARGE((flag, rand) -> new TESWorldGenDragonblood(flag, 6, 10, 1)), KANUKA((flag, rand) -> new TESWorldGenKanuka(flag));

	private final ITreeFactory treeFactory;

	TESTreeType(ITreeFactory factory) {
		treeFactory = factory;
	}

	public WorldGenAbstractTree create(boolean flag, Random rand) {
		return treeFactory.createTree(flag, rand);
	}

	public interface ITreeFactory {
		WorldGenAbstractTree createTree(boolean var1, Random var2);
	}

	public static class WeightedTreeType extends WeightedRandom.Item {
		private final TESTreeType treeType;

		public WeightedTreeType(TESTreeType tree, int i) {
			super(i);
			treeType = tree;
		}

		public TESTreeType getTreeType() {
			return treeType;
		}
	}
}