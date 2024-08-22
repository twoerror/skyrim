package tes.common.item;

import tes.common.enchant.TESEnchantmentHelper;
import tes.common.item.weapon.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ISpecialArmor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TESWeaponStats {
	public static final float MAX_MODIFIABLE_REACH = 2.0f;
	public static final float MAX_MODIFIABLE_SPEED = 1.6f;
	public static final int MAX_MODIFIABLE_KNOCKBACK = 2;

	private static final int BASE_PLAYER_MELEE_TIME = 15;

	private static final Map<Object, Float> MELEE_SPEED = new HashMap<>();
	private static final Map<Object, Float> MELEE_REACH = new HashMap<>();
	private static final Map<Object, Integer> MELEE_EXTRA_KNOCKBACK = new HashMap<>();

	static {
		registerMeleeSpeed(TESItemGreatsword.class, 0.667f);
		registerMeleeSpeed(TESItemLongsword.class, 0.8f);
		registerMeleeReach(TESItemGreatsword.class, 1.5f);
		registerMeleeReach(TESItemLongsword.class, 1.25f);
		registerMeleeSpeed(TESItemDagger.class, 1.5f);
		registerMeleeSpeed(TESItemSpear.class, 0.833f);
		registerMeleeSpeed(TESItemPolearm.class, 0.667f);
		registerMeleeSpeed(TESItemPolearmLong.class, 0.5f);
		registerMeleeSpeed(TESItemLance.class, 0.5f);
		registerMeleeSpeed(TESItemBattleaxe.class, 0.75f);
		registerMeleeSpeed(TESItemHammer.class, 0.667f);
		registerMeleeReach(TESItemDagger.class, 0.75f);
		registerMeleeReach(TESItemSpear.class, 1.5f);
		registerMeleeReach(TESItemPolearm.class, 1.5f);
		registerMeleeReach(TESItemPolearmLong.class, 2.0f);
		registerMeleeReach(TESItemLance.class, 2.0f);
		registerMeleeExtraKnockback(TESItemHammer.class, 1);
		registerMeleeExtraKnockback(TESItemLance.class, 1);
	}

	private TESWeaponStats() {
	}

	public static int getArmorProtection(ItemStack itemstack) {
		Item item;
		if (itemstack != null && (item = itemstack.getItem()) instanceof ItemArmor) {
			ItemArmor armor = (ItemArmor) item;
			int i = armor.damageReduceAmount;
			return i + TESEnchantmentHelper.calcCommonArmorProtection(itemstack);
		}
		return 0;
	}

	public static int getAttackTimeMob(ItemStack itemstack) {
		int baseMobMeleeTime = 20;
		return getAttackTimeWithBase(itemstack, baseMobMeleeTime);
	}

	public static int getAttackTimePlayer(ItemStack itemstack) {
		return getAttackTimeWithBase(itemstack, BASE_PLAYER_MELEE_TIME);
	}

	public static int getAttackTimeWithBase(ItemStack itemstack, int baseTime) {
		float time = baseTime;
		Float factor = (Float) getClassOrItemProperty(itemstack, MELEE_SPEED);
		if (factor != null) {
			time /= factor;
		}
		time /= TESEnchantmentHelper.calcMeleeSpeedFactor(itemstack);
		time = Math.max(time, 1.0f);
		return Math.round(time);
	}

	public static int getBaseExtraKnockback(ItemStack itemstack) {
		Integer extra = (Integer) getClassOrItemProperty(itemstack, MELEE_EXTRA_KNOCKBACK);
		if (extra != null) {
			return extra;
		}
		return 0;
	}

	private static Object getClassOrItemProperty(ItemStack itemstack, Map<?, ?> propertyMap) {
		if (itemstack != null) {
			Item item = itemstack.getItem();
			if (propertyMap.containsKey(item)) {
				return propertyMap.get(item);
			}
			Class<?> itemClass = item.getClass();
			do {
				if (propertyMap.containsKey(itemClass)) {
					return propertyMap.get(itemClass);
				}
				if (itemClass == Item.class) {
					break;
				}
				itemClass = itemClass.getSuperclass();
			} while (true);
		}
		return null;
	}

	public static float getMeleeDamageBonus(ItemStack itemstack) {
		Collection<AttributeModifier> weaponAttributes;
		float damage = 0.0f;
		if (itemstack != null && (weaponAttributes = itemstack.getAttributeModifiers().values()) != null) {
			for (AttributeModifier mod : weaponAttributes) {
				if (mod.getID() == TESItemSword.accessWeaponDamageModifier()) {
					damage = (float) (damage + mod.getAmount());
					damage += EnchantmentHelper.func_152377_a(itemstack, EnumCreatureAttribute.UNDEFINED);
				}
			}
		}
		return damage;
	}

	public static float getMeleeReachDistance(EntityPlayer entityplayer) {
		float reach = 3.0f;
		reach *= getMeleeReachFactor(entityplayer.getHeldItem());
		if (entityplayer.capabilities.isCreativeMode) {
			return (float) (reach + 3.0);
		}
		return reach;
	}

	public static float getMeleeReachFactor(ItemStack itemstack) {
		float reach = 1.0f;
		Float factor = (Float) getClassOrItemProperty(itemstack, MELEE_REACH);
		if (factor != null) {
			reach *= factor;
		}
		return reach * TESEnchantmentHelper.calcMeleeReachFactor(itemstack);
	}

	public static float getMeleeSpeed(ItemStack itemstack) {
		int base = BASE_PLAYER_MELEE_TIME;
		return 1.0f / ((float) getAttackTimeWithBase(itemstack, base) / base);
	}

	public static float getRangedDamageFactor(ItemStack itemstack, boolean launchSpeedOnly) {
		float baseArrowFactor = 2.0f;
		float weaponFactor = 0.0f;
		if (itemstack != null) {
			Item item = itemstack.getItem();
			if (item instanceof TESItemCrossbow) {
				weaponFactor = baseArrowFactor * (float) ((TESItemCrossbow) item).getBoltDamageFactor();
				weaponFactor *= TESEnchantmentHelper.calcRangedDamageFactor(itemstack);
				if (!launchSpeedOnly) {
					int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemstack);
					if (power > 0) {
						weaponFactor = (float) (weaponFactor + (power * 0.5 + 0.5));
					}
					weaponFactor *= 2.0f;
				}
			} else if (item instanceof ItemBow) {
				int power;
				weaponFactor = baseArrowFactor;
				if (item instanceof TESItemBow) {
					weaponFactor *= (float) ((TESItemBow) item).getArrowDamageFactor();
				}
				weaponFactor *= TESEnchantmentHelper.calcRangedDamageFactor(itemstack);
				if (!launchSpeedOnly && (power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemstack)) > 0) {
					weaponFactor = (float) (weaponFactor + (power * 0.5 + 0.5));
				}
			} else if (item instanceof TESItemSarbacane) {
				weaponFactor = baseArrowFactor;
				if (!launchSpeedOnly) {
					weaponFactor *= 1.0f / baseArrowFactor;
				}
				weaponFactor *= TESEnchantmentHelper.calcRangedDamageFactor(itemstack);
			} else if (item instanceof TESItemSpear) {
				weaponFactor = ((TESItemSpear) item).getRangedDamageMultiplier(itemstack, null, null);
			} else if (item instanceof TESItemThrowingAxe) {
				weaponFactor = ((TESItemThrowingAxe) item).getRangedDamageMultiplier(itemstack, null, null);
			}
		}
		if (weaponFactor > 0.0f) {
			return weaponFactor / baseArrowFactor;
		}
		return 0.0f;
	}

	public static int getRangedKnockback(ItemStack itemstack) {
		if (isMeleeWeapon(itemstack) || itemstack != null && itemstack.getItem() instanceof TESItemThrowingAxe) {
			return getTotalKnockback(itemstack);
		}
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemstack) + TESEnchantmentHelper.calcRangedKnockback(itemstack);
	}

	public static float getRangedSpeed(ItemStack itemstack) {
		int base = 20;
		int time = 0;
		if (itemstack != null) {
			Item item = itemstack.getItem();
			if (item instanceof TESItemCrossbow) {
				time = 50;
			} else if (item instanceof TESItemBow) {
				time = ((TESItemBow) item).getMaxDrawTime();
			} else if (item == Items.bow) {
				time = 20;
			}
			if (item instanceof TESItemSpear) {
				time = 20;
			}
			if (item instanceof TESItemSarbacane) {
				time = 5;
			}
		}
		if (time > 0) {
			return 1.0f / ((float) time / base);
		}
		return 0.0f;
	}

	@SuppressWarnings("InstanceofIncompatibleInterface")
	public static int getTotalArmorValue(EntityPlayer entityplayer) {
		int protection = 0;
		for (int i = 0; i < entityplayer.inventory.armorInventory.length; ++i) {
			ItemStack stack = entityplayer.inventory.armorInventory[i];
			if (stack != null && stack.getItem() instanceof ISpecialArmor) {
				protection += ((ISpecialArmor) stack.getItem()).getArmorDisplay(entityplayer, stack, i);
				continue;
			}
			if (stack == null || !(stack.getItem() instanceof ItemArmor)) {
				continue;
			}
			protection += getArmorProtection(stack);
		}
		return protection;
	}

	public static int getTotalKnockback(ItemStack itemstack) {
		return EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, itemstack) + getBaseExtraKnockback(itemstack) + TESEnchantmentHelper.calcExtraKnockback(itemstack);
	}

	public static boolean isMeleeWeapon(ItemStack itemstack) {
		Collection<AttributeModifier> weaponAttributes;
		if (itemstack != null && (weaponAttributes = itemstack.getAttributeModifiers().values()) != null) {
			for (AttributeModifier mod : weaponAttributes) {
				if (mod.getID() == TESItemSword.accessWeaponDamageModifier()) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isPoisoned(ItemStack itemstack) {
		if (itemstack != null) {
			Item item = itemstack.getItem();
			return item instanceof TESItemDagger && ((TESItemSword) item).getHitEffect() == TESItemSword.HitEffect.POISON;
		}
		return false;
	}

	public static boolean isRangedWeapon(ItemStack itemstack) {
		if (itemstack != null) {
			Item item = itemstack.getItem();
			return item instanceof ItemBow || item instanceof TESItemSpear || item instanceof TESItemSarbacane || item instanceof TESItemThrowingAxe;
		}
		return false;
	}

	private static void registerMeleeExtraKnockback(Object obj, int i) {
		MELEE_EXTRA_KNOCKBACK.put(obj, i);
	}

	private static void registerMeleeReach(Object obj, float f) {
		MELEE_REACH.put(obj, f);
	}

	private static void registerMeleeSpeed(Object obj, float f) {
		MELEE_SPEED.put(obj, f);
	}
}