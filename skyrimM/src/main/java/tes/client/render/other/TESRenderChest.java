package tes.client.render.other;

import cpw.mods.fml.common.FMLLog;
import tes.common.block.other.TESBlockChest;
import tes.common.block.other.TESBlockSpawnerChest;
import tes.common.tileentity.TESTileEntityChest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class TESRenderChest extends TileEntitySpecialRenderer {
	private static final Map<String, ResourceLocation> CHEST_TEXTURES = new HashMap<>();
	private static final TESTileEntityChest ITEM_ENTITY = new TESTileEntityChest();
	private static final ModelChest CHEST_MODEL = new ModelChest();

	private static ResourceLocation getChestTexture(String s) {
		ResourceLocation r = CHEST_TEXTURES.get(s);
		if (r == null) {
			r = new ResourceLocation("tes:textures/model/chest/" + s + ".png");
			CHEST_TEXTURES.put(s, r);
		}
		return r;
	}

	public void renderInvChest(Block block) {
		Block c;
		GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		ITEM_ENTITY.setTextureName("");
		if (block instanceof TESBlockChest) {
			ITEM_ENTITY.setTextureName(((TESBlockChest) block).getChestTextureName());
		} else if (block instanceof TESBlockSpawnerChest && (c = ((TESBlockSpawnerChest) block).getChestModel()) instanceof TESBlockChest) {
			ITEM_ENTITY.setTextureName(((TESBlockChest) c).getChestTextureName());
		}
		renderTileEntityAt(ITEM_ENTITY, 0.0, 0.0, 0.0, 0.0f);
		GL11.glEnable(32826);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		int meta;
		TESTileEntityChest chest = (TESTileEntityChest) tileentity;
		if (chest.hasWorldObj()) {
			Block block = tileentity.getBlockType();
			meta = tileentity.getBlockMetadata();
			if (block instanceof BlockChest && meta == 0) {
				try {
					((BlockChest) block).func_149954_e(tileentity.getWorldObj(), tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
				} catch (ClassCastException e) {
					FMLLog.severe("Attempted to render a chest at %d,  %d, %d that was not a chest", tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
				}
				meta = tileentity.getBlockMetadata();
			}
		} else {
			meta = 0;
		}
		GL11.glPushMatrix();
		GL11.glEnable(32826);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glTranslatef((float) d, (float) d1 + 1.0f, (float) d2 + 1.0f);
		GL11.glScalef(1.0f, -1.0f, -1.0f);
		GL11.glTranslatef(0.5f, 0.5f, 0.5f);
		float rot = 0.0f;
		if (meta == 2) {
			rot = 180.0f;
		}
		if (meta == 3) {
			rot = 0.0f;
		}
		if (meta == 4) {
			rot = 90.0f;
		}
		if (meta == 5) {
			rot = -90.0f;
		}
		GL11.glRotatef(rot, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		float lid = chest.getPrevLidAngle() + (chest.getLidAngle() - chest.getPrevLidAngle()) * f;
		lid = 1.0f - lid;
		lid = 1.0f - lid * lid * lid;
		CHEST_MODEL.chestLid.rotateAngleX = -(lid * 3.1415927f / 2.0f);
		bindTexture(getChestTexture(chest.getTextureName()));
		CHEST_MODEL.renderAll();
		GL11.glDisable(32826);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
}