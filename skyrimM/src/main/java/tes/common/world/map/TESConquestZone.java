package tes.common.world.map;

import tes.common.faction.TESFaction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TESConquestZone {
	private static List<TESFaction> allPlayableFacs;

	private final int gridX;
	private final int gridZ;

	private boolean isDummyZone;
	private boolean isLoaded = true;
	private boolean clientSide;
	private float[] conquestStrengths;
	private long lastChangeTime;
	private long isEmptyKey;

	public TESConquestZone(int i, int k) {
		gridX = i;
		gridZ = k;
		if (allPlayableFacs == null && (allPlayableFacs = TESFaction.getPlayableAlignmentFactions()).size() >= 62) {
			throw new RuntimeException("Too many factions! Need to upgrade TESConquestZone data format.");
		}
		conquestStrengths = new float[allPlayableFacs.size()];
	}

	public static TESConquestZone readFromNBT(NBTTagCompound nbt) {
		short x = nbt.getShort("X");
		short z = nbt.getShort("Z");
		long time = nbt.getLong("Time");
		TESConquestZone zone = new TESConquestZone(x, z);
		zone.isLoaded = false;
		zone.lastChangeTime = time;
		block0:
		for (TESFaction fac : allPlayableFacs) {
			Collection<String> names = new ArrayList<>();
			names.add(fac.codeName());
			for (String alias : names) {
				String facKey = alias + "_str";
				if (!nbt.hasKey(facKey)) {
					continue;
				}
				float str = nbt.getFloat(facKey);
				zone.setConquestStrengthRaw(fac, str);
				continue block0;
			}
		}
		zone.isLoaded = true;
		return zone;
	}

	public void addConquestStrength(TESFaction fac, float add, World world) {
		float str = getConquestStrength(fac, world);
		setConquestStrength(fac, str + add, world);
	}

	private float calcTimeStrReduction(long worldTime) {
		int dl = (int) (worldTime - lastChangeTime);
		float s = dl / 20.0f;
		float graceCap = 3600.0f;
		if (s > graceCap) {
			float decayRate = 3600.0f;
			return (s - graceCap) / decayRate;
		}
		return 0.0f;
	}

	public void checkForEmptiness(World world) {
		boolean emptyCheck = true;
		for (TESFaction fac : allPlayableFacs) {
			float str = getConquestStrength(fac, world);
			if (str == 0.0f) {
				continue;
			}
			emptyCheck = false;
			break;
		}
		if (emptyCheck) {
			conquestStrengths = new float[allPlayableFacs.size()];
			isEmptyKey = 0L;
			markDirty();
		}
	}

	public void clearAllFactions(World world) {
		for (TESFaction fac : allPlayableFacs) {
			setConquestStrengthRaw(fac, 0.0f);
		}
		lastChangeTime = world.getTotalWorldTime();
		markDirty();
	}

	public float getConquestStrength(TESFaction fac, long worldTime) {
		float str = getConquestStrengthRaw(fac);
		str -= calcTimeStrReduction(worldTime);
		return Math.max(str, 0.0f);
	}

	public float getConquestStrength(TESFaction fac, World world) {
		return getConquestStrength(fac, world.getTotalWorldTime());
	}

	public float getConquestStrengthRaw(TESFaction fac) {
		if (!fac.isPlayableAlignmentFaction()) {
			return 0.0f;
		}
		int index = allPlayableFacs.indexOf(fac);
		return conquestStrengths[index];
	}

	public int getGridX() {
		return gridX;
	}

	public int getGridZ() {
		return gridZ;
	}

	public long getLastChangeTime() {
		return lastChangeTime;
	}

	public void setLastChangeTime(long l) {
		lastChangeTime = l;
		markDirty();
	}

	public boolean isDummyZone() {
		return isDummyZone;
	}

	public boolean isEmpty() {
		return isEmptyKey == 0L;
	}

	private void markDirty() {
		if (isLoaded && !clientSide) {
			TESConquestGrid.markZoneDirty(this);
		}
	}

	public void setClientSide() {
		clientSide = true;
	}

	public void setConquestStrength(TESFaction fac, float str, World world) {
		setConquestStrengthRaw(fac, str);
		updateAllOtherFactions(fac, world);
		lastChangeTime = world.getTotalWorldTime();
		markDirty();
	}

	public void setConquestStrengthRaw(TESFaction fac, float str) {
		float str1 = str;
		if (!fac.isPlayableAlignmentFaction()) {
			return;
		}
		if (str1 < 0.0f) {
			str1 = 0.0f;
		}
		int index = allPlayableFacs.indexOf(fac);
		conquestStrengths[index] = str1;
		if (str1 == 0.0f) {
			isEmptyKey &= ~(1L << index);
		} else {
			isEmptyKey |= 1L << index;
		}
		markDirty();
	}

	public TESConquestZone setDummyZone() {
		isDummyZone = true;
		return this;
	}

	@Override
	public String toString() {
		return "TESConquestZone: " + gridX + ", " + gridZ;
	}

	private void updateAllOtherFactions(TESFaction fac, World world) {
		for (int i = 0; i < conquestStrengths.length; ++i) {
			TESFaction otherFac = allPlayableFacs.get(i);
			if (otherFac == fac || conquestStrengths[i] <= 0.0f) {
				continue;
			}
			float otherStr = getConquestStrength(otherFac, world);
			setConquestStrengthRaw(otherFac, otherStr);
		}
	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setShort("X", (short) gridX);
		nbt.setShort("Z", (short) gridZ);
		nbt.setLong("Time", lastChangeTime);
		for (int i = 0; i < conquestStrengths.length; ++i) {
			TESFaction fac = allPlayableFacs.get(i);
			String facKey = fac.codeName() + "_str";
			float str = conquestStrengths[i];
			if (str == 0.0f) {
				continue;
			}
			nbt.setFloat(facKey, str);
		}
	}
}