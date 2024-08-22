package tes.common.entity.other.iface;

import tes.common.database.TESInvasions;
import tes.common.database.TESUnitTradeEntries;

public interface TESUnitTradeable extends TESHireableBase {
	TESUnitTradeEntries getUnits();

	TESInvasions getWarhorn();
}