package tes.common.database;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tes.client.model.*;
import tes.common.entity.other.TESEntityNPC;
import tes.common.item.other.TESItemBanner;
import tes.common.item.other.TESItemSling;
import tes.common.item.weapon.TESItemCrossbow;
import tes.common.item.weapon.TESItemSpear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class TESArmorModels {
	public static final TESArmorModels INSTANCE = new TESArmorModels();

	private final Map<ModelBiped, Map<Item, ModelBiped>> specialArmorModels = new HashMap<>();

	private TESArmorModels() {
	}

	public static void copyBoxRotations(ModelRenderer target, ModelRenderer src) {
		target.rotationPointX = src.rotationPointX;
		target.rotationPointY = src.rotationPointY;
		target.rotationPointZ = src.rotationPointZ;
		target.rotateAngleX = src.rotateAngleX;
		target.rotateAngleY = src.rotateAngleY;
		target.rotateAngleZ = src.rotateAngleZ;
	}

	private static void copyModelRotations(ModelBiped target, ModelBiped src) {
		copyBoxRotations(target.bipedHead, src.bipedHead);
		copyBoxRotations(target.bipedHeadwear, src.bipedHeadwear);
		copyBoxRotations(target.bipedBody, src.bipedBody);
		copyBoxRotations(target.bipedRightArm, src.bipedRightArm);
		copyBoxRotations(target.bipedLeftArm, src.bipedLeftArm);
		copyBoxRotations(target.bipedRightLeg, src.bipedRightLeg);
		copyBoxRotations(target.bipedLeftLeg, src.bipedLeftLeg);
	}

	public static void setupArmorForSlot(ModelBiped model, int slot) {
		model.bipedHead.showModel = slot == 0;
		model.bipedHeadwear.showModel = slot == 0;
		model.bipedBody.showModel = slot == 1 || slot == 2;
		model.bipedRightArm.showModel = slot == 1;
		model.bipedLeftArm.showModel = slot == 1;
		model.bipedRightLeg.showModel = slot == 2 || slot == 3;
		model.bipedLeftLeg.showModel = slot == 2 || slot == 3;
	}

	private static void setupHeldItem(ModelBiped model, EntityLivingBase entity, ItemStack itemstack, boolean rightArm) {
		int value = 0;
		boolean aimBow = false;
		if (itemstack != null) {
			value = 1;
			Item item = itemstack.getItem();
			boolean isRanged = false;
			if (itemstack.getItemUseAction() == EnumAction.bow) {
				if (item instanceof TESItemSpear) {
					isRanged = entity instanceof EntityPlayer;
				} else {
					isRanged = !(item instanceof ItemSword);
				}
			}
			if (item instanceof TESItemSling) {
				isRanged = true;
			}
			if (isRanged) {
				boolean aiming = model.aimedBow;
				if (entity instanceof EntityPlayer && TESItemCrossbow.isLoaded(itemstack)) {
					aiming = true;
				}
				if (entity instanceof TESEntityNPC) {
					aiming = ((TESEntityNPC) entity).isClientCombatStance();
				}
				if (aiming) {
					value = 3;
					aimBow = true;
				}
			}
			if (item instanceof TESItemBanner) {
				value = 3;
			}
			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).getItemInUseCount() > 0 && itemstack.getItemUseAction() == EnumAction.block) {
				value = 3;
			}
			if (entity instanceof TESEntityNPC && ((TESEntityNPC) entity).isClientIsEating()) {
				value = 3;
			}
		}
		if (rightArm) {
			model.heldItemRight = value;
			model.aimedBow = aimBow;
		} else {
			model.heldItemLeft = value;
		}
	}

	public static void setupModelForRender(ModelBiped model, ModelBiped mainModel, EntityLivingBase entity) {
		if (mainModel != null) {
			model.onGround = mainModel.onGround;
			model.isRiding = mainModel.isRiding;
			model.isChild = mainModel.isChild;
			model.isSneak = mainModel.isSneak;
		} else {
			model.onGround = 0.0f;
			model.isRiding = false;
			model.isChild = false;
			model.isSneak = false;
		}
		if (entity instanceof TESEntityNPC) {
			model.bipedHeadwear.showModel = true;
		}
		if (entity instanceof EntityPlayer) {
			ItemStack heldRight = entity.getHeldItem();
			if (mainModel != null) {
				model.aimedBow = mainModel.aimedBow;
			}
			setupHeldItem(model, entity, heldRight, true);
		} else {
			ItemStack heldRight;
			if (entity == null) {
				heldRight = null;
			} else {
				heldRight = entity.getHeldItem();
			}
			ItemStack heldLeft;
			if (entity instanceof TESEntityNPC) {
				heldLeft = ((TESEntityNPC) entity).getHeldItemLeft();
			} else {
				heldLeft = null;
			}
			setupHeldItem(model, entity, heldRight, true);
			setupHeldItem(model, entity, heldLeft, false);
		}
	}

	public int getEntityArmorModel(RendererLivingEntity renderer, ModelBiped mainModel, EntityLivingBase entity, ItemStack armor, int slot) {
		ModelBiped armorModel = getSpecialArmorModel(armor, slot, mainModel);
		if (armorModel != null) {
			int color;
			Item armorItem;
			if (armor != null) {
				armorItem = armor.getItem();
			} else {
				armorItem = null;
			}
			if (armorItem instanceof ItemArmor) {
				Minecraft.getMinecraft().getTextureManager().bindTexture(RenderBiped.getArmorResource(entity, armor, slot, null));
			}
			renderer.setRenderPassModel(armorModel);
			setupModelForRender(armorModel, mainModel, entity);
			if (armorItem instanceof ItemArmor && (color = ((ItemArmor) armorItem).getColor(armor)) != -1) {
				float r = (color >> 16 & 0xFF) / 255.0f;
				float g = (color >> 8 & 0xFF) / 255.0f;
				float b = (color & 0xFF) / 255.0f;
				GL11.glColor3f(r, g, b);
				if (armor.isItemEnchanted()) {
					return 31;
				}
				return 16;
			}
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			if (armor != null && armor.isItemEnchanted()) {
				return 15;
			}
			return 1;
		}
		return 0;
	}

	@SubscribeEvent
	public void getPlayerArmorModel(RenderPlayerEvent.SetArmorModel event) {
		RenderPlayer renderer = event.renderer;
		ModelBiped mainModel = renderer.modelBipedMain;
		EntityPlayer entityplayer = event.entityPlayer;
		ItemStack armor = event.stack;
		int slot = event.slot;
		int result = getEntityArmorModel(renderer, mainModel, entityplayer, armor, 3 - slot);
		if (result > 0) {
			event.result = result;
		}
	}

	public ModelBiped getSpecialArmorModel(ItemStack itemstack, int slot, ModelBiped mainModel) {
		if (itemstack == null) {
			return null;
		}
		Item item = itemstack.getItem();
		ModelBiped model = getSpecialModels(mainModel).get(item);
		if (model == null) {
			return null;
		}
		if (model instanceof TESModelLeatherHat) {
			((TESModelLeatherHat) model).setHatItem(itemstack);
		}
		if (model instanceof TESModelRobes) {
			((TESModelRobes) model).setRobeItem(itemstack);
		}
		if (model instanceof TESModelPlateHead) {
			((TESModelPlateHead) model).setPlateItem(itemstack);
		}
		copyModelRotations(model, mainModel);
		setupArmorForSlot(model, slot);
		return model;
	}

	private Map<Item, ModelBiped> getSpecialModels(ModelBiped key) {
		Map<Item, ModelBiped> map = specialArmorModels.get(key);
		if (map == null) {
			map = new HashMap<>();
			map.put(TESItems.bittersteelHelmet, new TESModelSandorHelmet(1.0f));
			map.put(TESItems.ceramicPlate, new TESModelPlateHead());
			map.put(TESItems.harpy, new TESModelHarpy(1.0f));
			map.put(TESItems.leatherHat, new TESModelLeatherHat(0.0f));
			map.put(TESItems.plate, new TESModelPlateHead());
			for (ModelBiped armorModel : map.values()) {
				copyModelRotations(armorModel, key);
			}
			specialArmorModels.put(key, map);
		}
		return map;
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void preRenderEntity(RenderLivingEvent.Pre event) {
		EntityLivingBase entity = event.entity;
		RendererLivingEntity renderer = event.renderer;
		if (entity instanceof EntityPlayer && renderer instanceof RenderPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			RenderPlayer renderplayer = (RenderPlayer) renderer;
			ModelBiped mainModel = renderplayer.modelBipedMain;
			ModelBiped armorModel1 = renderplayer.modelArmorChestplate;
			ModelBiped armorModel2 = renderplayer.modelArmor;
			setupModelForRender(mainModel, mainModel, entityplayer);
			setupModelForRender(armorModel1, mainModel, entityplayer);
			setupModelForRender(armorModel2, mainModel, entityplayer);
		}
	}
}
