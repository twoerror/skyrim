package tes.common;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.network.TESPacketEnvironmentOverlay;
import tes.common.network.TESPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;

public class TESDamage {
	public static final DamageSource FROST = new DamageSource("tes.frost").setDamageBypassesArmor();
	public static final DamageSource POISON_DRINK = new DamageSource("tes.poisonDrink").setDamageBypassesArmor().setMagicDamage();
	public static final DamageSource PLANT_HURT = new DamageSource("tes.plantHurt").setDamageBypassesArmor();

	private TESDamage() {
	}

	public static void doBurnDamage(EntityPlayerMP entityplayer) {
		IMessage packet = new TESPacketEnvironmentOverlay(TESPacketEnvironmentOverlay.Overlay.BURN);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	public static void doFrostDamage(EntityPlayerMP entityplayer) {
		IMessage packet = new TESPacketEnvironmentOverlay(TESPacketEnvironmentOverlay.Overlay.FROST);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}
}