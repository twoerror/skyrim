package tes.client.sound;

import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class TESMusicTrack extends PositionedSound {
	private final List<String> authors = new ArrayList<>();
	private final Map<TESMusicRegion, TESTrackRegionInfo> regions = new EnumMap<>(TESMusicRegion.class);

	private final String filename;

	private String title;

	public TESMusicTrack(String s) {
		super(getMusicResource(s));
		volume = 1.0f;
		field_147663_c = 1.0f;
		xPosF = 0.0f;
		yPosF = 0.0f;
		zPosF = 0.0f;
		repeat = false;
		field_147665_h = 0;
		field_147666_i = ISound.AttenuationType.NONE;
		filename = s;
	}

	private static ResourceLocation getMusicResource(String s) {
		return new ResourceLocation("musicpacks", s);
	}

	public void addAuthor(String s) {
		authors.add(s);
	}

	public TESTrackRegionInfo createRegionInfo(TESMusicRegion reg) {
		TESTrackRegionInfo info = regions.get(reg);
		if (info == null) {
			info = new TESTrackRegionInfo(reg);
			regions.put(reg, info);
		}
		return info;
	}

	public Set<TESMusicRegion> getAllRegions() {
		return regions.keySet();
	}

	public List<String> getAuthors() {
		return authors;
	}

	public TESTrackRegionInfo getRegionInfo(TESMusicRegion reg) {
		if (regions.containsKey(reg)) {
			return regions.get(reg);
		}
		return null;
	}

	public String getTitle() {
		if (title != null) {
			return title;
		}
		return filename;
	}

	public void setTitle(String s) {
		title = s;
	}

	private void loadSoundResource() {
		SoundEventAccessorComposite soundAccessorComp;
		ResourceLocation resource = getPositionedSoundLocation();
		SoundList soundList = new SoundList();
		soundList.setReplaceExisting(true);
		soundList.setSoundCategory(SoundCategory.MUSIC);
		SoundList.SoundEntry soundEntry = new SoundList.SoundEntry();
		soundEntry.setSoundEntryName(filename);
		soundEntry.setSoundEntryVolume(getVolume());
		soundEntry.setSoundEntryPitch(getPitch());
		soundEntry.setSoundEntryWeight(1);
		soundEntry.setSoundEntryType(SoundList.SoundEntry.Type.SOUND_EVENT);
		soundEntry.setStreaming(true);
		soundList.getSoundList().add(soundEntry);
		SoundRegistry sndRegistry = TESMusic.Reflect.getSoundRegistry();
		if (sndRegistry.containsKey(resource) && !soundList.canReplaceExisting()) {
			soundAccessorComp = (SoundEventAccessorComposite) sndRegistry.getObject(resource);
		} else {
			soundAccessorComp = new SoundEventAccessorComposite(resource, 1.0, 1.0, soundList.getSoundCategory());
			sndRegistry.registerSound(soundAccessorComp);
		}
		SoundPoolEntry soundPoolEntry = new SoundPoolEntry(resource, soundEntry.getSoundEntryPitch(), soundEntry.getSoundEntryVolume(), soundEntry.isStreaming());
		ISoundEventAccessor soundAccessor = new TrackSoundAccessor(soundPoolEntry, soundEntry.getSoundEntryWeight());
		soundAccessorComp.addSoundToEventPool(soundAccessor);
	}

	public void loadTrack() {
		loadSoundResource();
		TESMusic.addTrackToRegions(this);
	}

	private static class TrackSoundAccessor implements ISoundEventAccessor {
		private final SoundPoolEntry soundEntry;
		private final int weight;

		private TrackSoundAccessor(SoundPoolEntry e, int i) {
			soundEntry = e;
			weight = i;
		}

		@Override
		public SoundPoolEntry func_148720_g() {
			return new SoundPoolEntry(soundEntry);
		}

		@Override
		public int func_148721_a() {
			return weight;
		}
	}
}