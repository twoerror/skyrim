package tes.common.world.spawning;

import tes.common.database.TESSpawnList;
import tes.common.faction.TESFaction;
import tes.common.world.map.TESConquestGrid;
import tes.common.world.map.TESConquestZone;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class TESFactionContainer {
	private final Collection<TESSpawnListContainer> spawnListContainers = new ArrayList<>();
	private final TESBiomeSpawnList parent;
	private final int baseWeight;

	private TESFaction theFaction;
	private float conquestSensitivity = 1.0f;

	public TESFactionContainer(TESBiomeSpawnList biomeList, int w) {
		parent = biomeList;
		baseWeight = w;
	}

	public void add(Collection<TESSpawnListContainer> list) {
		spawnListContainers.addAll(list);
	}

	public void determineFaction(World world) {
		if (theFaction == null) {
			for (TESSpawnListContainer cont : spawnListContainers) {
				TESSpawnList list = cont.getSpawnList();
				TESFaction fac = list.getListCommonFaction(world);
				if (theFaction == null) {
					theFaction = fac;
					continue;
				}
				if (fac == theFaction) {
					continue;
				}
				throw new IllegalArgumentException("Faction containers must include spawn lists of only one faction! Mismatched faction " + fac.codeName() + " in biome " + parent.getBiomeIdentifier());
			}
		}
	}

	public float getEffectiveConquestStrength(World world, TESConquestZone zone) {
		if (TESConquestGrid.conquestEnabled(world) && !zone.isEmpty()) {
			float conqStr = zone.getConquestStrength(theFaction, world);
			for (TESFaction allyFac : theFaction.getConquestBoostRelations()) {
				if (parent.isFactionPresent(world, allyFac)) {
					continue;
				}
				conqStr += zone.getConquestStrength(allyFac, world) * 0.333f;
			}
			return conqStr;
		}
		return 0.0f;
	}

	public int getFactionWeight(float conq) {
		if (conq > 0.0f) {
			float conqFactor = conq * 0.2f * conquestSensitivity;
			return baseWeight + Math.round(conqFactor);
		}
		return baseWeight;
	}

	public TESSpawnListContainer getRandomSpawnList(Random rand, float conq) {
		int totalWeight = 0;
		for (TESSpawnListContainer cont : spawnListContainers) {
			if (!TESSpawnListContainer.canSpawnAtConquestLevel(conq)) {
				continue;
			}
			totalWeight += cont.getWeight();
		}
		if (totalWeight > 0) {
			int w = rand.nextInt(totalWeight);
			for (TESSpawnListContainer cont : spawnListContainers) {
				if (!TESSpawnListContainer.canSpawnAtConquestLevel(conq) || (w -= cont.getWeight()) >= 0) {
					continue;
				}
				return cont;
			}
			return null;
		}
		return null;
	}

	public boolean isEmpty() {
		return spawnListContainers.isEmpty();
	}

	public Collection<TESSpawnListContainer> getSpawnListContainers() {
		return spawnListContainers;
	}

	public int getBaseWeight() {
		return baseWeight;
	}

	public TESFaction getTheFaction() {
		return theFaction;
	}

	public void setConquestSensitivity(float conquestSensitivity) {
		this.conquestSensitivity = conquestSensitivity;
	}
}