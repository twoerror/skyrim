package tes.client.sound;

import java.util.ArrayList;
import java.util.List;

public class TESTrackRegionInfo {
	private final List<String> subregions = new ArrayList<>();
	private final List<TESMusicCategory> categories = new ArrayList<>();

	private final TESMusicRegion region;

	private double weight;

	public TESTrackRegionInfo(TESMusicRegion r) {
		region = r;
		weight = 1.0;
	}

	public void addAllCategories() {
		for (TESMusicCategory cat : TESMusicCategory.values()) {
			addCategory(cat);
		}
	}

	public void addAllSubregions() {
		List<String> allSubs = region.getAllSubregions();
		if (!allSubs.isEmpty()) {
			for (String sub : allSubs) {
				addSubregion(sub);
			}
		}
	}

	public void addCategory(TESMusicCategory cat) {
		if (!categories.contains(cat)) {
			categories.add(cat);
		}
	}

	public void addSubregion(String sub) {
		if (!subregions.contains(sub)) {
			subregions.add(sub);
		}
	}

	public List<TESMusicCategory> getCategories() {
		return categories;
	}

	public List<String> getSubregions() {
		return subregions;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double d) {
		weight = d;
	}
}