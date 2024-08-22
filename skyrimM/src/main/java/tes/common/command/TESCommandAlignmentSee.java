package tes.common.command;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.network.TESPacketAlignmentSee;
import tes.common.network.TESPacketHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Collections;
import java.util.List;

public class TESCommandAlignmentSee extends CommandBase {
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
		}
		return Collections.emptyList();
	}

	@Override
	public String getCommandName() {
		return "alignmentsee";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "tes.command.alignmentsee.usage";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return i == 0;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1) {
			String username = args[0];
			EntityPlayerMP entityplayer = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
			GameProfile profile = entityplayer != null ? entityplayer.getGameProfile() : MinecraftServer.getServer().func_152358_ax().func_152655_a(username);
			if (profile == null || profile.getId() == null) {
				throw new PlayerNotFoundException("tes.command.alignmentsee.noPlayer", username);
			}
			if (sender instanceof EntityPlayerMP) {
				TESPlayerData playerData = TESLevelData.getData(profile.getId());
				IMessage packet = new TESPacketAlignmentSee(username, playerData);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) sender);
				return;
			}
		}
		throw new WrongUsageException(getCommandUsage(sender));
	}
}