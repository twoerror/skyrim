package tes.common.world.map;

import com.google.common.io.Files;
import cpw.mods.fml.common.FMLLog;
import tes.common.TESConfig;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipData;
import tes.common.util.TESLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TESCustomWaypointLogger {
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM", Locale.ROOT);
	private static final DateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("MM-dd", Locale.ROOT);
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);

	private TESCustomWaypointLogger() {
	}

	private static void log(String function, EntityPlayer entityplayer, TESCustomWaypoint cwp) {
		if (!TESConfig.cwpLog) {
			return;
		}
		try {
			File logFile;
			File dupeLogDir;
			LocalDateTime date = LocalDateTime.now();
			StringBuilder logLine = new StringBuilder(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", MONTH_DATE_FORMAT.format(date), TIME_FORMAT.format(date), function, entityplayer.getCommandSenderName(), entityplayer.getPersistentID(), cwp.getCodeName(), cwp.getCoordX(), cwp.getCoordYSaved(), cwp.getCoordZ(), cwp.isShared(), cwp.isShared() ? cwp.getSharingPlayerName() : "N/A", cwp.isShared() ? cwp.getSharingPlayerID() : "N/A"));
			if (cwp.isShared()) {
				List<UUID> fsIDs = cwp.getSharedFellowshipIDs();
				for (UUID id : fsIDs) {
					TESFellowship fellowship = TESFellowshipData.getActiveFellowship(id);
					if (fellowship == null || !fellowship.containsPlayer(entityplayer.getUniqueID())) {
						continue;
					}
					logLine.append(',');
					logLine.append(fellowship.getName());
				}
			}
			if (!(dupeLogDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "tes_cwp_logs")).exists()) {
				boolean created = dupeLogDir.mkdirs();
				if (!created) {
					TESLog.getLogger().info("TESCustomWaypointLogger: directory wasn't created");
				}
			}
			if (!(logFile = new File(dupeLogDir, DATE_FORMAT.format(date) + ".csv")).exists()) {
				Files.append("date,time,function,username,UUID,wp_name,x,y,z,shared,sharer_name,sharer_UUID,common_fellowships" + System.lineSeparator(), logFile, CHARSET);
			}
			Files.append(logLine.append(System.lineSeparator()).toString(), logFile, CHARSET);
		} catch (IOException e) {
			FMLLog.warning("Error logging custom waypoint activities");
			e.printStackTrace();
		}
	}

	public static void logCreate(EntityPlayer entityplayer, TESCustomWaypoint cwp) {
		log("CREATE", entityplayer, cwp);
	}

	public static void logDelete(EntityPlayer entityplayer, TESCustomWaypoint cwp) {
		log("DELETE", entityplayer, cwp);
	}

	public static void logRename(EntityPlayer entityplayer, TESCustomWaypoint cwp) {
		log("RENAME", entityplayer, cwp);
	}

	public static void logTravel(EntityPlayer entityplayer, TESCustomWaypoint cwp) {
		log("TRAVEL", entityplayer, cwp);
	}
}