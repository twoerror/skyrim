package tes.common.enchant;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESDamage;
import tes.common.item.TESWeaponStats;
import tes.common.item.weapon.TESItemLegendaryWhip;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketWeaponFX;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;

public class TESEnchantmentWeaponSpecial extends TESEnchantment {
	private boolean compatibleOtherSpecial;

	@SuppressWarnings("unused")
	public TESEnchantmentWeaponSpecial(String s) {
		super(s, new TESEnchantmentType[]{TESEnchantmentType.MELEE, TESEnchantmentType.THROWING_AXE, TESEnchantmentType.RANGED_LAUNCHER});
		valueModifier = 3.0F;
		bypassAnvilLimit = true;
	}

	public static void doChillAttack(EntityLivingBase entity) {
		if (entity instanceof EntityPlayerMP) {
			TESDamage.doFrostDamage((EntityPlayerMP) entity);
		}
		int duration = 5;
		entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, duration * 20, 1));

		IMessage packet = new TESPacketWeaponFX(TESPacketWeaponFX.Type.CHILLING, entity);
		TESPacketHandler.NETWORK_WRAPPER.sendToAllAround(packet, TESPacketHandler.nearEntity(entity, 64.0D));
	}

	@Override
	public boolean canApply(ItemStack itemstack, boolean considering) {
		if (super.canApply(itemstack, considering)) {
			Item item = itemstack.getItem();
			return !(item instanceof TESItemLegendaryWhip) || this != FIRE && this != CHILL;
		}
		return false;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		if (TESWeaponStats.isMeleeWeapon(itemstack)) {
			return StatCollector.translateToLocalFormatted("tes.enchant." + enchantName + ".desc.melee");
		}
		return StatCollector.translateToLocalFormatted("tes.enchant." + enchantName + ".desc.ranged");
	}

	@Override
	public boolean isBeneficial() {
		return true;
	}

	@Override
	public boolean isCompatibleWith(TESEnchantment other) {
		return compatibleOtherSpecial || !(other instanceof TESEnchantmentWeaponSpecial) || ((TESEnchantmentWeaponSpecial) other).compatibleOtherSpecial;
	}

	@SuppressWarnings("unused")
	public TESEnchantmentWeaponSpecial setCompatibleOtherSpecial(boolean compatibleOtherSpecial) {
		this.compatibleOtherSpecial = compatibleOtherSpecial;
		return this;
	}
}