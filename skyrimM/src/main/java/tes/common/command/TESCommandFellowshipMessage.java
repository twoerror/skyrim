package tes.common.command;

import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TESCommandFellowshipMessage extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		String[] args1 = args;
		TESPlayerData playerData = TESLevelData.getData(getCommandSenderAsPlayer(sender));
		String[] argsOriginal = Arrays.copyOf(args1, args1.length);
		if (args1.length >= 2 && "bind".equals(args1[0])) {
			args1 = TESCommandFellowship.fixArgsForFellowship(args1, 1, true);
			return TESCommandFellowship.listFellowshipsMatchingLastWord(args1, argsOriginal, 1, playerData, false);
		}
		if (args1.length >= 1) {
			args1 = TESCommandFellowship.fixArgsForFellowship(args1, 0, true);
			List<String> matches = new ArrayList<>();
			if (args1.length == 1 && (argsOriginal[0].isEmpty() || argsOriginal[0].charAt(0) != '\"')) {
				matches.addAll(getListOfStringsMatchingLastWord(args1, "bind", "unbind"));
			}
			matches.addAll(TESCommandFellowship.listFellowshipsMatchingLastWord(args1, argsOriginal, 0, playerData, false));
			return matches;
		}
		return Collections.emptyList();
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer || super.canCommandSenderUseCommand(sender);
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("fchat");
	}

	@Override
	public String getCommandName() {
		return "fmsg";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.fmsg.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		String[] args1 = args;
		EntityPlayerMP entityplayer = getCommandSenderAsPlayer(sender);
		TESPlayerData playerData = TESLevelData.getData(entityplayer);
		if (args1.length >= 1) {
			if ("bind".equals(args1[0]) && args1.length >= 2) {
				String fsName = TESCommandFellowship.fixArgsForFellowship(args1, 1, false)[1];
				TESFellowship fellowship = playerData.getFellowshipByName(fsName);
				if (fellowship != null && !fellowship.isDisbanded() && fellowship.containsPlayer(entityplayer.getUniqueID())) {
					playerData.setChatBoundFellowship(fellowship);
					IChatComponent notif = new ChatComponentTranslation("tes.command.fmsg.bind", fellowship.getName());
					notif.getChatStyle().setColor(EnumChatFormatting.GRAY);
					notif.getChatStyle().setItalic(true);
					sender.addChatMessage(notif);
					return;
				}
				throw new WrongUsageException("tes.command.fmsg.notFound", fsName);
			}
			if ("unbind".equals(args1[0])) {
				TESFellowship preBoundFellowship = playerData.getChatBoundFellowship();
				playerData.setChatBoundFellowshipID(null);
				IChatComponent notif = new ChatComponentTranslation("tes.command.fmsg.unbind", preBoundFellowship.getName());
				notif.getChatStyle().setColor(EnumChatFormatting.GRAY);
				notif.getChatStyle().setItalic(true);
				sender.addChatMessage(notif);
				return;
			}
			TESFellowship fellowship = null;
			int msgStartIndex = 0;
			if (!args1[0].isEmpty() && args1[0].charAt(0) == '\"') {
				String fsName = (args1 = TESCommandFellowship.fixArgsForFellowship(args1, 0, false))[0];
				fellowship = playerData.getFellowshipByName(fsName);
				if (fellowship == null) {
					throw new WrongUsageException("tes.command.fmsg.notFound", fsName);
				}
				msgStartIndex = 1;
			}
			if (fellowship == null) {
				fellowship = playerData.getChatBoundFellowship();
				if (fellowship == null) {
					throw new WrongUsageException("tes.command.fmsg.boundNone");
				}
				if (fellowship.isDisbanded() || !fellowship.containsPlayer(entityplayer.getUniqueID())) {
					throw new WrongUsageException("tes.command.fmsg.boundNotMember", fellowship.getName());
				}
			}
			IChatComponent message = func_147176_a(sender, args1, msgStartIndex, false);
			fellowship.sendFellowshipMessage(entityplayer, message.getUnformattedText());
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}