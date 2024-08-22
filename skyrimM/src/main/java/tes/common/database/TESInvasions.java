package tes.common.database;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.TESEntityHummel009;
import tes.common.faction.TESFaction;
import tes.common.item.other.TESItemConquestHorn;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Collection;

public enum TESInvasions {
	EMPIRE(TESFaction.EMPIRE);

	private final Collection<InvasionSpawnEntry> invasionMobs = new ArrayList<>();
	private final TESFaction invasionFaction;
	private final String subfaction;

	private Item invasionIcon;

	TESInvasions(TESFaction f) {
		this(f, null);
	}

	TESInvasions(TESFaction f, String s) {
		invasionFaction = f;
		subfaction = s;
	}

	public static TESInvasions forID(int ID) {
		for (TESInvasions i : values()) {
			if (i.ordinal() != ID) {
				continue;
			}
			return i;
		}
		return null;
	}

	public static TESInvasions forName(String name) {
		for (TESInvasions i : values()) {
			if (!i.codeName().equals(name)) {
				continue;
			}
			return i;
		}
		return null;
	}

	public static String[] listInvasionNames() {
		String[] names = new String[values().length];
		for (int i = 0; i < names.length; ++i) {
			names[i] = values()[i].codeName();
		}
		return names;
	}

	public static void preInit() {
		EMPIRE.invasionIcon = Items.golden_sword;
		EMPIRE.invasionMobs.add(new InvasionSpawnEntry(TESEntityHummel009.class, 10));
		/*/
		HILL_TRIBES.invasionMobs.add(new InvasionSpawnEntry(TESEntityHillmanWarrior.class, 10));
		HILL_TRIBES.invasionMobs.add(new InvasionSpawnEntry(TESEntityHillmanArcher.class, 5));
		HILL_TRIBES.invasionMobs.add(new InvasionSpawnEntry(TESEntityHillmanBerserker.class, 2));
		HILL_TRIBES.invasionMobs.add(new InvasionSpawnEntry(TESEntityHillmanBannerBearer.class, 2));
		DOTHRAKI.invasionMobs.add(new InvasionSpawnEntry(TESEntityDothraki.class, 10));
		DOTHRAKI.invasionMobs.add(new InvasionSpawnEntry(TESEntityDothrakiArcher.class, 5));
		IBBEN.invasionMobs.add(new InvasionSpawnEntry(TESEntityIbbenWarrior.class, 10));
		IBBEN.invasionMobs.add(new InvasionSpawnEntry(TESEntityIbbenArcher.class, 5));
		IBBEN.invasionMobs.add(new InvasionSpawnEntry(TESEntityIbbenBannerBearer.class, 2));
		JOGOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityJogos.class, 10));
		JOGOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityJogosArcher.class, 5));
		GIANT.invasionMobs.add(new InvasionSpawnEntry(TESEntityWildling.class, 10));
		GIANT.invasionMobs.add(new InvasionSpawnEntry(TESEntityWildlingArcher.class, 5));
		GIANT.invasionMobs.add(new InvasionSpawnEntry(TESEntityWildlingBannerBearer.class, 2));
		GIANT.invasionMobs.add(new InvasionSpawnEntry(TESEntityGiant.class, 1));
		WILDLING.invasionMobs.add(new InvasionSpawnEntry(TESEntityWildling.class, 10));
		WILDLING.invasionMobs.add(new InvasionSpawnEntry(TESEntityWildlingArcher.class, 5));
		WILDLING.invasionMobs.add(new InvasionSpawnEntry(TESEntityWildlingBannerBearer.class, 2));
		THENN.invasionMobs.add(new InvasionSpawnEntry(TESEntityThenn.class, 10));
		THENN.invasionMobs.add(new InvasionSpawnEntry(TESEntityThennArcher.class, 5));
		THENN.invasionMobs.add(new InvasionSpawnEntry(TESEntityThennBerserker.class, 3));
		THENN.invasionMobs.add(new InvasionSpawnEntry(TESEntityThennBannerBearer.class, 2));

		YI_TI.invasionMobs.add(new InvasionSpawnEntry(TESEntityYiTiSoldier.class, 10));
		YI_TI.invasionMobs.add(new InvasionSpawnEntry(TESEntityYiTiSoldierCrossbower.class, 5));
		YI_TI.invasionMobs.add(new InvasionSpawnEntry(TESEntityYiTiSamurai.class, 3));
		YI_TI.invasionMobs.add(new InvasionSpawnEntry(TESEntityYiTiFireThrower.class, 2));
		YI_TI.invasionMobs.add(new InvasionSpawnEntry(TESEntityYiTiBombardier.class, 1));
		YI_TI.invasionMobs.add(new InvasionSpawnEntry(TESEntityYiTiBannerBearer.class, 2));

		VOLANTIS.invasionMobs.add(new InvasionSpawnEntry(TESEntityVolantisSoldier.class, 10));
		VOLANTIS.invasionMobs.add(new InvasionSpawnEntry(TESEntityVolantisSoldierArcher.class, 5));
		VOLANTIS.invasionMobs.add(new InvasionSpawnEntry(TESEntityVolantisBannerBearer.class, 2));

		GHISCAR.invasionMobs.add(new InvasionSpawnEntry(TESEntityGhiscarCorsair.class, 10));
		GHISCAR.invasionMobs.add(new InvasionSpawnEntry(TESEntityGhiscarLevyman.class, 5));
		GHISCAR.invasionMobs.add(new InvasionSpawnEntry(TESEntityGhiscarLevymanArcher.class, 3));

		BRAAVOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityBraavosSoldier.class, 10));
		BRAAVOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityBraavosSoldierArcher.class, 5));
		BRAAVOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityBraavosBannerBearer.class, 2));

		NORVOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityNorvosLevyman.class, 10));
		NORVOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityNorvosLevymanArcher.class, 5));
		NORVOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityNorvosBannerBearer.class, 2));

		TYROSH.invasionMobs.add(new InvasionSpawnEntry(TESEntityTyroshSoldier.class, 10));
		TYROSH.invasionMobs.add(new InvasionSpawnEntry(TESEntityTyroshSoldierArcher.class, 5));
		TYROSH.invasionMobs.add(new InvasionSpawnEntry(TESEntityTyroshBannerBearer.class, 2));

		MYR.invasionMobs.add(new InvasionSpawnEntry(TESEntityMyrSoldier.class, 10));
		MYR.invasionMobs.add(new InvasionSpawnEntry(TESEntityMyrSoldier.class, 5));
		MYR.invasionMobs.add(new InvasionSpawnEntry(TESEntityMyrBannerBearer.class, 2));

		LYS.invasionMobs.add(new InvasionSpawnEntry(TESEntityLysSoldier.class, 10));
		LYS.invasionMobs.add(new InvasionSpawnEntry(TESEntityLysSoldierArcher.class, 5));
		LYS.invasionMobs.add(new InvasionSpawnEntry(TESEntityLysBannerBearer.class, 2));

		PENTOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityPentosLevyman.class, 10));
		PENTOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityPentosLevymanArcher.class, 5));
		PENTOS.invasionMobs.add(new InvasionSpawnEntry(TESEntityPentosBannerBearer.class, 2));

		IRONBORN.invasionMobs.add(new InvasionSpawnEntry(TESEntityIronbornSoldier.class, 10));
		IRONBORN.invasionMobs.add(new InvasionSpawnEntry(TESEntityIronbornSoldierArcher.class, 5));
		IRONBORN.invasionMobs.add(new InvasionSpawnEntry(TESEntityIronbornBannerBearer.class, 2));

		WESTERLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityWesterlandsSoldier.class, 10));
		WESTERLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityWesterlandsSoldierArcher.class, 5));
		WESTERLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityWesterlandsBannerBearer.class, 2));

		RIVERLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityRiverlandsSoldier.class, 10));
		RIVERLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityRiverlandsSoldierArcher.class, 5));
		RIVERLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityRiverlandsBannerBearer.class, 2));

		ARRYN.invasionMobs.add(new InvasionSpawnEntry(TESEntityArrynSoldier.class, 10));
		ARRYN.invasionMobs.add(new InvasionSpawnEntry(TESEntityArrynSoldierArcher.class, 5));
		ARRYN.invasionMobs.add(new InvasionSpawnEntry(TESEntityArrynBannerBearer.class, 2));

		DRAGONSTONE.invasionMobs.add(new InvasionSpawnEntry(TESEntityDragonstoneSoldier.class, 10));
		DRAGONSTONE.invasionMobs.add(new InvasionSpawnEntry(TESEntityDragonstoneSoldierArcher.class, 5));
		DRAGONSTONE.invasionMobs.add(new InvasionSpawnEntry(TESEntityDragonstoneBannerBearer.class, 2));

		STORMLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityStormlandsSoldier.class, 10));
		STORMLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityStormlandsSoldierArcher.class, 5));
		STORMLANDS.invasionMobs.add(new InvasionSpawnEntry(TESEntityStormlandsBannerBearer.class, 2));

		REACH.invasionMobs.add(new InvasionSpawnEntry(TESEntityReachSoldier.class, 10));
		REACH.invasionMobs.add(new InvasionSpawnEntry(TESEntityReachSoldierArcher.class, 5));
		REACH.invasionMobs.add(new InvasionSpawnEntry(TESEntityReachBannerBearer.class, 2));

		DORNE.invasionMobs.add(new InvasionSpawnEntry(TESEntityDorneSoldier.class, 10));
		DORNE.invasionMobs.add(new InvasionSpawnEntry(TESEntityDorneSoldierArcher.class, 5));
		DORNE.invasionMobs.add(new InvasionSpawnEntry(TESEntityDorneBannerBearer.class, 2));

		NORTH.invasionMobs.add(new InvasionSpawnEntry(TESEntityNorthSoldier.class, 10));
		NORTH.invasionMobs.add(new InvasionSpawnEntry(TESEntityNorthSoldierArcher.class, 5));
		NORTH.invasionMobs.add(new InvasionSpawnEntry(TESEntityNorthBannerBearer.class, 2));
		/*/
	}

