package tes.common.world.biome.other;

import tes.TES;
import tes.client.sound.TESMusicRegion;
import tes.common.database.TESAchievement;
import tes.common.database.TESBlocks;
import tes.common.entity.animal.TESEntitySeagull;
import tes.common.world.biome.TESBiome;
import tes.common.world.feature.TESWorldGenSeaBlock;
import tes.common.world.map.TESWaypoint;
import tes.common.world.spawning.TESEventSpawner;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESBiomeOcean extends TESBiome {
	private static final WorldGenerator SPONGE_GEN = new TESWorldGenSeaBlock(Blocks.sponge, 0, 24);
	private static final WorldGenerator CORAL_GEN = new TESWorldGenSeaBlock(TESBlocks.coralReef, 0, 64);

	public TESBiomeOcean(int i, boolean major) {
		super(i, major);
		setupStandardPlainsFauna();
		spawnableWaterCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySquid.class, 4, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntitySeagull.class, 20, 4, 4));
		decorator.addOre(new WorldGenMinable(TESBlocks.oreSalt, 8), 4.0f, 0, 64);
		decorator.addOre(new WorldGenMinable(TESBlocks.oreSalt, 8, Blocks.sand), 0.5f, 56, 80);
		decorator.addOre(new WorldGenMinable(TESBlocks.oreSalt, 8, TESBlocks.whiteSand), 0.5f, 56, 80);
		banditChance = TESEventSpawner.EventChance.NEVER;
	}

	public static boolean isFrozen(int k) {
		return k <= -23000 || k >= 490000;
	}

	@Override
	public void decorate(World world, Random random, int i, int k) {
		super.decorate(world, random, i, k);
		int i1 = i + random.nextInt(16) + 8;
		int k1 = k + random.nextInt(16) + 8;
		int j1 = world.getTopSolidOrLiquidBlock(i1, k1);
		if (j1 <= 43) {
			for (int l2 = 0; l2 < 50; ++l2) {
				int i3 = i + random.nextInt(16) + 8;
				int k3 = k + random.nextInt(16) + 8;
				int j3 = world.getTopSolidOrLiquidBlock(i3, k3);
				if (j3 <= 43 && (world.getBlock(i3, j3 - 1, k3) == Blocks.sand || world.getBlock(i3, j3 - 1, k3) == Blocks.dirt)) {
					int height = j3 + 4 + random.nextInt(4);
					for (int j2 = j3; j2 < height && !TES.isOpaque(world, i3, j2, k3); ++j2) {
						world.setBlock(i3, j2, k3, TESBlocks.kelp);
					}
				}
			}
		}
		if (!isFrozen(k)) {
			if (random.nextInt(12) == 0 && ((j1 = world.getTopSolidOrLiquidBlock(i1, k1)) < 60 || random.nextBoolean())) {
				SPONGE_GEN.generate(world, random, i1, j1, k1);
			}
			if (random.nextInt(4) == 0 && ((j1 = world.getTopSolidOrLiquidBlock(i1, k1)) < 60 || random.nextBoolean())) {
				CORAL_GEN.generate(world, random, i1, j1, k1);
			}
		}
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
		return TESWaypoint.Region.OCEAN;
	}

	@Override
	public boolean getEnableRiver() {
		return false;
	}
}