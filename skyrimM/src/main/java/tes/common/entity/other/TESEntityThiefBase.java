package tes.common.entity.other;

import tes.common.database.TESFoods;
import tes.common.database.TESItems;
import tes.common.entity.ai.*;
import tes.common.item.other.TESItemMug;
import tes.common.quest.TESMiniQuest;
import tes.common.quest.TESMiniQuestFactory;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public abstract class TESEntityThiefBase extends TESEntityHumanBase implements TESBiome.ImmuneToHeat, TESBiome.ImmuneToFrost {
	protected TESEntityThiefBase(World world) {
		super(world);
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new TESEntityAIHiredRemainStill(this));
		tasks.addTask(2, createThiefAttackAI());
		tasks.addTask(3, new TESEntityAIFollowHiringPlayer(this));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(6, new TESEntityAIEat(this, TESFoods.WESTEROS, 8000));
		tasks.addTask(6, new TESEntityAIDrink(this, TESFoods.WESTEROS_DRINK, 8000));
		tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 8.0f, 0.02f));
		tasks.addTask(7, new EntityAIWatchClosest2(this, TESEntityNPC.class, 5.0f, 0.02f));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(9, new EntityAILookIdle(this));
		questInfo.setOfferChance(1);
		addTargetTasks(false);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
	}

	@Override
	public TESMiniQuest createMiniQuest() {
		return TESMiniQuestFactory.CRIMINAL.createQuest(this);
	}

	private EntityAIBase createThiefAttackAI() {
		return new TESEntityAIAttackOnCollide(this, 1.4, false);
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

	@Override
	public float getBlockPathWeight(int i, int j, int k) {
		float f = 0.0f;
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		if (biome instanceof TESBiome) {
			f += 20.0f;
		}
		return f;
	}

	@Override
	public TESMiniQuestFactory getBountyHelpSpeechDir() {
		return TESMiniQuestFactory.CRIMINAL;
	}

	@Override
	public String getNPCName() {
		return familyInfo.getName();
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			return "standard/special/criminal_friendly";
		}
		return "standard/special/criminal_hostile";
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
}