package tes.common.world.biome.other;

import tes.client.sound.TESMusicRegion;
import tes.common.database.TESAchievement;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.TESWaypoint;
import tes.common.world.spawning.TESEventSpawner;

public class TESBiomeLake extends TESBiome {
	public TESBiomeLake(int i, boolean major) {
		super(i, major);
		setMinMaxHeight(-0.5f, 0.2f);
		spawnableCreatureList.clear();
		spawnableTESAmbientList.clear();
		npcSpawnList.clear();
		decorator.setSandPerChunk(0);
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

	@Override
	public boolean getEnableRiver() {
		return false;
	}
}