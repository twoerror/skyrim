package tes.common.world.biome;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.client.sound.TESMusicRegion;
import tes.common.TESDimension;
import tes.common.database.TESAchievement;
import tes.common.database.TESBlocks;
import tes.common.entity.animal.*;
import tes.common.entity.other.TESEntityLightSkinBandit;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESAmbientCreature;
import tes.common.world.TESWorldChunkManager;
import tes.common.world.biome.other.TESBiomeBeach;
import tes.common.world.biome.other.TESBiomeDesert;
import tes.common.world.biome.other.TESBiomeLake;
import tes.common.world.biome.other.TESBiomeOcean;
import tes.common.world.biome.other.TESBiomeRiver;
import tes.common.world.biome.variant.TESBiomeVariant;
import tes.common.world.biome.variant.TESBiomeVariantList;
import tes.common.world.biome.variant.TESBiomeVariantStorage;
import tes.common.world.feature.TESTreeType;
import tes.common.world.map.TESBezierType;
import tes.common.world.map.TESWaypoint;
import tes.common.world.spawning.TESBiomeInvasionSpawns;
import tes.common.world.spawning.TESBiomeSpawnList;
import tes.common.world.spawning.TESEventSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenDoublePlant;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.EnumHelper;

import java.awt.*;
import java.util.List;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "PublicField", "unused"})
public abstract class TESBiome extends BiomeGenBase {
	public static final Set<TESBiome> CONTENT = new HashSet<>();
	public static final int SPAWN = 600;
	public static final int CONQUEST_SPAWN = 100;
	public static final NoiseGeneratorPerlin BIOME_TERRAIN_NOISE = new NoiseGeneratorPerlin(new Random(1955L), 1);
	public static final Class<?>[][] CORRECT_CREATURE_TYPE_PARAMS = {{EnumCreatureType.class, Class.class, Integer.TYPE, Material.class, Boolean.TYPE, Boolean.TYPE}};
	public static final EnumCreatureType CREATURE_TYPE_TES_AMBIENT = EnumHelper.addEnum(CORRECT_CREATURE_TYPE_PARAMS, EnumCreatureType.class, "TESAmbient", TESAmbientCreature.class, 45, Material.air, true, false);

	private static final Random TERRAIN_RAND = new Random();
	private static final Color WATER_COLOR_NORTH = new Color(602979);
	private static final Color WATER_COLOR_SOUTH = new Color(4973293);

	public static TESBiome alwaysWinter;
	
