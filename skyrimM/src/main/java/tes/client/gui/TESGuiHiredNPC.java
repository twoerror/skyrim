package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketHiredUnitCommand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("WeakerAccess")
public abstract class TESGuiHiredNPC extends TESGuiScreenBase {
	protected static final int Y_SIZE = 220;
	protected static final int X_SIZE = 200;

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/npc/hired.png");

	protected final TESEntityNPC theNPC;
	protected int guiLeft;
	protected int guiTop;
	protected int page;

	protected TESGuiHiredNPC(TESEntityNPC npc) {
		theNPC = npc;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, X_SIZE, Y_SIZE);
		String s = theNPC.getNPCName();
		fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 11, 3618615);
		s = theNPC.getEntityClassName();
		fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 26, 3618615);
		if (page == 0 && theNPC.getHireableInfo().hasHiringRequirements()) {
			int x = guiLeft + 6;
			int y = guiTop + 170;
			s = StatCollector.translateToLocal("tes.hiredNPC.commandReq");
			fontRendererObj.drawString(s, x, y, 3618615);
			y += fontRendererObj.FONT_HEIGHT;
			x += 4;
			int maxWidth = X_SIZE - 12 - 4;
			TESFaction fac = theNPC.getFaction();
			String alignS = TESAlignmentValues.formatAlignForDisplay(theNPC.getHireableInfo().getAlignmentRequiredToCommand());
			String alignReq = StatCollector.translateToLocalFormatted("tes.hiredNPC.commandReq.align", alignS, fac.factionName());
			Collection<String> requirementLines = new ArrayList<String>(fontRendererObj.listFormattedStringToWidth(alignReq, maxWidth));
			TESUnitTradeEntry.PledgeType pledge = theNPC.getHireableInfo().getPledgeType();
			String pledgeReq = pledge.getCommandReqText(fac);
			if (pledgeReq != null) {
				requirementLines.addAll(fontRendererObj.listFormattedStringToWidth(pledgeReq, maxWidth));
			}
			for (String obj : requirementLines) {
				fontRendererObj.drawString(obj, x, y, 3618615);
				y += fontRendererObj.FONT_HEIGHT;
			}
		}
		super.drawScreen(i, j, f);
	}

	@Override
	public void initGui() {
		guiLeft = (width - X_SIZE) / 2;
		guiTop = (height - Y_SIZE) / 2;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		sendActionPacket(-1);
	}

	protected void sendActionPacket(int action) {
		sendActionPacket(action, 0);
	}

	protected void sendActionPacket(int action, int value) {
		IMessage packet = new TESPacketHiredUnitCommand(theNPC.getEntityId(), page, action, value);
		TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		if (!theNPC.isEntityAlive() || theNPC.getHireableInfo().getHiringPlayer() != mc.thePlayer || theNPC.getDistanceSqToEntity(mc.thePlayer) > 64.0) {
			mc.thePlayer.closeScreen();
		}
	}
}