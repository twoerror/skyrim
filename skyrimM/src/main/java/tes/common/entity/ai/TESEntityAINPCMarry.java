package tes.common.entity.ai;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.entity.other.TESEntityNPC;
import tes.common.faction.TESAlignmentValues;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class TESEntityAINPCMarry extends EntityAIBase {
	private final TESEntityNPC theNPC;
	private final World theWorld;
	private final double moveSpeed;

	private TESEntityNPC theSpouse;
	private int marryDelay;

	public TESEntityAINPCMarry(TESEntityNPC npc, double d) {
		theNPC = npc;
		theWorld = npc.worldObj;
		moveSpeed = d;
		setMutexBits(3);
	}

	@Override
	public boolean continueExecuting() {
		return theSpouse != null && theSpouse.isEntityAlive() && theNPC.getFamilyInfo().canMarryNPC(theSpouse) && theSpouse.getFamilyInfo().canMarryNPC(theNPC);
	}

	private void marry() {
		int maxChildren = theNPC.getFamilyInfo().getRandomMaxChildren();
		theNPC.getFamilyInfo().setSpouseUniqueID(theSpouse.getUniqueID());
		theSpouse.getFamilyInfo().setSpouseUniqueID(theNPC.getUniqueID());
		theNPC.setCurrentItemOrArmor(0, null);
		theNPC.setCurrentItemOrArmor(4, new ItemStack(TESItems.goldRing));
		theSpouse.setCurrentItemOrArmor(0, null);
		theSpouse.setCurrentItemOrArmor(4, new ItemStack(TESItems.goldRing));
		theNPC.getFamilyInfo().setMaxChildren(maxChildren);
		theSpouse.getFamilyInfo().setMaxChildren(maxChildren);
		theNPC.getFamilyInfo().setMaxBreedingDelay();
		theSpouse.getFamilyInfo().setMaxBreedingDelay();
		theNPC.spawnHearts();
		theSpouse.spawnHearts();
		EntityPlayer ringPlayer = theNPC.getFamilyInfo().getRingGivingPlayer();
		if (ringPlayer != null) {
			TESLevelData.getData(ringPlayer).addAlignment(ringPlayer, TESAlignmentValues.MARRIAGE_BONUS, theNPC.getFaction(), theNPC);

		}
		EntityPlayer ringPlayerSpouse = theSpouse.getFamilyInfo().getRingGivingPlayer();
		if (ringPlayerSpouse != null) {
			TESLevelData.getData(ringPlayerSpouse).addAlignment(ringPlayerSpouse, TESAlignmentValues.MARRIAGE_BONUS, theSpouse.getFaction(), theSpouse);
			TESLevelData.getData(ringPlayer).addAchievement(TESAchievement.marry);
		}
		theWorld.spawnEntityInWorld(new EntityXPOrb(theWorld, theNPC.posX, theNPC.posY, theNPC.posZ, theNPC.getRNG().nextInt(8) + 2));
	}

	@Override
	public void resetTask() {
		theSpouse = null;
		marryDelay = 0;
	}

	@Override
	public boolean shouldExecute() {
		if (theNPC.getClass() != theNPC.getFamilyInfo().getMarriageEntityClass() || theNPC.getFamilyInfo().getSpouseUniqueID() != null || theNPC.getFamilyInfo().getAge() != 0 || theNPC.getEquipmentInSlot(4) != null || theNPC.getEquipmentInSlot(0) == null) {
			return false;
		}
		List<TESEntityNPC> list = theNPC.worldObj.getEntitiesWithinAABB(theNPC.getFamilyInfo().getMarriageEntityClass(), theNPC.boundingBox.expand(16.0, 4.0, 16.0));
		TESEntityNPC spouse = null;
		double distanceSq = Double.MAX_VALUE;
		for (TESEntityNPC candidate : list) {
			double d;
			if (!theNPC.getFamilyInfo().canMarryNPC(candidate) || !candidate.getFamilyInfo().canMarryNPC(theNPC) || (d = theNPC.getDistanceSqToEntity(candidate)) > distanceSq) {
				continue;
			}
			distanceSq = d;
			spouse = candidate;
		}
		if (spouse == null) {
			return false;
		}
		theSpouse = spouse;
		return true;
	}

	@Override
	public void updateTask() {
		theNPC.getLookHelper().setLookPositionWithEntity(theSpouse, 10.0f, theNPC.getVerticalFaceSpeed());
		theNPC.getNavigator().tryMoveToEntityLiving(theSpouse, moveSpeed);
		++marryDelay;
		if (marryDelay % 20 == 0) {
			theNPC.spawnHearts();
		}
		if (marryDelay >= 60 && theNPC.getDistanceSqToEntity(theSpouse) < 9.0) {
			marry();
		}
	}
}