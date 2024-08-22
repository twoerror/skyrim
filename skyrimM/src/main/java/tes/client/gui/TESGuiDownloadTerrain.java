package tes.client.gui;

import tes.common.TESDimension;
import tes.common.world.map.TESWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiDownloadTerrain extends GuiDownloadTerrain {
	private static final TESGuiMap MAP_GUI = new TESGuiMap();
	private static final TESGuiRendererMap MAP_RENDERER = new TESGuiRendererMap();

	private int tickCounter;

	public TESGuiDownloadTerrain(NetHandlerPlayClient handler) {
		super(handler);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		int dimension = mc.thePlayer.dimension;
		super.drawScreen(i, j, f);
		
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int i, int j) {
		super.setWorldAndResolution(mc, i, j);
		MAP_GUI.setWorldAndResolution(mc, i, j);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		++tickCounter;
	}
}