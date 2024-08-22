package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESGuiId;
import tes.common.quest.TESMiniQuestEvent;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class TESItemQuestBook extends Item {
	@SideOnly(Side.CLIENT)
	private static IIcon questOfferIcon;

	public TESItemQuestBook() {
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_MISC);
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getQuestOfferIcon() {
		return questOfferIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		TESPlayerData playerData = TESLevelData.getData(entityplayer);
		int activeQuests = playerData.getActiveMiniQuests().size();
		list.add(StatCollector.translateToLocalFormatted("item.tes.mqbook.activeQuests", activeQuests));
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		entityplayer.openGui(TES.instance, TESGuiId.QUEST_BOOK.ordinal(), world, 0, 0, 0);
		if (!world.isRemote) {
			TESLevelData.getData(entityplayer).distributeMQEvent(new TESMiniQuestEvent.OpenRedBook());
		}
		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		super.registerIcons(iconregister);
		questOfferIcon = iconregister.registerIcon("tes:quest_offer");
	}
}