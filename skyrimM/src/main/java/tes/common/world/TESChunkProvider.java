package tes.common.world;

import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.variant.TESBiomeVariant;
import tes.common.world.biome.variant.TESBiomeVariantStorage;
import tes.common.world.map.*;
import tes.common.world.spawning.TESSpawnerAnimals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenStructure;

import java.util.List;
import java.util.Random;

public class TESChunkProvider implements IChunkProvider {
	private final TESMapGenCaves caveGenerator = new TESMapGenCaves();
	private final MapGenBase ravineGenerator = new TESMapGenRavine();

	private final NoiseGeneratorOctaves noiseGen1;
	private final NoiseGeneratorOctaves noiseGen2;
	private final NoiseGeneratorOctaves noiseGen3;
	private final NoiseGeneratorOctaves noiseGen5;
	private final NoiseGeneratorOctaves noiseGen6;
	private final NoiseGeneratorOctaves stoneNoiseGen;

	private final World worldObj;
	private final Random rand;

	private final int biomeSampleRadius;
	private final int biomeSampleWidth;
	private final float[] biomeHeightNoise;

	private BiomeGenBase[] biomesForGeneration;
	private TESBiomeVariant[] variantsForGeneration;

	private double[] noise1;
	private double[] noise2;
	private double[] noise3;
	private double[] noise5;
	private double[] noise6;
	private double[] stoneNoise = new double[256];
	private double[] heightNoise;
	private double[] blockHeightNoiseArray;

