package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import java.util.List;

public class TESItemTurban extends TESItemRobes {
	@SideOnly(Side.CLIENT)
	private IIcon ornamentIcon;

	public TESItemTurban() {
		super(0);
	}

	public static boolean hasOrnament(ItemStack itemstack) {
		return itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("TurbanOrnament") && itemstack.getTagCompound().getBoolean("TurbanOrnament");
	}

	public static void setHasOrnament(ItemStack itemstack, boolean flag) {
		if (itemstack.getTagCompound() == null) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setBoolean("TurbanOrnament", flag);
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		super.addInformation(itemstack, entityplayer, list, flag);
		if (hasOrnament(itemstack)) {
			list.add(StatCollector.translateToLocal("item.tes.robes.ornament"));
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return "tes:textures/armor/robes_turban.png";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		if (pass == 1 && hasOrnament(itemstack)) {
			return 16777215;
		}
		return super.getColorFromItemStack(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int pass) {
		if (pass == 1 && hasOrnament(itemstack)) {
			return ornamentIcon;
		}
		return itemIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		super.registerIcons(iconregister);
		ornamentIcon = iconregister.registerIcon(getIconString() + "_ornament");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}