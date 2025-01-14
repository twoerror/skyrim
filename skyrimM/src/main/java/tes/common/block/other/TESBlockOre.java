package tes.common.block.other;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESBlocks;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockOre extends Block {
	public TESBlockOre() {
		super(Material.rock);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(3.0f);
		setResistance(5.0f);
		setStepSound(soundTypeStone);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int meta, float f, int fortune) {
		super.dropBlockAsItemWithChance(world, i, j, k, meta, f, fortune);
		if (getItemDropped(meta, world.rand, fortune) != Item.getItemFromBlock(this)) {
			int amountXp = 0;

			if (this == TESBlocks.oreGlowstone) {
				amountXp = MathHelper.getRandomIntegerInRange(world.rand, 0, 2);
			}
			if (this == TESBlocks.oreSulfur || this == TESBlocks.oreSaltpeter) {
				amountXp = MathHelper.getRandomIntegerInRange(world.rand, 0, 2);
			}
			dropXpOnBlockBreak(world, i, j, k, amountXp);
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {

		if (this == TESBlocks.oreGlowstone) {
			return Items.glowstone_dust;
		}
		if (this == TESBlocks.oreSulfur) {
			return TESItems.sulfur;
		}
		if (this == TESBlocks.oreSaltpeter) {
			return TESItems.saltpeter;
		}
		return Item.getItemFromBlock(this);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
		super.harvestBlock(world, entityplayer, i, j, k, l);
		if (!world.isRemote) {
			if (this == TESBlocks.oreValyrian) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.mineValyrian);
			}
			if (this == TESBlocks.oreGlowstone) {
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.mineGlowstone);
			}
		}
	}

	@Override
	public int quantityDropped(Random random) {

		if (this == TESBlocks.oreGlowstone) {
			return 2 + random.nextInt(4);
		}
		if (this == TESBlocks.oreSulfur || this == TESBlocks.oreSaltpeter) {
			return 1 + random.nextInt(2);
		}
		return 1;
	}

	@Override
	public int quantityDroppedWithBonus(int i, Random random) {
		if (i > 0 && Item.getItemFromBlock(this) != getItemDropped(0, random, i)) {
			int factor = random.nextInt(i + 2) - 1;
			factor = Math.max(factor, 0);
			int drops = quantityDropped(random) * (factor + 1);
			if (this == TESBlocks.oreGlowstone) {
				return Math.min(drops, 8);
			}
			return drops;
		}
		return quantityDropped(random);
	}
}