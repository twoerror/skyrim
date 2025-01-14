package tes.common.entity.ai;

import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class TESEntityAINPCMate extends EntityAIBase {
	private final TESEntityNPC theNPC;
	private final World theWorld;
	private final double moveSpeed;

	private TESEntityNPC theSpouse;
	private int spawnBabyDelay;

	public TESEntityAINPCMate(TESEntityNPC npc, double d) {
		theNPC = npc;
		theWorld = npc.worldObj;
		moveSpeed = d;
		setMutexBits(3);
	}

	@Override
	public boolean continueExecuting() {
		return theSpouse.isEntityAlive() && spawnBabyDelay < 60 && theNPC.getFamilyInfo().getAge() == 0 && theSpouse.getFamilyInfo().getAge() == 0;
	}

	@Override
	public void resetTask() {
		theSpouse = null;
		spawnBabyDelay = 0;
	}

	@Override
	public boolean shouldExecute() {
		if (theNPC.getClass() != theNPC.getFamilyInfo().getMarriageEntityClass() || theNPC.getFamilyInfo().getSpouseUniqueID() == null || theNPC.getFamilyInfo().getChildren() >= 2 || theNPC.getFamilyInfo().getAge() != 0) {
			return false;
		}
		theSpouse = theNPC.getFamilyInfo().getSpouse();
		return theSpouse != null && theNPC.getDistanceToEntity(theSpouse) < 16.0 && theSpouse.getFamilyInfo().getChildren() < 2 && theSpouse.getFamilyInfo().getAge() == 0;
	}

	private void spawnBaby() {
		TESEntityNPC baby = (TESEntityNPC) EntityList.createEntityByName(TESEntityRegistry.getStringFromClass(theNPC.getFamilyInfo().getMarriageEntityClass()), theWorld);
		TESEntityNPC maleParent = theNPC.getFamilyInfo().isMale() ? theNPC : theSpouse;
		TESEntityNPC femaleParent = theNPC.getFamilyInfo().isMale() ? theSpouse : theNPC;
		baby.getFamilyInfo().setChild();
		baby.getFamilyInfo().setMale(baby.getRNG().nextBoolean());
		baby.getFamilyInfo().setMaleParentID(maleParent.getUniqueID());
		baby.getFamilyInfo().setFemaleParentID(femaleParent.getUniqueID());
		baby.onSpawnWithEgg(null);
		baby.setLocationAndAngles(theNPC.posX, theNPC.posY, theNPC.posZ, 0.0f, 0.0f);
		baby.setNPCPersistent(true);
		theWorld.spawnEntityInWorld(baby);
		theNPC.getFamilyInfo().setMaxBreedingDelay();
		theSpouse.getFamilyInfo().setMaxBreedingDelay();
		theNPC.getFamilyInfo().setChildren(theNPC.getFamilyInfo().getChildren() + 1);
		theSpouse.getFamilyInfo().setChildren(theSpouse.getFamilyInfo().getChildren() + 1);
		theNPC.spawnHearts();
		theSpouse.spawnHearts();
	}

	@Override
	public void updateTask() {
		theNPC.getLookHelper().setLookPositionWithEntity(theSpouse, 10.0f, theNPC.getVerticalFaceSpeed());
		theNPC.getNavigator().tryMoveToEntityLiving(theSpouse, moveSpeed);
		++spawnBabyDelay;
		if (spawnBabyDelay % 20 == 0) {
			theNPC.spawnHearts();
		}
		if (spawnBabyDelay >= 60 && theNPC.getDistanceSqToEntity(theSpouse) < 9.0) {
			spawnBaby();
		}
	}
}