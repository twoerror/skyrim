package tes.common.entity.other.iface;

import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;

public interface TESHireableBase {
	boolean canTradeWith(EntityPlayer var1);

	TESFaction getFaction();

	String getNPCName();

	void onUnitTrade(EntityPlayer var1);
}