	public TESChunkProvider(World world, long seed) {
		worldObj = world;
		rand = new Random(seed);
		noiseGen1 = new NoiseGeneratorOctaves(rand, 16);
		noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
		noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
		stoneNoiseGen = new NoiseGeneratorOctaves(rand, 4);
		noiseGen5 = new NoiseGeneratorOctaves(rand, 10);
		noiseGen6 = new NoiseGeneratorOctaves(rand, 16);
		biomeSampleRadius = 6;
		biomeSampleWidth = 2 * biomeSampleRadius + 1;
		biomeHeightNoise = new float[biomeSampleWidth * biomeSampleWidth];
		for (int i = -biomeSampleRadius; i <= biomeSampleRadius; ++i) {
			for (int k = -biomeSampleRadius; k <= biomeSampleRadius; ++k) {
				biomeHeightNoise[i + biomeSampleRadius + (k + biomeSampleRadius) * biomeSampleWidth] = 10.0f / MathHelper.sqrt_float(i * i + k * k + 0.2f);
			}
		}
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public boolean chunkExists(int i, int j) {
		return true;
	}

	@Override
	public ChunkPosition func_147416_a(World world, String type, int i, int j, int k) {
		return null;
	}

	private void generateTerrain(int i, int j, Block[] blocks) {
		TESWorldChunkManager chunkManager = (TESWorldChunkManager) worldObj.getWorldChunkManager();
		int byte0 = 4;
		int byte1 = 32;
		int k = byte0 + 1;
		int byte3 = 33;
		int l = byte0 + 1;
		biomesForGeneration = chunkManager.getBiomesForGeneration(biomesForGeneration, i * byte0 - biomeSampleRadius, j * byte0 - biomeSampleRadius, k + biomeSampleWidth, l + biomeSampleWidth);
		variantsForGeneration = chunkManager.getVariantsChunkGen(variantsForGeneration, i * byte0 - biomeSampleRadius, j * byte0 - biomeSampleRadius, k + biomeSampleWidth, l + biomeSampleWidth, biomesForGeneration);
		heightNoise = initializeHeightNoise(heightNoise, i * byte0, 0, j * byte0, k, byte3, l);
		blockHeightNoiseArray = new double[blocks.length];
		for (int i1 = 0; i1 < byte0; ++i1) {
			for (int j1 = 0; j1 < byte0; ++j1) {
				for (int k1 = 0; k1 < byte1; ++k1) {
					double d = 0.125;
					double d1 = heightNoise[(i1 * l + j1) * byte3 + k1];
					double d2 = heightNoise[(i1 * l + j1 + 1) * byte3 + k1];
					double d3 = heightNoise[((i1 + 1) * l + j1) * byte3 + k1];
					double d4 = heightNoise[((i1 + 1) * l + j1 + 1) * byte3 + k1];
					double d5 = (heightNoise[(i1 * l + j1) * byte3 + k1 + 1] - d1) * d;
					double d6 = (heightNoise[(i1 * l + j1 + 1) * byte3 + k1 + 1] - d2) * d;
					double d7 = (heightNoise[((i1 + 1) * l + j1) * byte3 + k1 + 1] - d3) * d;
					double d8 = (heightNoise[((i1 + 1) * l + j1 + 1) * byte3 + k1 + 1] - d4) * d;
					for (int l1 = 0; l1 < 8; ++l1) {
						double d9 = 0.25;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;
						for (int i2 = 0; i2 < 4; ++i2) {
							int j2 = i2 + i1 * 4 << 12 | j1 * 4 << 8 | k1 * 8 + l1;
							double d14 = 0.25;
							double d15 = (d11 - d10) * d14;
							for (int k2 = 0; k2 < 4; ++k2) {
								double blockHeightNoise;
								int blockIndex = j2 + k2 * 256;
								blockHeightNoiseArray[blockIndex] = blockHeightNoise = d10 + d15 * k2;
								blocks[blockIndex] = blockHeightNoise > 0.0 ? Blocks.stone : k1 * 8 + l1 <= 62 ? Blocks.water : Blocks.air;
							}
							d10 += d12;
							d11 += d13;
						}
						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, int i, int j, int k) {
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		return biome == null ? null : biome.getSpawnableList(creatureType);
	}

	private double[] initializeHeightNoise(double[] noise, int i, int j, int k, int xSize, int ySize, int zSize) {
		double[] noise4 = noise;
		if (noise4 == null) {
			noise4 = new double[xSize * ySize * zSize];
		}
		double xzNoiseScale = 400.0;
		double heightStretch = 6.0;
		noise5 = noiseGen5.generateNoiseOctaves(noise5, i, k, xSize, zSize, 1.121, 1.121, 0.5);
		noise6 = noiseGen6.generateNoiseOctaves(noise6, i, k, xSize, zSize, 200.0, 200.0, 0.5);
		noise3 = noiseGen3.generateNoiseOctaves(noise3, i, j, k, xSize, ySize, zSize, 684.412 / xzNoiseScale, 2.0E-4, 684.412 / xzNoiseScale);
		noise1 = noiseGen1.generateNoiseOctaves(noise1, i, j, k, xSize, ySize, zSize, 684.412, 1.0, 684.412);
		noise2 = noiseGen2.generateNoiseOctaves(noise2, i, j, k, xSize, ySize, zSize, 684.412, 1.0, 684.412);
		int noiseIndexXZ = 0;
		int noiseIndex = 0;
		for (int i1 = 0; i1 < xSize; ++i1) {
			for (int k1 = 0; k1 < zSize; ++k1) {
				int xPos = i + i1 << 2;
				int zPos = k + k1 << 2;
				float totalBaseHeight = 0.0f;
				float totalHeightVariation = 0.0f;
				float totalHeightNoise = 0.0f;
				float totalVariantHillFactor = 0.0f;
				float totalFlatBiomeHeight = 0.0f;
				int biomeCount = 0;
				int centreBiomeIndex = i1 + biomeSampleRadius + (k1 + biomeSampleRadius) * (xSize + biomeSampleWidth);
				BiomeGenBase centreBiome = biomesForGeneration[centreBiomeIndex];
				TESBiomeVariant centreVariant = variantsForGeneration[centreBiomeIndex];
				float centreHeight = centreBiome.rootHeight + centreVariant.getHeightBoost();
				if (centreVariant.isAbsoluteHeight()) {
					centreHeight = centreVariant.getHeightBoost();
				}
				for (int i2 = -biomeSampleRadius; i2 <= biomeSampleRadius; ++i2) {
					for (int k2 = -biomeSampleRadius; k2 <= biomeSampleRadius; ++k2) {
						int biomeIndex = i1 + i2 + biomeSampleRadius + (k1 + k2 + biomeSampleRadius) * (xSize + biomeSampleWidth);
						BiomeGenBase biome = biomesForGeneration[biomeIndex];
						TESBiomeVariant variant = variantsForGeneration[biomeIndex];
						float baseHeight = biome.rootHeight + variant.getHeightBoost();
						float heightVariation = biome.heightVariation * variant.getHillFactor();
						if (variant.isAbsoluteHeight()) {
							baseHeight = variant.getHeightBoost();
							heightVariation = variant.getHillFactor();
						}
						float hillFactor = variant.getHillFactor();
						float baseHeightPlus = baseHeight + 2.0f;
						if (baseHeightPlus == 0.0f) {
							baseHeightPlus = 0.001f;
						}
						float heightNoise2 = biomeHeightNoise[i2 + biomeSampleRadius + (k2 + biomeSampleRadius) * biomeSampleWidth] / baseHeightPlus / 2.0f;
						heightNoise2 = Math.abs(heightNoise2);
						if (baseHeight > centreHeight) {
							heightNoise2 /= 2.0f;
						}
						totalBaseHeight += baseHeight * heightNoise2;
						totalHeightVariation += heightVariation * heightNoise2;
						totalHeightNoise += heightNoise2;
						totalVariantHillFactor += hillFactor;
						float flatBiomeHeight = biome.rootHeight;
						boolean isWater = ((TESBiome) biome).getHeightBaseParameter() < 0.0f;
						if (variant.isAbsoluteHeight() && variant.getAbsoluteHeightLevel() < 0.0f) {
							isWater = true;
						}
						if (isWater) {
							flatBiomeHeight = baseHeight;
						}
						totalFlatBiomeHeight += flatBiomeHeight;
						++biomeCount;
					}
				}
				float avgBaseHeight = totalBaseHeight / totalHeightNoise;
				float avgHeightVariation = totalHeightVariation / totalHeightNoise;
				float avgFlatBiomeHeight = totalFlatBiomeHeight / biomeCount;
				float avgVariantHillFactor = totalVariantHillFactor / biomeCount;
				if (TESFixedStructures.hasMapFeatures(worldObj)) {
					int width = 32;
					if (centreBiome instanceof TESBiome.Marshes) {
						width = 8;
					}
					float roadNear = TESBeziers.isBezierNear(xPos, zPos, width, TESBeziers.Type.ROAD);
					if (roadNear >= 0.0f) {
						avgBaseHeight = avgFlatBiomeHeight + (avgBaseHeight - avgFlatBiomeHeight) * roadNear;
						avgHeightVariation *= roadNear;
					}
					float wallNear = TESBeziers.isBezierNear(xPos, zPos, width, TESBeziers.Type.WALL);
					if (wallNear >= 0.0f) {
						avgBaseHeight = avgFlatBiomeHeight + (avgBaseHeight - avgFlatBiomeHeight) * wallNear;
						avgHeightVariation *= wallNear;
					}
					float linkerNear = TESBeziers.isBezierNear(xPos, zPos, width, TESBeziers.Type.LINKER);
					if (linkerNear >= 0.0f) {
						avgBaseHeight = avgFlatBiomeHeight + (avgBaseHeight - avgFlatBiomeHeight) * linkerNear;
						avgHeightVariation *= linkerNear;
					}
					float mountain = TESMountains.getTotalHeightBoost(xPos, zPos);
					if (mountain > 0.005f) {
						avgBaseHeight += mountain;
						float mtnV = 0.2f;
						float dv = avgHeightVariation - mtnV;
						avgHeightVariation = mtnV + dv / (1.0f + mountain);
					}
				}
				avgBaseHeight = (avgBaseHeight * 4.0f - 1.0f) / 8.0f;
				if (avgHeightVariation == 0.0f) {
					avgHeightVariation = 0.001f;
				}
				double heightNoise = noise6[noiseIndexXZ] / 8000.0;
				if (heightNoise < 0.0) {
					heightNoise = -heightNoise * 0.3;
				}
				heightNoise = heightNoise * 3.0 - 2.0;
				if (heightNoise < 0.0) {
					heightNoise /= 2.0;
					if (heightNoise < -1.0) {
						heightNoise = -1.0;
					}
					heightNoise /= 1.4;
					heightNoise /= 2.0;
				} else {
					if (heightNoise > 1.0) {
						heightNoise = 1.0;
					}
					heightNoise /= 8.0;
				}
				++noiseIndexXZ;
				for (int j1 = 0; j1 < ySize; ++j1) {
					double baseHeight = avgBaseHeight;
					baseHeight += heightNoise * 0.2 * avgVariantHillFactor;
					baseHeight = baseHeight * ySize / 16.0;
					double var28 = ySize / 2.0 + baseHeight * 4.0;
					double totalNoise;
					double var32 = (j1 - var28) * heightStretch * 128.0 / 256.0 / avgHeightVariation;
					if (var32 < 0.0) {
						var32 *= 4.0;
					}
					double var34 = noise1[noiseIndex] / 512.0;
					double var36 = noise2[noiseIndex] / 512.0;
					double var38 = (noise3[noiseIndex] / 10.0 + 1.0) / 2.0 * avgVariantHillFactor;
					totalNoise = var38 < 0.0 ? var34 : var38 > 1.0 ? var36 : var34 + (var36 - var34) * var38;
					totalNoise -= var32;
					if (j1 > ySize - 4) {
						double var40 = (j1 - (ySize - 4)) / 3.0f;
						totalNoise = totalNoise * (1.0 - var40) + -10.0 * var40;
					}
					noise4[noiseIndex] = totalNoise;
					++noiseIndex;
				}
			}
		}
		return noise4;
	}

	@Override
	public Chunk loadChunk(int i, int k) {
		return provideChunk(i, k);
	}

	@Override
	public String makeString() {
		return "MiddleEarthLevelSource";
	}

	@Override
	public void populate(IChunkProvider ichunkprovider, int i, int j) {
		int j1;
		int k1;
		int i1;
		BlockFalling.fallInstantly = true;
		int k = i * 16;
		int l = j * 16;
		TESBiome biome = (TESBiome) TESCrashHandler.getBiomeGenForCoords(worldObj, k + 16, l + 16);
		TESBiomeVariant variant = ((TESWorldChunkManager) worldObj.getWorldChunkManager()).getBiomeVariantAt(k + 16, l + 16);
		rand.setSeed(worldObj.getSeed());
		long l1 = rand.nextLong() / 2L * 2L + 1L;
		long l2 = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed(i * l1 + j * l2 ^ worldObj.getSeed());
		if (rand.nextInt(4) == 0) {
			i1 = k + rand.nextInt(16) + 8;
			j1 = rand.nextInt(128);
			k1 = l + rand.nextInt(16) + 8;
			if (j1 < 60) {
				new WorldGenLakes(Blocks.water).generate(worldObj, rand, i1, j1, k1);
			}
		}
		if (rand.nextInt(8) == 0) {
			i1 = k + rand.nextInt(16) + 8;
			j1 = rand.nextInt(rand.nextInt(120) + 8);
			k1 = l + rand.nextInt(16) + 8;
			if (j1 < 60) {
				new WorldGenLakes(Blocks.lava).generate(worldObj, rand, i1, j1, k1);
			}
		}
		biome.decorate(worldObj, rand, k, l);
		if (biome.getChanceToSpawnAnimals() <= 1.0f) {
			if (rand.nextFloat() < biome.getChanceToSpawnAnimals()) {
				TESSpawnerAnimals.worldGenSpawnAnimals(worldObj, biome, variant, k + 8, l + 8, rand);
			}
		} else {
			int spawns = MathHelper.floor_double(biome.getChanceToSpawnAnimals());
			for (int i12 = 0; i12 < spawns; ++i12) {
				TESSpawnerAnimals.worldGenSpawnAnimals(worldObj, biome, variant, k + 8, l + 8, rand);
			}
		}
		k += 8;
		l += 8;
		for (i1 = 0; i1 < 16; ++i1) {
			for (int k12 = 0; k12 < 16; ++k12) {
				int j12 = worldObj.getPrecipitationHeight(k + i1, l + k12);
				if (worldObj.isBlockFreezable(i1 + k, j12 - 1, k12 + l)) {
					worldObj.setBlock(i1 + k, j12 - 1, k12 + l, Blocks.ice, 0, 2);
				}
				if (!worldObj.func_147478_e(i1 + k, j12, k12 + l, true)) {
					continue;
				}
				worldObj.setBlock(i1 + k, j12, k12 + l, Blocks.snow_layer, 0, 2);
			}
		}
		BlockFalling.fallInstantly = false;
	}

	@Override
	public Chunk provideChunk(int i, int k) {
		rand.setSeed(i * 341873128712L + k * 132897987541L);
		TESWorldChunkManager chunkManager = (TESWorldChunkManager) worldObj.getWorldChunkManager();
		Block[] blocks = new Block[65536];
		byte[] metadata = new byte[65536];
		ChunkFlags chunkFlags = new ChunkFlags();
		generateTerrain(i, k, blocks);
		biomesForGeneration = chunkManager.loadBlockGeneratorData(biomesForGeneration, i * 16, k * 16, 16, 16);
		variantsForGeneration = chunkManager.getBiomeVariants(variantsForGeneration, i * 16, k * 16, 16, 16);
		replaceBlocksForBiome(i, k, blocks, metadata, biomesForGeneration, variantsForGeneration, chunkFlags);
		caveGenerator.setChunkFlags(chunkFlags);
		caveGenerator.func_151539_a(this, worldObj, i, k, blocks);
		ravineGenerator.func_151539_a(this, worldObj, i, k, blocks);
		Chunk chunk = new Chunk(worldObj, i, k);
		ExtendedBlockStorage[] blockStorage = chunk.getBlockStorageArray();
		for (int i1 = 0; i1 < 16; ++i1) {
			for (int k1 = 0; k1 < 16; ++k1) {
				for (int j1 = 0; j1 < 256; ++j1) {
					int blockIndex = i1 << 12 | k1 << 8 | j1;
					Block block = blocks[blockIndex];
					if (block == null || block == Blocks.air) {
						continue;
					}
					byte meta = metadata[blockIndex];
					int j2 = j1 >> 4;
					if (blockStorage[j2] == null) {
						blockStorage[j2] = new ExtendedBlockStorage(j2 << 4, true);
					}
					blockStorage[j2].func_150818_a(i1, j1 & 0xF, k1, block);
					blockStorage[j2].setExtBlockMetadata(i1, j1 & 0xF, k1, meta & 0xF);
				}
			}
		}
		byte[] biomes = chunk.getBiomeArray();
		for (int l = 0; l < biomes.length; ++l) {
			biomes[l] = (byte) biomesForGeneration[l].biomeID;
		}
		byte[] variants = new byte[256];
		for (int l = 0; l < variants.length; ++l) {
			variants[l] = (byte) variantsForGeneration[l].getVariantID();
		}
		TESBiomeVariantStorage.setChunkBiomeVariants(worldObj, chunk, variants);
		chunk.generateSkylightMap();
		return chunk;
	}

	@Override
	public void recreateStructures(int i, int k) {
	}

	private void replaceBlocksForBiome(int i, int k, Block[] blocks, byte[] metadata, BiomeGenBase[] biomeArray, TESBiomeVariant[] variantArray, ChunkFlags chunkFlags) {
		double d = 0.03125;
		stoneNoise = stoneNoiseGen.generateNoiseOctaves(stoneNoise, i * 16, k * 16, 0, 16, 16, 1, d * 2.0, d * 2.0, d * 2.0);
		int ySize = blocks.length / 256;
		for (int i1 = 0; i1 < 16; ++i1) {
			for (int k1 = 0; k1 < 16; ++k1) {
				int index;
				int x = i * 16 + i1;
				int z = k * 16 + k1;
				int xzIndex = i1 * 16 + k1;
				int xzIndexBiome = i1 + k1 * 16;
				TESBiome biome = (TESBiome) biomeArray[xzIndexBiome];
				TESBiomeVariant variant = variantArray[xzIndexBiome];
				int height = 0;
				for (int j = ySize - 1; j >= 0; --j) {
					int index2 = xzIndex * ySize + j;
					Block block2 = blocks[index2];
					if (!block2.isOpaqueCube()) {
						continue;
					}
					height = j;
					break;
				}
				biome.generateBiomeTerrain(worldObj, rand, blocks, metadata, x, z, stoneNoise[xzIndex], height, variant);
				if (!TESFixedStructures.hasMapFeatures(worldObj)) {
					continue;
				}
				chunkFlags.getBezierFlags()[xzIndex] = TESBezierGenerator.generateBezier(worldObj, rand, x, z, biome, blocks, metadata, blockHeightNoiseArray);
				int lavaHeight = TESMountains.getLavaHeight(x, z);
				if (lavaHeight <= 0) {
					continue;
				}
				for (int j = lavaHeight; j >= 0 && !blocks[index = xzIndex * ySize + j].isOpaqueCube(); --j) {
					blocks[index] = Blocks.lava;
					metadata[index] = 0;
				}
			}
		}
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate update) {
		return true;
	}

	@Override
	public void saveExtraData() {
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	public static class ChunkFlags {
		private final boolean[] bezierFlags = new boolean[256];

		public boolean[] getBezierFlags() {
			return bezierFlags;
		}
	}
}