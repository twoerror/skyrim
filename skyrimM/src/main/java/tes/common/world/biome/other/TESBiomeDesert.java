package tes.common.world.biome.other;

import tes.common.entity.animal.TESEntitySeagull;
import tes.common.world.biome.TESBiome;
import tes.common.world.spawning.TESEventSpawner;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeGenBase;

public class TESBiomeDesert extends TESBiomeOcean {
	public TESBiomeDesert(int i, boolean major) {
		super(i, major);
		setMinMaxHeight(0.1f, 0.0f);
		setTemperatureRainfall(0.1f, 0.1f);
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableTESAmbientList.clear();
		banditChance = TESEventSpawner.EventChance.NEVER;
	}

	public TESBiome setBlock(Block block, int meta) {
		topBlock = Blocks.sand;
		topBlockMeta = meta;
		fillerBlock = block;
		fillerBlockMeta = meta;
		return this;
	}
}
