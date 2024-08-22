package tes.client.gui;

import tes.common.entity.other.iface.TESUnitTradeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TESGuiUnitTrade extends TESGuiHireBase {
	public TESGuiUnitTrade(EntityPlayer entityplayer, TESUnitTradeable trader, World world) {
		super(entityplayer, trader, world);
		setTrades(trader.getUnits());
	}
}