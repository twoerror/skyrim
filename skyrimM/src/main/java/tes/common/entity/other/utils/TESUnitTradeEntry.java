package tes.common.entity.other.utils;

import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.animal.TESEntityHorse;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESBannerBearer;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.entity.other.info.TESHireableInfo;
import tes.common.faction.TESFaction;
import tes.common.item.other.TESItemCoin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class TESUnitTradeEntry {
	private final Class<? extends Entity> entityClass;
	private final int initialCost;

	private Class<? extends Entity> mountClass;

	private TESHireableInfo.Task task = TESHireableInfo.Task.WARRIOR;
	private PledgeType pledgeType = PledgeType.NONE;

	private Item mountArmor;

	private String name;
	private String extraInfo;

	private float alignmentRequired;
	private float mountArmorChance;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESUnitTradeEntry(Class<? extends Entity> c, Class<? extends Entity> c1, String s, int cost, float alignment) {
		this(c, cost, alignment);
		mountClass = c1;
		name = s;
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESUnitTradeEntry(Class<? extends Entity> c, int cost, float alignment) {
		entityClass = c;
		initialCost = cost;
		alignmentRequired = alignment;
		if (TESBannerBearer.class.isAssignableFrom(entityClass)) {
			extraInfo = "Banner";
		}
	}

	public EntityLiving createHiredMount(World world) {
		if (mountClass == null) {
			return null;
		}
		EntityLiving entity = (EntityLiving) EntityList.createEntityByName(TESEntityRegistry.getStringFromClass(mountClass), world);
		if (entity instanceof TESEntityNPC) {
			((TESEntityNPC) entity).initCreatureForHire(null);
			((TESEntityNPC) entity).refreshCurrentAttackMode();
		} else {
			entity.onSpawnWithEgg(null);
		}
		if (mountArmor != null && world.rand.nextFloat() < mountArmorChance && entity instanceof TESEntityHorse) {
			((TESEntityHorse) entity).setMountArmor(new ItemStack(mountArmor));
		}
		return entity;
	}

	public int getCost(EntityPlayer entityplayer, TESHireableBase trader) {
		float f;
		float cost = initialCost;
		TESFaction fac = trader.getFaction();
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		float alignment = pd.getAlignment(fac);
		boolean pledged = pd.isPledgedTo(fac);
		float alignSurplus = Math.max(alignment - alignmentRequired, 0.0f);
		if (pledged) {
			f = alignSurplus / 1500.0f;
		} else {
			cost *= 2.0f;
			f = alignSurplus / 2000.0f;
		}
		f = MathHelper.clamp_float(f, 0.0f, 1.0f);
		cost *= 1.0f - f * 0.5f;
		int costI = Math.round(cost);
		return Math.max(costI, 1);
	}

	public String getFormattedExtraInfo() {
		return StatCollector.translateToLocal("tes.unitinfo." + extraInfo);
	}

	public TESEntityNPC getOrCreateHiredNPC(World world) {
		TESEntityNPC entity = (TESEntityNPC) EntityList.createEntityByName(TESEntityRegistry.getStringFromClass(entityClass), world);
		entity.initCreatureForHire(null);
		entity.refreshCurrentAttackMode();
		return entity;
	}

	public PledgeType getPledgeType() {
		return pledgeType;
	}

	private TESUnitTradeEntry setPledgeType(PledgeType t) {
		pledgeType = t;
		return this;
	}

	public String getUnitTradeName() {
		if (mountClass == null) {
			String entityName = TESEntityRegistry.getStringFromClass(entityClass);
			return StatCollector.translateToLocal("entity." + entityName + ".name");
		}
		return StatCollector.translateToLocal("tes.unit." + name);
	}

	public boolean hasExtraInfo() {
		return extraInfo != null;
	}

	public boolean hasRequiredCostAndAlignment(EntityPlayer entityplayer, TESHireableBase trader) {
		int coins = TESItemCoin.getInventoryValue(entityplayer, false);
		if (coins < getCost(entityplayer, trader)) {
			return false;
		}
		TESFaction fac = trader.getFaction();
		if (!pledgeType.canAcceptPlayer(entityplayer, fac)) {
			return false;
		}
		float alignment = TESLevelData.getData(entityplayer).getAlignment(fac);
		return alignment >= alignmentRequired;
	}

	public void hireUnit(EntityPlayer entityplayer, TESHireableBase trader, String squadron) {
		if (hasRequiredCostAndAlignment(entityplayer, trader)) {
			trader.onUnitTrade(entityplayer);
			int cost = getCost(entityplayer, trader);
			TESItemCoin.takeCoins(cost, entityplayer);
			((TESEntityNPC) trader).playTradeSound();
			World world = entityplayer.worldObj;
			TESEntityNPC hiredNPC = getOrCreateHiredNPC(world);
			if (hiredNPC != null) {
				boolean unitExists;
				EntityLiving mount = null;
				if (mountClass != null) {
					mount = createHiredMount(world);
				}
				hiredNPC.getHireableInfo().hireUnit(entityplayer, !(unitExists = world.loadedEntityList.contains(hiredNPC)), trader.getFaction(), this, squadron, mount);
				if (!unitExists) {
					world.spawnEntityInWorld(hiredNPC);
					if (mount != null) {
						world.spawnEntityInWorld(mount);
					}
				}
			}
		}
	}

	private TESUnitTradeEntry setMountArmor(Item item, float chance) {
		mountArmor = item;
		mountArmorChance = chance;
		return this;
	}

	public TESUnitTradeEntry setPledgeExclusive() {
		return setPledgeType(PledgeType.FACTION);
	}

	public Class<? extends Entity> getEntityClass() {
		return entityClass;
	}

	public Class<? extends Entity> getMountClass() {
		return mountClass;
	}

	public int getInitialCost() {
		return initialCost;
	}

	public float getAlignmentRequired() {
		return alignmentRequired;
	}

	public void setAlignmentRequired(float alignmentRequired) {
		this.alignmentRequired = alignmentRequired;
	}

	public TESHireableInfo.Task getTask() {
		return task;
	}

	public TESUnitTradeEntry setTask(TESHireableInfo.Task task) {
		this.task = task;
		return this;
	}

	public TESUnitTradeEntry setMountArmor(Item item) {
		return setMountArmor(item, 1.0f);
	}

	public enum PledgeType {
		NONE(0), FACTION(1);

		private final int typeID;

		PledgeType(int i) {
			typeID = i;
		}

		public static PledgeType forID(int i) {
			for (PledgeType t : values()) {
				if (t.typeID != i) {
					continue;
				}
				return t;
			}
			return NONE;
		}

		public boolean canAcceptPlayer(EntityPlayer entityplayer, TESFaction fac) {
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			return this == NONE || this == FACTION && pd.isPledgedTo(fac);
		}

		public String getCommandReqText(TESFaction fac) {
			if (this == NONE) {
				return null;
			}
			return StatCollector.translateToLocalFormatted("tes.hiredNPC.commandReq.pledge." + name(), fac.factionName());
		}

		public int getTypeID() {
			return typeID;
		}
	}
}