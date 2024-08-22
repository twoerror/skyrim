package tes.common.world.spawning;

import net.minecraft.world.biome.BiomeGenBase;

public class TESSpawnEntry extends BiomeGenBase.SpawnListEntry {
	public TESSpawnEntry(Class<?> c, int weight, int min, int max) {
		super(c, weight, min, max);
	}

	public static class Instance {
		private final TESSpawnEntry spawnEntry;
		private final int spawnChance;
		private final boolean isConquestSpawn;

		public Instance(TESSpawnEntry entry, int chance, boolean conquest) {
			spawnEntry = entry;
			spawnChance = chance;
			isConquestSpawn = conquest;
		}

		public TESSpawnEntry getSpawnEntry() {
			return spawnEntry;
		}

		public int getSpawnChance() {
			return spawnChance;
		}

		public boolean isConquestSpawn() {
			return isConquestSpawn;
		}
	}
}