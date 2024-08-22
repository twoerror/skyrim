package tes.common.world.spawning;

import cpw.mods.fml.common.FMLLog;
import tes.common.database.TESSpawnList;
import tes.common.faction.TESFaction;
import tes.common.util.TESLog;
import tes.common.world.biome.TESBiome;
import tes.common.world.map.TESConquestGrid;
import tes.common.world.map.TESConquestZone;
import net.minecraft.world.World;

import java.util.*;

public class TESBiomeSpawnList {
	private final Collection<TESFaction> presentFactions = new ArrayList<>();
	private final List<TESFactionContainer> factionContainers = new ArrayList<>();
	private final String biomeIdentifier;

	@SuppressWarnings("unused")
	public TESBiomeSpawnList(TESBiome biome) {
		this(biome.getClass().getName());
	}

	private TESBiomeSpawnList(String s) {
		biomeIdentifier = s;
	}

	public static TESSpawnListContainer entry(TESSpawnList list, int weight) {
		return new TESSpawnListContainer(list, weight);
	}

	public void clear() {
		factionContainers.clear();
		presentFactions.clear();
	}

	private void determineFactions(World world) {
		if (presentFactions.isEmpty() && !factionContainers.isEmpty()) {
			for (TESFactionContainer facContainer : factionContainers) {
				facContainer.determineFaction(world);
				TESFaction fac = facContainer.getTheFaction();
				if (presentFactions.contains(fac)) {
					continue;
				}
				presentFactions.add(fac);
			}
		}
	}

	public TESSpawnEntry.Instance getRandomSpawnEntry(Random rand, World world, int i, int k) {
		determineFactions(world);
		TESConquestZone zone = TESConquestGrid.getZoneByWorldCoords(i, k);
		int totalWeight = 0;
		HashMap<TESFactionContainer, Integer> cachedFacWeights = new HashMap<>();
		HashMap<TESFactionContainer, Float> cachedConqStrengths = new HashMap<>();
		for (TESFactionContainer cont : factionContainers) {
			int weight;
			float conq;
			if (cont.isEmpty() || (weight = cont.getFactionWeight(conq = cont.getEffectiveConquestStrength(world, zone))) <= 0) {
				continue;
			}
			totalWeight += weight;
			cachedFacWeights.put(cont, weight);
			cachedConqStrengths.put(cont, conq);
		}
		if (totalWeight > 0) {
			TESFactionContainer chosenFacContainer = null;
			boolean isConquestSpawn = false;
			int w = rand.nextInt(totalWeight);
			for (TESFactionContainer cont : factionContainers) {
				int facWeight;
				if (cont.isEmpty() || !cachedFacWeights.containsKey(cont) || (w -= facWeight = cachedFacWeights.get(cont)) >= 0) {
					continue;
				}
				chosenFacContainer = cont;
				if (facWeight <= cont.getBaseWeight()) {
					break;
				}
				isConquestSpawn = rand.nextFloat() < (float) (facWeight - cont.getBaseWeight()) / facWeight;
				break;
			}
			if (chosenFacContainer != null) {
				float conq = cachedConqStrengths.get(chosenFacContainer);
				TESSpawnListContainer spawnList = chosenFacContainer.getRandomSpawnList(rand, conq);
				if (spawnList == null || spawnList.getSpawnList() == null) {
					System.out.println("WARNING NPE in " + biomeIdentifier + ", " + chosenFacContainer.getTheFaction().codeName());
					FMLLog.severe("WARNING NPE in " + biomeIdentifier + ", " + chosenFacContainer.getTheFaction().codeName());
					TESLog.getLogger().warn("WARNING NPE in {}, {}", biomeIdentifier, chosenFacContainer.getTheFaction().codeName());
				}
				if (spawnList != null) {
					TESSpawnEntry entry = spawnList.getSpawnList().getRandomSpawnEntry(rand);
					int chance = spawnList.getSpawnChance();
					return new TESSpawnEntry.Instance(entry, chance, isConquestSpawn);
				}
			}
		}
		return null;
	}

	public boolean isFactionPresent(World world, TESFaction fac) {
		determineFactions(world);
		return presentFactions.contains(fac);
	}

	public TESFactionContainer newFactionList(int w) {
		return newFactionList(w, 1.0f);
	}

	private TESFactionContainer newFactionList(int w, float conq) {
		TESFactionContainer cont = new TESFactionContainer(this, w);
		cont.setConquestSensitivity(conq);
		factionContainers.add(cont);
		return cont;
	}

	public List<TESFactionContainer> getFactionContainers() {
		return factionContainers;
	}

	public String getBiomeIdentifier() {
		return biomeIdentifier;
	}
}