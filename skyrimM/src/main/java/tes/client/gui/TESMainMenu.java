package tes.client.gui;

import cpw.mods.fml.client.GuiModList;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TESMainMenu
extends GuiMainMenu {

private static final ResourceLocation titleTexture = new ResourceLocation("textures/gui/title/minecraft.png");
private static final ResourceLocation skyrim = new ResourceLocation("skyrim:gui/skyrim.png");
private static Random rand = new Random();
private static boolean isFirstMenu;

static {
isFirstMenu = true;
}

private boolean fadeIn = isFirstMenu;
private long firstRenderTime;



public void initGui() {
super.initGui();
int height_button = (this.height / 5) * 2;
int weidth_button = (this.width / 7) * 4;
this.buttonList.clear();
this.buttonList.add(new GuiButton(1, weidth_button, height_button, 140, 20, I18n.format((String)"menu.singleplayer", (Object[])new Object[]{0})));
this.buttonList.add(new GuiButton(2, weidth_button, height_button + 25, 140, 20, I18n.format((String)"menu.multiplayer", (Object[])new Object[]{0})));
this.buttonList.add(new GuiButton(3, weidth_button, height_button + 50, 140, 20, I18n.format((String)"menu.options", (Object[])new Object[]{0})));
this.buttonList.add(new GuiButton(4, weidth_button, height_button + 75, 140, 20, "Mods"));
this.buttonList.add(new GuiButton(5, weidth_button, height_button + 100, 140, 20, I18n.format((String)"menu.quit", (Object[])new Object[]{0})));
int lowerButtonMaxY = 0;
for (Object obj : buttonList) {
	GuiButton button = (GuiButton) obj;
	int buttonMaxY = button.yPosition + button.height;
	if (buttonMaxY <= lowerButtonMaxY) {
		continue;
	}
	lowerButtonMaxY = buttonMaxY;
}
int idealMoveDown = 50;
int lowestSuitableHeight = height - 25;
int moveDown = Math.min(idealMoveDown, lowestSuitableHeight - lowerButtonMaxY);
moveDown = Math.max(moveDown, 0);
for (int i = 0; i < buttonList.size(); ++i) {
	GuiButton button = (GuiButton) buttonList.get(i);
	button.yPosition += moveDown;
	if (button.getClass() != GuiButton.class) {
		continue;
	}

}
}

protected void actionPerformed(GuiButton idk) {
if (idk.id == 3) {
    this.mc.displayGuiScreen((GuiScreen)new GuiOptions((GuiScreen)this, this.mc.gameSettings));
}
if (idk.id == 1) {
    this.mc.displayGuiScreen((GuiScreen)new GuiSelectWorld((GuiScreen)this));
}
if (idk.id == 2) {
    this.mc.displayGuiScreen((GuiScreen)new GuiMultiplayer((GuiScreen)this));
}
if (idk.id == 5) {
    this.mc.shutdown();
}
if (idk.id == 4) {
    this.mc.displayGuiScreen((GuiScreen)new GuiModList((GuiScreen)this));
}
}


public void setWorldAndResolution(Minecraft mc, int i, int j) {
super.setWorldAndResolution(mc, i, j);
}

public void updateScreen() { super.updateScreen(); }

public void drawScreen(int i, int j, float f) {
//super.drawScreen(i, j, f);
float fade = fadeIn ? (float) (System.currentTimeMillis() - firstRenderTime) / 1000.0f : 1.0f;
float fadeAlpha = fadeIn ? MathHelper.clamp_float((float) (fade - 1.0f), (float) 0.0f, (float) 1.0f) : 1.0f;
int fadeAlphaI = MathHelper.ceiling_float_int((float) (fadeAlpha * 255.0f)) << 24;
GL11.glEnable((int)3008);

GL11.glDisable(GL11.GL_LIGHTING);
GL11.glDisable(GL11.GL_FOG);
Tessellator var2 = Tessellator.instance;
GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
mc.getTextureManager().bindTexture(skyrim);
GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
var2.startDrawingQuads();
var2.addVertexWithUV(0.0D, height, 0.0D, 0.0D, 1.0D);
var2.addVertexWithUV(width, height, 0.0D, 1.0D, 1.0D);
var2.addVertexWithUV(width, 0.0D, 0.0D, 1.0D, 0.0D);
var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
var2.draw();

String copyright = "сделано туерором";
drawString(fontRendererObj, copyright, width - fontRendererObj.getStringWidth(copyright) - 2, height - 10, -1);
if ((fadeAlphaI & 0xFC000000) != 0) {
	Tessellator tessellator = Tessellator.instance;
	int short1 = 274;
	int k = width / 2 - short1 / 2;
	int b0 = 30;
	mc.getTextureManager().bindTexture(titleTexture);
	GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) fadeAlpha);
	drawTexturedModalRect(k + 0, b0 + 0, 0, 0, 155, 44);
	drawTexturedModalRect(k + 155, b0 + 0, 0, 45, 155, 44);
	List brandings = Lists.reverse((List) FMLCommonHandler.instance().getBrandings(true));
	for (int l = 0; l < brandings.size(); ++l) {
		String brd = (String) brandings.get(l);
		if (Strings.isNullOrEmpty((String) brd)) {
			continue;
		}
		drawString(fontRendererObj, brd, 2, height - (10 + l * (fontRendererObj.FONT_HEIGHT + 1)), -1);
	}
	ForgeHooksClient.renderMainMenu((GuiMainMenu) this, (FontRenderer) fontRendererObj, (int) width, (int) height);
	String field_92025_p = (String) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_92025_p"});
	String field_146972_A = (String) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_146972_A"});
	int field_92024_r = (Integer) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_92024_r"});
	int field_92022_t = (Integer) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_92022_t"});
	int field_92021_u = (Integer) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_92021_u"});
	int field_92020_v = (Integer) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_92020_v"});
	int field_92019_w = (Integer) ObfuscationReflectionHelper.getPrivateValue(GuiMainMenu.class, this, (String[]) new String[]{"field_92019_w"});
	if (field_92025_p != null && !field_92025_p.isEmpty()) {
		drawRect((int) (field_92022_t - 2), (int) (field_92021_u - 2), (int) (field_92020_v + 2), (int) (field_92019_w - 1), (int) 1428160512);
		drawString(fontRendererObj, field_92025_p, field_92022_t, field_92021_u, -1);
		drawString(fontRendererObj, field_146972_A, (width - field_92024_r) / 2, ((GuiButton) buttonList.get((int) 0)).yPosition - 12, -1);
	}
	for (Object button : buttonList) {
		((GuiButton) button).drawButton(mc, i, j);
	}
	for (Object label : labelList) {
		((GuiLabel) label).func_146159_a(mc, i, j);
	}
}
}
}