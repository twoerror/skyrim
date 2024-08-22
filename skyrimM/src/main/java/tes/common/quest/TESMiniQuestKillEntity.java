package tes.common.quest;

import tes.common.TESPlayerData;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.Random;

public class TESMiniQuestKillEntity extends TESMiniQuestKill {
	private Class<? extends Entity> entityType;

	@SuppressWarnings("unused")
	public TESMiniQuestKillEntity(TESPlayerData pd) {
		super(pd);
	}

	@Override
	public void handleEvent(TESMiniQuestEvent event) {
	}

	@Override
	public String getKillTargetName() {
		String entityName = TESEntityRegistry.getStringFromClass(entityType);
		return StatCollector.translateToLocal("entity." + entityName + ".name");
	}

	@Override
	public boolean isValidQuest() {
		return super.isValidQuest() && entityType != null && EntityLivingBase.class.isAssignableFrom(entityType);
	}

	@Override
	public void onKill(EntityPlayer entityplayer, EntityLivingBase entity) {
		if (killCount < killTarget && entityType.isAssignableFrom(entity.getClass())) {
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
		entityType = TESEntityRegistry.getClassFromString(nbt.getString("KillClass"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("KillClass", TESEntityRegistry.getStringFromClass(entityType));
	}

	public static class QFKillEntity extends TESMiniQuestKill.QFKill<TESMiniQuestKillEntity> {
		protected Class<? extends Entity> entityType;

		public QFKillEntity(String name) {
			super(name);
		}

		@Override
		public TESMiniQuestKillEntity createQuest(TESEntityNPC npc, Random rand) {
			TESMiniQuestKillEntity quest = super.createQuest(npc, rand);
			quest.entityType = entityType;
			return quest;
		}

		@Override
		public Class<TESMiniQuestKillEntity> getQuestClass() {
			return TESMiniQuestKillEntity.class;
		}

		public QFKillEntity setKillEntity(Class<? extends Entity> entityClass, int min, int max) {
			entityType = entityClass;
			setKillTarget(min, max);
			return this;
		}
	}
}