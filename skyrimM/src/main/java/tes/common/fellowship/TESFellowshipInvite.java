package tes.common.fellowship;

import java.util.UUID;

public class TESFellowshipInvite {
	private final UUID inviterID;

	private final UUID fellowshipID;

	public TESFellowshipInvite(UUID fs, UUID inviter) {
		fellowshipID = fs;
		inviterID = inviter;
	}

	public UUID getFellowshipID() {
		return fellowshipID;
	}

	public UUID getInviterID() {
		return inviterID;
	}
}