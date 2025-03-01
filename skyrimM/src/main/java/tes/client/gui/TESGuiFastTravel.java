package tes.client.gui;

import tes.client.TESClientProxy;
import tes.common.database.TESSpeech;
import tes.common.util.TESFunctions;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESWaypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TESGuiFastTravel extends TESGuiScreenBase {
	private static final ResourceLocation FT_SOUND = new ResourceLocation("tes:event.fastTravel");

	private final TESAbstractWaypoint theWaypoint;

	private final TESGuiRendererMap mapRenderer;
	private final TESGuiMap mapGui;

	private final String message;

	private final double mapScaleFactor;
	private final float zoomBase;

	private boolean reachedWP;
	private boolean chunkLoaded;
	private boolean playedSound;
	private boolean finishedZoomIn;

	private double mapSpeed;
	private double mapVelX;
	private double mapVelY;

	private float currentZoom;
	private float prevZoom;

	private int tickCounter;

	public TESGuiFastTravel(TESAbstractWaypoint waypoint, int x, int z) {
		theWaypoint = waypoint;
		message = TESSpeech.getRandomSpeech("standard/special/fast_travel");
		mapGui = new TESGuiMap();
		mapRenderer = new TESGuiRendererMap();
		mapRenderer.setSepia(true);
		mapRenderer.setMapX(TESWaypoint.worldToMapX(x));
		mapRenderer.setMapY(TESWaypoint.worldToMapZ(z));
		double dx = theWaypoint.getImgX() - mapRenderer.getMapX();
		double dy = theWaypoint.getImgY() - mapRenderer.getMapY();
		double distSq = dx * dx + dy * dy;
		double dist = Math.sqrt(distSq);
		mapScaleFactor = dist / 100.0;
		zoomBase = -((float) (Math.log(mapScaleFactor * 0.30000001192092896) / Math.log(2.0)));
		currentZoom = prevZoom = zoomBase + 0.5f;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		GL11.glEnable(3008);
		GL11.glEnable(3042);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		mapRenderer.setZoomExp(prevZoom + (currentZoom - prevZoom) * f);
		mapRenderer.setZoomStable((float) Math.pow(2.0, zoomBase));
		mapRenderer.renderMap(this, mapGui, f);
		TESGuiRendererMap.renderVignettes(this, zLevel, 4);
		GL11.glEnable(3042);
		String title = StatCollector.translateToLocalFormatted("tes.fastTravel.travel", theWaypoint.getDisplayName());
		String titleExtra = new String[]{"", ".", "..", "..."}[tickCounter / 10 % 4];
		List<String> messageLines = fontRendererObj.listFormattedStringToWidth(message, width - 100);
		String skipText = StatCollector.translateToLocalFormatted("tes.fastTravel.skip", GameSettings.getKeyDisplayString(mc.gameSettings.keyBindInventory.getKeyCode()));
		float boxAlpha = 0.5f;
		int boxColor = (int) (boxAlpha * 255.0f) << 24;
		int fh = fontRendererObj.FONT_HEIGHT;
		int border = fh * 2;
		if (chunkLoaded) {
			drawRect(0, 0, width, border + fh * 3 + border, boxColor);
		} else {
			drawRect(0, 0, width, border + fh + border, boxColor);
		}
		int messageY = height - border - messageLines.size() * fh;
		drawRect(0, messageY - border, width, height, boxColor);
		GL11.glDisable(3042);
		fontRendererObj.drawStringWithShadow(title + titleExtra, width / 2 - fontRendererObj.getStringWidth(title) / 2, border, 16777215);
		for (String obj : messageLines) {
			drawCenteredString(fontRendererObj, obj, width / 2, messageY, 16777215);
			messageY += fh;
		}
		if (chunkLoaded) {
			float skipAlpha = TESFunctions.triangleWave(tickCounter + f, 0.3f, 1.0f, 80.0f);
			int skipColor = 0xFFFFFF | TESClientProxy.getAlphaInt(skipAlpha) << 24;
			GL11.glEnable(3042);
			fontRendererObj.drawString(skipText, width / 2 - fontRendererObj.getStringWidth(skipText) / 2, border + fh * 2, skipColor);
		}
		GL11.glDisable(3042);
		super.drawScreen(i, j, f);
	}

	@Override
	public void keyTyped(char c, int i) {
		if (chunkLoaded && (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode())) {
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int i, int j) {
		super.setWorldAndResolution(mc, i, j);
		mapGui.setWorldAndResolution(mc, i, j);
	}

	@Override
	public void updateScreen() {
		if (!chunkLoaded && TESClientProxy.doesClientChunkExist(mc.theWorld, theWaypoint.getCoordX(), theWaypoint.getCoordZ())) {
			chunkLoaded = true;
		}
		if (!playedSound) {
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147673_a(FT_SOUND));
			playedSound = true;
		}
		mapRenderer.updateTick();
		++tickCounter;
		prevZoom = currentZoom;
		if (reachedWP) {
			currentZoom += 0.008333334f;
			currentZoom = Math.min(currentZoom, zoomBase + 0.5f);
			if (currentZoom >= zoomBase + 0.5f) {
				finishedZoomIn = true;
			}
		} else {
			double dy;
			double dx = theWaypoint.getImgX() - mapRenderer.getMapX();
			double distSq = dx * dx + (dy = theWaypoint.getImgY() - mapRenderer.getMapY()) * dy;
			double dist = Math.sqrt(distSq);
			if (dist <= mapScaleFactor) {
				reachedWP = true;
				mapSpeed = 0.0;
				mapVelX = 0.0;
				mapVelY = 0.0;
			} else {
				mapSpeed += 0.009999999776482582;
				mapSpeed = Math.min(mapSpeed, 2.0);
				double vXNew = dx / dist * mapSpeed;
				double vYNew = dy / dist * mapSpeed;
				double a = 0.20000000298023224;
				mapVelX += (vXNew - mapVelX) * a;
				mapVelY += (vYNew - mapVelY) * a;
			}
			mapRenderer.setMapX(mapRenderer.getMapX() + mapVelX * mapScaleFactor);
			mapRenderer.setMapY(mapRenderer.getMapY() + mapVelY * mapScaleFactor);
			currentZoom -= 0.008333334f;
			currentZoom = Math.max(currentZoom, zoomBase);
		}
		if (chunkLoaded && reachedWP && finishedZoomIn) {
			mc.displayGuiScreen(null);
		}
	}
}