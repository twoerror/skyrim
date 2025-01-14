package tes.common.item.weapon;

import com.google.common.collect.Multimap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import tes.common.item.TESMaterialFinder;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.UUID;

public class TESItemSword extends ItemSword implements TESMaterialFinder {
	private final ToolMaterial tesMaterial;

	protected HitEffect effect;
	protected float tesWeaponDamage;

	private boolean isGlowing;

	@SideOnly(Side.CLIENT)
	private IIcon glowingIcon;

	public TESItemSword(Item.ToolMaterial material) {
		super(material);
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		tesWeaponDamage = material.getDamageVsEntity() + 4.0f;
		tesMaterial = material;
	}

	protected TESItemSword(Item.ToolMaterial material, HitEffect e) {
		this(material);
		effect = e;
	}

	public static UUID accessWeaponDamageModifier() {
		return field_111210_e;
	}

	public static void applyStandardFire(EntityLivingBase entity) {
		EnumDifficulty difficulty = entity.worldObj.difficultySetting;
		int duration = 1 + difficulty.getDifficultyId() * 10;
		entity.setFire(duration);
	}

	public static void applyStandardPoison(EntityLivingBase entity) {
		EnumDifficulty difficulty = entity.worldObj.difficultySetting;
		int duration = 1 + difficulty.getDifficultyId() * 2;
		PotionEffect poison = new PotionEffect(Potion.poison.id, (duration + itemRand.nextInt(duration)) * 20);
		entity.addPotionEffect(poison);
	}

	public static void applyStandardWither(EntityLivingBase entity) {
		EnumDifficulty difficulty = entity.worldObj.difficultySetting;
		int duration = 1 + difficulty.getDifficultyId() * 2;
		PotionEffect poison = new PotionEffect(Potion.wither.id, (duration + itemRand.nextInt(duration)) * 20);
		entity.addPotionEffect(poison);
	}

	protected float getTESWeaponDamage() {
		return tesWeaponDamage;
	}

	public HitEffect getHitEffect() {
		return effect;
	}

	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers() {
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers();
		multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "TES Weapon modifier", tesWeaponDamage, 0));
		return multimap;
	}

	@Override
	public ToolMaterial getMaterial() {
		return tesMaterial;
	}

	@Override
	public boolean hitEntity(ItemStack itemstack, EntityLivingBase hitEntity, EntityLivingBase user) {
		itemstack.damageItem(1, user);
		if (effect == HitEffect.NONE) {
			return true;
		}
		if (effect == HitEffect.POISON) {
			applyStandardPoison(hitEntity);
		}
		if (effect == HitEffect.FIRE) {
			applyStandardFire(hitEntity);
		}
		return true;
	}

	public boolean isGlowing() {
		return isGlowing;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (getItemUseAction(itemstack) == EnumAction.none) {
			return itemstack;
		}
		return super.onItemRightClick(itemstack, world, entityplayer);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		super.registerIcons(iconregister);
		if (isGlowing) {
			glowingIcon = iconregister.registerIcon(getIconString() + "_glowing");
		}
	}

	public TESItemSword setIsGlowing() {
		isGlowing = true;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getGlowingIcon() {
		return glowingIcon;
	}

	public enum HitEffect {
		NONE, FIRE, POISON
	}
}