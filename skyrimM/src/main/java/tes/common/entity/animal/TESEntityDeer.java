package tes.common.entity.animal;

import tes.common.database.TESItems;
import tes.common.entity.ai.TESEntityAIAttackOnCollide;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class TESEntityDeer extends TESEntityHorse implements TESRandomSkinEntity {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityDeer(World world) {
		super(world);
		setSize(1.6f, 1.8f);
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0);
	}

	@Override
	public double clampChildHealth(double health) {
		return MathHelper.clamp_double(health, 16.0, 50.0);
	}

	@Override
	public double clampChildSpeed(double speed) {
		return MathHelper.clamp_double(speed, 0.08, 0.34);
	}

	@Override
	public EntityAIBase createMountAttackAI() {
		return new TESEntityAIAttackOnCollide(this, 1.4, true);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		int hide = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < hide; ++l) {
			dropItem(Items.leather, 1);
		}
		int meat = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < meat; ++l) {
			if (isBurning()) {
				dropItem(TESItems.deerCooked, 1);
				continue;
			}
			dropItem(TESItems.deerRaw, 1);
		}
	}

	@Override
	public String getDeathSound() {
		return "tes:elk.death";
	}

	@Override
	public int getHorseType() {
		return 0;
	}

	@Override
	public String getHurtSound() {
		return "tes:elk.hurt";
	}

	@Override
	public String getLivingSound() {
		return "tes:elk.say";
	}

	@Override
	public boolean isBreedingItem(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.wheat;
	}

	@Override
	public boolean isMountHostile() {
		return true;
	}

	@Override
	public void onTESHorseSpawn() {
		double maxHealth = getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealth * (1.0f + rand.nextFloat() * 0.5f));
	}

	@Override
	public void setUniqueID(UUID uuid) {
		entityUniqueID = uuid;
	}
}