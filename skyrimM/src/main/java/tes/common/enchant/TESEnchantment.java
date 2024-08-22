package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.text.DecimalFormat;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class TESEnchantment {
	public static final Collection<TESEnchantment> CONTENT = new ArrayList<>();
	public static final Map<String, TESEnchantment> ENCHANTS_BY_NAME = new HashMap<>();

	public static final TESEnchantment STRONG_1 = new TESEnchantmentDamage("strong1", 0.5F).setEnchantWeight(10);
	public static final TESEnchantment STRONG_2 = new TESEnchantmentDamage("strong2", 1.0F).setEnchantWeight(5);
	public static final TESEnchantment STRONG_3 = new TESEnchantmentDamage("strong3", 2.0F).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment STRONG_4 = new TESEnchantmentDamage("strong4", 3.0F).setEnchantWeight(1).setSkilful(true);
	public static final TESEnchantment WEAK_1 = new TESEnchantmentDamage("weak1", -0.5F).setEnchantWeight(6);
	public static final TESEnchantment WEAK_2 = new TESEnchantmentDamage("weak2", -1.0F).setEnchantWeight(4);
	public static final TESEnchantment WEAK_3 = new TESEnchantmentDamage("weak3", -2.0F).setEnchantWeight(2);
	public static final TESEnchantment DURABLE_1 = new TESEnchantmentDurability("durable1", 1.25F).setEnchantWeight(15);
	public static final TESEnchantment DURABLE_2 = new TESEnchantmentDurability("durable2", 1.5F).setEnchantWeight(8);
	public static final TESEnchantment DURABLE_3 = new TESEnchantmentDurability("durable3", 2.0F).setEnchantWeight(4).setSkilful(true);
	public static final TESEnchantment MELEE_SPEED_1 = new TESEnchantmentMeleeSpeed("meleeSpeed1", 1.25F).setEnchantWeight(6);
	public static final TESEnchantment MELEE_SLOW_1 = new TESEnchantmentMeleeSpeed("meleeSlow1", 0.75F).setEnchantWeight(4);
	public static final TESEnchantment MELEE_REACH_1 = new TESEnchantmentMeleeReach("meleeReach1", 1.25F).setEnchantWeight(6);
	public static final TESEnchantment MELEE_UNREACH_1 = new TESEnchantmentMeleeReach("meleeUnreach1", 0.75F).setEnchantWeight(4);
	public static final TESEnchantment KNOCKBACK_1 = new TESEnchantmentKnockback("knockback1", 1).setEnchantWeight(6);
	public static final TESEnchantment KNOCKBACK_2 = new TESEnchantmentKnockback("knockback2", 2).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment TOOL_SPEED_1 = new TESEnchantmentToolSpeed("toolSpeed1", 1.5F).setEnchantWeight(20);
	public static final TESEnchantment TOOL_SPEED_2 = new TESEnchantmentToolSpeed("toolSpeed2", 2.0F).setEnchantWeight(10);
	public static final TESEnchantment TOOL_SPEED_3 = new TESEnchantmentToolSpeed("toolSpeed3", 3.0F).setEnchantWeight(5).setSkilful(true);
	public static final TESEnchantment TOOL_SPEED_4 = new TESEnchantmentToolSpeed("toolSpeed4", 4.0F).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment TOOL_SLOW_1 = new TESEnchantmentToolSpeed("toolSlow1", 0.75F).setEnchantWeight(10);
	public static final TESEnchantment TOOL_SILK = new TESEnchantmentSilkTouch("toolSilk").setEnchantWeight(10).setSkilful(true);
	public static final TESEnchantment LOOTING_1 = new TESEnchantmentLooting("looting1", 1).setEnchantWeight(6);
	public static final TESEnchantment LOOTING_2 = new TESEnchantmentLooting("looting2", 2).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment LOOTING_3 = new TESEnchantmentLooting("looting3", 3).setEnchantWeight(1).setSkilful(true);
	public static final TESEnchantment PROTECT_1 = new TESEnchantmentProtection("protect1", 1).setEnchantWeight(10);
	public static final TESEnchantment PROTECT_2 = new TESEnchantmentProtection("protect2", 2).setEnchantWeight(3).setSkilful(true);
	public static final TESEnchantment PROTECT_WEAK_1 = new TESEnchantmentProtection("protectWeak1", -1).setEnchantWeight(5);
	public static final TESEnchantment PROTECT_WEAK_2 = new TESEnchantmentProtection("protectWeak2", -2).setEnchantWeight(2);
	public static final TESEnchantment PROTECT_FIRE_1 = new TESEnchantmentProtectionFire("protectFire1", 1).setEnchantWeight(5);
	public static final TESEnchantment PROTECT_FIRE_2 = new TESEnchantmentProtectionFire("protectFire2", 2).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment PROTECT_FIRE_3 = new TESEnchantmentProtectionFire("protectFire3", 3).setEnchantWeight(1).setSkilful(true);
	public static final TESEnchantment PROTECT_FALL_1 = new TESEnchantmentProtectionFall("protectFall1", 1).setEnchantWeight(5);
	public static final TESEnchantment PROTECT_FALL_2 = new TESEnchantmentProtectionFall("protectFall2", 2).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment PROTECT_FALL_3 = new TESEnchantmentProtectionFall("protectFall3", 3).setEnchantWeight(1).setSkilful(true);
	public static final TESEnchantment PROTECT_RANGED_1 = new TESEnchantmentProtectionRanged("protectRanged1", 1).setEnchantWeight(5);
	public static final TESEnchantment PROTECT_RANGED_2 = new TESEnchantmentProtectionRanged("protectRanged2", 2).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment PROTECT_RANGED_3 = new TESEnchantmentProtectionRanged("protectRanged3", 3).setEnchantWeight(1).setSkilful(true);
	public static final TESEnchantment RANGED_STRONG_1 = new TESEnchantmentRangedDamage("rangedStrong1", 1.1F).setEnchantWeight(10);
	public static final TESEnchantment RANGED_STRONG_2 = new TESEnchantmentRangedDamage("rangedStrong2", 1.2F).setEnchantWeight(3);
	public static final TESEnchantment RANGED_STRONG_3 = new TESEnchantmentRangedDamage("rangedStrong3", 1.3F).setEnchantWeight(1).setSkilful(true);
	public static final TESEnchantment RANGED_WEAK_1 = new TESEnchantmentRangedDamage("rangedWeak1", 0.75F).setEnchantWeight(8);
	public static final TESEnchantment RANGED_WEAK_2 = new TESEnchantmentRangedDamage("rangedWeak2", 0.5F).setEnchantWeight(3);
	public static final TESEnchantment RANGED_KNOCKBACK_1 = new TESEnchantmentRangedKnockback("rangedKnockback1", 1).setEnchantWeight(6);
	public static final TESEnchantment RANGED_KNOCKBACK_2 = new TESEnchantmentRangedKnockback("rangedKnockback2", 2).setEnchantWeight(2).setSkilful(true);
	public static final TESEnchantment FIRE = new TESEnchantmentWeaponSpecial("fire").setEnchantWeight(0).setApplyToProjectile(true);
	public static final TESEnchantment CHILL = new TESEnchantmentWeaponSpecial("chill").setEnchantWeight(0).setApplyToProjectile(true);
	public static final TESEnchantment HEADHUNTING = new TESEnchantmentWeaponSpecial("headhunting").setCompatibleOtherSpecial(true).setEnchantWeight(0).setApplyToProjectile(true);

	private final List<TESEnchantmentType> itemTypes;

	protected String enchantName;
	protected float valueModifier = 1.0F;
	protected boolean bypassAnvilLimit;

	private int enchantWeight;
	private boolean skilful;
	private boolean applyToProjectile;

	protected TESEnchantment(String s, TESEnchantmentType type) {
		this(s, new TESEnchantmentType[]{type});
	}

	protected TESEnchantment(String s, TESEnchantmentType[] types) {
		enchantName = s;
		itemTypes = Arrays.asList(types);
		CONTENT.add(this);
		ENCHANTS_BY_NAME.put(enchantName, this);
	}

	public static TESEnchantment getEnchantmentByName(String s) {
		return ENCHANTS_BY_NAME.get(s);
	}

	protected static String formatAdditive(float f) {
		String s = formatDecimalNumber(f);
		if (f >= 0.0F) {
			return '+' + s;
		}
		return s;
	}

	protected static String formatAdditiveInt(int i) {
		String s = String.valueOf(i);
		if (i >= 0) {
			return '+' + s;
		}
		return s;
	}

	protected static String formatDecimalNumber(float f) {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(1);
		return df.format(f);
	}

	protected static String formatMultiplicative(float f) {
		String s = formatDecimalNumber(f);
		return 'x' + s;
	}

	public boolean getApplyToProjectile() {
		return applyToProjectile;
	}

	protected TESEnchantment setApplyToProjectile(boolean applyToProjectile) {
		this.applyToProjectile = applyToProjectile;
		return this;
	}

	public boolean getBypassAnvilLimit() {
		return bypassAnvilLimit;
	}

	public boolean canApply(ItemStack itemstack, boolean considering) {
		for (TESEnchantmentType type : itemTypes) {
			if (type.canApply(itemstack)) {
				return true;
			}
		}
		return false;
	}

	protected abstract String getDescription(ItemStack paramItemStack);

	public String getDisplayName() {
		return StatCollector.translateToLocal("tes.enchant." + enchantName);
	}

	public int getEnchantWeight() {
		return enchantWeight;
	}

	protected TESEnchantment setEnchantWeight(int enchantWeight) {
		this.enchantWeight = enchantWeight;
		return this;
	}

	public String getNamedFormattedDescription(ItemStack itemstack) {
		String s = StatCollector.translateToLocalFormatted("tes.enchant.descFormat", getDisplayName(), getDescription(itemstack));
		if (isBeneficial()) {
			return EnumChatFormatting.GRAY + s;
		}
		return EnumChatFormatting.DARK_GRAY + s;
	}

	public boolean hasTemplateItem() {
		return enchantWeight > 0 && isBeneficial();
	}

	public abstract boolean isBeneficial();

	public boolean isCompatibleWith(TESEnchantment other) {
		return getClass() != other.getClass();
	}

	public boolean getSkilful() {
		return skilful;
	}

	protected TESEnchantment setSkilful(boolean skilful) {
		this.skilful = skilful;
		return this;
	}

	public float getValueModifier() {
		return valueModifier;
	}

	public String getEnchantName() {
		return enchantName;
	}

	public List<TESEnchantmentType> getItemTypes() {
		return itemTypes;
	}
}