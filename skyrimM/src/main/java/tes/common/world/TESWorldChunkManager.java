package tes.common.world;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.TESDimension;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.variant.TESBiomeVariant;
import tes.common.world.biome.variant.TESBiomeVariantList;
import tes.common.world.biome.variant.TESBiomeVariantStorage;
import tes.common.world.genlayer.*;
import tes.common.world.map.TESFixedStructures;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.MapGenStructure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TESWorldChunkManager extends WorldChunkManager {
	private static final TESBiomeVariantList EMPTY_LIST = new TESBiomeVariantList();
	private static final int LAYER_BIOME = 0;

	private final BiomeCache biomeCache;
	private final World worldObj;

	private final TESDimension tesDimension;

	private TESGenLayer[] chunkGenLayers;
	private TESGenLayer[] worldLayers;

	public TESWorldChunkManager(World world, TESDimension dim) {
		worldObj = world;
		biomeCache = new BiomeCache(this);
		tesDimension = dim;
		setupGenLayers();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean areBiomesViable(int i, int k, int range, List list) {
		TESIntCache.get(worldObj).resetIntCache();
		int i1 = i - range >> 2;
		int k1 = k - range >> 2;
		int i2 = i + range >> 2;
		int k2 = k + range >> 2;
		int i3 = i2 - i1 + 1;
		int k3 = k2 - k1 + 1;
		int[] ints = chunkGenLayers[LAYER_BIOME].getInts(worldObj, i1, k1, i3, k3);
		for (int l = 0; l < i3 * k3; ++l) {
			TESBiome biome = tesDimension.getBiomeList()[ints[l]];
			if (list.contains(biome)) {
				continue;
			}
			return false;
		}
		return true;
	}

	public boolean areVariantsSuitableSettlement(int i, int k, int range) {
		int i1 = i - range >> 2;
		int k1 = k - range >> 2;
		int i2 = i + range >> 2;
		int k2 = k + range >> 2;
		int i3 = i2 - i1 + 1;
		int k3 = k2 - k1 + 1;
		BiomeGenBase[] biomes = getBiomesForGeneration(null, i1, k1, i3, k3);
		for (TESBiomeVariant v : getVariantsChunkGen(null, i1, k1, i3, k3, biomes)) {
			if (v.getHillFactor() > 1.0f || v.getTreeFactor() > 1.0f || v.isDisableSettlements() || v.isAbsoluteHeight() && v.getAbsoluteHeightLevel() < 0.0f) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void cleanupCache() {
		biomeCache.cleanupCache();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public ChunkPosition findBiomePosition(int i, int k, int range, List list, Random random) {
		TESIntCache.get(worldObj).resetIntCache();
		int i1 = i - range >> 2;
		int k1 = k - range >> 2;
		int i2 = i + range >> 2;
		int k2 = k + range >> 2;
		int i3 = i2 - i1 + 1;
		int k3 = k2 - k1 + 1;
		int[] ints = chunkGenLayers[LAYER_BIOME].getInts(worldObj, i1, k1, i3, k3);
		ChunkPosition chunkpos = null;
		int j = 0;
		for (int l = 0; l < i3 * k3; ++l) {
			int xPos = i1 + l % i3 << 2;
			int zPos = k1 + l / i3 << 2;
			TESBiome biome = tesDimension.getBiomeList()[ints[l]];
			if (!list.contains(biome) || chunkpos != null && random.nextInt(j + 1) != 0) {
				continue;
			}
			chunkpos = new ChunkPosition(xPos, 0, zPos);
			++j;
		}
		return chunkpos;
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int i, int k, int xSize, int zSize, boolean useCache) {
		BiomeGenBase[] bms = biomes;
		TESIntCache.get(worldObj).resetIntCache();
		if (bms == null || bms.length < xSize * zSize) {
			bms = new BiomeGenBase[xSize * zSize];
		}
		if (useCache && xSize == 16 && zSize == 16 && (i & 0xF) == 0 && (k & 0xF) == 0) {
			BiomeGenBase[] cachedBiomes = biomeCache.getCachedBiomes(i, k);
			System.arraycopy(cachedBiomes, 0, bms, 0, 16 * 16);
			return bms;
		}
		int[] ints = worldLayers[LAYER_BIOME].getInts(worldObj, i, k, xSize, zSize);
		for (int l = 0; l < xSize * zSize; ++l) {
			int biomeID = ints[l];
			bms[l] = tesDimension.getBiomeList()[biomeID];
		}
		return bms;
	}

	@Override
	public BiomeGenBase getBiomeGenAt(int i, int k) {
		return biomeCache.getBiomeGenAt(i, k);
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int i, int k, int xSize, int zSize) {
		BiomeGenBase[] biomes1 = biomes;
		TESIntCache.get(worldObj).resetIntCache();
		if (biomes1 == null || biomes1.length < xSize * zSize) {
			biomes1 = new BiomeGenBase[xSize * zSize];
		}
		int[] ints = chunkGenLayers[LAYER_BIOME].getInts(worldObj, i, k, xSize, zSize);
		for (int l = 0; l < xSize * zSize; ++l) {
			int biomeID = ints[l];
			biomes1[l] = tesDimension.getBiomeList()[biomeID];
		}
		return biomes1;
	}

	public TESBiomeVariant getBiomeVariantAt(int i, int k) {
		byte[] variants;
		Chunk chunk = worldObj.getChunkFromBlockCoords(i, k);
		if (chunk != null && (variants = TESBiomeVariantStorage.getChunkBiomeVariants(worldObj, chunk)) != null) {
			if (variants.length == 256) {
				int chunkX = i & 0xF;
				int chunkZ = k & 0xF;
				byte variantID = variants[chunkX + chunkZ * 16];
				return TESBiomeVariant.getVariantForID(variantID);
			}
			FMLLog.severe("Found chunk biome variant array of unexpected length " + variants.length);
		}
		if (!worldObj.isRemote) {
			return getBiomeVariants(null, i, k, 1, 1)[0];
		}
		return TESBiomeVariant.STANDARD;
	}

	public TESBiomeVariant[] getBiomeVariants(TESBiomeVariant[] variants, int i, int k, int xSize, int zSize) {
		return getBiomeVariantsFromLayers(variants, i, k, xSize, zSize, null, false);
	}

	private TESBiomeVariant[] getBiomeVariantsFromLayers(TESBiomeVariant[] variants, int i, int k, int xSize, int zSize, BiomeGenBase[] biomeSource, boolean isChunkGeneration) {
		TESBiomeVariant[] variants1 = variants;
		TESIntCache.get(worldObj).resetIntCache();
		BiomeGenBase[] biomes = new BiomeGenBase[xSize * zSize];
		if (biomeSource != null) {
			biomes = biomeSource;
		} else {
			for (int k1 = 0; k1 < zSize; ++k1) {
				for (int i1 = 0; i1 < xSize; ++i1) {
					int index = i1 + k1 * xSize;
					biomes[index] = TESCrashHandler.getBiomeGenForCoords(worldObj, i + i1, k + k1);
				}
			}
		}
		if (variants1 == null || variants1.length < xSize * zSize) {
			variants1 = new TESBiomeVariant[xSize * zSize];
		}
		TESGenLayer[] sourceGenLayers = isChunkGeneration ? chunkGenLayers : worldLayers;
		int LAYER_VARIANTS_LARGE = 1;
		TESGenLayer variantsLarge = sourceGenLayers[LAYER_VARIANTS_LARGE];
		int LAYER_VARIANTS_SMALL = 2;
		TESGenLayer variantsSmall = sourceGenLayers[LAYER_VARIANTS_SMALL];
		int LAYER_VARIANTS_LAKES = 3;
		TESGenLayer variantsLakes = sourceGenLayers[LAYER_VARIANTS_LAKES];
		int LAYER_VARIANTS_RIVERS = 4;
		TESGenLayer variantsRivers = sourceGenLayers[LAYER_VARIANTS_RIVERS];
		int[] variantsLargeInts = variantsLarge.getInts(worldObj, i, k, xSize, zSize);
		int[] variantsSmallInts = variantsSmall.getInts(worldObj, i, k, xSize, zSize);
		int[] variantsLakesInts = variantsLakes.getInts(worldObj, i, k, xSize, zSize);
		int[] variantsRiversInts = variantsRivers.getInts(worldObj, i, k, xSize, zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				int riverCode;
				int index = i1 + k1 * xSize;
				TESBiome biome = (TESBiome) biomes[index];
				TESBiomeVariant variant = TESBiomeVariant.STANDARD;
				int xPos = i + i1;
				int zPos = k + k1;
				if (isChunkGeneration) {
					xPos <<= 2;
					zPos <<= 2;
				}
				boolean[] flags = TESFixedStructures.isMountainOrStructureNear(worldObj, xPos, zPos);
				boolean mountainNear = flags[0];
				boolean structureNear = flags[1];
				boolean fixedSettlementNear = biome.getDecorator().anyFixedSettlementsAt(worldObj, xPos, zPos);
				if (fixedSettlementNear) {
					variant = TESBiomeVariant.STEPPE;
				} else {
					if (!mountainNear) {
						float variantChance = biome.getVariantChance();
						if (variantChance > 0.0f) {
							for (int pass = 0; pass <= 1; ++pass) {
								TESBiomeVariantList variantList = pass == 0 ? EMPTY_LIST : biome.getBiomeVariants();
								if (variantList.isEmpty()) {
									continue;
								}
								int[] sourceInts = pass == 0 ? variantsLargeInts : variantsSmallInts;
								int variantCode = sourceInts[index];
								float variantF = (float) variantCode / TESGenLayerBiomeVariants.RANDOM_MAX;
								if (variantF < variantChance) {
									float variantFNormalised = variantF / variantChance;
									variant = variantList.get(variantFNormalised);
									break;
								}
								variant = TESBiomeVariant.STANDARD;
							}
						}
						if (!structureNear && biome.getEnableRiver()) {
							int lakeCode = variantsLakesInts[index];
							if (TESGenLayerBiomeVariantsLake.getFlag(lakeCode, 1)) {
								variant = TESBiomeVariant.LAKE;
							}
						}
					}
					riverCode = variantsRiversInts[index];
					if (riverCode == 2 || riverCode == 1 && biome.getEnableRiver() && !structureNear) {
						variant = TESBiomeVariant.RIVER;
					}
				}
				variants1[index] = variant;
			}
		}
		return variants1;
	}

	@Override
	public float[] getRainfall(float[] rainfall, int i, int k, int xSize, int zSize) {
		float[] rainfall1 = rainfall;
		TESIntCache.get(worldObj).resetIntCache();
		if (rainfall1 == null || rainfall1.length < xSize * zSize) {
			rainfall1 = new float[xSize * zSize];
		}
		int[] ints = worldLayers[LAYER_BIOME].getInts(worldObj, i, k, xSize, zSize);
		for (int l = 0; l < xSize * zSize; ++l) {
			int biomeID = ints[l];
			float f = tesDimension.getBiomeList()[biomeID].getIntRainfall() / 65536.0f;
			if (f > 1.0f) {
				f = 1.0f;
			}
			rainfall1[l] = f;
		}
		return rainfall1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getTemperatureAtHeight(float f, int height) {
		if (worldObj.isRemote && TES.isNewYear()) {
			return 0.0f;
		}
		return f;
	}

	public TESBiomeVariant[] getVariantsChunkGen(TESBiomeVariant[] variants, int i, int k, int xSize, int zSize, BiomeGenBase[] biomeSource) {
		return getBiomeVariantsFromLayers(variants, i, k, xSize, zSize, biomeSource, true);
	}

	@Override
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int i, int k, int xSize, int zSize) {
		return getBiomeGenAt(biomes, i, k, xSize, zSize, true);
	}

	private void setupGenLayers() {
		int i;
		long seed = worldObj.getSeed() + 1954L;
		chunkGenLayers = TESGenLayerWorld.createWorld(tesDimension, worldObj.getWorldInfo().getTerrainType());
		worldLayers = new TESGenLayer[chunkGenLayers.length];
		for (i = 0; i < worldLayers.length; ++i) {
			TESGenLayer layer = chunkGenLayers[i];
			worldLayers[i] = new TESGenLayerZoomVoronoi(10L, layer);
		}
		for (i = 0; i < worldLayers.length; ++i) {
			chunkGenLayers[i].initWorldGenSeed(seed);
			worldLayers[i].initWorldGenSeed(seed);
		}
	}
}