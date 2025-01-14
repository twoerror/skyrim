package tes.common.world;

import com.google.common.math.IntMath;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.client.render.other.TESCloudRenderer;
import tes.client.render.other.TESSkyRenderer;
import tes.client.render.other.TESWeatherRenderer;
import tes.common.*;
import tes.common.util.TESCrashHandler;
import tes.common.util.TESModChecker;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.TESClimateType;
import tes.common.world.biome.other.TESBiomeOcean;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.common.ForgeModContainer;

public class TESWorldProvider extends WorldProvider {
	public static final int MOON_PHASES = 8;

	@SideOnly(Side.CLIENT)
	private IRenderHandler TESSkyRenderer;

	@SideOnly(Side.CLIENT)
	private IRenderHandler TESCloudRenderer;

	@SideOnly(Side.CLIENT)
	private IRenderHandler TESWeatherRenderer;

	public static int getTESMoonPhase() {
		int day = TESDate.AegonCalendar.getCurrentDay();
		return IntMath.mod(day, MOON_PHASES);
	}

	public static boolean isLunarEclipse() {
		int day = TESDate.AegonCalendar.getCurrentDay();
		return getTESMoonPhase() == 0 && IntMath.mod(day / MOON_PHASES, 4) == 3;
	}

	public static float[] handleFinalFogColors(float[] rgb) {
		return rgb;
	}

	public static void setRingPortalLocation(int i, int j, int k) {
		TESLevelData.markGameOfThronesPortalLocation(i, j, k);
	}

	@Override
	public float calculateCelestialAngle(long time, float partialTick) {
		float daytime = ((int) (time % TESTime.DAY_LENGTH) + partialTick) / TESTime.DAY_LENGTH - 0.25f;
		if (daytime < 0.0f) {
			daytime += 1.0f;
		}
		if (daytime > 1.0f) {
			daytime -= 1.0f;
		}
		float angle = 1.0f - (float) ((Math.cos(daytime * 3.141592653589793) + 1.0) / 2.0);
		return daytime + (angle - daytime) / 3.0f;
	}

	@Override
	public boolean canBlockFreeze(int i, int j, int k, boolean isBlockUpdate) {
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		if (biome instanceof TESBiomeOcean) {
			return TESBiomeOcean.isFrozen(k) && canFreezeIgnoreTemp(i, j, k, isBlockUpdate);
		}
		boolean standardColdBiome = biome instanceof TESBiome && ((TESBiome) biome).getClimateType() == TESClimateType.WINTER;
		boolean altitudeColdBiome = biome instanceof TESBiome && ((TESBiome) biome).getClimateType() != null && ((TESBiome) biome).getClimateType().isAltitudeZone() && k >= 140;
		return (standardColdBiome || altitudeColdBiome) && worldObj.canBlockFreezeBody(i, j, k, isBlockUpdate);
	}

	private boolean canFreezeIgnoreTemp(int i, int j, int k, boolean isBlockUpdate) {
		Block block;
		if (j >= 0 && j < worldObj.getHeight() && worldObj.getSavedLightValue(EnumSkyBlock.Block, i, j, k) < 10 && ((block = worldObj.getBlock(i, j, k)) == Blocks.water || block == Blocks.flowing_water) && worldObj.getBlockMetadata(i, j, k) == 0) {
			if (!isBlockUpdate) {
				return true;
			}
			boolean surroundWater = worldObj.getBlock(i - 1, j, k).getMaterial() == Material.water;
			if (surroundWater && worldObj.getBlock(i + 1, j, k).getMaterial() != Material.water) {
				surroundWater = false;
			}
			if (surroundWater && worldObj.getBlock(i, j, k - 1).getMaterial() != Material.water) {
				surroundWater = false;
			}
			if (surroundWater && worldObj.getBlock(i, j, k + 1).getMaterial() != Material.water) {
				surroundWater = false;
			}
			return !surroundWater;
		}
		return false;
	}

