package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.database.TESItems;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketHornSelect;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TESGuiHornSelect extends TESGuiScreenBase {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/horn_select.png");
	private static final RenderItem ITEM_RENDERER = new RenderItem();

	private static final int X_SIZE = 176;
	private static final int Y_SIZE = 256;

	private int guiLeft;
	private int guiTop;

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			IMessage packet = new TESPacketHornSelect(button.id);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			mc.thePlayer.closeScreen();
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, X_SIZE, Y_SIZE);
		String s = StatCollector.translateToLocal("tes.gui.hornSelect.title");
		fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 11, 4210752);
		super.drawScreen(i, j, f);
		for (GuiButton element : (List<GuiButton>) buttonList) {
			ITEM_RENDERER.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), new ItemStack(TESItems.commandHorn, 1, element.id), element.xPosition - 22, element.yPosition + 2);
		}
	}

	@Override
	public void initGui() {
		guiLeft = (width - X_SIZE) / 2;
		guiTop = (height - Y_SIZE) / 2;
		buttonList.add(new TESGuiButton(1, guiLeft + 40, guiTop + 40, 120, 20, StatCollector.translateToLocal("tes.gui.hornSelect.haltReady")));
		buttonList.add(new TESGuiButton(3, guiLeft + 40, guiTop + 75, 120, 20, StatCollector.translateToLocal("tes.gui.hornSelect.summon")));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
		if (itemstack == null || itemstack.getItem() != TESItems.commandHorn || itemstack.getItemDamage() != 0) {
			mc.thePlayer.closeScreen();
		}
	}
}