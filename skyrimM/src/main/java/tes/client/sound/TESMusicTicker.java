package tes.client.sound;

import tes.client.TESClientProxy;
import tes.common.TESConfig;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.Random;

public class TESMusicTicker {
	private static TESMusicTrack currentTrack;
	private static boolean wasPlayingMenu = true;
	private static int timing = 100;

	private TESMusicTicker() {
	}

	private static TESMusicCategory getCurrentCategory(Minecraft mc) {
		WorldClient world = mc.theWorld;
		EntityClientPlayerMP entityplayer = mc.thePlayer;
		if (world != null && entityplayer != null) {
			int k = MathHelper.floor_double(entityplayer.posZ);
			int j = MathHelper.floor_double(entityplayer.boundingBox.minY);
			int i = MathHelper.floor_double(entityplayer.posX);
			if (TESMusicCategory.isCave(world, i, j, k)) {
				return TESMusicCategory.CAVE;
			}
			if (TESMusicCategory.isDay(world)) {
				return TESMusicCategory.DAY;
			}
			return TESMusicCategory.NIGHT;
		}
		return null;
	}

	private static TESMusicRegion.Sub getCurrentRegion(Minecraft mc) {
		WorldClient worldClient = mc.theWorld;
		EntityClientPlayerMP entityClientPlayerMP = mc.thePlayer;
		if (TESMusic.isMenuMusic()) {
			return TESMusicRegion.MENU.getWithoutSub();
		}
		if (TESMusic.isTESDimension()) {
			int i = MathHelper.floor_double(entityClientPlayerMP.posX);
			int k = MathHelper.floor_double(entityClientPlayerMP.posZ);
			if (TESClientProxy.doesClientChunkExist(worldClient, i, k)) {
				BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldClient, i, k);
				if (biome instanceof TESBiome) {
					TESBiome tesbiome = (TESBiome) biome;
					return tesbiome.getBiomeMusic();
				}
			}
		}
		return null;
	}

	private static TESMusicTrack getNewTrack(Minecraft mc, Random rand) {
		TESMusicRegion.Sub regionSub = getCurrentRegion(mc);
		TESMusicCategory category = getCurrentCategory(mc);
		if (regionSub != null) {
			TESMusicRegion region = regionSub.getRegion();
			String sub = regionSub.getSubregion();
			TESTrackSorter.Filter filter = category != null ? TESTrackSorter.forRegionAndCategory(region, category) : TESTrackSorter.forAny();
			TESRegionTrackPool trackPool = TESMusic.getTracksForRegion(region, sub);
			return trackPool.getRandomTrack(rand, filter);
		}
		return null;
	}

	private static void resetTiming(Random rand) {
		timing = TESMusic.isMenuMusic() ? MathHelper.getRandomIntegerInRange(rand, TESConfig.musicIntervalMenuMin * 20, TESConfig.musicIntervalMenuMax * 20) : MathHelper.getRandomIntegerInRange(rand, TESConfig.musicIntervalMin * 20, TESConfig.musicIntervalMax * 20);
	}

	public static void update(Random rand) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean noMusic = mc.gameSettings.getSoundLevel(SoundCategory.MUSIC) <= 0.0f;
		boolean menu = TESMusic.isMenuMusic();
		if (wasPlayingMenu != menu) {
			if (currentTrack != null) {
				mc.getSoundHandler().stopSound(currentTrack);
				currentTrack = null;
			}
			wasPlayingMenu = menu;
			timing = 100;
		}
		if (currentTrack != null) {
			if (noMusic) {
				mc.getSoundHandler().stopSound(currentTrack);
			}
			if (!mc.getSoundHandler().isSoundPlaying(currentTrack)) {
				currentTrack = null;
				resetTiming(rand);
			}
		}
		if (!noMusic) {
			boolean update = menu || TESMusic.isTESDimension() && !Minecraft.getMinecraft().isGamePaused();
			if (update && currentTrack == null) {
				--timing;
				if (timing <= 0) {
					currentTrack = getNewTrack(mc, rand);
					if (currentTrack != null) {
						wasPlayingMenu = menu;
						mc.getSoundHandler().playSound(currentTrack);
						timing = Integer.MAX_VALUE;
					} else {
						timing = 400;
					}
				}
			}
		}
	}

	public static TESMusicTrack getCurrentTrack() {
		return currentTrack;
	}
}