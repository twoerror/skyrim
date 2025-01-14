package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.TESLevelData;
import tes.common.database.TESCreativeTabs;
import tes.common.world.structure.TESStructureRegistry;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class TESItemStructureSpawner extends Item {
	private static int lastStructureSpawnTick;

	@SideOnly(Side.CLIENT)
	private IIcon iconBase;

	@SideOnly(Side.CLIENT)
	private IIcon iconOverlay;

	@SideOnly(Side.CLIENT)
	private IIcon iconSettlementBase;

	@SideOnly(Side.CLIENT)
	private IIcon iconSettlementOverlay;

	public TESItemStructureSpawner() {
		setHasSubtypes(true);
		setCreativeTab(TESCreativeTabs.TAB_SPAWN);
	}

	public static int getLastStructureSpawnTick() {
		return lastStructureSpawnTick;
	}

	public static void setLastStructureSpawnTick(int lastStructureSpawnTick) {
		TESItemStructureSpawner.lastStructureSpawnTick = lastStructureSpawnTick;
	}

	private static boolean spawnStructure(EntityPlayer entityplayer, World world, int id, int i, int j, int k) {
		if (!TESStructureRegistry.STRUCTURE_ITEM_SPAWNERS.containsKey(id)) {
			return false;
		}
		TESStructureRegistry.IStructureProvider strProvider = TESStructureRegistry.getStructureForID(id);
		if (strProvider != null) {
			boolean generated = strProvider.generateStructure(world, entityplayer, i, j, k);
			if (generated) {
				lastStructureSpawnTick = 20;
				world.playSoundAtEntity(entityplayer, "tes:item.structureSpawner", 1.0f, 1.0f);
			}
			return generated;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		TESStructureRegistry.StructureColorInfo info = TESStructureRegistry.STRUCTURE_ITEM_SPAWNERS.get(itemstack.getItemDamage());
		if (info != null) {
			if (pass == 0) {
				return info.getColorBackground();
			}
			return info.getColorForeground();
		}
		return 16777215;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int pass) {
		TESStructureRegistry.StructureColorInfo info = TESStructureRegistry.STRUCTURE_ITEM_SPAWNERS.get(i);
		if (info != null) {
			if (info.isSettlement()) {
				if (pass == 0) {
					return iconSettlementBase;
				}
				return iconSettlementOverlay;
			}
			if (pass == 0) {
				return iconBase;
			}
			return iconOverlay;
		}
		return iconBase;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		StringBuilder s = new StringBuilder(StatCollector.translateToLocal(getUnlocalizedName() + ".name").trim());
		String structureName = TESStructureRegistry.getNameFromID(itemstack.getItemDamage());
		if (structureName != null) {
			s.append(' ').append(StatCollector.translateToLocal("tes.structure." + structureName + ".name"));
		}
		return s.toString();
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (TESStructureRegistry.StructureColorInfo info : TESStructureRegistry.STRUCTURE_ITEM_SPAWNERS.values()) {
			if (info.isHidden()) {
				continue;
			}
			list.add(new ItemStack(item, 1, info.getSpawnedID()));
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float f, float f1, float f2) {
		if (world.isRemote) {
			return true;
		}
		if (TESLevelData.getStructuresBanned() == 1) {
			entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.spawnStructure.disabled"));
			return false;
		}
		if (TESLevelData.isPlayerBannedForStructures(entityplayer)) {
			entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.spawnStructure.banned"));
			return false;
		}
		if (lastStructureSpawnTick > 0) {
			entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.spawnStructure.wait", lastStructureSpawnTick / 20.0));
			return false;
		}
		if (spawnStructure(entityplayer, world, itemstack.getItemDamage(), i + Facing.offsetsXForSide[side], j + Facing.offsetsYForSide[side], k + Facing.offsetsZForSide[side]) && !entityplayer.capabilities.isCreativeMode) {
			--itemstack.stackSize;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		iconBase = iconregister.registerIcon(getIconString() + "_base");
		iconOverlay = iconregister.registerIcon(getIconString() + "_overlay");
		iconSettlementBase = iconregister.registerIcon(getIconString() + "_village_base");
		iconSettlementOverlay = iconregister.registerIcon(getIconString() + "_village_overlay");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}