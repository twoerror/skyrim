package tes.common.world.map;


import tes.common.entity.other.TESEntityHummel009;
import tes.common.entity.other.TESEntityNPC;
import tes.common.world.biome.TESBiome;
import tes.common.world.feature.TESTreeType;
import tes.common.world.feature.TESWorldGenPartyTrees;
import net.minecraft.world.World;

import java.util.*;

import static tes.common.world.map.TESCoordConverter.toEssosTownGate;

public class TESFixer {
	private TESFixer() {
	}

	public static void addSpecialLocations(World world, Random random, int i, int k) {
	}

	public static void onInit() {
		List<TESAbstractWaypoint> wps = new ArrayList<>();
		
	}

	public enum Dir {
		NORTH, EAST, SOUTH, WEST
	}

	public static class SpawnInfo {
		private final TESEntityNPC npc;
		private final int i;
		private final int k;

		@SuppressWarnings("WeakerAccess")
		public SpawnInfo(TESEntityNPC npc, int i, int k) {
			this.npc = npc;
			this.i = i;
			this.k = k;
		}

		public TESEntityNPC getNPC() {
			return npc;
		}

		public int getI() {
			return i;
		}

		public int getK() {
			return k;
		}
	}
}