package tes.common.command;

import tes.common.TESLevelData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.Collections;
import java.util.List;

public class TESCommandEnableAlignmentZones extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "enable", "disable");
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "alignmentZones";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.alignmentZones.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1) {
			String flag = args[0];
			if ("enable".equals(flag)) {
				TESLevelData.setEnableAlignmentZones(true);
				func_152373_a(sender, this, "tes.command.alignmentZones.enable");
				return;
			}
			if ("disable".equals(flag)) {
				TESLevelData.setEnableAlignmentZones(false);
				func_152373_a(sender, this, "tes.command.alignmentZones.disable");
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}