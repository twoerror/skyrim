package tes.common.world.spawning;

import tes.common.database.TESSpawnList;

public class TESSpawnListContainer {
	private final TESSpawnList spawnList;
	private final int weight;

	private int spawnChance;

	TESSpawnListContainer(TESSpawnList list, int w) {
		spawnList = list;
		weight = w;
	}

	public static boolean canSpawnAtConquestLevel(float conq) {
		return conq > -1.0f;
	}

	public int getWeight() {
		return weight;
	}

	public TESSpawnList getSpawnList() {
		return spawnList;
	}

	public int getSpawnChance() {
		return spawnChance;
	}

	public TESSpawnListContainer setSpawnChance(int i) {
		spawnChance = i;
		return this;
	}
}