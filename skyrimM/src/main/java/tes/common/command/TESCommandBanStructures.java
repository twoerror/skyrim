package tes.common.command;

import tes.common.TESLevelData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.Collections;
import java.util.List;

public class TESCommandBanStructures extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "banStructures";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.banStructures.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0) {
			if (TESLevelData.getStructuresBanned() == 1) {
				throw new WrongUsageException("tes.command.banStructures.alreadyBanned");
			}
			TESLevelData.setStructuresBanned(true);
			func_152373_a(sender, this, "tes.command.banStructures.ban");
		} else {
			TESLevelData.setPlayerBannedForStructures(args[0], true);
			func_152373_a(sender, this, "tes.command.banStructures.banPlayer", args[0]);
			EntityPlayerMP entityplayer = getPlayer(sender, args[0]);
			entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.banStructures"));
		}
	}
}