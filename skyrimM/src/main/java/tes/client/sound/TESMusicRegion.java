package tes.client.sound;

import java.util.ArrayList;
import java.util.List;

public enum TESMusicRegion {
	MENU("menu"), ESSOS("essos"), WESTEROS("westeros"), ULTHOS("ulthos"), SOTHORYOS("sothoryos"), OCEAN("ocean");

	private final String regionName;
	private final List<String> subregions = new ArrayList<>();

	TESMusicRegion(String s) {
		regionName = s;
	}

	public static TESMusicRegion forName(String s) {
		for (TESMusicRegion r : values()) {
			if (!s.equalsIgnoreCase(r.regionName)) {
				continue;
			}
			return r;
		}
		return null;
	}

	public Sub getSubregion(String s) {
		if (s != null && !subregions.contains(s)) {
			subregions.add(s);
		}
		return new Sub(this, s);
	}

	public Sub getWithoutSub() {
		return new Sub(this, null);
	}

	public boolean hasSubregion(String s) {
		return subregions.contains(s);
	}

	public List<String> getAllSubregions() {
		return subregions;
	}

	public boolean hasNoSubregions() {
		return subregions.isEmpty();
	}

	public String getRegionName() {
		return regionName;
	}

	public static class Sub {
		private final TESMusicRegion region;
		private final String subregion;

		protected Sub(TESMusicRegion r, String s) {
			region = r;
			subregion = s;
		}

		public TESMusicRegion getRegion() {
			return region;
		}

		public String getSubregion() {
			return subregion;
		}
	}
}