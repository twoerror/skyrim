package tes.common.block.leaves;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import tes.common.util.TESCrashHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TESBlockLeavesBase extends BlockLeaves {
	private static final Collection<Block> ALL_LEAF_BLOCKS = new ArrayList<>();

	protected String[] leafNames;

	@SideOnly(Side.CLIENT)
	private IIcon[][] leafIcons;

	private String vanillaTextureName;

	protected TESBlockLeavesBase() {
		this(false, null);
	}

	protected TESBlockLeavesBase(boolean vanilla, String vname) {
		if (vanilla) {
			setCreativeTab(CreativeTabs.tabDecorations);
			vanillaTextureName = vname;
		} else {
			setCreativeTab(TESCreativeTabs.TAB_DECO);
		}
		ALL_LEAF_BLOCKS.add(this);
	}

	protected static int getBiomeLeafColor(IBlockAccess world, int i, int j, int k) {
		int r = 0;
		int g = 0;
		int b = 0;
		int count = 0;
		int range = 1;
		for (int i1 = -range; i1 <= range; ++i1) {
			for (int k1 = -range; k1 <= range; ++k1) {
				int biomeColor = TESCrashHandler.getBiomeGenForCoords(world, i + i1, k + k1).getBiomeFoliageColor(i + i1, j, k + k1);
				r += (biomeColor & 0xFF0000) >> 16;
				g += (biomeColor & 0xFF00) >> 8;
				b += biomeColor & 0xFF;
				++count;
			}
		}
		return (r / count & 0xFF) << 16 | (g / count & 0xFF) << 8 | b / count & 0xFF;
	}

	public static void setAllGraphicsLevels(boolean flag) {
		for (Object allLeafBlock : ALL_LEAF_BLOCKS) {
			((BlockLeaves) allLeafBlock).setGraphicsLevel(flag);
		}
	}

	protected static int calcFortuneModifiedDropChance(int baseChance, int fortune) {
		int chance = baseChance;
		if (fortune > 0) {
			chance -= 2 << fortune;
			chance = Math.max(chance, baseChance / 2);
			return Math.max(chance, 1);
		}
		return chance;
	}

	protected abstract void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune);

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, int i, int j, int k) {
		return 16777215;
	}

	@Override
	public String[] func_150125_e() {
		return leafNames;
	}

	public String[] getAllLeafNames() {
		return leafNames;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		int saplingChanceBase = getSaplingChance(meta & 3);
		int saplingChance = calcFortuneModifiedDropChance(saplingChanceBase, fortune);
		if (world.rand.nextInt(saplingChance) == 0) {
			drops.add(new ItemStack(getItemDropped(meta, world.rand, fortune), 1, damageDropped(meta)));
		}
		addSpecialLeafDrops(drops, world, meta, fortune);
		return drops;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int meta = j & 3;
		if (meta >= leafNames.length) {
			meta = 0;
		}
		return leafIcons[meta][field_150121_P ? 0 : 1];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColor(int i) {
		return 16777215;
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getLeavesRenderID();
	}

	protected int getSaplingChance(int meta) {
		return 20;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int j = 0; j < leafNames.length; ++j) {
			list.add(new ItemStack(item, 1, j));
		}
	}

	@Override
	public String getTextureName() {
		if (vanillaTextureName != null) {
			return vanillaTextureName;
		}
		return super.getTextureName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		leafIcons = new IIcon[leafNames.length][2];
		for (int i = 0; i < leafNames.length; ++i) {
			IIcon fancy = iconregister.registerIcon(getTextureName() + '_' + leafNames[i] + "_fancy");
			IIcon fast = iconregister.registerIcon(getTextureName() + '_' + leafNames[i] + "_fast");
			leafIcons[i][0] = fancy;
			leafIcons[i][1] = fast;
		}
	}
}