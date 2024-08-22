package tes.common;

import cpw.mods.fml.common.FMLLog;
import tes.TES;
import tes.common.world.TESWorldInfo;
import tes.common.world.TESWorldProvider;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.nio.file.Files;

public class TESTime {
	public static final int DAY_LENGTH = 48000;

	private static long totalTime;
	private static long worldTime;

	private static boolean needsLoad = true;

	private TESTime() {
	}

	public static void addWorldTime(long time) {
		worldTime += time;
	}

	public static void advanceToMorning() {
		long l = worldTime + DAY_LENGTH;
		worldTime = l - l % DAY_LENGTH;
	}

	private static File getTimeDat() {
		return new File(TESLevelData.getOrCreateTESDir(), "TESTime.dat");
	}

	public static void load() {
		try {
			NBTTagCompound timeData = TESLevelData.loadNBTFromFile(getTimeDat());
			totalTime = timeData.getLong("TESTotalTime");
			worldTime = timeData.getLong("TESWorldTime");
			needsLoad = false;
			save();
		} catch (Exception e) {
			FMLLog.severe("Error loading TES time data");
			e.printStackTrace();
		}
	}

	public static void save() {
		try {
			File time_dat = getTimeDat();
			if (!time_dat.exists()) {
				CompressedStreamTools.writeCompressed(new NBTTagCompound(), Files.newOutputStream(time_dat.toPath()));
			}
			NBTTagCompound timeData = new NBTTagCompound();
			timeData.setLong("TESTotalTime", totalTime);
			timeData.setLong("TESWorldTime", worldTime);
			TESLevelData.saveNBTToFile(time_dat, timeData);
		} catch (Exception e) {
			FMLLog.severe("Error saving TES time data");
			e.printStackTrace();
		}
	}

	public static void update() {
		MinecraftServer server = MinecraftServer.getServer();
		WorldServer overworld = server.worldServerForDimension(0);
		if (TES.doDayCycle(overworld)) {
			++worldTime;
		}
		++totalTime;
		for (WorldServer world : server.worldServers) {
			if (world.provider instanceof TESWorldProvider) {
				TESWorldInfo worldinfo = (TESWorldInfo) world.getWorldInfo();
				worldinfo.tes_setTotalTime(totalTime);
				worldinfo.tes_setWorldTime(worldTime);
			}
		}
	}

	public static boolean isNeedsLoad() {
		return needsLoad;
	}

	public static void setNeedsLoad(boolean needsLoad) {
		TESTime.needsLoad = needsLoad;
	}

	public static void setWorldTime(long time) {
		worldTime = time;
	}
}