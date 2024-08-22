package tes.common.database;

import tes.common.entity.animal.TESEntityHorse;
import tes.common.entity.animal.TESEntityWoolyRhino;
import tes.common.entity.animal.TESEntityZebra;

import tes.common.entity.other.TESEntityProstitute;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.entity.other.utils.TESUnitTradeEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TESUnitTradeEntries {
	public static final Collection<TESUnitTradeEntries> CONTENT = new ArrayList<>();

	public static final int LEVYMAN = 5;
	public static final int LEVYMANA = 10;
	public static final int SOLDIER = 10;
	public static final int SOLDIERA = 15;
	public static final int SOLDIERH = 15;
	public static final int SOLDIERHA = 20;
	public static final int SOLDIERE = 50;
	public static final int SOLDIEREA = 55;

	public static final float LEVYMAN_F = 5.0f;
	public static final float LEVYMANA_F = 5.0f;
	public static final float SOLDIER_F = 10.0f;
	public static final float SOLDIERA_F = 10.0f;
	public static final float SOLDIERH_F = 15.0f;
	public static final float SOLDIERHA_F = 15.0f;

	public static final int SLAVE = 10;
	public static final float SLAVE_F = 0.0f;
	
	static {
	}

	private final TESUnitTradeEntry[] tradeEntries;

	@SuppressWarnings("WeakerAccess")
	public TESUnitTradeEntries(float baseAlignment, List<TESUnitTradeEntry> list) {
		TESUnitTradeEntry[] arr = new TESUnitTradeEntry[list.size()];
		arr = list.toArray(arr);
		for (TESUnitTradeEntry trade : tradeEntries = arr) {
			trade.setAlignmentRequired(trade.getAlignmentRequired() + baseAlignment);
			if (trade.getAlignmentRequired() >= 0.0f) {
				continue;
			}
			throw new IllegalArgumentException("Units cannot require negative alignment!");
		}
		CONTENT.add(this);
	}

	public TESUnitTradeEntry[] getTradeEntries() {
		return tradeEntries;
	}
}