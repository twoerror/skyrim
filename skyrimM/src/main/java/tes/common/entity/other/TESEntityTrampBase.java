package tes.common.entity.other;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESFoods;
import tes.common.database.TESTradeEntries;
import tes.common.entity.ai.TESEntityAIAttackOnCollide;
import tes.common.entity.ai.TESEntityAIDrink;
import tes.common.entity.ai.TESEntityAIEat;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class TESEntityTrampBase extends TESEntityHumanBase implements TESTradeable.Smith, TESBiome.ImmuneToHeat, TESBiome.ImmuneToFrost {
	protected TESEntityTrampBase(World world) {
		super(world);
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new TESEntityAIAttackOnCollide(this, 1.4, true));
		tasks.addTask(2, new EntityAIOpenDoor(this, true));
		tasks.addTask(3, new EntityAIWander(this, 1.0));
		tasks.addTask(4, new TESEntityAIEat(this, TESFoods.WILD, 8000));
		tasks.addTask(4, new TESEntityAIDrink(this, TESFoods.WILD_DRINK, 8000));
		tasks.addTask(5, new EntityAIWatchClosest2(this, EntityPlayer.class, 10.0f, 0.1f));
		tasks.addTask(5, new EntityAIWatchClosest2(this, TESEntityNPC.class, 5.0f, 0.05f));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(7, new EntityAILookIdle(this));
		addTargetTasks(false);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
	}

	@Override
	public boolean canTradeWith(EntityPlayer entityplayer) {
		return isFriendly(entityplayer);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		super.dropFewItems(flag, i);
		int bones = rand.nextInt(2) + rand.nextInt(i + 1);
		for (int l = 0; l < bones; ++l) {
			dropItem(Items.bone, 1);
		}
	}

	@Override
	public String getNPCName() {
		return familyInfo.getName();
	}

	@Override
	public String getSpeechBank(EntityPlayer entityplayer) {
		if (isFriendly(entityplayer)) {
			return "standard/civilized/usual_friendly";
		}
		return "standard/civilized/usual_hostile";
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
	public void onPlayerTrade(EntityPlayer entityplayer, TESTradeEntries.TradeType type, ItemStack itemstack) {
		TESLevelData.getData(entityplayer).addAchievement(TESAchievement.trade);
	}
}