package tes.client.render.other;

import com.mojang.authlib.GameProfile;
import tes.client.model.TESModelBiped;
import tes.common.database.TESArmorModels;
import tes.common.database.TESCapes;
import tes.common.database.TESShields;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

public abstract class TESRenderBiped extends RenderBiped {
	protected static final TESModelBiped CAPE_MODEL = new TESModelBiped();

	protected float heldItemYTranslation = 0.1875f;

	private ModelBiped npcRenderPassModel;

	protected TESRenderBiped(ModelBiped model, float f) {
		super(model, f);
	}

	private static void renderNPCCape(TESEntityNPC entity) {
		TESCapes cape = entity.getCape();
		if (cape != null) {
			TESRenderCape.renderCape(cape, CAPE_MODEL);
		}
	}

	@Override
	public void doRender(EntityLiving entity, double d, double d1, double d2, float f, float f1) {
		super.doRender(entity, d, d1, d2, f, f1);
		if (Minecraft.isGuiEnabled() && entity instanceof TESEntityNPC) {
			TESEntityNPC npc = (TESEntityNPC) entity;
			if (npc.getHireableInfo().getHiringPlayer() == renderManager.livingPlayer) {
				TESNPCRendering.renderHiredIcon(npc, d, d1 + 0.5, d2);
				TESNPCRendering.renderNPCHealthBar(npc, d, d1 + 0.5, d2);
			}
			TESNPCRendering.renderQuestBook(npc, d, d1, d2);
			TESNPCRendering.renderQuestOffer(npc, d, d1, d2);
		}
	}

	@Override
	public void func_82408_c(EntityLiving entity, int pass, float f) {
		super.func_82408_c(entity, pass, f);
	}

	@Override
	public void func_82420_a(EntityLiving entity, ItemStack itemstack) {
		TESArmorModels.setupModelForRender(modelBipedMain, modelBipedMain, entity);
		TESArmorModels.setupModelForRender(field_82425_h, modelBipedMain, entity);
		TESArmorModels.setupModelForRender(field_82423_g, modelBipedMain, entity);
		if (npcRenderPassModel != null) {
			TESArmorModels.setupModelForRender(npcRenderPassModel, modelBipedMain, entity);
		}
	}

