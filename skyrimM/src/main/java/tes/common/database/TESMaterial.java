package tes.common.database;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class TESMaterial {
	public static final ItemArmor.ArmorMaterial ANONYMOUS = EnumHelper.addArmorMaterial("TES_ANONYMOUS", 33, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial ARRYN = EnumHelper.addArmorMaterial("TES_ARRYN", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial ARRYNGUARD = EnumHelper.addArmorMaterial("TES_ARRYNGUARD", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial ASSHAI = EnumHelper.addArmorMaterial("TES_ASSHAI", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial BLACKFYRE = EnumHelper.addArmorMaterial("TES_BLACKFYRE", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial BLACKSKIN = EnumHelper.addArmorMaterial("TES_BLACKSKIN", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial BONE = EnumHelper.addArmorMaterial("TES_BONE", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial BRAAVOS = EnumHelper.addArmorMaterial("TES_BRAAVOS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial BRONZE_CHAINMAIL = EnumHelper.addArmorMaterial("TES_BRONZE_CHAINMAIL", 15, new int[]{2, 4, 3, 1}, 12);
	public static final ItemArmor.ArmorMaterial BRONZE = EnumHelper.addArmorMaterial("TES_BRONZE", 15, new int[]{2, 5, 4, 1}, 9);
	public static final ItemArmor.ArmorMaterial CROWNLANDS = EnumHelper.addArmorMaterial("TES_CROWNLANDS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial DORNE = EnumHelper.addArmorMaterial("TES_DORNE", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial DOTHRAKI = EnumHelper.addArmorMaterial("TES_DOTHRAKI", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial DRAGONSTONE = EnumHelper.addArmorMaterial("TES_DRAGONSTONE", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial FUR = EnumHelper.addArmorMaterial("TES_FUR", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial GEMSBOK = EnumHelper.addArmorMaterial("TES_GEMSBOK", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial GHISCAR = EnumHelper.addArmorMaterial("TES_GHISCAR", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial GIFT = EnumHelper.addArmorMaterial("TES_GIFT", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial GOLDENCOMPANY = EnumHelper.addArmorMaterial("TES_GOLDENCOMPANY", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial HAND = EnumHelper.addArmorMaterial("TES_HAND", 25, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial HELMET = EnumHelper.addArmorMaterial("TES_HELMET", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial HILLMEN = EnumHelper.addArmorMaterial("TES_HILLMEN", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial IBBEN = EnumHelper.addArmorMaterial("TES_IBBEN", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial ICE = EnumHelper.addArmorMaterial("TES_ICE", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial IRONBORN = EnumHelper.addArmorMaterial("TES_IRONBORN", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial JOGOS = EnumHelper.addArmorMaterial("TES_JOGOS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial KAFTAN = EnumHelper.addArmorMaterial("TES_KAFTAN", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial KINGSGUARD = EnumHelper.addArmorMaterial("TES_KINGSGUARD", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial LHAZAR = EnumHelper.addArmorMaterial("TES_LHAZAR", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial LHAZAR_LION = EnumHelper.addArmorMaterial("TES_LHAZAR_LION", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial LORATH = EnumHelper.addArmorMaterial("TES_LORATH", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial LYS = EnumHelper.addArmorMaterial("TES_LYS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial MOSSOVY = EnumHelper.addArmorMaterial("TES_MOSSOVY", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial MYR = EnumHelper.addArmorMaterial("TES_MYR", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial NORTH = EnumHelper.addArmorMaterial("TES_NORTH", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial NORTHGUARD = EnumHelper.addArmorMaterial("TES_NORTHGUARD", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial NORVOS = EnumHelper.addArmorMaterial("TES_NORVOS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial PENTOS = EnumHelper.addArmorMaterial("TES_PENTOS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial QARTH = EnumHelper.addArmorMaterial("TES_QARTH", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial QOHOR = EnumHelper.addArmorMaterial("TES_QOHOR", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial REACH = EnumHelper.addArmorMaterial("TES_REACH", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial REACHGUARD = EnumHelper.addArmorMaterial("TES_REACHGUARD", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial REDKING = EnumHelper.addArmorMaterial("TES_REDKING", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial RENLY = EnumHelper.addArmorMaterial("TES_RENLY", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial RIVERLANDS = EnumHelper.addArmorMaterial("TES_RIVERLANDS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial ROBES = EnumHelper.addArmorMaterial("TES_ROBES", 10, new int[]{1, 3, 2, 1}, 15);
	public static final ItemArmor.ArmorMaterial ROYCE = EnumHelper.addArmorMaterial("TES_ROYCE", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial SOTHORYOS = EnumHelper.addArmorMaterial("TES_SOTHORYOS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial SOTHORYOS_GOLD = EnumHelper.addArmorMaterial("TES_SOTHORYOS_GOLD", 13, new int[]{2, 5, 3, 1}, 25);
	public static final ItemArmor.ArmorMaterial STORMLANDS = EnumHelper.addArmorMaterial("TES_STORMLANDS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial SUMMER = EnumHelper.addArmorMaterial("TES_SUMMER", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial TARGARYEN = EnumHelper.addArmorMaterial("TES_TARGARYEN", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial TYROSH = EnumHelper.addArmorMaterial("TES_TYROSH", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial UNSULLIED = EnumHelper.addArmorMaterial("TES_UNSULLIED", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial VALYRIAN_CHAINMAIL = EnumHelper.addArmorMaterial("TES_VALYRIAN_CHAINMAIL", 33, new int[]{3, 7, 5, 2}, 13);
	public static final ItemArmor.ArmorMaterial VALYRIAN = EnumHelper.addArmorMaterial("TES_VALYRIAN", 33, new int[]{3, 8, 6, 3}, 10);
	public static final ItemArmor.ArmorMaterial VOLANTIS = EnumHelper.addArmorMaterial("TES_VOLANTIS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial WESTERLANDS = EnumHelper.addArmorMaterial("TES_WESTERLANDS", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial WESTERLANDSGUARD = EnumHelper.addArmorMaterial("TES_WESTERLANDSGUARD", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial WESTKING = EnumHelper.addArmorMaterial("TES_WESTKING", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial YITI = EnumHelper.addArmorMaterial("TES_YITI", 20, new int[]{2, 6, 5, 2}, 9);
	public static final ItemArmor.ArmorMaterial YITI_FRONTIER = EnumHelper.addArmorMaterial("TES_YITI_FRONTIER", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial YITI_SAMURAI = EnumHelper.addArmorMaterial("TES_YITI_SAMURAI", 25, new int[]{2, 7, 6, 3}, 9);
	public static final ItemArmor.ArmorMaterial IRON = EnumHelper.addArmorMaterial("TES_IRON", 15, new int[]{2, 4, 3, 1}, 4);
	
	public static final Item.ToolMaterial BRONZE_TOOL = EnumHelper.addToolMaterial("TES_BRONZE_TOOL", 2, 250, 5.0F, 1.5F, 10);
	public static final Item.ToolMaterial COBALT_TOOL = EnumHelper.addToolMaterial("TES_COBALT_TOOL", 3, 1500, 8.0F, 3.0F, 10);
	public static final Item.ToolMaterial FLINT_TOOL = EnumHelper.addToolMaterial("TES_FLINT_TOOL", 1, 200, 4.0F, 1.0F, 5);
	public static final Item.ToolMaterial HORN_TOOL = EnumHelper.addToolMaterial("TES_HORN_TOOL", 1, 200, 4.0F, 1.0F, 5);
	public static final Item.ToolMaterial ICE_TOOL = EnumHelper.addToolMaterial("TES_ICE_TOOL", 2, 450, 6.0F, 2.0F, 14);
	public static final Item.ToolMaterial OBSIDIAN_TOOL = EnumHelper.addToolMaterial("TES_OBSIDIAN_TOOL", 2, 450, 6.0F, 2.0F, 14);
	public static final Item.ToolMaterial SILVER_TOOL = EnumHelper.addToolMaterial("TES_SILVER_TOOL", 2, 450, 6.0F, 2.0F, 14);
	public static final Item.ToolMaterial VALYRIAN_TOOL = EnumHelper.addToolMaterial("TES_VALYRIAN_TOOL", 4, 2500, 9.0F, 5.0F, 8);
	public static final Item.ToolMaterial ASSHAI_TOOL = EnumHelper.addToolMaterial("TES_ASSHAI_TOOL", 2, 450, 6.0F, 2.0F, 14);

	private TESMaterial() {
	}

	public static Item.ToolMaterial getToolMaterialByName(String name) {
		try {
			return Item.ToolMaterial.valueOf(name);
		} catch (Exception e) {
			return Item.ToolMaterial.IRON;
		}
	}

	public static void onInit() {
		ANONYMOUS.customCraftingMaterial = Items.paper;
		ARRYN.customCraftingMaterial = Items.iron_ingot;
		ARRYNGUARD.customCraftingMaterial = Items.iron_ingot;
		ASSHAI.customCraftingMaterial = Items.iron_ingot;
		BLACKFYRE.customCraftingMaterial = Items.iron_ingot;
		BLACKSKIN.customCraftingMaterial = Items.iron_ingot;
		BONE.customCraftingMaterial = Items.bone;
		BRAAVOS.customCraftingMaterial = Items.iron_ingot;
		BRONZE.customCraftingMaterial = TESItems.bronzeIngot;
		BRONZE_CHAINMAIL.customCraftingMaterial = TESItems.bronzeIngot;
		CROWNLANDS.customCraftingMaterial = Items.iron_ingot;
		DORNE.customCraftingMaterial = Items.iron_ingot;
		DOTHRAKI.customCraftingMaterial = Item.getItemFromBlock(TESBlocks.driedReeds);
		DRAGONSTONE.customCraftingMaterial = Items.iron_ingot;
		FUR.customCraftingMaterial = TESItems.fur;
		GHISCAR.customCraftingMaterial = Items.iron_ingot;
		GIFT.customCraftingMaterial = Items.iron_ingot;
		GOLDENCOMPANY.customCraftingMaterial = Items.iron_ingot;
		HAND.customCraftingMaterial = Items.string;
		HELMET.customCraftingMaterial = Items.iron_ingot;
		HILLMEN.customCraftingMaterial = Items.iron_ingot;
		IBBEN.customCraftingMaterial = Items.iron_ingot;
		ICE.customCraftingMaterial = Items.iron_ingot;
		IRONBORN.customCraftingMaterial = Items.iron_ingot;
		JOGOS.customCraftingMaterial = Items.iron_ingot;
		KAFTAN.customCraftingMaterial = Item.getItemFromBlock(Blocks.wool);
		KINGSGUARD.customCraftingMaterial = Items.iron_ingot;
		LHAZAR_LION.customCraftingMaterial = TESItems.lionFur;
		LORATH.customCraftingMaterial = Items.iron_ingot;
		LYS.customCraftingMaterial = Items.iron_ingot;
		MOSSOVY.customCraftingMaterial = Items.iron_ingot;
		MYR.customCraftingMaterial = Items.iron_ingot;
		NORTH.customCraftingMaterial = Items.iron_ingot;
		NORTHGUARD.customCraftingMaterial = Items.iron_ingot;
		NORVOS.customCraftingMaterial = Items.iron_ingot;
		PENTOS.customCraftingMaterial = Items.iron_ingot;
		QARTH.customCraftingMaterial = Items.iron_ingot;
		QOHOR.customCraftingMaterial = Items.iron_ingot;
		REACH.customCraftingMaterial = Items.iron_ingot;
		REACHGUARD.customCraftingMaterial = Items.iron_ingot;
		REDKING.customCraftingMaterial = Items.iron_ingot;
		RENLY.customCraftingMaterial = Items.iron_ingot;
		RIVERLANDS.customCraftingMaterial = Items.iron_ingot;
		ROBES.customCraftingMaterial = Item.getItemFromBlock(Blocks.wool);
		ROYCE.customCraftingMaterial = TESItems.bronzeIngot;
		SOTHORYOS.customCraftingMaterial = Items.iron_ingot;
		SOTHORYOS_GOLD.customCraftingMaterial = Items.gold_ingot;
		STORMLANDS.customCraftingMaterial = Items.iron_ingot;
		SUMMER.customCraftingMaterial = Items.iron_ingot;
		TARGARYEN.customCraftingMaterial = Items.iron_ingot;
		TYROSH.customCraftingMaterial = Items.iron_ingot;
		UNSULLIED.customCraftingMaterial = Items.iron_ingot;
		VOLANTIS.customCraftingMaterial = Items.iron_ingot;
		WESTERLANDS.customCraftingMaterial = Items.iron_ingot;
		WESTERLANDSGUARD.customCraftingMaterial = Items.iron_ingot;
		WESTKING.customCraftingMaterial = Items.iron_ingot;
		YITI.customCraftingMaterial = Items.iron_ingot;
		YITI_FRONTIER.customCraftingMaterial = Items.iron_ingot;
		YITI_SAMURAI.customCraftingMaterial = Items.iron_ingot;
		IRON.customCraftingMaterial = Items.iron_ingot;

		BRONZE_TOOL.setRepairItem(new ItemStack(TESItems.bronzeIngot));
		COBALT_TOOL.setRepairItem(new ItemStack(TESItems.alloySteelIngot));
		FLINT_TOOL.setRepairItem(new ItemStack(Items.flint));
		HORN_TOOL.setRepairItem(new ItemStack(TESItems.horn));
		ICE_TOOL.setRepairItem(new ItemStack(TESItems.iceShard));
		OBSIDIAN_TOOL.setRepairItem(new ItemStack(TESItems.obsidianShard));
		SILVER_TOOL.setRepairItem(new ItemStack(TESItems.silverIngot));
		ASSHAI_TOOL.setRepairItem(new ItemStack(Items.iron_ingot));
	}
}