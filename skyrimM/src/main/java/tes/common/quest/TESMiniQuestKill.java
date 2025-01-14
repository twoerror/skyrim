package tes.common.quest;

import tes.common.TESPlayerData;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

import java.util.Random;

public abstract class TESMiniQuestKill extends TESMiniQuest {
	protected int killTarget;
	protected int killCount;

	protected TESMiniQuestKill(TESPlayerData pd) {
		super(pd);
	}

	@Override
	public float getAlignmentBonus() {
		return killTarget * rewardFactor;
	}

	@Override
	public int getCoinBonus() {
		return Math.round(killTarget * 2.0f * rewardFactor);
	}

	@Override
	public float getCompletionFactor() {
		return (float) killCount / killTarget;
	}

	protected abstract String getKillTargetName();

	@Override
	public String getObjectiveInSpeech() {
		return killTarget + " " + getKillTargetName();
	}

	@Override
	public String getProgressedObjectiveInSpeech() {
		return killTarget - killCount + " " + getKillTargetName();
	}

	@Override
	public ItemStack getQuestIcon() {
		return new ItemStack(Items.iron_sword);
	}

	@Override
	public String getQuestObjective() {
		return StatCollector.translateToLocalFormatted("tes.miniquest.kill", killTarget, getKillTargetName());
	}

	@Override
	public String getQuestProgress() {
		return StatCollector.translateToLocalFormatted("tes.miniquest.kill.progress", killCount, killTarget);
	}

	@Override
	public String getQuestProgressShorthand() {
		return StatCollector.translateToLocalFormatted("tes.miniquest.progressShort", killCount, killTarget);
	}

	@Override
	public boolean isValidQuest() {
		return super.isValidQuest() && killTarget > 0;
	}

	@Override
	public void onInteract(EntityPlayer entityplayer, TESEntityNPC npc) {
		if (killCount >= killTarget) {
			complete(entityplayer, npc);
		} else {
			sendProgressSpeechbank(entityplayer, npc);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		killTarget = nbt.getInteger("Target");
		killCount = nbt.getInteger("Count");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("Target", killTarget);
		nbt.setInteger("Count", killCount);
	}

	public abstract static class QFKill<Q extends TESMiniQuestKill> extends TESMiniQuest.QuestFactoryBase<Q> {
		protected int minTarget;
		protected int maxTarget;

		protected QFKill(String name) {
			super(name);
		}

		@Override
		public Q createQuest(TESEntityNPC npc, Random rand) {
			Q quest = super.createQuest(npc, rand);
			quest.killTarget = MathHelper.getRandomIntegerInRange(rand, minTarget, maxTarget);
			return quest;
		}

		public void setKillTarget(int min, int max) {
			minTarget = min;
			maxTarget = max;
		}
	}
}