package tes.common.world.biome.other;

import tes.client.sound.TESMusicRegion;
import tes.common.database.TESAchievement;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.TESWaypoint;
import tes.common.world.spawning.TESEventSpawner;

public class TESBiomeRiver extends TESBiome {
	public TESBiomeRiver(int i, boolean major) {
		super(i, major);
		spawnableCreatureList.clear();
		npcSpawnList.clear();
		invasionSpawns.clearInvasions();
		banditChance = TESEventSpawner.EventChance.NEVER;
	}

	@Override
	public TESAchievement getBiomeAchievement() {
		return null;
	}

	@Override
	public TESMusicRegion.Sub getBiomeMusic() {
		return TESMusicRegion.OCEAN.getSubregion(biomeName);
	}

	@Override
	public TESWaypoint.Region getBiomeWaypoints() {
		return null;
	}
}