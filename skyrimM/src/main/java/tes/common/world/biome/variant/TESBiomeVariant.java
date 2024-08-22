package tes.common.world.biome.variant;

import tes.common.world.feature.TESTreeType;
import net.minecraft.block.Block;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class TESBiomeVariant {
	private static final TESBiomeVariant[] ALL_VARIANTS = new TESBiomeVariant[256];

	public static final TESBiomeVariant STANDARD = new TESBiomeVariant(0, "standard");
	public static final TESBiomeVariant FLOWERS = new TESBiomeVariant(1, "flowers").setFlowers(10.0f);
	public static final TESBiomeVariant FOREST = new TESBiomeVariantForest(2, "forest");
	public static final TESBiomeVariant FOREST_LIGHT = new TESBiomeVariant(3, "forest_light").setTrees(3.0f).setGrass(2.0f);
	public static final TESBiomeVariant STEPPE = new TESBiomeVariant(4, "standard").setHeight(0.0f, 0.1f).setTrees(0.0f);
	public static final TESBiomeVariant HILLS = new TESBiomeVariant(6, "hills").setHeight(0.5f, 1.5f).setGrass(0.5f);
	public static final TESBiomeVariant HILLS_FOREST = new TESBiomeVariant(7, "hills_forest").setHeight(0.5f, 1.5f).setTrees(3.0f);
	public static final TESBiomeVariant MOUNTAIN = new TESBiomeVariant(8, "mountain").setHeight(1.2f, 3.0f);
	public static final TESBiomeVariant CLEARING = new TESBiomeVariant(9, "clearing").setHeight(0.0f, 0.5f).setTrees(0.0f).setGrass(2.0f).setFlowers(3.0f);
	public static final TESBiomeVariant SWAMP_LOWLAND = new TESBiomeVariant(18, "swampLowland").setHeight(-0.12f, 0.2f).setTrees(0.5f).setGrass(5.0f).setMarsh();
	public static final TESBiomeVariant SAVANNAH_BAOBAB = new TESBiomeVariant(20, "savannahBaobab").setHeight(0.0f, 0.5f).setTrees(1.5f).setGrass(0.5f).addTreeTypes(0.6f, TESTreeType.BAOBAB, 100);
	public static final TESBiomeVariant LAKE = new TESBiomeVariant(21, "lake").setAbsoluteHeight(-0.5f, 0.05f);
	public static final TESBiomeVariant VINEYARD = new TESBiomeVariant(26, "vineyard").setHeight(0.0f, 0.0f).setTrees(0.0f).setGrass(0.0f).setFlowers(0.0f).disableStructuresSettlements();
	public static final TESBiomeVariant FOREST_ASPEN = new TESBiomeVariantForest(27, "forest_aspen").addTreeTypes(0.8f, TESTreeType.ASPEN, 1000, TESTreeType.ASPEN_LARGE, 50);
	public static final TESBiomeVariant FOREST_BIRCH = new TESBiomeVariantForest(28, "forest_birch").addTreeTypes(0.8f, TESTreeType.BIRCH, 1000, TESTreeType.BIRCH_LARGE, 150);
	public static final TESBiomeVariant FOREST_BEECH = new TESBiomeVariantForest(29, "forest_beech").addTreeTypes(0.8f, TESTreeType.BEECH, 1000, TESTreeType.BEECH_LARGE, 150);
	public static final TESBiomeVariant FOREST_MAPLE = new TESBiomeVariantForest(30, "forest_maple").addTreeTypes(0.8f, TESTreeType.MAPLE, 1000, TESTreeType.MAPLE_LARGE, 150);
	public static final TESBiomeVariant FOREST_LARCH = new TESBiomeVariantForest(31, "forest_larch").addTreeTypes(0.8f, TESTreeType.LARCH, 1000);
	public static final TESBiomeVariant FOREST_PINE = new TESBiomeVariantForest(32, "forest_pine").addTreeTypes(0.8f, TESTreeType.PINE, 1000);
	public static final TESBiomeVariant ORCHARD_APPLE_PEAR = new TESBiomeVariantOrchard(34, "orchard_apple_pear").addTreeTypes(1.0f, TESTreeType.APPLE, 100, TESTreeType.PEAR, 100);
	public static final TESBiomeVariant ORCHARD_ORANGE = new TESBiomeVariantOrchard(35, "orchard_orange").addTreeTypes(1.0f, TESTreeType.ORANGE, 100);
	public static final TESBiomeVariant ORCHARD_LEMON = new TESBiomeVariantOrchard(36, "orchard_lemon").addTreeTypes(1.0f, TESTreeType.LEMON, 100);
	public static final TESBiomeVariant ORCHARD_LIME = new TESBiomeVariantOrchard(37, "orchard_lime").addTreeTypes(1.0f, TESTreeType.LIME, 100);
	public static final TESBiomeVariant ORCHARD_ALMOND = new TESBiomeVariantOrchard(38, "orchard_almond").addTreeTypes(1.0f, TESTreeType.ALMOND, 100);
	public static final TESBiomeVariant ORCHARD_OLIVE = new TESBiomeVariantOrchard(39, "orchard_olive").addTreeTypes(1.0f, TESTreeType.OLIVE, 100);
	public static final TESBiomeVariant ORCHARD_PLUM = new TESBiomeVariantOrchard(40, "orchard_plum").addTreeTypes(1.0f, TESTreeType.PLUM, 100);
	public static final TESBiomeVariant RIVER = new TESBiomeVariant(41, "river").setAbsoluteHeight(-0.5f, 0.05f);
	public static final TESBiomeVariant ORCHARD_DATE = new TESBiomeVariantOrchard(45, "orchard_date").addTreeTypes(1.0f, TESTreeType.DATE_PALM, 100);
	public static final TESBiomeVariant ORCHARD_POMEGRANATE = new TESBiomeVariantOrchard(47, "orchard_pomegranate").addTreeTypes(1.0f, TESTreeType.POMEGRANATE, 100);
	public static final TESBiomeVariant FOREST_CHERRY = new TESBiomeVariantForest(52, "forest_cherry").addTreeTypes(0.8f, TESTreeType.CHERRY, 1000);
	public static final TESBiomeVariant FOREST_LEMON = new TESBiomeVariantForest(53, "forest_lemon").addTreeTypes(0.8f, TESTreeType.LEMON, 1000);
	public static final TESBiomeVariant FOREST_LIME = new TESBiomeVariantForest(54, "forest_lime").addTreeTypes(0.8f, TESTreeType.LIME, 1000);
	public static final TESBiomeVariant FOREST_CEDAR = new TESBiomeVariantForest(60, "forest_cedar").addTreeTypes(0.8f, TESTreeType.CEDAR, 1000, TESTreeType.CEDAR_LARGE, 50);
	public static final TESBiomeVariant FOREST_CYPRESS = new TESBiomeVariantForest(61, "forest_cypress").addTreeTypes(0.8f, TESTreeType.CYPRESS, 1000, TESTreeType.CYPRESS_LARGE, 50);
	public static final NoiseGeneratorPerlin MARSH_NOISE = new NoiseGeneratorPerlin(new Random(444L), 1);

	private final int variantID;
	private final String variantName;
	private final Collection<TESTreeType.WeightedTreeType> treeTypes = new ArrayList<>();

	private float heightBoost;
	private float variantTreeChance;
	private float absoluteHeightLevel;
	private float hillFactor = 1.0f;
	private float treeFactor = 1.0f;
	private float grassFactor = 1.0f;
	private float flowerFactor = 1.0f;
	private boolean hasMarsh;
	private boolean absoluteHeight;
	private boolean disableStructures;
	private boolean disableSettlements;

	protected TESBiomeVariant(int i, String s) {
		if (ALL_VARIANTS[i] != null) {
			throw new IllegalArgumentException("tes Biome variant already exists at index " + i);
		}
		variantID = i;
		ALL_VARIANTS[i] = this;
		variantName = s;
	}

	public static TESBiomeVariant getVariantForID(int i) {
		TESBiomeVariant variant = ALL_VARIANTS[i];
		if (variant == null) {
			return STANDARD;
		}
		return variant;
	}

	protected TESBiomeVariant addTreeTypes(float f, Object... trees) {
		variantTreeChance = f;
		for (int i = 0; i < trees.length / 2; ++i) {
			Object obj1 = trees[i * 2];
			Object obj2 = trees[i * 2 + 1];
			treeTypes.add(new TESTreeType.WeightedTreeType((TESTreeType) obj1, (Integer) obj2));
		}
		return this;
	}

	public void decorateVariant(World world, Random random, int i, int k) {
	}

	protected void disableSettlements() {
		disableSettlements = true;
	}

	private TESBiomeVariant disableStructuresSettlements() {
		disableStructures = true;
		disableSettlements = true;
		return this;
	}

	public void generateVariantTerrain(Block[] blocks, byte[] meta, int i, int k) {
	}

	public float getHeightBoost() {
		return heightBoost;
	}

	public TESTreeType getRandomTree(Random random) {
		WeightedRandom.Item item = WeightedRandom.getRandomItem(random, treeTypes);
		return ((TESTreeType.WeightedTreeType) item).getTreeType();
	}

	public String getUnlocalizedName() {
		return "tes.variant." + variantName + ".name";
	}

	private TESBiomeVariant setAbsoluteHeight(float height, float hills) {
		absoluteHeight = true;
		absoluteHeightLevel = height;
		float f = height;
		f -= 2.0f;
		heightBoost = f + 0.2f;
		hillFactor = hills;
		return this;
	}

	private TESBiomeVariant setFlowers(float f) {
		flowerFactor = f;
		return this;
	}

	protected TESBiomeVariant setGrass(float f) {
		grassFactor = f;
		return this;
	}

	protected TESBiomeVariant setHeight(float height, float hills) {
		heightBoost = height;
		hillFactor = hills;
		return this;
	}

	private TESBiomeVariant setMarsh() {
		hasMarsh = true;
		return this;
	}

	protected TESBiomeVariant setTrees(float f) {
		treeFactor = f;
		return this;
	}

	public int getVariantID() {
		return variantID;
	}

	public String getVariantName() {
		return variantName;
	}

	public boolean isAbsoluteHeight() {
		return absoluteHeight;
	}

	public float getAbsoluteHeightLevel() {
		return absoluteHeightLevel;
	}

	public float getHillFactor() {
		return hillFactor;
	}

	public float getTreeFactor() {
		return treeFactor;
	}

	public float getGrassFactor() {
		return grassFactor;
	}

	public float getFlowerFactor() {
		return flowerFactor;
	}

	public boolean isHasMarsh() {
		return hasMarsh;
	}

	public boolean isDisableStructures() {
		return disableStructures;
	}

	public boolean isDisableSettlements() {
		return disableSettlements;
	}

	public Collection<TESTreeType.WeightedTreeType> getTreeTypes() {
		return treeTypes;
	}

	public float getVariantTreeChance() {
		return variantTreeChance;
	}
}