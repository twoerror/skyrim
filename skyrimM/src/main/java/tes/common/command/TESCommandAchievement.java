package tes.common.command;

import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESAchievement;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TESCommandAchievement extends CommandBase {
	private static TESAchievement findAchievementByName(String name) {
		TESAchievement ach = TESAchievement.findByName(name);
		if (ach == null) {
			throw new CommandException("tes.command.tes_achievement.unknown", name);
		}
		return ach;
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		switch (args.length) {
			case 1:
				return getListOfStringsMatchingLastWord(args, "give", "remove");
			case 2:
				List<TESAchievement> achievements = TESAchievement.getAllAchievements();
				List<String> names = new ArrayList<>();
				for (TESAchievement a : achievements) {
					names.add(a.getName());
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
		return "tes_achievement";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.tes_achievement.usage";
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
			String achName = args[1];
			EntityPlayerMP entityplayer = args.length >= 3 ? getPlayer(sender, args[2]) : getCommandSenderAsPlayer(sender);
			TESPlayerData playerData = TESLevelData.getData(entityplayer);
			if ("give".equalsIgnoreCase(args[0])) {
				if ("all".equalsIgnoreCase(achName)) {
					for (TESAchievement ach : TESAchievement.CONTENT) {
						if (!playerData.hasAchievement(ach)) {
							playerData.addAchievement(ach);
						}
					}
					func_152373_a(sender, this, "tes.command.tes_achievement.addAll", entityplayer.getCommandSenderName());
					return;
				}
				TESAchievement ach = findAchievementByName(achName);
				if (playerData.hasAchievement(ach)) {
					throw new WrongUsageException("tes.command.tes_achievement.give.fail", entityplayer.getCommandSenderName(), ach.getTitle(entityplayer));
				}
				playerData.addAchievement(ach);
				func_152373_a(sender, this, "tes.command.tes_achievement.give", entityplayer.getCommandSenderName(), ach.getTitle(entityplayer));
				return;
			}
			if ("remove".equalsIgnoreCase(args[0])) {
				if ("all".equalsIgnoreCase(achName)) {
					Iterable<TESAchievement> allAchievements = new ArrayList<>(playerData.getAchievements());
					for (TESAchievement ach : allAchievements) {
						playerData.removeAchievement(ach);
					}
					func_152373_a(sender, this, "tes.command.tes_achievement.removeAll", entityplayer.getCommandSenderName());
					return;
				}
				TESAchievement ach = findAchievementByName(achName);
				if (!playerData.hasAchievement(ach)) {
					throw new WrongUsageException("tes.command.tes_achievement.remove.fail", entityplayer.getCommandSenderName(), ach.getTitle(entityplayer));
				}
				playerData.removeAchievement(ach);
				func_152373_a(sender, this, "tes.command.tes_achievement.remove", entityplayer.getCommandSenderName(), ach.getTitle(entityplayer));
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}