package tes.client.sound;

import java.util.*;

public class TESRegionTrackPool {
	private final TESMusicRegion region;
	private final Collection<TESMusicTrack> trackList = new ArrayList<>();

	public TESRegionTrackPool(TESMusicRegion r) {
		region = r;
	}

	public void addTrack(TESMusicTrack track) {
		trackList.add(track);
	}

	public boolean isEmpty() {
		return trackList.isEmpty();
	}

	public TESMusicTrack getRandomTrack(Random rand, TESTrackSorter.Filter filter) {
		List<TESMusicTrack> sortedTracks = TESTrackSorter.sortTracks(trackList, filter);
		double totalWeight = 0.0;
		for (TESMusicTrack tesMusicTrack : sortedTracks) {
			double weight = tesMusicTrack.getRegionInfo(region).getWeight();
			totalWeight += weight;
		}
		double randWeight = rand.nextDouble();
		randWeight *= totalWeight;
		Iterator<TESMusicTrack> it = sortedTracks.iterator();
		TESMusicTrack track = null;
		do {
			if (!it.hasNext()) {
				return track;
			}
			track = it.next();
			double weight = track.getRegionInfo(region).getWeight();
			randWeight -= weight;
		} while (randWeight >= 0.0D);
		return track;
	}
}