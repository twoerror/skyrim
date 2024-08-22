package tes.common.database;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESCreativeTabs extends CreativeTabs {
	public static final TESCreativeTabs TAB_BLOCK = new TESCreativeTabs("blocks");
	public static final TESCreativeTabs TAB_UTIL = new TESCreativeTabs("util");
	public static final TESCreativeTabs TAB_DECO = new TESCreativeTabs("decorations");
	public static final TESCreativeTabs TAB_FOOD = new TESCreativeTabs("food");
	public static final TESCreativeTabs TAB_MATERIALS = new TESCreativeTabs("materials");
	public static final TESCreativeTabs TAB_MISC = new TESCreativeTabs("misc");
	public static final TESCreativeTabs TAB_TOOLS = new TESCreativeTabs("tools");
	public static final TESCreativeTabs TAB_COMBAT = new TESCreativeTabs("combat");
	public static final TESCreativeTabs TAB_STORY = new TESCreativeTabs("story");
	public static final TESCreativeTabs TAB_SPAWN = new TESCreativeTabs("spawning");
	public static final TESCreativeTabs TAB_BANNER = new TESCreativeTabs("banner");

	private ItemStack theIcon;

	@SuppressWarnings("WeakerAccess")
	public TESCreativeTabs(String label) {
		super(label);
	}

	public static void onInit() {
		TAB_BLOCK.theIcon = new ItemStack(TESBlocks.brick1, 1, 1);
		TAB_UTIL.theIcon = new ItemStack(TESBlocks.unsmeltery);
		TAB_DECO.theIcon = new ItemStack(TESBlocks.chandelier, 1, 3);
		TAB_FOOD.theIcon = new ItemStack(TESItems.mugVodka);
		TAB_MATERIALS.theIcon = new ItemStack(Items.iron_sword);
		TAB_MISC.theIcon = new ItemStack(TESItems.coin, 1, 6);
		TAB_TOOLS.theIcon = new ItemStack(Items.iron_axe);
		TAB_COMBAT.theIcon = new ItemStack(Items.iron_helmet);
		TAB_STORY.theIcon = new ItemStack(TESItems.bane);
		TAB_SPAWN.theIcon = new ItemStack(TESItems.spawnEgg, 1, 248);
		TAB_BANNER.theIcon = new ItemStack(TESItems.bannerTab);
	}

	@Override
	public ItemStack getIconItemStack() {
		return theIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return theIcon.getItem();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return StatCollector.translateToLocal("tes.tab." + getTabLabel());
	}
}