	public static TESBiome desert_nirn;
	public static TESBiome arryn;
	public static TESBiome arrynForest;
	public static TESBiome arrynMountains;
	public static TESBiome arrynMountainsFoothills;
	public static TESBiome arrynTown;
	public static TESBiome beach;
	public static TESBiome beachGravel;
	public static TESBiome beachWhite;
	public static TESBiome bleedingBeach;
	public static TESBiome bleedingSea;
	public static TESBiome boneMountains;
	public static TESBiome braavos;
	public static TESBiome braavosForest;
	public static TESBiome braavosHills;
	public static TESBiome cannibalSands;
	public static TESBiome cannibalSandsHills;
	public static TESBiome coldCoast;
	public static TESBiome crownlands;
	public static TESBiome crownlandsForest;
	public static TESBiome crownlandsTown;
	public static TESBiome disputedLands;
	public static TESBiome disputedLandsForest;
	public static TESBiome dorne;
	public static TESBiome dorneDesert;
	public static TESBiome dorneForest;
	public static TESBiome dorneMesa;
	public static TESBiome dorneMountains;
	public static TESBiome dothrakiSea;
	public static TESBiome dothrakiSeaForest;
	public static TESBiome dothrakiSeaHills;
	public static TESBiome dragonstone;
	public static TESBiome essos;
	public static TESBiome essosForest;
	public static TESBiome essosMarshes;
	public static TESBiome essosMountains;
	public static TESBiome frostfangs;
	public static TESBiome ghiscar;
	public static TESBiome ghiscarAstapor;
	public static TESBiome ghiscarColony;
	public static TESBiome ghiscarForest;
	public static TESBiome ghiscarMeereen;
	public static TESBiome ghiscarNewGhis;
	public static TESBiome ghiscarYunkai;
	public static TESBiome giftNew;
	public static TESBiome giftOld;
	public static TESBiome hauntedForest;
	public static TESBiome ibben;
	public static TESBiome ibbenColony;
	public static TESBiome ibbenColonyForest;
	public static TESBiome ibbenColonyHills;
	public static TESBiome ibbenMountains;
	public static TESBiome ibbenTaiga;
	public static TESBiome ifekevronForest;
	public static TESBiome ironIslands;
	public static TESBiome ironIslandsForest;
	public static TESBiome ironIslandsHills;
	public static TESBiome island;
	public static TESBiome isleOfFaces;
	public static TESBiome jogosNhai;
	public static TESBiome jogosNhaiDesert;
	public static TESBiome jogosNhaiDesertHills;
	public static TESBiome jogosNhaiForest;
	public static TESBiome jogosNhaiHills;
	public static TESBiome kingSpears;
	public static TESBiome kingswoodNorth;
	public static TESBiome kingswoodSouth;
	public static TESBiome lake;
	public static TESBiome lhazar;
	public static TESBiome lhazarForest;
	public static TESBiome lhazarHills;
	public static TESBiome longSummer;
	public static TESBiome lorath;
	public static TESBiome lorathForest;
	public static TESBiome lorathHills;
	public static TESBiome lorathMaze;
	public static TESBiome lys;
	public static TESBiome massy;
	public static TESBiome massyHills;
	public static TESBiome mossovy;
	public static TESBiome mossovyMarshes;
	public static TESBiome mossovyMountains;
	public static TESBiome mossovyTaiga;
	public static TESBiome myr;
	public static TESBiome myrForest;
	public static TESBiome naath;
	public static TESBiome neck;
	public static TESBiome neckForest;
	public static TESBiome north;
	public static TESBiome northBarrows;
	public static TESBiome northForest;
	public static TESBiome northForestIrontree;
	public static TESBiome northHills;
	public static TESBiome northMountains;
	public static TESBiome northTown;
	public static TESBiome northWild;
	public static TESBiome norvos;
	public static TESBiome norvosForest;
	public static TESBiome norvosHills;
	public static TESBiome ocean1;
	public static TESBiome ocean2;
	public static TESBiome ocean3;
	public static TESBiome ocean;
	public static TESBiome pentos;
	public static TESBiome pentosForest;
	public static TESBiome pentosHills;
	public static TESBiome qarth;
	public static TESBiome qarthColony;
	public static TESBiome qarthDesert;
	public static TESBiome qohor;
	public static TESBiome qohorForest;
	public static TESBiome qohorHills;
	public static TESBiome reach;
	public static TESBiome reachArbor;
	public static TESBiome reachFireField;
	public static TESBiome reachForest;
	public static TESBiome reachHills;
	public static TESBiome reachTown;
	public static TESBiome river;
	public static TESBiome riverlands;
	public static TESBiome riverlandsForest;
	public static TESBiome shadowLand;
	public static TESBiome shadowMountains;
	public static TESBiome shadowTown;
	public static TESBiome shrykesLand;
	public static TESBiome skagos;
	public static TESBiome skirlingPass;
	public static TESBiome sothoryosBushland;
	public static TESBiome sothoryosDesert;
	public static TESBiome sothoryosDesertCold;
	public static TESBiome sothoryosDesertHills;
	public static TESBiome sothoryosForest;
	public static TESBiome sothoryosFrost;
	public static TESBiome sothoryosHell;
	public static TESBiome sothoryosJungle;
	public static TESBiome sothoryosJungleEdge;
	public static TESBiome sothoryosMangrove;
	public static TESBiome sothoryosMountains;
	public static TESBiome sothoryosSavannah;
	public static TESBiome sothoryosTaiga;
	public static TESBiome stepstones;
	public static TESBiome stoneCoast;
	public static TESBiome stormlands;
	public static TESBiome stormlandsForest;
	public static TESBiome stormlandsTarth;
	public static TESBiome stormlandsTarthForest;
	public static TESBiome stormlandsTown;
	public static TESBiome summerColony;
	public static TESBiome summerColonyMangrove;
	public static TESBiome summerIslands;
	public static TESBiome summerIslandsTropicalForest;
	public static TESBiome thennLand;
	public static TESBiome tyrosh;
	public static TESBiome ulthos;
	public static TESBiome ulthosDesert;
	public static TESBiome ulthosDesertCold;
	public static TESBiome ulthosForest;
	public static TESBiome ulthosForestEdge;
	public static TESBiome ulthosFrost;
	public static TESBiome ulthosMarshes;
	public static TESBiome ulthosMarshesForest;
	public static TESBiome ulthosMountains;
	public static TESBiome ulthosRedForest;
	public static TESBiome ulthosRedForestEdge;
	public static TESBiome ulthosTaiga;
	public static TESBiome ulthosTaigaEdge;
	public static TESBiome valyria;
	public static TESBiome valyriaSea;
	public static TESBiome valyriaSea1;
	public static TESBiome valyriaSea2;
	public static TESBiome valyriaVolcano;
	public static TESBiome volantis;
	public static TESBiome volantisForest;
	public static TESBiome volantisMarshes;
	public static TESBiome volantisOrangeForest;
	public static TESBiome westerlands;
	public static TESBiome westerlandsForest;
	public static TESBiome westerlandsHills;
	public static TESBiome westerlandsTown;
	public static TESBiome westerosFrost;
	public static TESBiome whisperingWood;
	public static TESBiome wolfswood;
	public static TESBiome yeen;
	public static TESBiome yiTi;
	public static TESBiome yiTiMarshes;
	public static TESBiome yiTiTropicalForest;
	public static TESBiome yiTiBorderZone;

