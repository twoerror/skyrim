package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.item.other.TESItemBrandingIron;
import tes.common.network.TESPacketBrandingIron;
import tes.common.network.TESPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

public class TESGuiBrandingIron extends TESGuiScreenBase {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/brandingIron.png");
	private static final RenderItem ITEM_RENDERER = new RenderItem();

	private static final int X_SIZE = 200;
	private static final int Y_SIZE = 132;

	private GuiTextField brandNameField;
	private GuiButton buttonDone;
	private ItemStack theItem;

	private int guiLeft;
	private int guiTop;

	@Override
	public void actionPerformed(GuiButton button) {
		if (button == buttonDone) {
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, X_SIZE, Y_SIZE);
		String s = StatCollector.translateToLocal("tes.gui.brandingIron.title");
		fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 11, 4210752);
		s = StatCollector.translateToLocal("tes.gui.brandingIron.naming");
		fontRendererObj.drawString(s, brandNameField.xPosition, brandNameField.yPosition - fontRendererObj.FONT_HEIGHT - 3, 4210752);
		s = StatCollector.translateToLocal("tes.gui.brandingIron.unnameHint");
		fontRendererObj.drawString(s, brandNameField.xPosition, brandNameField.yPosition + brandNameField.height + 3, 4210752);
		brandNameField.drawTextBox();
		buttonDone.enabled = !StringUtils.isBlank(brandNameField.getText());
		super.drawScreen(i, j, f);
		if (theItem != null) {
			ITEM_RENDERER.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), theItem, guiLeft + 8, guiTop + 8);
		}
	}

	@Override
	public void initGui() {
		guiLeft = (width - X_SIZE) / 2;
		guiTop = (height - Y_SIZE) / 2;
		buttonDone = new TESGuiButton(1, guiLeft + X_SIZE / 2 - 40, guiTop + 97, 80, 20, StatCollector.translateToLocal("tes.gui.brandingIron.done"));
		buttonList.add(buttonDone);
		ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
		if (itemstack != null && itemstack.getItem() instanceof TESItemBrandingIron) {
			theItem = itemstack;
			brandNameField = new GuiTextField(fontRendererObj, guiLeft + X_SIZE / 2 - 80, guiTop + 50, 160, 20);
		}
		if (theItem == null) {
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void keyTyped(char c, int i) {
		if (brandNameField.getVisible() && brandNameField.textboxKeyTyped(c, i)) {
			return;
		}
		super.keyTyped(c, i);
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		brandNameField.mouseClicked(i, j, k);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		String brandName = brandNameField.getText();
		if (!StringUtils.isBlank(brandName)) {
			IMessage packet = new TESPacketBrandingIron(brandName);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		brandNameField.updateCursorCounter();
		ItemStack itemstack = mc.thePlayer.getCurrentEquippedItem();
		if (itemstack == null || !(itemstack.getItem() instanceof TESItemBrandingIron)) {
			mc.thePlayer.closeScreen();
		} else {
			theItem = itemstack;
		}
	}
}