	public String codeName() {
		StringBuilder s = new StringBuilder(invasionFaction.codeName());
		if (subfaction != null) {
			s.append('_').append(subfaction);
		}
		return s.toString();
	}

	public String codeNameHorn() {
		return "tes.invasion." + codeName() + ".horn";
	}

	public ItemStack createConquestHorn() {
		ItemStack horn = new ItemStack(TESItems.conquestHorn);
		TESItemConquestHorn.setInvasionType(horn, this);
		return horn;
	}

	public ItemStack getInvasionIcon() {
		Item sword = invasionIcon;
		if (sword == null) {
			sword = Items.iron_sword;
		}
		return new ItemStack(sword);
	}

	public String invasionName() {
		if (subfaction == null) {
			return invasionFaction.factionName();
		}
		return StatCollector.translateToLocal("tes.invasion." + codeName());
	}

	public TESFaction getInvasionFaction() {
		return invasionFaction;
	}

	public Collection<InvasionSpawnEntry> getInvasionMobs() {
		return invasionMobs;
	}

	public static class InvasionSpawnEntry extends WeightedRandom.Item {
		private final Class<? extends TESEntityNPC> entityClass;

		@SuppressWarnings("WeakerAccess")
		public InvasionSpawnEntry(Class<? extends TESEntityNPC> c, int chance) {
			super(chance);
			entityClass = c;
		}

		public Class<? extends TESEntityNPC> getEntityClass() {
			return entityClass;
		}
	}
}