package tes.common.world.biome.variant;

public class TESBiomeVariantForest extends TESBiomeVariant {
	public TESBiomeVariantForest(int i, String s) {
		super(i, s);
		setTrees(8.0f);
		setGrass(2.0f);
		disableSettlements();
	}
}