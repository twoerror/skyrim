package tes.common.command;

import tes.common.entity.TESEntityRegistry;
import net.minecraft.command.server.CommandSummon;

public class TESCommandSummon extends CommandSummon {
	@Override
	public String[] func_147182_d() {
		return TESEntityRegistry.getAllEntityNames().toArray(new String[0]);
	}

	@Override
	public String getCommandName() {
		return "tes_summon";
	}
}