package tes.common.entity.other;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.entity.ai.TESEntityAIAttackOnCollide;
import tes.common.entity.ai.TESEntityAIBanditFlee;
import tes.common.entity.ai.TESEntityAIBanditSteal;
import tes.common.entity.ai.TESEntityAINearestAttackableTargetBandit;
import tes.common.faction.TESFaction;
import tes.common.inventory.TESInventoryNPC;
import tes.common.item.other.TESItemMug;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public abstract class TESEntityBanditBase extends TESEntityHumanBase implements TESBiome.ImmuneToHeat, TESBiome.ImmuneToFrost {
	private static final int MAX_THEFTS = 3;

	private final TESInventoryNPC banditInventory = new TESInventoryNPC("BanditInventory", this, MAX_THEFTS);

	protected TESEntityBanditBase(World world) {
		super(world);
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new TESEntityAIAttackOnCollide(this, 1.0, false));
		tasks.addTask(2, new TESEntityAIBanditSteal(this, 1.2));
		tasks.addTask(3, new TESEntityAIBanditFlee(this, 1.0));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(6, new EntityAIWatchClosest2(this, EntityPlayer.class, 8.0f, 0.1f));
		tasks.addTask(6, new EntityAIWatchClosest2(this, TESEntityNPC.class, 5.0f, 0.05f));
		tasks.addTask(7, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(8, new EntityAILookIdle(this));
		addTargetTasks(true, TESEntityAINearestAttackableTargetBandit.class);
	}

	public static IChatComponent getTheftChatMsg() {
		return new ChatComponentTranslation("tes.chat.banditSteal");
	}

	public static boolean canStealFromPlayerInv(EntityPlayer entityplayer) {
		for (int slot = 0; slot < entityplayer.inventory.mainInventory.length; ++slot) {
			if (slot == entityplayer.inventory.currentItem || entityplayer.inventory.getStackInSlot(slot) == null) {
				continue;
			}
			return true;
		}
		return false;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		super.dropFewItems(flag, i);
		int bones = rand.nextInt(2) + rand.nextInt(i + 1);
		for (int l = 0; l < bones; ++l) {
			dropItem(Items.bone, 1);
		}
		int coins = 10 + rand.nextInt(10) + rand.nextInt((i + 1) * 10);
		for (int l = 0; l < coins; ++l) {
			dropItem(TESItems.coin, 1);
		}
		if (rand.nextInt(5) == 0) {
			entityDropItem(TESItemMug.Vessel.SKULL.getEmptyVessel(), 0.0f);
		}
	}

	public TESEntityNPC getBanditAsNPC() {
		return this;
	}

	public TESInventoryNPC getBanditInventory() {
		return banditInventory;
	}

	@Override
	public TESFaction getFaction() {
		return TESFaction.HOSTILE;
	}

	@Override
	public String getNPCName() {
		return familyInfo.getName();
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		return "standard/special/bandit";
	}

	public String getTheftSpeechBank(EntityPlayer player) {
		return getSpeechBank(player);
	}

	@Override
	public int getTotalArmorValue() {
		return 10;
	}

	@Override
	public void onAttackModeChange(TESEntityNPC.AttackMode mode, boolean mounted) {
		if (mode == TESEntityNPC.AttackMode.IDLE) {
			setCurrentItemOrArmor(0, npcItemsInv.getIdleItem());
		} else {
			setCurrentItemOrArmor(0, npcItemsInv.getMeleeWeapon());
		}
	}

	@Override
	public void onDeath(DamageSource damagesource) {
		super.onDeath(damagesource);
		if (!worldObj.isRemote && damagesource.getEntity() instanceof EntityPlayer && !banditInventory.isEmpty()) {
			EntityPlayer entityplayer = (EntityPlayer) damagesource.getEntity();
			TESLevelData.getData(entityplayer).addAchievement(TESAchievement.killThievingBandit);
		}
		if (!worldObj.isRemote) {
			banditInventory.dropAllItems();
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		banditInventory.readFromNBT(nbt);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		banditInventory.writeToNBT(nbt);
	}
}