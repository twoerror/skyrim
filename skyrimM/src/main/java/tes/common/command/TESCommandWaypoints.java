package tes.common.command;

import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESWaypoint;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TESCommandWaypoints extends CommandBase {
	private static TESWaypoint.Region findRegionByName(String name) {
		TESWaypoint.Region region = TESWaypoint.regionForName(name);
		if (region == null) {
			throw new CommandException("tes.command.waypoints.unknown", name);
		}
		return region;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		switch (args.length) {
			case 1:
				return getListOfStringsMatchingLastWord(args, "unlock", "lock");
			case 2:
				List<String> names = new ArrayList<>();
				for (TESWaypoint.Region region : TESWaypoint.Region.values()) {
					names.add(region.name());
				}
				names.add("all");
				return getListOfStringsMatchingLastWord(args, names.toArray(new String[0]));
			case 3:
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "waypoints";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.waypoints.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 2) {
			String regionName = args[1];
			EntityPlayerMP entityplayer = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
			TESPlayerData playerData = TESLevelData.getData(entityplayer);
			if ("unlock".equalsIgnoreCase(args[0])) {
				if ("all".equalsIgnoreCase(regionName)) {
					for (TESWaypoint.Region region : TESWaypoint.Region.values()) {
						playerData.unlockFTRegion(region);
					}
					func_152373_a(sender, this, "tes.command.waypoints.unlockAll", entityplayer.getCommandSenderName());
					return;
				}
				TESWaypoint.Region region = findRegionByName(regionName);
				if (playerData.isFTRegionUnlocked(Collections.singletonList(region))) {
					throw new WrongUsageException("tes.command.waypoints.unlock.fail", entityplayer.getCommandSenderName(), region.name());
				}
				playerData.unlockFTRegion(region);
				func_152373_a(sender, this, "tes.command.waypoints.unlock", entityplayer.getCommandSenderName(), region.name());
				return;
			}
			if ("lock".equalsIgnoreCase(args[0])) {
				if ("all".equalsIgnoreCase(regionName)) {
					for (TESWaypoint.Region region : TESWaypoint.Region.values()) {
						playerData.lockFTRegion(region);
					}
					func_152373_a(sender, this, "tes.command.waypoints.lockAll", entityplayer.getCommandSenderName());
					return;
				}
				TESWaypoint.Region region = findRegionByName(regionName);
				if (!playerData.isFTRegionUnlocked(Collections.singletonList(region))) {
					throw new WrongUsageException("tes.command.waypoints.lock.fail", entityplayer.getCommandSenderName(), region.name());
				}
				playerData.lockFTRegion(region);
				func_152373_a(sender, this, "tes.command.waypoints.lock", entityplayer.getCommandSenderName(), region.name());
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}