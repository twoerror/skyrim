package tes.common.quest;

import tes.TES;
import tes.common.TESPlayerData;
import tes.common.entity.other.TESEntityNPC;
import tes.common.faction.TESFaction;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

public class TESMiniQuestKillFaction extends TESMiniQuestKill {
	private TESFaction killFaction;

	@SuppressWarnings("unused")
	public TESMiniQuestKillFaction(TESPlayerData pd) {
		super(pd);
	}

	@Override
	public void handleEvent(TESMiniQuestEvent event) {
	}

	@Override
	public String getKillTargetName() {
		return killFaction.factionEntityName();
	}

	@Override
	public boolean isValidQuest() {
		return super.isValidQuest() && killFaction != null;
	}

	@Override
	public void onKill(EntityPlayer entityplayer, EntityLivingBase entity) {
		if (killCount < killTarget && TES.getNPCFaction(entity) == killFaction) {
			++killCount;
			playerData.updateMiniQuest(this);
		}
	}

	@Override
	public void onKilledByPlayer(EntityPlayer entityplayer, EntityPlayer killer) {
	}

	@Override
	public void onPlayerTick() {
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		killFaction = TESFaction.forName(nbt.getString("KillFaction"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("KillFaction", killFaction.codeName());
	}

	public static class QFKillFaction extends TESMiniQuestKill.QFKill<TESMiniQuestKillFaction> {
		protected TESFaction killFaction;

		public QFKillFaction() {
			super("kill");
		}

		@Override
		public TESMiniQuestKillFaction createQuest(TESEntityNPC npc, Random rand) {
			TESMiniQuestKillFaction quest = super.createQuest(npc, rand);
			quest.killFaction = killFaction;
			return quest;
		}

		@Override
		public Class<TESMiniQuestKillFaction> getQuestClass() {
			return TESMiniQuestKillFaction.class;
		}

		public QFKillFaction setKillFaction(TESFaction faction, int min, int max) {
			killFaction = faction;
			setKillTarget(min, max);
			return this;
		}
	}
}