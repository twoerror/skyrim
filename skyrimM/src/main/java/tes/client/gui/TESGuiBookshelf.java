package tes.client.gui;

import tes.common.inventory.TESContainerBookshelf;
import tes.common.tileentity.TESTileEntityBookshelf;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiBookshelf extends GuiContainer {
	private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

	private final IInventory playerInv;
	private final IInventory shelfInv;

	private final int inventoryRows;

	public TESGuiBookshelf(IInventory player, TESTileEntityBookshelf shelf) {
		super(new TESContainerBookshelf(player, shelf));
		playerInv = player;
		shelfInv = shelf;
		allowUserInput = false;
		int i = 222;
		int j = i - 108;
		inventoryRows = shelf.getSizeInventory() / 9;
		ySize = j + inventoryRows * 18;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(CHEST_TEXTURE);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, inventoryRows * 18 + 17);
		drawTexturedModalRect(k, l + inventoryRows * 18 + 17, 0, 126, xSize, 96);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		fontRendererObj.drawString(shelfInv.hasCustomInventoryName() ? shelfInv.getInventoryName() : StatCollector.translateToLocal(shelfInv.getInventoryName()), 8, 6, 4210752);
		fontRendererObj.drawString(playerInv.hasCustomInventoryName() ? playerInv.getInventoryName() : StatCollector.translateToLocal(playerInv.getInventoryName()), 8, ySize - 96 + 2, 4210752);
	}
}