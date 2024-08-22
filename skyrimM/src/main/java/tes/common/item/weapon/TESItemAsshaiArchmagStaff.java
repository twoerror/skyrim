package tes.common.item.weapon;

import tes.TES;
import tes.common.database.TESCreativeTabs;
import tes.common.entity.dragon.TESEntityDragon;
import tes.common.entity.other.TESEntitySpiderBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;

import java.util.List;

public class TESItemAsshaiArchmagStaff extends TESItemSword {
	public TESItemAsshaiArchmagStaff() {
		super(ToolMaterial.WOOD);
		setCreativeTab(TESCreativeTabs.TAB_STORY);
		setMaxDamage(1500);
		tesWeaponDamage = 8.0f;
	}

	private static ItemStack useStaff(ItemStack itemstack, World world, EntityLivingBase user) {
		user.swingItem();
		if (!world.isRemote) {
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, user.boundingBox.expand(64, 64, 64));
			for (EntityLivingBase entity : entities) {
				if (entity != user && (!(entity instanceof EntityHorse) || !((EntityHorse) entity).isTame()) && (!(entity instanceof EntityTameable) || !((EntityTameable) entity).isTamed()) && (!(entity instanceof TESEntitySpiderBase) || !((TESEntitySpiderBase) entity).isNPCTamed())) {
					entity.attackEntityFrom(new EntityDamageSourceIndirect("tes.staff", entity, user).setMagicDamage().setDamageBypassesArmor().setDamageAllowedInCreativeMode(), 5);
					if (TES.canPlayerAttackEntity((EntityPlayer) user, entity, false)) {
						world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ));
					}
				}
			}
		}
		return itemstack;
	}

	public static void wizardUseStaff(World world, EntityLivingBase user) {
		user.swingItem();
		if (!world.isRemote) {
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, user.boundingBox.expand(64, 64, 64));
			for (EntityLivingBase entity : entities) {
				if (entity != user && (!(entity instanceof EntityHorse) || !((EntityHorse) entity).isTame()) && (!(entity instanceof TESEntityDragon) || !((EntityTameable) entity).isTamed()) && (!(entity instanceof EntityTameable) || !((EntityTameable) entity).isTamed()) && (!(entity instanceof TESEntitySpiderBase) || !((TESEntitySpiderBase) entity).isNPCTamed())) {
					entity.attackEntityFrom(new EntityDamageSourceIndirect("tes.staff", entity, user).setMagicDamage().setDamageBypassesArmor().setDamageAllowedInCreativeMode(), 5);
					if (TES.canNPCAttackEntity((EntityCreature) user, entity, false)) {
						world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ));
					}
				}
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		itemstack.damageItem(2, entityplayer);
		return useStaff(itemstack, world, entityplayer);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}
}