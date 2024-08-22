package tes.client.gui;

import tes.common.database.TESUnitTradeEntries;
import tes.common.entity.other.iface.TESMercenary;
import tes.common.entity.other.utils.TESMercenaryTradeEntry;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;

public class TESGuiMercenaryHire extends TESGuiHireBase {
	public TESGuiMercenaryHire(EntityPlayer entityplayer, TESMercenary mercenary, World world) {
		super(entityplayer, mercenary, world);
		TESMercenaryTradeEntry e = TESMercenaryTradeEntry.createFor(mercenary);
		ArrayList<TESUnitTradeEntry> sus = new ArrayList<>();
		sus.add(e);
		TESUnitTradeEntries trades = new TESUnitTradeEntries(0.0f, sus);
		setTrades(trades);
	}
}