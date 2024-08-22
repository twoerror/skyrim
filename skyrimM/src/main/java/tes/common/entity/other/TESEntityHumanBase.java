package tes.common.entity.other;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.entity.ai.*;
import tes.common.entity.other.utils.TESEntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public abstract class TESEntityHumanBase extends TESEntityNPC {
	private final boolean canBeMarried;

	protected TESEntityHumanBase(World world) {
		super(world);
		canBeMarried = TESEntityUtils.canBeMarried(this);
		if (canBeMarried) {
			tasks.addTask(2, new TESEntityAINPCAvoidEvilPlayer(this, 8.0f, 1.5, 1.8));
			tasks.addTask(5, new TESEntityAINPCMarry(this, 1.3));
			tasks.addTask(6, new TESEntityAINPCMate(this, 1.3));
			tasks.addTask(7, new TESEntityAINPCFollowParent(this, 1.4));
			tasks.addTask(8, new TESEntityAINPCFollowSpouse(this, 1.1));
			tasks.addTask(7, new TESEntityAINPCFollowParent(this, 1.4));
			familyInfo.setMarriageEntityClass(getClass());
		}

		boolean canSmoke = TESEntityUtils.canSmokeDrink(this);
		if (canSmoke) {
			tasks.addTask(6, new TESEntityAISmoke(this, 8000));
		}
	}

	@Override
	public TESAchievement getKillAchievement() {
		return TESAchievement.killer;
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		return canBeMarried && familyInfo.interact(entityplayer) || super.interact(entityplayer);
	}

	@Override
	public void onArtificalSpawn() {
		if (canBeMarried && getClass() == familyInfo.getMarriageEntityClass() && rand.nextInt(7) == 0) {
			familyInfo.setChild();
		}
	}

	@Override
	public void setupNPCGender() {
		familyInfo.setMale(rand.nextBoolean());
	}

	@Override
	public boolean speakTo(EntityPlayer entityplayer) {
		boolean flag = super.speakTo(entityplayer);
		if (flag && isDrunkard() && entityplayer.isPotionActive(Potion.confusion.id)) {
			TESLevelData.getData(entityplayer).addAchievement(TESAchievement.speakToDrunkard);
		}
		return flag;
	}
}