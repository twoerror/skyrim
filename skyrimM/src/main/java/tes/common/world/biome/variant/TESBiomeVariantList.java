package tes.common.world.biome.variant;

import java.util.ArrayList;
import java.util.Collection;

public class TESBiomeVariantList {
	private final Collection<VariantBucket> variantList = new ArrayList<>();

	private float totalWeight;

	public void add(TESBiomeVariant v, float f) {
		variantList.add(new VariantBucket(v, totalWeight, totalWeight + f));
		totalWeight += f;
	}

	public void clear() {
		totalWeight = 0.0f;
		variantList.clear();
	}

	public TESBiomeVariant get(float index) {
		float v = index;
		if (v < 0.0f) {
			v = 0.0f;
		}
		if (v >= 1.0f) {
			v = 0.9999f;
		}
		float f = v * totalWeight;
		for (VariantBucket bucket : variantList) {
			if (f < bucket.getMin() || f >= bucket.getMax()) {
				continue;
			}
			return bucket.getVariant();
		}
		return null;
	}

	public boolean isEmpty() {
		return totalWeight == 0.0f;
	}

	public Collection<VariantBucket> getVariantList() {
		return variantList;
	}

	public static class VariantBucket {
		private final TESBiomeVariant variant;
		private final float min;
		private final float max;

		private VariantBucket(TESBiomeVariant v, float f0, float f1) {
			variant = v;
			min = f0;
			max = f1;
		}

		public TESBiomeVariant getVariant() {
			return variant;
		}

		private float getMin() {
			return min;
		}

		private float getMax() {
			return max;
		}
	}
}