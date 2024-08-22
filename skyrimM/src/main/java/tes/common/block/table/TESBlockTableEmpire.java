package tes.common.block.table;

import tes.common.database.TESGuiId;
import tes.common.faction.TESFaction;
import net.minecraft.block.material.Material;

public class TESBlockTableEmpire extends TESBlockCraftingTable {
	public TESBlockTableEmpire() {
		super(Material.wood, TESFaction.EMPIRE, TESGuiId.TABLE_EMPIRE);
		setStepSound(soundTypeWood);
	}
}