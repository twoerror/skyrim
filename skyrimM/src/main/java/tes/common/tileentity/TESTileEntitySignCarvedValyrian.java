package tes.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TESTileEntitySignCarvedValyrian extends TESTileEntitySignCarved {
	private static final TESTileEntityGlowLogic GLOW_LOGIC = new TESTileEntityGlowLogic().setPlayerRange(8);

	public float getGlowBrightness(float f) {
		if (isFakeGuiSign()) {
			return 1.0f;
		}
		return GLOW_LOGIC.getGlowBrightness(worldObj, xCoord, yCoord, zCoord, f);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getMaxRenderDistanceSquared() {
		return 1024.0;
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		GLOW_LOGIC.update(worldObj, xCoord, yCoord, zCoord);
	}
}