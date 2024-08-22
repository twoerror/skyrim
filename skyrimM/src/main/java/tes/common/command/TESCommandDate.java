package tes.common.command;

import tes.common.TESDate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.List;

public class TESCommandDate extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "get", "set", "add");
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "date";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.date.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1 && "get".equals(args[0])) {
			int date = TESDate.AegonCalendar.getCurrentDay();
			String dateName = TESDate.AegonCalendar.getDate().getDateName(false);
			IChatComponent message = new ChatComponentTranslation("tes.command.date.get", date, dateName);
			sender.addChatMessage(message);
			return;
		}
		if (args.length >= 2) {
			int newDate = TESDate.AegonCalendar.getCurrentDay();
			if ("set".equals(args[0])) {
				newDate = parseInt(sender, args[1]);
			} else if ("add".equals(args[0])) {
				int date = parseInt(sender, args[1]);
				newDate += date;
			}
			if (Math.abs(newDate) > 1000000) {
				throw new WrongUsageException("tes.command.date.outOfBounds");
			}
			TESDate.setDate(newDate);
			String dateName = TESDate.AegonCalendar.getDate().getDateName(false);
			func_152373_a(sender, this, "tes.command.date.set", newDate, dateName);
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}