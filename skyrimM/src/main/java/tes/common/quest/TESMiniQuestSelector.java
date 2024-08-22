package tes.common.quest;

import tes.common.faction.TESFaction;

import java.util.UUID;
import java.util.function.Supplier;

public class TESMiniQuestSelector {
	private boolean activeOnly;

	public boolean include(TESMiniQuest quest) {
		return !activeOnly || quest.isActive();
	}

	public TESMiniQuestSelector setActiveOnly() {
		activeOnly = true;
		return this;
	}

	public static class BountyActiveAnyFaction extends TESMiniQuestSelector {
		protected BountyActiveAnyFaction() {
			setActiveOnly();
		}

		@Override
		public boolean include(TESMiniQuest quest) {
			if (super.include(quest) && quest instanceof TESMiniQuestBounty) {
				TESMiniQuestBounty bQuest = (TESMiniQuestBounty) quest;
				return !bQuest.isKilled();
			}
			return false;
		}
	}

	public static class BountyActiveFaction extends BountyActiveAnyFaction {
		protected final Supplier<TESFaction> factionGet;

		public BountyActiveFaction(Supplier<TESFaction> sup) {
			factionGet = sup;
		}

		@Override
		public boolean include(TESMiniQuest quest) {
			return super.include(quest) && quest.getEntityFaction() == factionGet.get();
		}
	}

	public static class EntityId extends TESMiniQuestSelector {
		protected final UUID entityID;

		public EntityId(UUID id) {
			entityID = id;
		}

		@Override
		public boolean include(TESMiniQuest quest) {
			return super.include(quest) && quest.getEntityUUID().equals(entityID);
		}
	}

	public static class Faction extends TESMiniQuestSelector {
		protected final Supplier<TESFaction> factionGet;

		public Faction(Supplier<TESFaction> sup) {
			factionGet = sup;
		}

		@Override
		public boolean include(TESMiniQuest quest) {
			return super.include(quest) && quest.getEntityFaction() == factionGet.get();
		}
	}
}