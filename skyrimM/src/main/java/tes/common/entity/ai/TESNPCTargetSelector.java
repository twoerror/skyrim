package tes.common.entity.ai;

import tes.TES;
import tes.common.TESLevelData;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.faction.TESFaction;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class TESNPCTargetSelector implements IEntitySelector {
	private final EntityLiving owner;
	private final TESFaction ownerFaction;

	public TESNPCTargetSelector(EntityLiving entity) {
		owner = entity;
		ownerFaction = TES.getNPCFaction(entity);
	}

	@Override
	public boolean isEntityApplicable(Entity target) {
		if ((ownerFaction == TESFaction.HOSTILE) && target instanceof TESEntityNPC && TES.getNPCFaction(target) == TESFaction.UNALIGNED) {
			return true;
		}
		if (ownerFaction == TESFaction.HOSTILE && (target.getClass().isAssignableFrom(owner.getClass()) || owner.getClass().isAssignableFrom(target.getClass()))) {
			return false;
		}
		if (target.isEntityAlive()) {
			if (target instanceof TESEntityNPC && ((TESEntityNPC) target).isNotAttackable()) {
				return false;
			}
			if (!ownerFaction.isApprovesWarCrimes() && target instanceof TESEntityNPC && ((TESEntityNPC) target).isCivilianNPC()) {
				return false;
			}
			TESFaction targetFaction = TES.getNPCFaction(target);
			if (ownerFaction.isBadRelation(targetFaction)) {
				return true;
			}
			if (ownerFaction.isNeutral(targetFaction)) {
				EntityPlayer hiringPlayer = null;
				if (owner instanceof TESEntityNPC) {
					TESEntityNPC npc = (TESEntityNPC) owner;
					if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiredTask() != TESHireableInfo.Task.FARMER) {
						hiringPlayer = npc.getHireableInfo().getHiringPlayer();
					}
				}
				return hiringPlayer != null && TESLevelData.getData(hiringPlayer).getAlignment(targetFaction) < 0.0f;
			}
		}
		return false;
	}
}