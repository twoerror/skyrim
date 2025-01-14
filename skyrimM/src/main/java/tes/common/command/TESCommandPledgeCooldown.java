package tes.common.command;

import tes.common.TESLevelData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class TESCommandPledgeCooldown extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "pledgeCooldown";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.pledgeCooldown.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 1;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1) {
			EntityPlayerMP entityplayer;
			int cd = parseIntBounded(sender, args[0], 0, 10000000);
			if (args.length >= 2) {
				entityplayer = getPlayer(sender, args[1]);
			} else {
				entityplayer = getCommandSenderAsPlayer(sender);
			}
			TESLevelData.getData(entityplayer).setPledgeBreakCooldown(cd);
			func_152373_a(sender, this, "tes.command.pledgeCooldown.set", entityplayer.getCommandSenderName(), cd, TESLevelData.getHMSTime_Ticks(cd));
			return;
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}