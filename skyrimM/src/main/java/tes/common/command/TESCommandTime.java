package tes.common.command;

import tes.common.TESTime;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.Collections;
import java.util.List;

public class TESCommandTime extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "set", "add");
		}
		if (args.length == 2 && "set".equals(args[0])) {
			return getListOfStringsMatchingLastWord(args, "day", "night");
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "tes_time";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.time.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 2) {
			if ("set".equals(args[0])) {
				long time = "day".equals(args[1]) ? 1440 : "night".equals(args[1]) ? 28800 : parseIntWithMin(sender, args[1], 0);
				TESTime.setWorldTime(time);
				func_152373_a(sender, this, "tes.command.time.set", time);
				return;
			}
			if ("add".equals(args[0])) {
				int time = parseIntWithMin(sender, args[1], 0);
				TESTime.addWorldTime(time);
				func_152373_a(sender, this, "tes.command.time.add", time);
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}