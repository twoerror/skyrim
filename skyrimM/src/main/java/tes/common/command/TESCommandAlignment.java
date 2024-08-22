package tes.common.command;

import tes.common.TESLevelData;
import tes.common.faction.TESFaction;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TESCommandAlignment extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		switch (args.length) {
			case 1:
				return getListOfStringsMatchingLastWord(args, "set", "add");
			case 2:
				List<String> list = TESFaction.getPlayableAlignmentFactionNames();
				list.add("all");
				return getListOfStringsMatchingLastWord(args, list.toArray(new String[0]));
			case 4:
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "alignment";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.alignment.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 3;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 2) {
			List<TESFaction> factions = new ArrayList<>();
			if ("all".equalsIgnoreCase(args[1])) {
				factions = TESFaction.getPlayableAlignmentFactions();
			} else {
				TESFaction faction = TESFaction.forName(args[1]);
				if (faction == null) {
					throw new WrongUsageException("tes.command.alignment.noFaction", args[1]);
				}
				factions.add(faction);
			}
			if ("set".equals(args[0])) {
				EntityPlayerMP entityplayer;
				float alignment = (float) parseDoubleBounded(sender, args[2], -2147483647.0, 2147483647.0);
				if (args.length >= 4) {
					entityplayer = getPlayer(sender, args[3]);
				} else {
					entityplayer = getCommandSenderAsPlayer(sender);
				}
				for (TESFaction f : factions) {
					TESLevelData.getData(entityplayer).setAlignmentFromCommand(f, alignment);
					func_152373_a(sender, this, "tes.command.alignment.set", entityplayer.getCommandSenderName(), f.factionName(), alignment);
				}
				return;
			}
			if ("add".equals(args[0])) {
				EntityPlayerMP entityplayer;
				float newAlignment;
				float alignment = (float) parseDouble(sender, args[2]);
				if (args.length >= 4) {
					entityplayer = getPlayer(sender, args[3]);
				} else {
					entityplayer = getCommandSenderAsPlayer(sender);
				}
				for (TESFaction f : factions) {
					newAlignment = TESLevelData.getData(entityplayer).getAlignment(f) + alignment;
					if (newAlignment < -2147483647.0f) {
						throw new WrongUsageException("tes.command.alignment.tooLow", -2147483647.0f);
					}
					if (newAlignment > 2147483647.0f) {
						throw new WrongUsageException("tes.command.alignment.tooHigh", 2147483647.0f);
					}
				}
				for (TESFaction f : factions) {
					TESLevelData.getData(entityplayer).addAlignmentFromCommand(f, alignment);
					func_152373_a(sender, this, "tes.command.alignment.add", alignment, entityplayer.getCommandSenderName(), f.factionName());
				}
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}