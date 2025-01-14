package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.TESSquadrons;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESGuiId;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class TESItemCommandHorn extends Item implements TESSquadrons.SquadronItem {
	public TESItemCommandHorn() {
		setHasSubtypes(true);
		setMaxDamage(0);
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 40;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int j = 0; j <= 3; ++j) {
			list.add(new ItemStack(item, 1, j));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		switch (itemstack.getItemDamage()) {
			case 1:
				return "item.tes.commandHorn.halt";
			case 2:
				return "item.tes.commandHorn.ready";
			case 3:
				return "item.tes.commandHorn.summon";
			default:
				return super.getUnlocalizedName(itemstack);
		}
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			List<? extends Entity> entities = world.loadedEntityList;
			for (Entity entity : entities) {
				if (entity instanceof TESEntityNPC) {
					TESEntityNPC npc = (TESEntityNPC) entity;
					if (!npc.getHireableInfo().isActive() || npc.getHireableInfo().getHiringPlayer() != entityplayer || !TESSquadrons.areSquadronsCompatible(npc, itemstack)) {
						continue;
					}
					if (itemstack.getItemDamage() == 1 && npc.getHireableInfo().getObeyHornHaltReady()) {
						npc.getHireableInfo().halt();
						continue;
					}
					if (itemstack.getItemDamage() == 2 && npc.getHireableInfo().getObeyHornHaltReady()) {
						npc.getHireableInfo().ready();
						continue;
					}
					if (itemstack.getItemDamage() != 3 || !npc.getHireableInfo().getObeyHornSummon()) {
						continue;
					}
					npc.getHireableInfo().tryTeleportToHiringPlayer(true);
				}
			}
		}
		if (itemstack.getItemDamage() == 1) {
			itemstack.setItemDamage(2);
		} else if (itemstack.getItemDamage() == 2) {
			itemstack.setItemDamage(1);
		}
		world.playSoundAtEntity(entityplayer, "tes:item.horn", 4.0f, 1.0f);
		return itemstack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (itemstack.getItemDamage() == 0) {
			entityplayer.openGui(TES.instance, TESGuiId.HORN_SELECT.ordinal(), world, 0, 0, 0);
		} else {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}
}