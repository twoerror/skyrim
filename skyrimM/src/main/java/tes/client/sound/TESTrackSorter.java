package tes.client.sound;

import java.util.ArrayList;
import java.util.List;

public class TESTrackSorter {
	private TESTrackSorter() {
	}

	public static Filter forAny() {
		return track -> true;
	}

	public static Filter forRegionAndCategory(TESMusicRegion reg, TESMusicCategory cat) {
		return track -> track.getRegionInfo(reg).getCategories().contains(cat);
	}

	public static List<TESMusicTrack> sortTracks(Iterable<TESMusicTrack> tracks, Filter filter) {
		List<TESMusicTrack> sorted = new ArrayList<>();
		for (TESMusicTrack track : tracks) {
			if (filter.accept(track)) {
				sorted.add(track);
			}
		}
		return sorted;
	}

	public interface Filter {
		boolean accept(TESMusicTrack var1);
	}
}