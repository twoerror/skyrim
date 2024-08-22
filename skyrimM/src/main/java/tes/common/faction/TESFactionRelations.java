package tes.common.faction;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.network.TESPacketFactionRelations;
import tes.common.network.TESPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TESFactionRelations {
	public static final Map<FactionPair, Relation> DEFAULT_MAP = new HashMap<>();

	private static final Map<FactionPair, Relation> OVERRIDE_MAP = new HashMap<>();

	private static boolean needsLoad = true;
	private static boolean needsSave;

	private TESFactionRelations() {
	}

	private static Relation getFromDefaultMap(FactionPair key) {
		if (DEFAULT_MAP.containsKey(key)) {
			return DEFAULT_MAP.get(key);
		}
		return Relation.NEUTRAL;
	}

	public static Relation getRelations(TESFaction f1, TESFaction f2) {
		if (f1 == TESFaction.UNALIGNED || f2 == TESFaction.UNALIGNED) {
			return Relation.NEUTRAL;
		}
		if (f1 == TESFaction.HOSTILE || f2 == TESFaction.HOSTILE) {
			return Relation.MORTAL_ENEMY;
		}
		if (f1 == f2) {
			return Relation.ALLY;
		}
		FactionPair key = new FactionPair(f1, f2);
		if (OVERRIDE_MAP.containsKey(key)) {
			return OVERRIDE_MAP.get(key);
		}
		return getFromDefaultMap(key);
	}

	private static File getRelationsFile() {
		return new File(TESLevelData.getOrCreateTESDir(), "faction_relations.dat");
	}

	public static void load() {
		try {
			NBTTagCompound facData = TESLevelData.loadNBTFromFile(getRelationsFile());
			OVERRIDE_MAP.clear();
			NBTTagList relationTags = facData.getTagList("Overrides", 10);
			for (int i = 0; i < relationTags.tagCount(); ++i) {
				NBTTagCompound nbt = relationTags.getCompoundTagAt(i);
				FactionPair pair = FactionPair.readFromNBT(nbt);
				Relation rel = Relation.forName(nbt.getString("Rel"));
				if (pair == null || rel == null) {
					continue;
				}
				OVERRIDE_MAP.put(pair, rel);
			}
			needsLoad = false;
			save();
		} catch (Exception e) {
			FMLLog.severe("Error loading TES faction relations");
			e.printStackTrace();
		}
	}

	private static void markDirty() {
		needsSave = true;
	}

	public static boolean needsSave() {
		return needsSave;
	}

	public static void overrideRelations(TESFaction f1, TESFaction f2, Relation relation) {
		setRelations(f1, f2, relation, false);
	}

	public static void resetAllRelations() {
		boolean wasEmpty = OVERRIDE_MAP.isEmpty();
		OVERRIDE_MAP.clear();
		if (!wasEmpty) {
			markDirty();
			TESPacketFactionRelations pkt = TESPacketFactionRelations.reset();
			sendPacketToAll(pkt);
		}
	}

	public static void save() {
		try {
			File datFile = getRelationsFile();
			if (!datFile.exists()) {
				TESLevelData.saveNBTToFile(datFile, new NBTTagCompound());
			}
			NBTTagCompound facData = new NBTTagCompound();
			NBTTagList relationTags = new NBTTagList();
			for (Map.Entry<FactionPair, Relation> e : OVERRIDE_MAP.entrySet()) {
				FactionPair pair = e.getKey();
				Relation rel = e.getValue();
				NBTTagCompound nbt = new NBTTagCompound();
				pair.writeToNBT(nbt);
				nbt.setString("Rel", rel.codeName());
				relationTags.appendTag(nbt);
			}
			facData.setTag("Overrides", relationTags);
			TESLevelData.saveNBTToFile(datFile, facData);
			needsSave = false;
		} catch (Exception e) {
			FMLLog.severe("Error saving TES faction relations");
			e.printStackTrace();
		}
	}

	public static void sendAllRelationsTo(EntityPlayerMP entityplayer) {
		TESPacketFactionRelations pkt = TESPacketFactionRelations.fullMap(OVERRIDE_MAP);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(pkt, entityplayer);
	}

	private static void sendPacketToAll(IMessage packet) {
		MinecraftServer srv = MinecraftServer.getServer();
		if (srv != null) {
			for (EntityPlayerMP entityplayer : (List<EntityPlayerMP>) srv.getConfigurationManager().playerEntityList) {
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
			}
		}
	}

	public static void setRelations(TESFaction f1, TESFaction f2, Relation relation) {
		setRelations(f1, f2, relation, true);
	}

	private static void setRelations(TESFaction f1, TESFaction f2, Relation relation, boolean isDefault) {
		if (f1 == TESFaction.UNALIGNED || f2 == TESFaction.UNALIGNED) {
			throw new IllegalArgumentException("Cannot alter UNALIGNED!");
		}
		if (f1 == TESFaction.HOSTILE || f2 == TESFaction.HOSTILE) {
			throw new IllegalArgumentException("Cannot alter HOSTILE!");
		}
		if (f1 == f2) {
			throw new IllegalArgumentException("Cannot alter a faction's relations with itself!");
		}
		FactionPair key = new FactionPair(f1, f2);
		if (isDefault) {
			if (relation == Relation.NEUTRAL) {
				DEFAULT_MAP.remove(key);
			} else {
				DEFAULT_MAP.put(key, relation);
			}
		} else {
			Relation defaultRelation = getFromDefaultMap(key);
			if (relation == defaultRelation) {
				OVERRIDE_MAP.remove(key);
			} else {
				OVERRIDE_MAP.put(key, relation);
			}
			markDirty();
			TESPacketFactionRelations pkt = TESPacketFactionRelations.oneEntry(key, relation);
			sendPacketToAll(pkt);
		}
	}

	public static boolean isNeedsLoad() {
		return needsLoad;
	}

	public static void setNeedsLoad(boolean needsLoad) {
		TESFactionRelations.needsLoad = needsLoad;
	}

	public enum Relation {
		ALLY, FRIEND, NEUTRAL, ENEMY, MORTAL_ENEMY;

		public static Relation forID(int id) {
			for (Relation rel : values()) {
				if (rel.ordinal() != id) {
					continue;
				}
				return rel;
			}
			return null;
		}

		public static Relation forName(String name) {
			for (Relation rel : values()) {
				if (!rel.codeName().equals(name)) {
					continue;
				}
				return rel;
			}
			return null;
		}

		public static List<String> listRelationNames() {
			List<String> names = new ArrayList<>();
			for (Relation rel : values()) {
				names.add(rel.codeName());
			}
			return names;
		}

		private String codeName() {
			return name();
		}

		public String getDisplayName() {
			return StatCollector.translateToLocal("tes.faction.rel." + codeName());
		}
	}

	public static class FactionPair {
		private final TESFaction fac1;
		private final TESFaction fac2;

		public FactionPair(TESFaction f1, TESFaction f2) {
			fac1 = f1;
			fac2 = f2;
		}

		private static FactionPair readFromNBT(NBTTagCompound nbt) {
			TESFaction f1 = TESFaction.forName(nbt.getString("FacPair1"));
			TESFaction f2 = TESFaction.forName(nbt.getString("FacPair2"));
			if (f1 != null && f2 != null) {
				return new FactionPair(f1, f2);
			}
			return null;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof FactionPair) {
				FactionPair pair = (FactionPair) obj;
				return fac1 == pair.fac1 && fac2 == pair.fac2 || fac1 == pair.fac2 && fac2 == pair.fac1;
			}
			return false;
		}

		public TESFaction getLeft() {
			return fac1;
		}

		public TESFaction getRight() {
			return fac2;
		}

		@Override
		public int hashCode() {
			int f1 = fac1.ordinal();
			int f2 = fac2.ordinal();
			int lower = Math.min(f1, f2);
			int upper = Math.max(f1, f2);
			return upper << 16 | lower;
		}

		private void writeToNBT(NBTTagCompound nbt) {
			nbt.setString("FacPair1", fac1.codeName());
			nbt.setString("FacPair2", fac2.codeName());
		}
	}
}