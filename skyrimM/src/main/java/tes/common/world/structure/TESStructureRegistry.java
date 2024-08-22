package tes.common.world.structure;

import cpw.mods.fml.common.FMLLog;
import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.*;

public class TESStructureRegistry {
	public static final Collection<Class<? extends WorldGenerator>> CONTENT = new HashSet<Class<? extends WorldGenerator>>();

	public static final Map<Integer, StructureColorInfo> STRUCTURE_ITEM_SPAWNERS = new LinkedHashMap<Integer, StructureColorInfo>();
	public static final Map<Class<? extends WorldGenerator>, String> CLASS_TO_NAME_MAPPING = new HashMap<Class<? extends WorldGenerator>, String>();
	public static final Map<Class<?>, Set<String>> S_CLASS_TO_NAME_MAPPING = new HashMap<Class<?>, Set<String>>();

	private static final Map<Integer, IStructureProvider> ID_TO_CLASS_MAPPING = new HashMap<Integer, IStructureProvider>();
	private static final Map<Integer, String> ID_TO_STRING_MAPPING = new HashMap<Integer, String>();

	private TESStructureRegistry() {
	}

	public static String getNameFromID(int ID) {
		return ID_TO_STRING_MAPPING.get(ID);
	}

	public static int getRotationFromPlayer(EntityPlayer entityplayer) {
		return MathHelper.floor_double(entityplayer.rotationYaw * 4.0f / 360.0f + 0.5) & 3;
	}

	public static IStructureProvider getStructureForID(int ID) {
		return ID_TO_CLASS_MAPPING.get(ID);
	}

	public static void register(int id, Class<? extends WorldGenerator> strClass, TESFaction faction) {
		String name = strClass.getSimpleName();
		String cut = name.replace("TESStructure", "");
		registerStructure(id, strClass, cut, faction.getEggColor(), faction.getEggColor(), false);
		CLASS_TO_NAME_MAPPING.put(strClass, cut);
		CONTENT.add(strClass);
	}

	public static void register(int id, Class<? extends WorldGenerator> strClass, int color) {
		String name = strClass.getSimpleName();
		String cut = name.replace("TESStructure", "");
		registerStructure(id, strClass, cut, color, color, false);
		CLASS_TO_NAME_MAPPING.put(strClass, cut);
		CONTENT.add(strClass);
	}
 

	private static void registerStructure(int id, Class<? extends WorldGenerator> strClass, String name, int colorBG, int colorFG, boolean hide) {
		IStructureProvider strProvider = new IStructureProvider() {

			@Override
			public boolean generateStructure(World world, EntityPlayer entityplayer, int i, int j, int k) {
				WorldGenerator generator = null;
				try {
					generator = strClass.getConstructor(Boolean.TYPE).newInstance(true);
				} catch (Exception e) {
					FMLLog.warning("Failed to build TES structure " + strClass.getName());
					e.printStackTrace();
				}

				return false;
			}

			@Override
			public boolean isSettlement() {
				return false;
			}
		};
		registerStructure(id, strProvider, name, colorBG, colorFG, hide);
	}

	private static void registerStructure(int id, IStructureProvider str, String name, int colorBG, int colorFG, boolean hide) {
		ID_TO_CLASS_MAPPING.put(id, str);
		ID_TO_STRING_MAPPING.put(id, name);
		STRUCTURE_ITEM_SPAWNERS.put(id, new StructureColorInfo(id, colorBG, colorFG, str.isSettlement(), hide));
	}

	public interface ISettlementProperties<V> {
		void apply(V var1);
	}

	public interface IStructureProvider {
		boolean generateStructure(World var1, EntityPlayer var2, int var3, int var4, int var5);

		boolean isSettlement();
	}

	public static class StructureColorInfo {
		private final int spawnedID;
		private final int colorBackground;
		private final int colorForeground;
		private final boolean isSettlement;
		private final boolean isHidden;

		protected StructureColorInfo(int i, int colorBG, int colorFG, boolean vill, boolean hide) {
			spawnedID = i;
			colorBackground = colorBG;
			colorForeground = colorFG;
			isSettlement = vill;
			isHidden = hide;
		}

		public int getSpawnedID() {
			return spawnedID;
		}

		public int getColorBackground() {
			return colorBackground;
		}

		public int getColorForeground() {
			return colorForeground;
		}

		public boolean isSettlement() {
			return isSettlement;
		}

		public boolean isHidden() {
			return isHidden;
		}
	}
}