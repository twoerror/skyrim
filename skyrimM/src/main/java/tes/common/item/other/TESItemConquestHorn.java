package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.TESBannerProtection;
import tes.common.TESLevelData;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESInvasions;
import tes.common.database.TESItems;
import tes.common.entity.other.inanimate.TESEntityInvasionSpawner;
import tes.common.entity.other.inanimate.TESEntityNPCRespawner;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class TESItemConquestHorn extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon baseIcon;

	@SideOnly(Side.CLIENT)
	private IIcon overlayIcon;

	public TESItemConquestHorn() {
		setMaxStackSize(1);
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
	}

	public static ItemStack createHorn(TESInvasions type) {
		ItemStack itemstack = new ItemStack(TESItems.conquestHorn);
		setInvasionType(itemstack, type);
		return itemstack;
	}

	private static TESInvasions getInvasionType(ItemStack itemstack) {
		String s;
		TESInvasions invasionType = null;
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("InvasionType")) {
			s = itemstack.getTagCompound().getString("InvasionType");
			invasionType = TESInvasions.forName(s);
		}
		if (invasionType == null && itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("HornFaction")) {
			s = itemstack.getTagCompound().getString("HornFaction");
			invasionType = TESInvasions.forName(s);
		}

		return invasionType;
	}

	public static void setInvasionType(ItemStack itemstack, TESInvasions type) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setString("InvasionType", type.codeName());
	}

	private static boolean canUseHorn(ItemStack itemstack, World world, EntityPlayer entityplayer, boolean sendMessage) {
		TESInvasions invasionType = getInvasionType(itemstack);
		TESFaction invasionFaction = invasionType.getInvasionFaction();
		float alignmentRequired = 1000.0f;
		if (TESLevelData.getData(entityplayer).getAlignment(invasionFaction) >= alignmentRequired) {
			boolean blocked = TESBannerProtection.isProtected(world, entityplayer, TESBannerProtection.forFaction(invasionFaction), false);
			if (TESEntityNPCRespawner.isSpawnBlocked(entityplayer, invasionFaction)) {
				blocked = true;
			}
			if (blocked) {
				if (sendMessage && !world.isRemote) {
					entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.conquestHornProtected", invasionFaction.factionName()));
				}
				return false;
			}
			return true;
		}
		if (sendMessage && !world.isRemote) {
			TESAlignmentValues.notifyAlignmentNotHighEnough(entityplayer, alignmentRequired, invasionType.getInvasionFaction());
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		if (pass == 0) {
			TESFaction faction = getInvasionType(itemstack).getInvasionFaction();
			return faction.getFactionColor();
		}
		return 16777215;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int pass) {
		return pass > 0 ? overlayIcon : baseIcon;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {
		TESInvasions type = getInvasionType(itemstack);
		if (type != null) {
			return StatCollector.translateToLocal(type.codeNameHorn());
		}
		return super.getItemStackDisplayName(itemstack);
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
		for (TESInvasions type : TESInvasions.values()) {
			ItemStack itemstack = new ItemStack(item);
			setInvasionType(itemstack, type);
			list.add(itemstack);
		}
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		TESInvasions invasionType = getInvasionType(itemstack);
		if (canUseHorn(itemstack, world, entityplayer, true)) {
			if (!world.isRemote) {
				TESEntityInvasionSpawner invasion = new TESEntityInvasionSpawner(world);
				invasion.setInvasionType(invasionType);
				invasion.setWarhorn(true);
				invasion.setSpawnsPersistent(true);
				invasion.setLocationAndAngles(entityplayer.posX, entityplayer.posY + 3.0, entityplayer.posZ, 0.0f, 0.0f);
				world.spawnEntityInWorld(invasion);
				invasion.startInvasion(entityplayer);
			}
			if (!entityplayer.capabilities.isCreativeMode) {
				--itemstack.stackSize;
			}
		}
		return itemstack;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		canUseHorn(itemstack, world, entityplayer, false);
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		baseIcon = iconregister.registerIcon(getIconString() + "_base");
		overlayIcon = iconregister.registerIcon(getIconString() + "_overlay");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}