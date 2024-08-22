package tes.common.entity.animal;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.util.TESReflection;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntityCamel extends TESEntityHorse {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityCamel(World world) {
		super(world);
		setSize(1.6f, 1.8f);
	}

	@Override
	public double clampChildHealth(double health) {
		return MathHelper.clamp_double(health, 12.0, 36.0);
	}

	@Override
	public double clampChildJump(double jump) {
		return MathHelper.clamp_double(jump, 0.1, 0.6);
	}

	@Override
	public double clampChildSpeed(double speed) {
		return MathHelper.clamp_double(speed, 0.1, 0.35);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		int hides = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < hides; ++l) {
			dropItem(Items.leather, 1);
		}
		int meat = rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < meat; ++l) {
			if (isBurning()) {
				dropItem(TESItems.camelCooked, 1);
				continue;
			}
			dropItem(TESItems.camelRaw, 1);
		}
	}

	@Override
	public boolean func_110259_cr() {
		return true;
	}

	public int getCamelCarpetColor() {
		ItemStack itemstack = getMountArmor();
		if (itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.carpet)) {
			int meta = itemstack.getItemDamage();
			int dyeMeta = BlockColored.func_150031_c(meta);
			int[] colors = ItemDye.field_150922_c;
			dyeMeta = MathHelper.clamp_int(dyeMeta, 0, colors.length);
			return colors[dyeMeta];
		}
		return -1;
	}

	@Override
	public int getHorseType() {
		return 1;
	}

	@Override
	public boolean isBreedingItem(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.wheat;
	}

	public boolean isCamelWearingCarpet() {
		ItemStack itemstack = getMountArmor();
		return itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.carpet);
	}

	@Override
	public boolean isMountArmorValid(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Item.getItemFromBlock(Blocks.carpet) || super.isMountArmorValid(itemstack);
	}

	@Override
	public void onTESHorseSpawn() {
		double jumpStrength = getEntityAttribute(TESReflection.getHorseJumpStrength()).getAttributeValue();
		getEntityAttribute(TESReflection.getHorseJumpStrength()).setBaseValue(jumpStrength * 0.5);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!worldObj.isRemote && riddenByEntity instanceof EntityPlayer && isMountSaddled()) {
			TESLevelData.getData((EntityPlayer) riddenByEntity).addAchievement(TESAchievement.rideCamel);
		}
	}

	public void setNomadChestAndCarpet() {
		setChestedForWorldGen();
		ItemStack carpet = new ItemStack(Blocks.carpet, 1, rand.nextInt(16));
		setMountArmor(carpet);
	}
}