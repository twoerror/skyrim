package tes.common.fellowship;

import cpw.mods.fml.common.FMLLog;
import tes.common.TESLevelData;
import tes.common.util.TESLog;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TESFellowshipData {
	private static final Map<UUID, TESFellowship> FELLOWSHIP_MAP = new HashMap<>();

	private static boolean needsLoad = true;

	private TESFellowshipData() {
	}

	public static void addFellowship(TESFellowship fs) {
		if (!FELLOWSHIP_MAP.containsKey(fs.getFellowshipID())) {
			FELLOWSHIP_MAP.put(fs.getFellowshipID(), fs);
		}
	}

	public static boolean anyDataNeedsSave() {
		for (TESFellowship fs : FELLOWSHIP_MAP.values()) {
			if (!fs.needsSave()) {
				continue;
			}
			return true;
		}
		return false;
	}

	private static void destroyAllFellowshipData() {
		FELLOWSHIP_MAP.clear();
	}

	public static TESFellowship getActiveFellowship(UUID fsID) {
		TESFellowship fs = getFellowship(fsID);
		if (fs != null && fs.isDisbanded()) {
			return null;
		}
		return fs;
	}

	public static TESFellowship getFellowship(UUID fsID) {
		TESFellowship fs = FELLOWSHIP_MAP.get(fsID);
		if (fs == null && (fs = loadFellowship(fsID)) != null) {
			FELLOWSHIP_MAP.put(fsID, fs);
		}
		return fs;
	}

	private static File getFellowshipDat(UUID fsID) {
		return new File(getFellowshipDir(), fsID.toString() + ".dat");
	}

	private static File getFellowshipDir() {
		File fsDir = new File(TESLevelData.getOrCreateTESDir(), "fellowships");
		if (!fsDir.exists()) {
			boolean created = fsDir.mkdirs();
			if (!created) {
				TESLog.getLogger().info("TESFellowshipData: directory wasn't created");
			}
		}
		return fsDir;
	}

	public static void loadAll() {
		try {
			destroyAllFellowshipData();
			needsLoad = false;
			saveAll();
		} catch (Exception e) {
			FMLLog.severe("Error loading TES fellowship data");
			e.printStackTrace();
		}
	}

	private static TESFellowship loadFellowship(UUID fsID) {
		File fsDat = getFellowshipDat(fsID);
		try {
			NBTTagCompound nbt = TESLevelData.loadNBTFromFile(fsDat);
			if (nbt.hasNoTags()) {
				return null;
			}
			TESFellowship fs = new TESFellowship(fsID);
			fs.load(nbt);
			return fs;
		} catch (Exception e) {
			FMLLog.severe("Error loading TES fellowship data for %s", fsDat.getName());
			e.printStackTrace();
			return null;
		}
	}

	public static void saveAll() {
		try {
			for (TESFellowship fs : FELLOWSHIP_MAP.values()) {
				if (!fs.needsSave()) {
					continue;
				}
				saveFellowship(fs);
			}
		} catch (Exception e) {
			FMLLog.severe("Error saving TES fellowship data");
			e.printStackTrace();
		}
	}

	private static void saveFellowship(TESFellowship fs) {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			fs.save(nbt);
			TESLevelData.saveNBTToFile(getFellowshipDat(fs.getFellowshipID()), nbt);
		} catch (Exception e) {
			FMLLog.severe("Error saving TES fellowship data for %s", fs.getFellowshipID());
			e.printStackTrace();
		}
	}

	public static boolean isNeedsLoad() {
		return needsLoad;
	}

	public static void setNeedsLoad(boolean needsLoad) {
		TESFellowshipData.needsLoad = needsLoad;
	}
}