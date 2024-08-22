package tes.client.render.other;

import tes.client.TESClientProxy;
import tes.common.tileentity.TESTileEntitySignCarved;
import tes.common.tileentity.TESTileEntitySignCarvedValyrian;
import net.minecraft.tileentity.TileEntity;

public class TESRenderSignCarvedValyrian extends TESRenderSignCarved {
	@Override
	public int getTextColor(TESTileEntitySignCarved sign, float f) {
		float glow = ((TESTileEntitySignCarvedValyrian) sign).getGlowBrightness(f);
		int alpha = TESClientProxy.getAlphaInt(glow);
		return 0xFFFFFF | alpha << 24;
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		TESTileEntitySignCarvedValyrian sign = (TESTileEntitySignCarvedValyrian) tileentity;
		float alphaFunc = TESRenderSilverGlow.setupGlow(sign.getGlowBrightness(f));
		super.renderTileEntityAt(tileentity, d, d1, d2, f);
		TESRenderSilverGlow.endGlow(alphaFunc);
	}
}