package tes.common.command;

import tes.common.TESConfig;
import net.minecraft.command.server.CommandMessage;

public class TESCommandMessageFixed extends CommandMessage {
	@Override
	public boolean isUsernameIndex(String[] args, int i) {
		return !TESConfig.preventMessageExploit && super.isUsernameIndex(args, i);
	}
}