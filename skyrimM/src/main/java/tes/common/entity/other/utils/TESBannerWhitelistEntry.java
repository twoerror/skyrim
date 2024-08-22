package tes.common.entity.other.utils;

import com.mojang.authlib.GameProfile;
import tes.common.TESBannerProtection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class TESBannerWhitelistEntry {
	private final Set<TESBannerProtection.Permission> perms = EnumSet.noneOf(TESBannerProtection.Permission.class);
	private final GameProfile profile;

	public TESBannerWhitelistEntry(GameProfile p) {
		profile = p;
		if (profile == null) {
			throw new IllegalArgumentException("Banner whitelist entry cannot have a null profile!");
		}
	}

	public static List<TESBannerProtection.Permission> static_decodePermBitFlags(int i) {
		List<TESBannerProtection.Permission> decoded = new ArrayList<>();
		for (TESBannerProtection.Permission p : TESBannerProtection.Permission.values()) {
			if ((i & p.getBitFlag()) == 0) {
				continue;
			}
			decoded.add(p);
		}
		return decoded;
	}

	public static int static_encodePermBitFlags(Iterable<TESBannerProtection.Permission> permList) {
		int i = 0;
		for (TESBannerProtection.Permission p : permList) {
			i |= p.getBitFlag();
		}
		return i;
	}

	public void addPermission(TESBannerProtection.Permission p) {
		perms.add(p);
	}

	public boolean allowsPermission(TESBannerProtection.Permission p) {
		return isPermissionEnabled(TESBannerProtection.Permission.FULL) || isPermissionEnabled(p);
	}

	public void clearPermissions() {
		perms.clear();
	}

	public void decodePermBitFlags(int i) {
		setPermissions(static_decodePermBitFlags(i));
	}

	public int encodePermBitFlags() {
		return static_encodePermBitFlags(perms);
	}

	public boolean isPermissionEnabled(TESBannerProtection.Permission p) {
		return perms.contains(p);
	}

	public Set<TESBannerProtection.Permission> listPermissions() {
		return perms;
	}

	public void removePermission(TESBannerProtection.Permission p) {
		perms.remove(p);
	}

	public void setFullPerms() {
		clearPermissions();
		addPermission(TESBannerProtection.Permission.FULL);
	}

	public void setPermissions(Iterable<TESBannerProtection.Permission> perms) {
		clearPermissions();
		for (TESBannerProtection.Permission p : perms) {
			addPermission(p);
		}
	}

	public GameProfile getProfile() {
		return profile;
	}
}