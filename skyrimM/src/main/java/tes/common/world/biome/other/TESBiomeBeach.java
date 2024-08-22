package tes.common.world.biome.other;

import tes.common.entity.animal.TESEntitySeagull;
import tes.common.world.biome.TESBiome;
import tes.common.world.spawning.TESEventSpawner;
import net.minecraft.block.Block;
import net.minecraft.world.biome.BiomeGenBase;

public class TESBiomeBeach extends TESBiomeOcean {
	public TESBiomeBeach(int i, boolean major) {
		super(i, major);
		setMinMaxHeight(0.1f, 0.0f);
		setTemperatureRainfall(0.8f, 0.4f);
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntitySeagull.class, 20, 4, 4));
		banditChance = TESEventSpawner.EventChance.NEVER;
	}

	public TESBiome setBeachBlock(Block block, int meta) {
		topBlock = block;
		topBlockMeta = meta;
		fillerBlock = block;
		fillerBlockMeta = meta;
		return this;
	}
}