	@Override
	public void func_82421_b() {
		field_82423_g = new TESModelBiped(1.0f);
		field_82425_h = new TESModelBiped(0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return super.getEntityTexture(entity);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		float f1 = 0.9375f;
		GL11.glScalef(f1, f1, f1);
	}

	@Override
	public void renderEquippedItems(EntityLivingBase entity, float f) {
		float f1;
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		ItemStack headItem = entity.getEquipmentInSlot(4);
		if (headItem != null) {
			GL11.glPushMatrix();
			modelBipedMain.bipedHead.postRender(0.0625f);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(headItem, IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, headItem, IItemRenderer.ItemRendererHelper.BLOCK_3D);
			if (headItem.getItem() instanceof ItemBlock) {
				if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(headItem.getItem()).getRenderType())) {
					f1 = 0.625f;
					GL11.glTranslatef(0.0f, -0.25f, 0.0f);
					GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
					GL11.glScalef(f1, -f1, -f1);
				}
				renderManager.itemRenderer.renderItem(entity, headItem, 0);
			} else if (headItem.getItem() == Items.skull) {
				f1 = 1.0625f;
				GL11.glScalef(f1, -f1, -f1);
				GameProfile gameprofile = null;
				if (headItem.hasTagCompound()) {
					NBTTagCompound nbttagcompound = headItem.getTagCompound();
					if (nbttagcompound.hasKey("SkullOwner", new NBTTagCompound().getId())) {
						gameprofile = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("SkullOwner"));
					} else if (nbttagcompound.hasKey("SkullOwner", new NBTTagString().getId()) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkullOwner"))) {
						gameprofile = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
					}
				}
				TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5f, 0.0f, -0.5f, 1, 180.0f, headItem.getItemDamage(), gameprofile);
			}
			GL11.glPopMatrix();
		}
		ItemStack heldItem = entity.getHeldItem();
		if (heldItem != null) {
			float f12;
			GL11.glPushMatrix();
			if (mainModel.isChild) {
				float f13 = 0.5f;
				GL11.glTranslatef(0.0f, 0.625f, 0.0f);
				GL11.glRotatef(-20.0f, -1.0f, 0.0f, 0.0f);
				GL11.glScalef(f13, f13, f13);
			}
			modelBipedMain.bipedRightArm.postRender(0.0625f);
			GL11.glTranslatef(-0.0625f, 0.4375f, 0.0625f);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(heldItem, IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, heldItem, IItemRenderer.ItemRendererHelper.BLOCK_3D);
			if (heldItem.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(heldItem.getItem()).getRenderType()))) {
				f12 = 0.5f;
				GL11.glTranslatef(0.0f, heldItemYTranslation, -0.3125f);
				GL11.glRotatef(20.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
				GL11.glScalef(-(f12 *= 0.75f), -f12, f12);
			} else if (heldItem.getItem() == Items.bow) {
				f12 = 0.625f;
				GL11.glTranslatef(0.0f, heldItemYTranslation, 0.3125f);
				GL11.glRotatef(-20.0f, 0.0f, 1.0f, 0.0f);
				GL11.glScalef(f12, -f12, f12);
				GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
			} else if (heldItem.getItem().isFull3D()) {
				f12 = 0.625f;
				if (heldItem.getItem().shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
					GL11.glTranslatef(0.0f, -heldItemYTranslation, 0.0f);
				}
				GL11.glTranslatef(0.0f, heldItemYTranslation, 0.0f);
				GL11.glScalef(f12, -f12, f12);
				GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
			} else {
				f12 = 0.375f;
				GL11.glTranslatef(0.25f, heldItemYTranslation, -0.1875f);
				GL11.glScalef(f12, f12, f12);
				GL11.glRotatef(60.0f, 0.0f, 0.0f, 1.0f);
				GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(20.0f, 0.0f, 0.0f, 1.0f);
			}
			renderManager.itemRenderer.renderItem(entity, heldItem, 0);
			if (heldItem.getItem().requiresMultipleRenderPasses()) {
				for (int x = 1; x < heldItem.getItem().getRenderPasses(heldItem.getItemDamage()); ++x) {
					renderManager.itemRenderer.renderItem(entity, heldItem, x);
				}
			}
			GL11.glPopMatrix();
		}
		ItemStack heldItemLeft = ((TESEntityNPC) entity).getHeldItemLeft();
		if (heldItemLeft != null) {
			float f14;
			GL11.glPushMatrix();
			if (mainModel.isChild) {
				f1 = 0.5f;
				GL11.glTranslatef(0.0f, 0.625f, 0.0f);
				GL11.glRotatef(-20.0f, -1.0f, 0.0f, 0.0f);
				GL11.glScalef(f1, f1, f1);
			}
			modelBipedMain.bipedLeftArm.postRender(0.0625f);
			GL11.glTranslatef(0.0625f, 0.4375f, 0.0625f);
			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(heldItemLeft, IItemRenderer.ItemRenderType.EQUIPPED);
			boolean is3D = customRenderer != null && customRenderer.shouldUseRenderHelper(IItemRenderer.ItemRenderType.EQUIPPED, heldItemLeft, IItemRenderer.ItemRendererHelper.BLOCK_3D);
			if (heldItemLeft.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(heldItemLeft.getItem()).getRenderType()))) {
				f14 = 0.5f;
				GL11.glTranslatef(0.0f, heldItemYTranslation, -0.3125f);
				GL11.glRotatef(20.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
				GL11.glScalef(-(f14 *= 0.75f), -f14, f14);
			} else {
				if (heldItemLeft.getItem().isFull3D()) {
					f14 = 0.625f;
					if (heldItemLeft.getItem().shouldRotateAroundWhenRendering()) {
						GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
						GL11.glTranslatef(0.0f, -heldItemYTranslation, 0.0f);
					}
				} else {
					f14 = 0.3175f;
				}
				GL11.glTranslatef(0.0f, heldItemYTranslation, 0.0f);
				GL11.glScalef(f14, -f14, f14);
				GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
			}
			renderManager.itemRenderer.renderItem(entity, heldItemLeft, 0);
			if (heldItemLeft.getItem().requiresMultipleRenderPasses()) {
				for (int i = 1; i < heldItemLeft.getItem().getRenderPasses(heldItemLeft.getItemDamage()); ++i) {
					renderManager.itemRenderer.renderItem(entity, heldItemLeft, i);
				}
			}
			GL11.glPopMatrix();
		}
		renderNPCShield((TESEntityNPC) entity);
		renderNPCCape((TESEntityNPC) entity);
	}

	private void renderNPCShield(TESEntityNPC entity) {
		TESShields shield = entity.getShield();
		if (shield != null) {
			TESRenderShield.renderShield(shield, entity, modelBipedMain);
		}
	}

	@Override
	public void setRenderPassModel(ModelBase model) {
		super.setRenderPassModel(model);
		if (model instanceof ModelBiped) {
			npcRenderPassModel = (ModelBiped) model;
		}
	}

	@Override
	public int shouldRenderPass(EntityLiving entity, int pass, float f) {
		ItemStack armor = entity.getEquipmentInSlot(3 - pass + 1);
		int specialArmorResult = TESArmorModels.INSTANCE.getEntityArmorModel(this, modelBipedMain, entity, armor, pass);
		if (specialArmorResult > 0) {
			return specialArmorResult;
		}
		return super.shouldRenderPass(entity, pass, f);
	}

	public ModelBiped getNpcRenderPassModel() {
		return npcRenderPassModel;
	}
}