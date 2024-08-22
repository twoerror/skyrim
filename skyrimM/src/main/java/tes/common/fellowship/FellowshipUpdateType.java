package tes.common.fellowship;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESPlayerData;
import tes.common.database.TESTitle;
import tes.common.network.TESPacketFellowship;
import tes.common.network.TESPacketFellowshipPartialUpdate;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public interface FellowshipUpdateType {
	IMessage createUpdatePacket(TESPlayerData var1, TESFellowship var2);

	List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship var1);

	class AddMember implements FellowshipUpdateType {
		protected final UUID memberID;

		public AddMember(UUID member) {
			memberID = member;
		}

		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.AddMember(fs, memberID);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return ImmutableList.of(memberID);
		}
	}

	class ChangeIcon implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.ChangeIcon(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class Full implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowship(pd, fs, false);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return fs.getAllPlayerUUIDs();
		}
	}

	class RemoveAdmin implements FellowshipUpdateType {
		protected final UUID adminID;

		public RemoveAdmin(UUID admin) {
			adminID = admin;
		}

		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.RemoveAdmin(fs, adminID, fs.isAdmin(pd.getPlayerUUID()));
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class RemoveMember implements FellowshipUpdateType {
		protected final UUID memberID;

		public RemoveMember(UUID member) {
			memberID = member;
		}

		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.RemoveMember(fs, memberID);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return ImmutableList.of(memberID);
		}
	}

	class Rename implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.Rename(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class SetAdmin implements FellowshipUpdateType {
		protected final UUID adminID;

		public SetAdmin(UUID admin) {
			adminID = admin;
		}

		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.SetAdmin(fs, adminID, fs.isAdmin(pd.getPlayerUUID()));
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class SetOwner implements FellowshipUpdateType {
		protected final UUID ownerID;

		public SetOwner(UUID owner) {
			ownerID = owner;
		}

		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.SetOwner(fs, ownerID, fs.isOwner(pd.getPlayerUUID()));
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return ImmutableList.of(ownerID);
		}
	}

	class ToggleHiredFriendlyFire implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.ToggleHiredFriendlyFire(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class TogglePvp implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.TogglePvp(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class ToggleShowMapLocations implements FellowshipUpdateType {
		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.ToggleShowMap(fs);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}

	class UpdatePlayerTitle implements FellowshipUpdateType {
		protected final UUID playerID;
		protected final TESTitle.PlayerTitle playerTitle;

		public UpdatePlayerTitle(UUID player, TESTitle.PlayerTitle title) {
			playerID = player;
			playerTitle = title;
		}

		@Override
		public IMessage createUpdatePacket(TESPlayerData pd, TESFellowship fs) {
			return new TESPacketFellowshipPartialUpdate.UpdatePlayerTitle(fs, playerID, playerTitle);
		}

		@Override
		public List<UUID> getPlayersToCheckSharedWaypointsFrom(TESFellowship fs) {
			return Collections.emptyList();
		}
	}
}