	protected final List<SpawnListEntry> spawnableTESAmbientList = new ArrayList<>();
	protected final TESBiomeVariantList biomeVariants = new TESBiomeVariantList();
	protected final TESBiomeSpawnList npcSpawnList = new TESBiomeSpawnList(this);
	protected final BiomeColors biomeColors = new BiomeColors(this);

	private final TESDimension biomeDimension;

	protected TESBiomeDecorator decorator;
	protected TESBiomeInvasionSpawns invasionSpawns;
	protected Class<? extends TESEntityNPC> banditEntityClass;
	protected TESEventSpawner.EventChance banditChance;

	protected boolean enableRocky;
	protected float variantChance = 0.4f;
	protected int fillerBlockMeta;
	protected int topBlockMeta;

	private TESClimateType climateType;
	private float heightBaseParameter;

	protected TESBiome(int i, boolean major) {
		this(i, major, TESDimension.GAME_OF_THRONES);
	}

	private TESBiome(int i, boolean major, TESDimension dim) {
		super(i, false);
		biomeDimension = dim;
		if (biomeDimension.getBiomeList()[i] != null) {
			throw new IllegalArgumentException("TES biome already exists at index " + i + " for dimension " + biomeDimension.getDimensionName() + '!');
		}
		biomeDimension.getBiomeList()[i] = this;
		if (major) {
			biomeDimension.getMajorBiomes().add(this);
		}
		waterColorMultiplier = BiomeColors.DEFAULT_WATER;
		decorator = new TESBiomeDecorator(this);
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableMonsterList.clear();
		spawnableCaveCreatureList.clear();
		spawnableWaterCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityFish.class, 10, 4, 4));
		spawnableCaveCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityBat.class, 10, 8, 8));
		banditChance = TESEventSpawner.EventChance.COMMON;
		invasionSpawns = new TESBiomeInvasionSpawns(this);
		CONTENT.add(this);
	}

	public static void preInit() {
		beach = new TESBiomeBeach(1, false).setBeachBlock(Blocks.sand, 0).setColor(0xF0E647).setBiomeName("beach");
		lake = new TESBiomeLake(2, false).setTemperatureRainfall(0.8F, 0.8F).setColor(0x162677).setBiomeName("lake");
		river = new TESBiomeRiver(3, false).setMinMaxHeight(-0.5f, 0.0f).setColor(0x0E184B).setBiomeName("river");
		ocean = new TESBiomeOcean(4, true).setTemperatureRainfall(0.8F, 0.8F).setColor(0x2A8BFE).setMinMaxHeight(-1.0f, 0.3f).setBiomeName("ocean");
		island = new TESBiomeOcean(5, false).setTemperatureRainfall(0.8F, 0.8F).setColor(0x616C10).setMinMaxHeight(0.0f, 0.3f).setBiomeName("island");
		desert_nirn = new TESBiomeDesert(6, false).setTemperatureRainfall(0.1F, 0.1F).setColor(0xFEFA7C).setMinMaxHeight(0.1f, 0.0f).setBiomeName("desert");
		}

	public static void updateWaterColor(int k) {
		int min = 0;
		int waterLimitNorth = -40000;
		int waterLimitSouth = 160000;
		int max = waterLimitSouth - waterLimitNorth;
		float latitude = (float) MathHelper.clamp_int(k - waterLimitNorth, min, max) / max;
		float[] northColors = WATER_COLOR_NORTH.getColorComponents(null);
		float[] southColors = WATER_COLOR_SOUTH.getColorComponents(null);
		float dR = southColors[0] - northColors[0];
		float dG = southColors[1] - northColors[1];
		float dB = southColors[2] - northColors[2];
		float r = dR * latitude;
		float g = dG * latitude;
		float b = dB * latitude;
		r += northColors[0];
		g += northColors[1];
		b += northColors[2];
		Color water = new Color(r, g, b);
		int waterRGB = water.getRGB();
		for (TESBiome biome : TESDimension.GAME_OF_THRONES.getBiomeList()) {
			if (biome != null && !biome.biomeColors.hasCustomWater()) {
				biome.biomeColors.updateWater(waterRGB);
			}
		}
	}

	public static void postInit() {
		Color baseWater = new Color(4876527);
		int baseR = baseWater.getRed();
		int baseG = baseWater.getGreen();
		int baseB = baseWater.getBlue();
		for (BiomeGenBase biome : getBiomeGenArray()) {
			if (biome == null) {
				continue;
			}
			Color water = new Color(biome.waterColorMultiplier);
			float[] rgb = water.getColorComponents(null);
			int r = (int) (baseR * rgb[0]);
			int g = (int) (baseG * rgb[1]);
			int b = (int) (baseB * rgb[2]);
			biome.waterColorMultiplier = new Color(r, g, b).getRGB();
		}
	}

	public BiomeColors getBiomeColors() {
		return biomeColors;
	}

	public void addBiomeF3Info(Collection<String> info, World world, TESBiomeVariant variant) {
		info.add("The Elder Scrolls biome: " + getBiomeDisplayName() + ", ID: " + biomeID + ';');
		info.add("Variant: " + StatCollector.translateToLocal(variant.getUnlocalizedName()) + ", loaded: " + TESBiomeVariantStorage.getSize(world));
	}

	@Override
	public boolean canSpawnLightningBolt() {
		return !getEnableSnow() && super.canSpawnLightningBolt();
	}

	@Override
	public BiomeGenBase createMutation() {
		return this;
	}

	@Override
	public void decorate(World world, Random random, int i, int k) {
		decorator.decorate(world, random, i, k);
	}

	@Override
	public WorldGenAbstractTree func_150567_a(Random random) {
		TESTreeType tree = decorator.getRandomTree(random);
		return tree.create(false, random);
	}

	public void generateBiomeTerrain(World world, Random random, Block[] blocks, byte[] meta, int i, int k, double stoneNoise, int height, TESBiomeVariant variant) {
		int chunkX = i & 0xF;
		int chunkZ = k & 0xF;
		int xzIndex = chunkX * 16 + chunkZ;
		int ySize = blocks.length / 256;
		int seaLevel = 63;
		int fillerDepthBase = (int) (stoneNoise / 4.0 + 5.0 + random.nextDouble() * 0.25);
		int fillerDepth = -1;
		Block top = topBlock;
		byte topMeta = (byte) topBlockMeta;
		Block filler = fillerBlock;
		byte fillerMeta = (byte) fillerBlockMeta;
		if (enableRocky && height >= 90) {
			float hFactor = (height - 90) / 10.0f;
			float thresh = 1.2f - hFactor * 0.2f;
			thresh = Math.max(thresh, 0.0f);
			double d12 = BIOME_TERRAIN_NOISE.func_151601_a(i * 0.03, k * 0.03);
			if (d12 + BIOME_TERRAIN_NOISE.func_151601_a(i * 0.3, k * 0.3) > thresh) {
				if (random.nextInt(5) == 0) {
					top = Blocks.gravel;
				} else {
					top = Blocks.stone;
				}
				topMeta = 0;
				filler = Blocks.stone;
				fillerMeta = 0;
			}
		}
		boolean podzol = false;
		if (topBlock == Blocks.grass) {
			float trees = decorator.getTreesPerChunk() + 0.1f;
			trees = Math.max(trees, variant.getTreeFactor() * 0.5f);
			if (trees >= 1.0f) {
				float thresh = 0.8f;
				thresh -= trees * 0.15f;
				thresh = Math.max(thresh, 0.0f);
				double d = 0.06;
				double randNoise = BIOME_TERRAIN_NOISE.func_151601_a(i * d, k * d);
				if (randNoise > thresh) {
					podzol = true;
				}
			}
		}
		if (podzol) {
			TERRAIN_RAND.setSeed(world.getSeed());
			TERRAIN_RAND.setSeed(TERRAIN_RAND.nextLong() + i * 4668095025L + k * 1387590552L ^ world.getSeed());
			float pdzRand = TERRAIN_RAND.nextFloat();
			if (pdzRand < 0.35f) {
				top = Blocks.dirt;
				topMeta = 2;
			} else if (pdzRand < 0.5f) {
				top = Blocks.dirt;
				topMeta = 1;
			} else if (pdzRand < 0.51f) {
				top = Blocks.gravel;
				topMeta = 0;
			}
		}
		if (variant.isHasMarsh() && TESBiomeVariant.MARSH_NOISE.func_151601_a(i * 0.1, k * 0.1) > -0.1) {
			for (int j = ySize - 1; j >= 0; --j) {
				int index = xzIndex * ySize + j;
				if (blocks[index] == null || blocks[index].getMaterial() != Material.air) {
					if (j != seaLevel - 1 || blocks[index] == Blocks.water) {
						break;
					}
					blocks[index] = Blocks.water;
					break;
				}
			}
		}
		for (int j = ySize - 1; j >= 0; --j) {
			int index = xzIndex * ySize + j;
			if (j <= random.nextInt(5)) {
				blocks[index] = Blocks.bedrock;
			} else {

				Block block = blocks[index];
				if (block == Blocks.air) {
					fillerDepth = -1;
				} else if (block == Blocks.stone) {
					if (fillerDepth == -1) {
						if (fillerDepthBase <= 0) {
							top = Blocks.air;
							topMeta = 0;
							filler = Blocks.stone;
							fillerMeta = 0;
						} else if (j >= seaLevel - 4 && j <= seaLevel + 1) {
							top = topBlock;
							topMeta = (byte) topBlockMeta;
							filler = fillerBlock;
							fillerMeta = (byte) fillerBlockMeta;
						}
						if (j < seaLevel && top == Blocks.air) {
							top = Blocks.water;
							topMeta = 0;
						}
						fillerDepth = fillerDepthBase;
						if (j >= seaLevel - 1) {
							blocks[index] = top;
							meta[index] = topMeta;
						} else {
							blocks[index] = filler;
							meta[index] = fillerMeta;
						}
					} else if (fillerDepth > 0) {
						blocks[index] = filler;
						meta[index] = fillerMeta;
						--fillerDepth;
						if (fillerDepth == 0) {
							boolean sand = false;
							if (filler == Blocks.sand) {
								if (fillerMeta == 1) {
									filler = TESBlocks.redSandstone;
								} else {
									filler = Blocks.sandstone;
								}
								fillerMeta = 0;
								sand = true;
							}
							if (filler == TESBlocks.whiteSand) {
								filler = TESBlocks.whiteSandstone;
								fillerMeta = 0;
								sand = true;
							}
							if (sand) {
								fillerDepth = 10 + random.nextInt(4);
							}
						}
						if (fillerDepth == 0 && fillerBlock != TESBlocks.rock && filler == fillerBlock) {
							fillerDepth = 6 + random.nextInt(3);
							filler = Blocks.stone;
							fillerMeta = 0;
						}
					}
				}
			}
		}
		int rockDepth = (int) (stoneNoise * 6.0 + 2.0 + random.nextDouble() * 0.25);
		if (this instanceof Mountains) {
			((Mountains) this).generateMountainTerrain(world, random, blocks, meta, i, k, xzIndex, ySize, height, rockDepth, variant);
		}
		variant.generateVariantTerrain(blocks, meta, i, k);
	}

	public Class<? extends TESEntityNPC> getBanditEntityClass() {
		if (banditEntityClass == null) {
			return TESEntityLightSkinBandit.class;
		}
		return banditEntityClass;
	}

	@SideOnly(Side.CLIENT)
	private int getBaseFoliageColor(int i, int j, int k) {
		float temp = getFloatTemperature(i, j, k);
		float rain = rainfall;
		temp = MathHelper.clamp_float(temp, 0.0f, 1.0f);
		rain = MathHelper.clamp_float(rain, 0.0f, 1.0f);
		return ColorizerFoliage.getFoliageColor(temp, rain);
	}

	@SideOnly(Side.CLIENT)
	private int getBaseGrassColor(int i, int j, int k) {
		float temp = getFloatTemperature(i, j, k);
		float rain = rainfall;
		temp = MathHelper.clamp_float(temp, 0.0f, 1.0f);
		rain = MathHelper.clamp_float(rain, 0.0f, 1.0f);
		return ColorizerGrass.getGrassColor(temp, rain);
	}

	public abstract TESAchievement getBiomeAchievement();

	public TESDimension getBiomeDimension() {
		return biomeDimension;
	}

	public String getBiomeDisplayName() {
		return StatCollector.translateToLocal("tes.biome." + biomeName + ".name");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBiomeFoliageColor(int i, int j, int k) {
		return biomeColors.getFoliage() != null ? biomeColors.getFoliage().getRGB() : getBaseFoliageColor(i, j, k);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBiomeGrassColor(int i, int j, int k) {
		return biomeColors.getGrass() != null ? biomeColors.getGrass().getRGB() : getBaseGrassColor(i, j, k);
	}

	public abstract TESMusicRegion.Sub getBiomeMusic();

	public TESBiomeVariantList getBiomeVariants() {
		return biomeVariants;
	}

	public abstract TESWaypoint.Region getBiomeWaypoints();

	public float getChanceToSpawnAnimals() {
		return 0.2f;
	}

	public TESClimateType getClimateType() {
		return climateType;
	}

	protected TESBiome setClimateType(TESClimateType type) {
		climateType = type;
		decorator.setGenerateAgriculture(type == TESClimateType.SUMMER);
		return this;
	}

	public void getCloudColor(Vec3 clouds) {
		if (biomeColors.getClouds() != null) {
			float[] colors = biomeColors.getClouds().getColorComponents(null);
			clouds.xCoord *= colors[0];
			clouds.yCoord *= colors[1];
			clouds.zCoord *= colors[2];
		}
	}

	public boolean getEnableRiver() {
		return true;
	}

	public int getFillerBlockMeta() {
		return fillerBlockMeta;
	}

	public void setFillerBlockMeta(int fillerBlockMeta) {
		this.fillerBlockMeta = fillerBlockMeta;
	}

	public void getFogColor(Vec3 fog) {
		if (biomeColors.getFog() != null) {
			float[] colors = biomeColors.getFog().getColorComponents(null);
			fog.xCoord *= colors[0];
			fog.yCoord *= colors[1];
			fog.zCoord *= colors[2];
		}
	}

	public float getHeightBaseParameter() {
		return heightBaseParameter;
	}

	public TESBiomeInvasionSpawns getInvasionSpawns() {
		return invasionSpawns;
	}

	public void setInvasionSpawns(TESBiomeInvasionSpawns invasionSpawns) {
		this.invasionSpawns = invasionSpawns;
	}

	public BiomeGenBase.FlowerEntry getRandomFlower(Random random) {
		return (BiomeGenBase.FlowerEntry) WeightedRandom.getRandomItem(random, flowers);
	}

	public GrassBlockAndMeta getRandomGrass(Random random) {
		if (random.nextInt(5) == 0) {
			return new GrassBlockAndMeta(Blocks.tallgrass, 2);
		}
		if (random.nextInt(30) == 0) {
			return new GrassBlockAndMeta(TESBlocks.plantain, 2);
		}
		if (random.nextInt(200) == 0) {
			return new GrassBlockAndMeta(TESBlocks.tallGrass, 3);
		}
		if (random.nextInt(16) == 0) {
			return new GrassBlockAndMeta(TESBlocks.tallGrass, 1);
		}
		if (random.nextInt(10) == 0) {
			return new GrassBlockAndMeta(TESBlocks.tallGrass, 2);
		}
		if (random.nextInt(80) == 0) {
			return new GrassBlockAndMeta(TESBlocks.tallGrass, 4);
		}
		if (random.nextInt(2) == 0) {
			return new GrassBlockAndMeta(TESBlocks.tallGrass, 0);
		}
		if (random.nextInt(3) == 0) {
			return new GrassBlockAndMeta(TESBlocks.clover, 0);
		}
		return new GrassBlockAndMeta(Blocks.tallgrass, 1);
	}

	public WorldGenerator getRandomWorldGenForDoubleFlower(Random random) {
		WorldGenDoublePlant doubleFlowerGen = new WorldGenDoublePlant();
		int i = random.nextInt(3);
		switch (i) {
			case 0:
				doubleFlowerGen.func_150548_a(1);
				return doubleFlowerGen;
			case 1:
				doubleFlowerGen.func_150548_a(4);
				return doubleFlowerGen;
			case 2:
				doubleFlowerGen.func_150548_a(5);
				return doubleFlowerGen;
			default:
				return null;
		}
	}

	public WorldGenerator getRandomWorldGenForDoubleGrass() {
		WorldGenDoublePlant generator = new WorldGenDoublePlant();
		generator.func_150548_a(2);
		return generator;
	}

	@Override
	public WorldGenerator getRandomWorldGenForGrass(Random random) {
		GrassBlockAndMeta obj = getRandomGrass(random);
		return new WorldGenTallGrass(obj.getBlock(), obj.getMeta());
	}

	public TESBezierType getRoadBlock() {
		return TESBezierType.PATH_DIRTY;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getSkyColorByTemp(float f) {
		if (biomeColors.getSky() != null) {
			return biomeColors.getSky().getRGB();
		}
		return super.getSkyColorByTemp(f);
	}

	public List<SpawnListEntry> getSpawnableTESAmbientList() {
		return spawnableTESAmbientList;
	}

	@Override
	public List<SpawnListEntry> getSpawnableList(EnumCreatureType creatureType) {
		if (creatureType == CREATURE_TYPE_TES_AMBIENT) {
			return spawnableTESAmbientList;
		}
		return super.getSpawnableList(creatureType);
	}

	public int getTopBlockMeta() {
		return topBlockMeta;
	}

	public void setTopBlockMeta(int topBlockMeta) {
		this.topBlockMeta = topBlockMeta;
	}

	public WorldGenAbstractTree getTreeGen(World world, Random random, int i, int k) {
		TESWorldChunkManager chunkManager = (TESWorldChunkManager) world.getWorldChunkManager();
		TESBiomeVariant variant = chunkManager.getBiomeVariantAt(i, k);
		TESTreeType tree = decorator.getRandomTreeForVariant(random, variant);
		return tree.create(false, random);
	}

	public TESEventSpawner.EventChance getBanditChance() {
		return banditChance;
	}

	public float getVariantChance() {
		return variantChance;
	}

	public void setVariantChance(float variantChance) {
		this.variantChance = variantChance;
	}

	public TESBezierType getWallBlock() {
		return TESBezierType.WALL_ICE;
	}

	public int getWallTop() {
		return 0;
	}

	@Override
	public TESBiome setBiomeName(String s) {
		return (TESBiome) super.setBiomeName(s);
	}

	@Override
	public TESBiome setColor(int color) {
		int color1 = color;
		color1 |= 0xFF000000;
		Integer existingBiomeID = biomeDimension.getColorsToBiomeIDs().get(color1);
		if (existingBiomeID != null) {
			throw new RuntimeException("TES biome (ID " + biomeID + ") is duplicating the color of another TES biome (ID " + existingBiomeID + ')');
		}
		biomeDimension.getColorsToBiomeIDs().put(color1, biomeID);
		return (TESBiome) super.setColor(color1);
	}

	public TESBiome setMinMaxHeight(float f, float f1) {
		float f2 = f;
		heightBaseParameter = f2;
		f2 -= 2.0f;
		rootHeight = f2 + 0.2f;
		heightVariation = f1 / 2.0f;
		return this;
	}

	@Override
	public TESBiome setTemperatureRainfall(float f, float f1) {
		super.setTemperatureRainfall(f, f1);
		return this;
	}

	protected void setupDesertFauna() {
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityCamel.class, 100, 1, 2));
		spawnableTESAmbientList.clear();
	}

	protected void setupExoticFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(TESBlocks.essosFlower, 0, 10));
		flowers.add(new FlowerEntry(TESBlocks.essosFlower, 1, 10));
		flowers.add(new FlowerEntry(TESBlocks.essosFlower, 3, 20));
		flowers.add(new FlowerEntry(TESBlocks.essosFlower, 3, 20));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityZebra.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityGemsbok.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityWhiteOryx.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityDikDik.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityGiraffe.class, 10, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityRabbit.class, 10, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityLion.class, 5, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityLioness.class, 5, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityRhino.class, 5, 1, 1));
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityButterfly.class, 50, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityBird.class, 30, 2, 3));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityGorcrow.class, 20, 2, 3));
	}

	protected void setupFrostFauna() {
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCaveCreatureList.clear();
		spawnableTESAmbientList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntitySnowBear.class, 60, 1, 1));
	}

	protected void setupJungleFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(Blocks.yellow_flower, 0, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 0, 10));
		flowers.add(new FlowerEntry(TESBlocks.essosFlower, 3, 20));
		flowers.add(new FlowerEntry(TESBlocks.essosFlower, 3, 20));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityFlamingo.class, 100, 2, 3));
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityButterfly.class, 60, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityBird.class, 40, 2, 3));
	}

	protected void setupMarshFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(TESBlocks.deadMarshPlant, 0, 10));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBeaver.class, 40, 1, 1));
		spawnableWaterCreatureList.clear();
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityMidges.class, 90, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntitySwan.class, 10, 1, 2));
	}

	protected void setupStandardDomesticFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(Blocks.red_flower, 4, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 5, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 6, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 7, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 0, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 3, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 8, 20));
		flowers.add(new FlowerEntry(Blocks.yellow_flower, 0, 30));
		flowers.add(new FlowerEntry(TESBlocks.bluebell, 0, 5));
		flowers.add(new FlowerEntry(TESBlocks.marigold, 0, 10));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityHorse.class, 30, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 20, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityPig.class, 15, 2, 4));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityRabbit.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityCow.class, 10, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 1, 2));
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityButterfly.class, 50, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityBird.class, 30, 2, 3));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityGorcrow.class, 5, 2, 3));
	}

	protected void setupStandardForestFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(Blocks.yellow_flower, 0, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 0, 10));
		flowers.add(new FlowerEntry(TESBlocks.bluebell, 0, 5));
		flowers.add(new FlowerEntry(TESBlocks.marigold, 0, 10));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityDeer.class, 30, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBoar.class, 20, 2, 3));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityRabbit.class, 20, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBear.class, 10, 1, 1));
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityButterfly.class, 50, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityBird.class, 30, 2, 3));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityGorcrow.class, 5, 2, 3));
		spawnableCreatureList.add(new SpawnListEntry(TESEntityBison.class, 20, 1, 2));
		
	}

	protected void setupStandardPlainsFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(Blocks.red_flower, 4, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 5, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 6, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 7, 3));
		flowers.add(new FlowerEntry(Blocks.red_flower, 0, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 3, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 8, 20));
		flowers.add(new FlowerEntry(Blocks.yellow_flower, 0, 30));
		flowers.add(new FlowerEntry(TESBlocks.bluebell, 0, 5));
		flowers.add(new FlowerEntry(TESBlocks.marigold, 0, 10));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityHorse.class, 30, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntitySheep.class, 20, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBoar.class, 15, 2, 3));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityRabbit.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 1, 1));
		spawnableTESAmbientList.clear();
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityButterfly.class, 50, 4, 4));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityBird.class, 30, 2, 3));
		spawnableTESAmbientList.add(new BiomeGenBase.SpawnListEntry(TESEntityGorcrow.class, 5, 2, 3));
		spawnableCreatureList.add(new SpawnListEntry(TESEntityBison.class, 10, 1, 2));
		
	}

	protected void setupTaigaFauna() {
		flowers.clear();
		flowers.add(new FlowerEntry(Blocks.yellow_flower, 0, 20));
		flowers.add(new FlowerEntry(Blocks.red_flower, 0, 10));
		flowers.add(new FlowerEntry(TESBlocks.bluebell, 0, 5));
		flowers.add(new FlowerEntry(TESBlocks.marigold, 0, 10));
		spawnableCreatureList.clear();
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityDeer.class, 30, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBoar.class, 20, 2, 3));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBison.class, 15, 1, 2));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityBear.class, 15, 1, 1));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityWoolyRhino.class, 10, 1, 1));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(TESEntityMammoth.class, 10, 1, 1));
		spawnableTESAmbientList.clear();
	}

	public int spawnCountMultiplier() {
		return 1;
	}

	public TESBiomeSpawnList getNPCSpawnList() {
		return npcSpawnList;
	}

	public TESBiomeDecorator getDecorator() {
		return decorator;
	}

	public interface Mountains {
		void generateMountainTerrain(World world, Random random, Block[] blocks, byte[] meta, int i, int k, int xzIndex, int ySize, int height, int rockDepth, TESBiomeVariant variant);
	}

	public interface Desert {
	}

	public interface Marshes {
	}

	public interface ImmuneToFrost {
	}

	public interface ImmuneToHeat {
	}

	public static class BiomeColors {
		private static final int DEFAULT_WATER = 7186907;

		private final TESBiome biome;

		private Color grass;
		private Color foliage;
		private Color sky;
		private Color clouds;
		private Color fog;

		private boolean foggy;
		private boolean customWater;

		private BiomeColors(TESBiome biome) {
			this.biome = biome;
		}

		public boolean hasCustomWater() {
			return customWater;
		}

		public void setWater(Color rgb) {
			biome.waterColorMultiplier = rgb.getRGB();
			if (rgb.getRGB() != DEFAULT_WATER) {
				customWater = true;
			}
		}

		public void updateWater(int rgb) {
			biome.waterColorMultiplier = rgb;
		}

		public Color getGrass() {
			return grass;
		}

		public void setGrass(Color grass) {
			this.grass = grass;
		}

		public Color getFoliage() {
			return foliage;
		}

		public void setFoliage(Color foliage) {
			this.foliage = foliage;
		}

		public Color getSky() {
			return sky;
		}

		public void setSky(Color sky) {
			this.sky = sky;
		}

		public Color getClouds() {
			return clouds;
		}

		public void setClouds(Color clouds) {
			this.clouds = clouds;
		}

		public Color getFog() {
			return fog;
		}

		public void setFog(Color fog) {
			this.fog = fog;
		}

		public boolean isFoggy() {
			return foggy;
		}

		public void setFoggy(boolean flag) {
			foggy = flag;
		}
	}

	public static class GrassBlockAndMeta {
		private final Block block;
		private final int meta;

		public GrassBlockAndMeta(Block b, int i) {
			block = b;
			meta = i;
		}

		public Block getBlock() {
			return block;
		}

		public int getMeta() {
			return meta;
		}
	}
}