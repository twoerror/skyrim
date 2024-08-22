package tes.client.sound;

import net.minecraft.world.World;

public enum TESMusicCategory {
	DAY("day"), NIGHT("night"), CAVE("cave");

	private final String categoryName;

	TESMusicCategory(String s) {
		categoryName = s;
	}

	public static TESMusicCategory forName(String s) {
		for (TESMusicCategory cat : values()) {
			if (!s.equalsIgnoreCase(cat.categoryName)) {
				continue;
			}
			return cat;
		}
		return null;
	}

	public static boolean isCave(World world, int i, int j, int k) {
		return j < 50 && !world.canBlockSeeTheSky(i, j, k);
	}

	public static boolean isDay(World world) {
		return world.calculateSkylightSubtracted(1.0f) < 5;
	}

	public String getCategoryName() {
		return categoryName;
	}
}