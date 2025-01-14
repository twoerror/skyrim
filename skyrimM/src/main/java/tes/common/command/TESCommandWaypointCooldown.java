package tes.common.command;

import tes.common.TESLevelData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.Collections;
import java.util.List;

public class TESCommandWaypointCooldown extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "max", "min");
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "wpCooldown";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.wpCooldown.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		String function = null;
		int cooldown = -1;
		int MAX_COOLDOWN = 86400;
		if (args.length == 1) {
			function = "max";
			cooldown = parseIntBounded(sender, args[0], 0, MAX_COOLDOWN);
		} else if (args.length >= 2) {
			function = args[0];
			cooldown = parseIntBounded(sender, args[1], 0, MAX_COOLDOWN);
		}
		if (function != null && cooldown >= 0) {
			int max = TESLevelData.getWaypointCooldownMax();
			int min = TESLevelData.getWaypointCooldownMin();
			if ("max".equals(function)) {
				boolean updatedMin = false;
				max = cooldown;
				if (max < min) {
					min = max;
					updatedMin = true;
				}
				TESLevelData.setWaypointCooldown(max, min);
				func_152373_a(sender, this, "tes.command.wpCooldown.setMax", max, TESLevelData.getHMSTime_Seconds(max));
				if (updatedMin) {
					func_152373_a(sender, this, "tes.command.wpCooldown.updateMin", min);
				}
				return;
			}
			if ("min".equals(function)) {
				boolean updatedMax = false;
				min = cooldown;
				if (min > max) {
					max = min;
					updatedMax = true;
				}
				TESLevelData.setWaypointCooldown(max, min);
				func_152373_a(sender, this, "tes.command.wpCooldown.setMin", min, TESLevelData.getHMSTime_Seconds(min));
				if (updatedMax) {
					func_152373_a(sender, this, "tes.command.wpCooldown.updateMax", max);
				}
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}