	@Override
	public boolean canSnowAt(int i, int j, int k, boolean checkLight) {
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		if (biome instanceof TESBiomeOcean) {
			return TESBiomeOcean.isFrozen(k) && canSnowIgnoreTemp(i, j, k, checkLight);
		}
		boolean standardColdBiome = biome instanceof TESBiome && ((TESBiome) biome).getClimateType() == TESClimateType.WINTER;
		boolean altitudeColdBiome = biome instanceof TESBiome && ((TESBiome) biome).getClimateType() != null && ((TESBiome) biome).getClimateType().isAltitudeZone() && k >= 140;
		return (standardColdBiome || altitudeColdBiome) && worldObj.canSnowAtBody(i, j, k, checkLight);
	}

	private boolean canSnowIgnoreTemp(int i, int j, int k, boolean checkLight) {
		return !checkLight || j >= 0 && j < worldObj.getHeight() && worldObj.getSavedLightValue(EnumSkyBlock.Block, i, j, k) < 10 && worldObj.getBlock(i, j, k).getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(worldObj, i, j, k);
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new TESChunkProvider(worldObj, worldObj.getSeed());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int i, int k) {
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		if (biome instanceof TESBiome) {
			return ((TESBiome) biome).getBiomeColors().isFoggy();
		}
		return super.doesXZShowFog(i, k);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 drawClouds(float f) {
		Minecraft mc = Minecraft.getMinecraft();
		int i = (int) mc.renderViewEntity.posX;
		int k = (int) mc.renderViewEntity.posZ;
		Vec3 clouds = super.drawClouds(f);
		double cloudsB = 0.0;
		double cloudsG = 0.0;
		double cloudsR = 0.0;
		GameSettings settings = mc.gameSettings;
		int[] ranges = ForgeModContainer.blendRanges;
		int distance = 0;
		if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length) {
			distance = ranges[settings.renderDistanceChunks];
		}
		int l = 0;
		for (int i1 = -distance; i1 <= distance; ++i1) {
			for (int k1 = -distance; k1 <= distance; ++k1) {
				Vec3 tempClouds = Vec3.createVectorHelper(clouds.xCoord, clouds.yCoord, clouds.zCoord);
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i + i1, k + k1);
				if (biome instanceof TESBiome) {
					((TESBiome) biome).getCloudColor(tempClouds);
				}
				cloudsR += tempClouds.xCoord;
				cloudsG += tempClouds.yCoord;
				cloudsB += tempClouds.zCoord;
				++l;
			}
		}
		cloudsR /= l;
		cloudsG /= l;
		cloudsB /= l;
		return Vec3.createVectorHelper(cloudsR, cloudsG, cloudsB);
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int i, int k) {
		Chunk chunk;
		if (worldObj.blockExists(i, 0, k) && (chunk = worldObj.getChunkFromBlockCoords(i, k)) != null) {
			int chunkX = i & 0xF;
			int chunkZ = k & 0xF;
			int biomeID = chunk.getBiomeArray()[chunkZ << 4 | chunkX] & 0xFF;
			if (biomeID == 255) {
				BiomeGenBase biomegenbase = worldChunkMgr.getBiomeGenAt((chunk.xPosition << 4) + chunkX, (chunk.zPosition << 4) + chunkZ);
				biomeID = biomegenbase.biomeID;
				chunk.getBiomeArray()[chunkZ << 4 | chunkX] = (byte) (biomeID & 0xFF);
			}
			TESDimension dim = TESDimension.GAME_OF_THRONES;
			return dim.getBiomeList()[dim.getBiomeList()[biomeID] == null ? 0 : biomeID];
		}
		return worldChunkMgr.getBiomeGenAt(i, k);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight() {
		return 192.0f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer() {
		if (!TESModChecker.hasShaders() && TESConfig.cloudRange > 0) {
			if (TESCloudRenderer == null) {
				TESCloudRenderer = new TESCloudRenderer();
			}
			return TESCloudRenderer;
		}
		return super.getCloudRenderer();
	}

	@Override
	public String getDepartMessage() {
		return StatCollector.translateToLocalFormatted("TES.dimension.exit", TESDimension.GAME_OF_THRONES.getTranslatedDimensionName());
	}

	@Override
	public String getDimensionName() {
		return TESDimension.GAME_OF_THRONES.getDimensionName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float f, float f1) {
		Minecraft mc = Minecraft.getMinecraft();
		int i = (int) mc.renderViewEntity.posX;
		int k = (int) mc.renderViewEntity.posZ;
		Vec3 fog = super.getFogColor(f, f1);
		double fogB = 0.0;
		double fogG = 0.0;
		double fogR = 0.0;
		GameSettings settings = mc.gameSettings;
		int[] ranges = ForgeModContainer.blendRanges;
		int distance = 0;
		if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length) {
			distance = ranges[settings.renderDistanceChunks];
		}
		int l = 0;
		for (int i1 = -distance; i1 <= distance; ++i1) {
			for (int k1 = -distance; k1 <= distance; ++k1) {
				Vec3 tempFog = Vec3.createVectorHelper(fog.xCoord, fog.yCoord, fog.zCoord);
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i + i1, k + k1);
				if (biome instanceof TESBiome) {
					((TESBiome) biome).getFogColor(tempFog);
				}
				fogR += tempFog.xCoord;
				fogG += tempFog.yCoord;
				fogB += tempFog.zCoord;
				++l;
			}
		}
		fogR /= l;
		fogG /= l;
		fogB /= l;
		return Vec3.createVectorHelper(fogR, fogG, fogB);
	}

	@Override
	public int getMoonPhase(long time) {
		return getTESMoonPhase();
	}

	@Override
	public String getSaveFolder() {
		return TESDimension.GAME_OF_THRONES.getDimensionName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer() {
		if (!TESModChecker.hasShaders() && TESConfig.enableTESSky) {
			if (TESSkyRenderer == null) {
				TESSkyRenderer = new TESSkyRenderer();
			}
			return TESSkyRenderer;
		}
		return super.getSkyRenderer();
	}

	@Override
	public ChunkCoordinates getSpawnPoint() {
		return new ChunkCoordinates(TESLevelData.getGameOfThronesPortalX(), TESLevelData.getGameOfThronesPortalY(), TESLevelData.getGameOfThronesPortalZ());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getWeatherRenderer() {
		if (TESWeatherRenderer == null) {
			TESWeatherRenderer = new TESWeatherRenderer();
		}
		return TESWeatherRenderer;
	}

	@Override
	public String getWelcomeMessage() {
		return StatCollector.translateToLocalFormatted("TES.dimension.enter", TESDimension.GAME_OF_THRONES.getTranslatedDimensionName());
	}

	public float[] modifyFogIntensity(float farPlane, int fogMode) {
		Minecraft mc = Minecraft.getMinecraft();
		int i = (int) mc.renderViewEntity.posX;
		int k = (int) mc.renderViewEntity.posZ;
		float fogStart = 0.0f;
		float fogEnd = 0.0f;
		GameSettings settings = mc.gameSettings;
		int[] ranges = ForgeModContainer.blendRanges;
		int distance = 0;
		if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.length) {
			distance = ranges[settings.renderDistanceChunks];
		}
		int l = 0;
		for (int i1 = -distance; i1 <= distance; ++i1) {
			for (int k1 = -distance; k1 <= distance; ++k1) {
				float thisFogStart;
				float thisFogEnd;
				boolean foggy = doesXZShowFog(i + i1, k + k1);
				if (foggy) {
					thisFogStart = farPlane * 0.05f;
					thisFogEnd = Math.min(farPlane, 192.0f) * 0.5f;
				} else {
					if (fogMode < 0) {
						thisFogStart = 0.0f;
					} else {
						thisFogStart = farPlane * 0.75f;
					}
					thisFogEnd = farPlane;
				}
				fogStart += thisFogStart;
				fogEnd += thisFogEnd;
				++l;
			}
		}
		return new float[]{fogStart / l, fogEnd / l};
	}

	@Override
	public void registerWorldChunkManager() {
		worldChunkMgr = new TESWorldChunkManager(worldObj, TESDimension.GAME_OF_THRONES);
		dimensionId = TESDimension.GAME_OF_THRONES.getDimensionID();
	}

	@Override
	public void resetRainAndThunder() {
		super.resetRainAndThunder();
		if (TES.doDayCycle(worldObj)) {
			TESTime.advanceToMorning();
		}
	}

	@Override
	public void setSpawnPoint(int i, int j, int k) {
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z) {
		return false;
	}
}