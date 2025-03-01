package tes.common.entity.animal;

import tes.common.database.TESItems;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.ai.TESEntityAIAvoidWithChance;
import tes.common.entity.ai.TESEntityAIFlee;
import tes.common.entity.ai.TESEntityAIRabbitEatCrops;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESAmbientCreature;
import tes.common.entity.other.iface.TESFarmhand;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.List;
import java.util.UUID;

public class TESEntityRabbit extends EntityCreature implements TESAmbientCreature, TESRandomSkinEntity, TESBiome.ImmuneToFrost {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityRabbit(World world) {
		super(world);
		setSize(0.5f, 0.5f);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new TESEntityAIFlee(this, 2.0));
		String fleeSound = "tes:rabbit.flee";
		tasks.addTask(2, new TESEntityAIAvoidWithChance(this, EntityPlayer.class, 4.0f, 1.3, 1.5, 0.05f, fleeSound));
		tasks.addTask(2, new TESEntityAIAvoidWithChance(this, TESEntityNPC.class, 4.0f, 1.3, 1.5, 0.05f, fleeSound));
		tasks.addTask(3, new TESEntityAIRabbitEatCrops(this, 1.2));
		tasks.addTask(4, new EntityAIWander(this, 1.0));
		tasks.addTask(5, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0f, 0.05f));
		tasks.addTask(6, new EntityAILookIdle(this));
	}

	public boolean anyFarmhandsNearby(int i, int j, int k) {
		int range = 16;
		List<? extends TESFarmhand> farmhands = worldObj.getEntitiesWithinAABB(TESFarmhand.class, AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1).expand(range, range, range));
		return !farmhands.isEmpty();
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
	}

	@Override
	public boolean canDespawn() {
		return true;
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		int meat = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < meat; ++l) {
			if (isBurning()) {
				dropItem(TESItems.rabbitCooked, 1);
				continue;
			}
			dropItem(TESItems.rabbitRaw, 1);
		}
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(17, (byte) 0);
	}

	@Override
	public float getBlockPathWeight(int i, int j, int k) {
		Block block = worldObj.getBlock(i, j - 1, k);
		if (block == Blocks.grass) {
			return 10.0f;
		}
		return worldObj.getLightBrightness(i, j, k) - 0.5f;
	}

	@Override
	public boolean getCanSpawnHere() {
		if (super.getCanSpawnHere()) {
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(boundingBox.minY);
			int k = MathHelper.floor_double(posZ);
			BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
			return biome.temperature != 0.0f && j > 62 && j < 140 && worldObj.getBlock(i, j - 1, k) == biome.topBlock;
		}
		return false;
	}

	@Override
	public String getDeathSound() {
		return "tes:rabbit.death";
	}

	@Override
	public int getExperiencePoints(EntityPlayer entityplayer) {
		return 1 + rand.nextInt(2);
	}

	@Override
	public String getHurtSound() {
		return "tes:rabbit.hurt";
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(TESItems.spawnEgg, 1, TESEntityRegistry.getEntityID(this));
	}

	@Override
	public int getTalkInterval() {
		return 200;
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	public boolean isRabbitEating() {
		return dataWatcher.getWatchableObjectByte(17) == 1;
	}

	public void setRabbitEating(boolean flag) {
		dataWatcher.updateObject(17, flag ? (byte) 1 : 0);
	}

	@Override
	public void setUniqueID(UUID uuid) {
		entityUniqueID = uuid;
	}
}