package tes.common.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.WorldType;

public class TESWorldType extends WorldType {
	public TESWorldType(String name) {
		super(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean showWorldInfoNotice() {
		return true;
	}
}