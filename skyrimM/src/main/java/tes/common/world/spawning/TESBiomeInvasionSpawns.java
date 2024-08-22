package tes.common.world.spawning;

import cpw.mods.fml.common.FMLLog;
import tes.common.database.TESInvasions;
import tes.common.world.biome.TESBiome;

import java.util.*;

public class TESBiomeInvasionSpawns {
	private final Map<TESEventSpawner.EventChance, List<TESInvasions>> invasionsByChance = new EnumMap<>(TESEventSpawner.EventChance.class);
	private final Collection<TESInvasions> registeredInvasions = new ArrayList<>();
	private final TESBiome theBiome;

	public TESBiomeInvasionSpawns(TESBiome biome) {
		theBiome = biome;
	}

	public void addInvasion(TESInvasions invasion, TESEventSpawner.EventChance chance) {
		List<TESInvasions> chanceList = getInvasionsForChance(chance);
		if (chanceList.contains(invasion) || registeredInvasions.contains(invasion)) {
			FMLLog.warning("TES biome %s already has invasion %s registered", theBiome.biomeName, invasion.codeName());
		} else {
			chanceList.add(invasion);
			registeredInvasions.add(invasion);
		}
	}

	public void clearInvasions() {
		invasionsByChance.clear();
		registeredInvasions.clear();
	}

	public List<TESInvasions> getInvasionsForChance(TESEventSpawner.EventChance chance) {
		List<TESInvasions> chanceList = invasionsByChance.get(chance);
		if (chanceList == null) {
			chanceList = new ArrayList<>();
		}
		invasionsByChance.put(chance, chanceList);
		return chanceList;
	}

	public Collection<TESInvasions> getRegisteredInvasions() {
		return registeredInvasions;
	}
}