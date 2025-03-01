package tes.common.item.weapon;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.TES;
import tes.common.TESSquadrons;
import tes.common.entity.ai.TESEntityAINearestAttackableTargetBasic;
import tes.common.entity.other.TESEntityNPC;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketLocationFX;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class TESItemCommandSword extends TESItemSword implements TESSquadrons.SquadronItem {
	public TESItemCommandSword() {
		super(ToolMaterial.IRON);
		setMaxDamage(0);
		tesWeaponDamage = 1.0f;
	}

	private static void command(EntityPlayer entityplayer, World world, ItemStack itemstack, MovingObjectPosition hitTarget) {
		entityplayer.setRevengeTarget(null);
		Collection<Entity> spreadTargets = new ArrayList<>();
		if (hitTarget != null) {
			Vec3 vec = hitTarget.hitVec;
			AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(vec.xCoord, vec.yCoord, vec.zCoord, vec.xCoord, vec.yCoord, vec.zCoord);
			aabb = aabb.expand(6.0D, 6.0D, 6.0D);
			spreadTargets = world.selectEntitiesWithinAABB(EntityLivingBase.class, aabb, new EntitySelectorImpl(entityplayer));
		}
		boolean anyAttackCommanded = false;
		List<TESEntityNPC> nearbyHiredUnits = world.getEntitiesWithinAABB(TESEntityNPC.class, entityplayer.boundingBox.expand(12.0D, 12.0D, 12.0D));
		for (TESEntityNPC npc : nearbyHiredUnits) {
			if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer() == entityplayer && npc.getHireableInfo().getObeyCommandSword() && TESSquadrons.areSquadronsCompatible(npc, itemstack)) {
				List<EntityLivingBase> validTargets = new ArrayList<>();
				if (!spreadTargets.isEmpty()) {
					for (Object obj : spreadTargets) {
						EntityLivingBase entity = (EntityLivingBase) obj;
						if (TES.canNPCAttackEntity(npc, entity, true)) {
							validTargets.add(entity);
						}
					}
				}
				if (validTargets.isEmpty()) {
					npc.getHireableInfo().commandSwordCancel();
				} else {
					Comparator<Entity> targetSorter = new TESEntityAINearestAttackableTargetBasic.TargetSorter(npc);
					validTargets.sort(targetSorter);
					EntityLivingBase target = validTargets.get(0);
					npc.getHireableInfo().commandSwordAttack(target);
					npc.getHireableInfo().setWasAttackCommanded(true);
					anyAttackCommanded = true;
				}
			}
		}
		if (anyAttackCommanded) {
			Vec3 vec = hitTarget.hitVec;
			IMessage lOTRPacketLocationFX = new TESPacketLocationFX(TESPacketLocationFX.Type.SWORD_COMMAND, vec.xCoord, vec.yCoord, vec.zCoord);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(lOTRPacketLocationFX, (EntityPlayerMP) entityplayer);
		}
	}

	private static Entity getEntityTarget(EntityPlayer entityplayer) {
		double range = 64.0;
		Vec3 eyePos = Vec3.createVectorHelper(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);
		Vec3 look = entityplayer.getLookVec();
		Vec3 sight = eyePos.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
		float sightWidth = 1.0f;
		List<? extends Entity> list = entityplayer.worldObj.getEntitiesWithinAABBExcludingEntity(entityplayer, entityplayer.boundingBox.addCoord(look.xCoord * range, look.yCoord * range, look.zCoord * range).expand(sightWidth, sightWidth, sightWidth));
		Entity pointedEntity = null;
		double entityDist = range;
		for (Entity element : list) {
			double d;
			if (!(element instanceof EntityLivingBase) || !element.canBeCollidedWith()) {
				continue;
			}
			float width = 1.0f;
			AxisAlignedBB axisalignedbb = element.boundingBox.expand(width, width, width);
			MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(eyePos, sight);
			if (axisalignedbb.isVecInside(eyePos)) {
				if (entityDist < 0.0) {
					continue;
				}
				pointedEntity = element;
				entityDist = 0.0;
				continue;
			}
			if (movingobjectposition == null || (d = eyePos.distanceTo(movingobjectposition.hitVec)) >= entityDist && entityDist != 0.0) {
				continue;
			}
			if (element == entityplayer.ridingEntity && !element.canRiderInteract()) {
				if (entityDist != 0.0) {
					continue;
				}
				pointedEntity = element;
				continue;
			}
			pointedEntity = element;
			entityDist = d;
		}
		return pointedEntity;
	}

	@Override
	public int getItemEnchantability() {
		return 0;
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		entityplayer.swingItem();
		if (!world.isRemote) {
			Entity entity = getEntityTarget(entityplayer);
			if (entity != null) {
				MovingObjectPosition entityHit = new MovingObjectPosition(entity, Vec3.createVectorHelper(entity.posX, entity.boundingBox.minY + entity.height / 2.0f, entity.posZ));
				command(entityplayer, world, itemstack, entityHit);
			} else {
				double range = 64.0;
				Vec3 eyePos = Vec3.createVectorHelper(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);
				Vec3 look = entityplayer.getLookVec();
				Vec3 sight = eyePos.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
				MovingObjectPosition rayTrace = world.func_147447_a(eyePos, sight, false, false, true);
				if (rayTrace != null && rayTrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
					command(entityplayer, world, itemstack, rayTrace);
				} else {
					command(entityplayer, world, itemstack, null);
				}
			}
		}
		return itemstack;
	}

	private static class EntitySelectorImpl implements IEntitySelector {
		private final EntityPlayer entityplayer;

		private EntitySelectorImpl(EntityPlayer entityplayer) {
			this.entityplayer = entityplayer;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return entity.isEntityAlive() && TES.canPlayerAttackEntity(entityplayer, (EntityLivingBase) entity, false);
		}
	}
}