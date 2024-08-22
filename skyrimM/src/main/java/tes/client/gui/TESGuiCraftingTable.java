package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.inventory.TESContainerCraftingTable;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketSetOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class TESGuiCraftingTable extends GuiContainer {
	private static final ResourceLocation CRAFTING_TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");

	private final String unlocalizedName;
	private final TESContainerCraftingTable container;

	private GuiButton tableSwitcher;

	protected TESGuiCraftingTable(TESContainerCraftingTable container, String s) {
		super(container);
		this.container = container;
		unlocalizedName = s;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button instanceof TESGuiButtonTableSwitcher) {
				IMessage packet = new TESPacketSetOption(button.id);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else {
				super.actionPerformed(button);
			}
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		Minecraft mc = Minecraft.getMinecraft();
		tableSwitcher.drawButton(mc, i, j);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(CRAFTING_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		TESPlayerData pd = TESLevelData.getData(mc.thePlayer);
		boolean tableSwitched = pd.getTableSwitched();
		String title;
		if (tableSwitched) {
			title = StatCollector.translateToLocal("container.crafting");
		} else {
			title = StatCollector.translateToLocal("tes.container.crafting." + unlocalizedName);
		}
		fontRendererObj.drawString(title, 28, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	public void initGui() {
		super.initGui();
		tableSwitcher = new TESGuiButtonTableSwitcher(9, guiLeft + 9, guiTop + 35, StatCollector.translateToLocal("tes.gui.tableSwitcher"), container.getTableBlock());
		buttonList.add(tableSwitcher);
	}

	public static class Empire extends TESGuiCraftingTable {
		public Empire(InventoryPlayer inv, World world, int i, int j, int k) {
			super(new TESContainerCraftingTable.Empire(inv, world, i, j, k), "empire");
		}
	}
}