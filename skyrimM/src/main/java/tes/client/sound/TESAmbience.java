package tes.client.sound;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tes.TES;
import tes.client.TESTickHandlerClient;
import tes.client.render.other.TESWeatherRenderer;
import tes.common.TESConfig;
import tes.common.util.TESCrashHandler;
import tes.common.world.TESWorldProvider;
import tes.common.world.biome.other.TESBiomeOcean;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class TESAmbience {
	public static final TESAmbience INSTANCE = new TESAmbience();

	private final Collection<ISound> playingWindSounds = new ArrayList<>();
	private final Collection<ISound> playingSeaSounds = new ArrayList<>();

	private int ticksSinceWight;

	private TESAmbience() {
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onPlaySound(PlaySoundEvent17 event) {
		String name = event.name;
		ISound sound = event.sound;
		if (sound instanceof PositionedSound) {
			WorldClient world = Minecraft.getMinecraft().theWorld;
			if (world != null && world.provider instanceof TESWorldProvider) {
				if ("ambient.weather.rain".equals(name)) {
					event.result = new PositionedSoundRecord(new ResourceLocation("tes:ambient.weather.rain"), sound.getVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
				} else if ("ambient.weather.thunder".equals(name)) {
					event.result = new PositionedSoundRecord(new ResourceLocation("tes:ambient.weather.thunder"), sound.getVolume(), sound.getPitch(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
				}
			}
		}
	}

	public void updateAmbience(World world, EntityPlayer entityplayer) {
		Minecraft mc;
		block42:
		{
			int xzRange;
			world.theProfiler.startSection("tesAmbience");
			mc = Minecraft.getMinecraft();
			boolean enableAmbience = TESConfig.enableAmbience;
			double x = entityplayer.posX;
			double y = entityplayer.boundingBox.minY;
			double z = entityplayer.posZ;
			int i = MathHelper.floor_double(x);
			int j = MathHelper.floor_double(y);
			int k = MathHelper.floor_double(z);
			BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(world, i, k);
			Random rand = world.rand;
			if (enableAmbience) {
				if (ticksSinceWight > 0) {
					--ticksSinceWight;
				} else {
					boolean wights = TESTickHandlerClient.isAnyWightsViewed() && rand.nextInt(20) == 0 || rand.nextInt(3000) == 0;
					if (wights) {
						world.playSound(x, y, z, "tes:wight.ambience", 1.0f, 0.8f + rand.nextFloat() * 0.4f, false);
						ticksSinceWight = 300;
					}
				}
				boolean spookyBiomeNoise = false;
				float spookyPitch = 1.0f;
			}
			if (world.provider instanceof TESWorldProvider) {
				if (playingWindSounds.size() < 4) {
					xzRange = 16;
					int minWindHeight = 100;
					int fullWindHeight = 180;
					if (rand.nextInt(20) == 0) {
						minWindHeight -= 10;
						if (rand.nextInt(10) == 0) {
							minWindHeight -= 10;
						}
					}
					if (world.isRaining()) {
						minWindHeight = 80;
						fullWindHeight = 120;
						if (rand.nextInt(20) == 0) {
							minWindHeight -= 20;
						}
						if (TESWeatherRenderer.isSandstormBiome(biome)) {
							minWindHeight = 60;
							fullWindHeight = 80;
						}
					}
					for (int l = 0; l < 2; ++l) {
						int i1 = i + MathHelper.getRandomIntegerInRange(rand, -xzRange, xzRange);
						int k1 = k + MathHelper.getRandomIntegerInRange(rand, -xzRange, xzRange);
						int j1 = j + MathHelper.getRandomIntegerInRange(rand, -16, 16);
						if (j1 < minWindHeight || !world.canBlockSeeTheSky(i1, j1, k1)) {
							continue;
						}
						float windiness = (float) (j1 - minWindHeight) / (fullWindHeight - minWindHeight);
						if ((windiness = MathHelper.clamp_float(windiness, 0.0f, 1.0f)) < rand.nextFloat()) {
							continue;
						}
						float x1 = i1 + 0.5f;
						float y1 = j1 + 0.5f;
						float z1 = k1 + 0.5f;
						float vol = Math.max(0.25f, windiness);
						float pitch = 0.8f + rand.nextFloat() * 0.4f;
						AmbientSoundNoAttentuation wind = new AmbientSoundNoAttentuation(new ResourceLocation("tes:ambient.weather.wind"), vol, pitch, x1, y1, z1).calcAmbientVolume(entityplayer, xzRange);
						mc.getSoundHandler().playSound(wind);
						playingWindSounds.add(wind);
						break;
					}
				} else {
					Collection<ISound> removes = new HashSet<>();
					for (ISound wind : playingWindSounds) {
						if (mc.getSoundHandler().isSoundPlaying(wind)) {
							continue;
						}
						removes.add(wind);
					}
					playingWindSounds.removeAll(removes);
				}
			}
			if (enableAmbience) {
				if (playingSeaSounds.size() < 3) {
					if (biome instanceof TESBiomeOcean) {
						float[] rangeChecks = {0.25f, 0.5f, 0.75f, 1.0f};
						xzRange = 64;
						for (float fr : rangeChecks) {
							int range = (int) (xzRange * fr);
							for (int l = 0; l < 8; ++l) {
								int i1 = i + MathHelper.getRandomIntegerInRange(rand, -range, range);
								int k1 = k + MathHelper.getRandomIntegerInRange(rand, -range, range);
								int j1 = j + MathHelper.getRandomIntegerInRange(rand, -16, 8);
								Block block = world.getBlock(i1, j1, k1);
								if (block.getMaterial() != Material.water || j1 < world.getTopSolidOrLiquidBlock(i1, k1)) {
									continue;
								}
								float x1 = i1 + 0.5f;
								float y1 = j1 + 0.5f;
								float z1 = k1 + 0.5f;
								float vol = 1.0f;
								float pitch = 0.8f + rand.nextFloat() * 0.4f;
								AmbientSoundNoAttentuation sea = new AmbientSoundNoAttentuation(new ResourceLocation("tes:ambient.terrain.sea"), vol, pitch, x1, y1, z1).calcAmbientVolume(entityplayer, xzRange);
								mc.getSoundHandler().playSound(sea);
								playingSeaSounds.add(sea);
								int j2 = world.getHeightValue(i1, k1) - 1;
								if (world.getBlock(i1, j2, k1).getMaterial() == Material.water) {
									double dx = i1 + 0.5 - entityplayer.posX;
									double dz = k1 + 0.5 - entityplayer.posZ;
									float angle = (float) Math.atan2(dz, dx);
									float cos = MathHelper.cos(angle);
									float sin = MathHelper.sin(angle);
									float angle90 = angle - 1.5707963267948966f;
									float cos90 = MathHelper.cos(angle90);
									float sin90 = MathHelper.sin(angle90);
									float waveSpeed = MathHelper.randomFloatClamp(rand, 0.3f, 0.5f);
									int waveR = 40 + rand.nextInt(100);
									for (int w = -waveR; w <= waveR; ++w) {
										float f = w / 8.0f;
										double d0 = i1 + 0.5;
										double d1 = j2 + 1.0 + MathHelper.randomFloatClamp(rand, 0.02f, 0.1f);
										double d2 = k1 + 0.5;
										if (world.getBlock(MathHelper.floor_double(d0 += f * cos90), MathHelper.floor_double(d1) - 1, MathHelper.floor_double(d2 += f * sin90)).getMaterial() != Material.water) {
											continue;
										}
										double d3 = waveSpeed * -cos;
										double d4 = 0.0;
										double d5 = waveSpeed * -sin;
										TES.proxy.spawnParticle("wave", d0, d1, d2, d3, d4, d5);
									}
								}
								break block42;
							}
						}
					}
				} else {
					Collection<ISound> removes = new HashSet<>();
					for (ISound sea : playingSeaSounds) {
						if (mc.getSoundHandler().isSoundPlaying(sea)) {
							continue;
						}
						removes.add(sea);
					}
					playingSeaSounds.removeAll(removes);
				}
			}
		}
		world.theProfiler.endSection();
	}

	private static class AmbientSoundNoAttentuation extends PositionedSoundRecord {
		private AmbientSoundNoAttentuation(ResourceLocation sound, float vol, float pitch, float x, float y, float z) {
			super(sound, vol, pitch, x, y, z);
			field_147666_i = ISound.AttenuationType.NONE;
		}

		private AmbientSoundNoAttentuation calcAmbientVolume(EntityPlayer entityplayer, int maxRange) {
			float distFr = (float) entityplayer.getDistance(xPosF, yPosF, zPosF);
			distFr /= maxRange;
			distFr = Math.min(distFr, 1.0f);
			distFr = 1.0f - distFr;
			distFr *= 1.5f;
			distFr = MathHelper.clamp_float(distFr, 0.1f, 1.0f);
			volume *= distFr;
			return this;
		}
	}
}