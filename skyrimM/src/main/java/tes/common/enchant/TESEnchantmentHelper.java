package tes.common.enchant;

import com.google.common.collect.Lists;
import tes.common.TESConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.WeightedRandom;

import java.util.*;

public class TESEnchantmentHelper {
	private static final Map<UUID, ItemStack[]> LAST_KNOWN_PLAYER_INVENTORIES = new HashMap<>();

	private static Random backupRand;

	private TESEnchantmentHelper() {
	}

	public static void applyRandomEnchantments(ItemStack itemstack, Random random, boolean skilful, boolean keepBanes) {
		if (keepBanes) {
			List<TESEnchantment> list = getEnchantList(itemstack);
			for (TESEnchantment ench : list) {
				removeEnchant(itemstack, ench);
			}
		} else {
			clearEnchantsAndProgress(itemstack);
		}
		int enchants = 0;
		float chance = random.nextFloat();
		if (skilful) {
			if (chance < 0.15F) {
				enchants = 2;
			} else if (chance < 0.8F) {
				enchants = 1;

			}
		} else if (chance < 0.1F) {
			enchants = 2;
		} else if (chance < 0.65F) {
			enchants = 1;
		}
		Collection<WeightedRandomEnchant> applicable = new ArrayList<>();
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (ench.canApply(itemstack, true) && (!ench.getSkilful() || skilful)) {
				int weight = ench.getEnchantWeight();
				if (weight > 0) {
					if (skilful) {
						weight = getSkilfulWeight(ench);
					} else {
						weight *= 100;
					}
					if (weight > 0 && itemstack.getItem() instanceof ItemTool && !ench.getItemTypes().contains(TESEnchantmentType.TOOL) && !ench.getItemTypes().contains(TESEnchantmentType.BREAKABLE)) {
						weight /= 3;
						weight = Math.max(weight, 1);
					}
					WeightedRandomEnchant wre = new WeightedRandomEnchant(ench, weight);
					applicable.add(wre);
				}
			}
		}
		if (!applicable.isEmpty()) {
			Collection<TESEnchantment> chosenEnchants = new ArrayList<>();

			for (int l = 0; l < enchants; l++) {
				if (applicable.isEmpty()) {
					break;
				}
				WeightedRandomEnchant chosenWre = (WeightedRandomEnchant) WeightedRandom.getRandomItem(random, applicable);
				TESEnchantment chosenEnch = chosenWre.getTheEnchant();
				chosenEnchants.add(chosenEnch);

				applicable.remove(chosenWre);

				Collection<WeightedRandomEnchant> nowIncompatibles = new ArrayList<>();
				for (WeightedRandomEnchant wre : applicable) {
					TESEnchantment otherEnch = wre.getTheEnchant();
					if (!otherEnch.isCompatibleWith(chosenEnch)) {
						nowIncompatibles.add(wre);
					}
				}
				applicable.removeAll(nowIncompatibles);
			}
			for (TESEnchantment ench : chosenEnchants) {
				if (ench.canApply(itemstack, false)) {
					setHasEnchant(itemstack, ench);
				}
			}
		}
		if (!getEnchantList(itemstack).isEmpty() || canApplyAnyEnchant(itemstack)) {
			setAppliedRandomEnchants(itemstack);
		}
	}

	public static float calcBaseMeleeDamageBoost(ItemStack itemstack) {
		float damage = 0.0F;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentDamage) {
					damage += ((TESEnchantmentDamage) ench).getBaseDamageBoost();
				}
			}
		}
		return damage;
	}

	public static int calcCommonArmorProtection(ItemStack itemstack) {
		int protection = 0;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentProtection) {
					protection += ((TESEnchantmentProtection) ench).getProtectLevel();
				}
			}
		}
		return protection;
	}

	public static int calcExtraKnockback(ItemStack itemstack) {
		int kb = 0;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentKnockback) {
					kb += ((TESEnchantmentKnockback) ench).getKnockback();
				}
			}
		}
		return kb;
	}

	public static int calcFireAspect(ItemStack itemstack) {
		int fire = 0;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench == TESEnchantment.FIRE) {
					fire += 2;
				}
			}
		}
		return fire;
	}

	public static int calcFireAspectForMelee(ItemStack itemstack) {
		if (itemstack != null && TESEnchantmentType.MELEE.canApply(itemstack)) {
			return calcFireAspect(itemstack);
		}
		return 0;
	}

	public static int calcLootingLevel(ItemStack itemstack) {
		int looting = 0;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentLooting) {
					looting += ((TESEnchantmentLooting) ench).getLootLevel();
				}
			}
		}
		return looting;
	}

	public static float calcMeleeReachFactor(ItemStack itemstack) {
		float reach = 1.0F;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentMeleeReach) {
					reach *= ((TESEnchantmentMeleeReach) ench).getReachFactor();
				}
			}
		}
		return reach;
	}

	public static float calcMeleeSpeedFactor(ItemStack itemstack) {
		float speed = 1.0F;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentMeleeSpeed) {
					speed *= ((TESEnchantmentMeleeSpeed) ench).getSpeedFactor();
				}
			}
		}
		return speed;
	}

	public static float calcRangedDamageFactor(ItemStack itemstack) {
		float damage = 1.0F;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentRangedDamage) {
					damage *= ((TESEnchantmentRangedDamage) ench).getDamageFactor();
				}
			}
		}
		return damage;
	}

	public static int calcRangedKnockback(ItemStack itemstack) {
		int kb = 0;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentRangedKnockback) {
					kb += ((TESEnchantmentRangedKnockback) ench).getKnockback();
				}
			}
		}
		return kb;
	}

	public static int calcSpecialArmorSetProtection(ItemStack[] armor, DamageSource source) {
		int protection = 0;

		if (armor != null) {
			for (ItemStack itemstack : armor) {
				if (itemstack != null) {
					List<TESEnchantment> enchants = getEnchantList(itemstack);
					for (TESEnchantment ench : enchants) {
						if (ench instanceof TESEnchantmentProtectionSpecial) {
							protection += ((TESEnchantmentProtectionSpecial) ench).calcSpecialProtection(source);
						}
					}
				}
			}
		}
		return protection;
	}

	public static float calcToolEfficiency(ItemStack itemstack) {
		float speed = 1.0F;

		if (itemstack != null) {
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentToolSpeed) {
					speed *= ((TESEnchantmentToolSpeed) ench).getSpeedFactor();
				}
			}
		}
		return speed;
	}

	public static float calcTradeValueFactor(ItemStack itemstack) {
		float value = 1.0F;

		List<TESEnchantment> enchants = getEnchantList(itemstack);
		for (TESEnchantment ench : enchants) {
			value *= ench.getValueModifier();
			if (ench.getSkilful()) {
				value *= 1.5F;
			}
		}
		return value;
	}

	private static boolean canApplyAnyEnchant(ItemStack itemstack) {
		for (TESEnchantment ench : TESEnchantment.CONTENT) {
			if (ench.canApply(itemstack, true)) {
				return true;
			}
		}
		return false;
	}

	public static boolean checkEnchantCompatible(ItemStack itemstack, TESEnchantment ench) {
		List<TESEnchantment> enchants = getEnchantList(itemstack);
		for (TESEnchantment itemEnch : enchants) {
			if (!itemEnch.isCompatibleWith(ench) || !ench.isCompatibleWith(itemEnch)) {
				return false;
			}
		}
		return true;
	}

	private static void clearEnchants(ItemStack itemstack) {
		NBTTagCompound itemData = itemstack.getTagCompound();
		if (itemData != null && itemData.hasKey("TESEnch")) {
			itemData.removeTag("TESEnch");
		}
	}

	public static void clearEnchantsAndProgress(ItemStack itemstack) {
		clearEnchants(itemstack);
		NBTTagCompound itemData = itemstack.getTagCompound();
		if (itemData != null && itemData.hasKey("TESEnchProgress")) {
			itemData.removeTag("TESEnchProgress");
		}
	}

	public static int getAnvilCost(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		if (nbt != null && nbt.hasKey("TESRepairCost")) {
			return nbt.getInteger("TESRepairCost");
		}
		return 0;
	}

	public static List<TESEnchantment> getEnchantList(ItemStack itemstack) {
		List<TESEnchantment> enchants = new ArrayList<>();

		NBTTagList tags = getItemEnchantTags(itemstack, false);
		if (tags != null) {
			for (int i = 0; i < tags.tagCount(); i++) {
				String s = tags.getStringTagAt(i);
				TESEnchantment ench = TESEnchantment.getEnchantmentByName(s);
				if (ench != null) {
					enchants.add(ench);
				}
			}
		}
		return enchants;
	}

	private static NBTTagList getEntityEnchantTags(Entity entity, boolean create) {
		NBTTagCompound data = entity.getEntityData();
		NBTTagList tags = null;
		if (data != null && data.hasKey("TESEnchEntity")) {
			return data.getTagList("TESEnchEntity", 8);
		}
		if (create) {
			tags = new NBTTagList();
			data.setTag("TESEnchEntity", tags);
		}
		return tags;
	}

	public static String getFullEnchantedName(ItemStack itemstack, String name) {
		String name1 = name;
		List<TESEnchantment> enchants = getEnchantList(itemstack);
		enchants = Lists.reverse(enchants);
		for (TESEnchantment ench : enchants) {
			name1 = StatCollector.translateToLocalFormatted("tes.enchant.nameFormat", name1, EnumChatFormatting.GOLD + ('[' + ench.getDisplayName() + ']'));
		}
		return name1;
	}

	private static NBTTagList getItemEnchantTags(ItemStack itemstack, boolean create) {
		NBTTagCompound itemData = itemstack.getTagCompound();
		NBTTagList tags = null;
		if (itemData != null && itemData.hasKey("TESEnch")) {
			return itemData.getTagList("TESEnch", 8);
		}
		if (create) {
			if (itemData == null) {
				itemData = new NBTTagCompound();
				itemstack.setTagCompound(itemData);
			}
			tags = new NBTTagList();
			itemData.setTag("TESEnch", tags);
		}
		return tags;
	}

	public static int getMaxFireProtectionLevel(ItemStack[] armor) {
		int max = 0;

		if (armor != null) {
			for (ItemStack itemstack : armor) {
				if (itemstack != null) {
					List<TESEnchantment> enchants = getEnchantList(itemstack);
					for (TESEnchantment ench : enchants) {
						if (ench instanceof TESEnchantmentProtectionFire) {
							int protection = ((TESEnchantmentProtectionSpecial) ench).getProtectLevel();
							if (protection > max) {
								max = protection;
							}
						}
					}
				}
			}
		}
		return max;
	}

	public static int getSkilfulWeight(TESEnchantment ench) {
		int weight = ench.getEnchantWeight();
		double wd = weight;
		if (ench.isBeneficial()) {
			wd = Math.pow(wd, 0.3D);
		}
		wd *= 100.0D;

		if (!ench.isBeneficial()) {
			wd *= 0.15D;
		}
		weight = (int) Math.round(wd);
		return Math.max(weight, 1);
	}

	private static boolean hasAppliedRandomEnchants(ItemStack itemstack) {
		NBTTagCompound nbt = itemstack.getTagCompound();
		return nbt != null && nbt.hasKey("TESRandomEnch") && nbt.getBoolean("TESRandomEnch");
	}

	public static boolean hasEnchant(ItemStack itemstack, TESEnchantment ench) {
		NBTTagList tags = getItemEnchantTags(itemstack, false);
		if (tags != null) {
			for (int i = 0; i < tags.tagCount(); i++) {
				String s = tags.getStringTagAt(i);
				if (s.equals(ench.getEnchantName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasMeleeOrRangedEnchant(DamageSource source, TESEnchantment ench) {
		Entity attacker = source.getEntity();
		Entity sourceEntity = source.getSourceOfDamage();

		if (attacker instanceof EntityLivingBase) {
			EntityLivingBase attackerLiving = (EntityLivingBase) attacker;
			if (attackerLiving == sourceEntity) {
				ItemStack weapon = attackerLiving.getHeldItem();
				if (weapon != null && TESEnchantmentType.MELEE.canApply(weapon) && hasEnchant(weapon, ench)) {
					return true;
				}
			}
		}
		return sourceEntity != null && hasProjectileEnchantment(sourceEntity, ench);
	}

	private static boolean hasProjectileEnchantment(Entity entity, TESEnchantment ench) {
		NBTTagList tags = getEntityEnchantTags(entity, false);
		if (tags != null) {
			for (int i = 0; i < tags.tagCount(); i++) {
				String s = tags.getStringTagAt(i);
				if (s.equals(ench.getEnchantName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isReforgeable(ItemStack itemstack) {
		return !getEnchantList(itemstack).isEmpty() || canApplyAnyEnchant(itemstack);
	}

	public static boolean isSilkTouch(ItemStack itemstack) {
		return itemstack != null && hasEnchant(itemstack, TESEnchantment.TOOL_SILK);
	}

	public static boolean negateDamage(ItemStack itemstack, Random random) {
		Random random1 = random;
		if (itemstack != null) {
			if (random1 == null) {
				if (backupRand == null) {
					backupRand = new Random();
				}
				random1 = backupRand;
			}
			List<TESEnchantment> enchants = getEnchantList(itemstack);
			for (TESEnchantment ench : enchants) {
				if (ench instanceof TESEnchantmentDurability) {
					float durability = ((TESEnchantmentDurability) ench).getDurabilityFactor();
					if (durability > 1.0F) {
						float inv = 1.0F / durability;
						if (random1.nextFloat() > inv) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static void onEntityUpdate(EntityLivingBase entity) {
		Random rand = entity.getRNG();

		if (TESConfig.enchantingTES) {
			if (entity instanceof EntityLiving) {
				boolean init = entity.getEntityData().getBoolean("TESEnchantInit");
				if (!init) {
					for (int i = 0; i < entity.getLastActiveItems().length; i++) {
						ItemStack itemstack = entity.getEquipmentInSlot(i);
						tryApplyRandomEnchantsForEntity(itemstack, rand);
					}
					entity.getEntityData().setBoolean("TESEnchantInit", true);
				}
			}
			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayer = (EntityPlayerMP) entity;
				UUID playerID = entityplayer.getUniqueID();
				InventoryPlayer inv = entityplayer.inventory;

				ItemStack[] lastKnownInv = LAST_KNOWN_PLAYER_INVENTORIES.get(playerID);
				if (lastKnownInv == null) {
					lastKnownInv = new ItemStack[inv.getSizeInventory()];
				}
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack itemstack = inv.getStackInSlot(i);
					ItemStack lastKnownItem = lastKnownInv[i];
					if (!ItemStack.areItemStacksEqual(itemstack, lastKnownItem)) {
						tryApplyRandomEnchantsForEntity(itemstack, rand);

						lastKnownItem = itemstack == null ? null : itemstack.copy();
						lastKnownInv[i] = lastKnownItem;
					}
				}
				if (tryApplyRandomEnchantsForEntity(inv.getItemStack(), rand)) {
					entityplayer.updateHeldItem();
				}
				LAST_KNOWN_PLAYER_INVENTORIES.put(playerID, lastKnownInv);
				if (LAST_KNOWN_PLAYER_INVENTORIES.size() > 200) {
					LAST_KNOWN_PLAYER_INVENTORIES.clear();
				}
			}
		}
	}

	public static void removeEnchant(ItemStack itemstack, TESEnchantment ench) {
		NBTTagList tags = getItemEnchantTags(itemstack, true);
		if (tags != null) {
			String enchName = ench.getEnchantName();
			for (int i = 0; i < tags.tagCount(); i++) {
				String s = tags.getStringTagAt(i);
				if (s.equals(enchName)) {
					tags.removeTag(i);
					break;
				}
			}
		}
	}

	public static void setAnvilCost(ItemStack itemstack, int cost) {
		if (!itemstack.hasTagCompound()) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setInteger("TESRepairCost", cost);
	}

	private static void setAppliedRandomEnchants(ItemStack itemstack) {
		if (!itemstack.hasTagCompound()) {
			itemstack.setTagCompound(new NBTTagCompound());
		}
		itemstack.getTagCompound().setBoolean("TESRandomEnch", true);
	}

	public static void setEnchantList(ItemStack itemstack, Iterable<TESEnchantment> enchants) {
		clearEnchants(itemstack);
		for (TESEnchantment ench : enchants) {
			setHasEnchant(itemstack, ench);
		}
	}

	public static void setHasEnchant(ItemStack itemstack, TESEnchantment ench) {
		if (!hasEnchant(itemstack, ench)) {
			NBTTagList tags = getItemEnchantTags(itemstack, true);
			if (tags != null) {
				String enchName = ench.getEnchantName();
				tags.appendTag(new NBTTagString(enchName));
			}
		}
	}

	public static void setProjectileEnchantment(Entity entity, TESEnchantment ench) {
		if (!hasProjectileEnchantment(entity, ench)) {
			NBTTagList tags = getEntityEnchantTags(entity, true);
			if (tags != null) {
				String enchName = ench.getEnchantName();
				tags.appendTag(new NBTTagString(enchName));
			}
		}
	}

	private static boolean tryApplyRandomEnchantsForEntity(ItemStack itemstack, Random rand) {
		if (itemstack != null && !hasAppliedRandomEnchants(itemstack) && canApplyAnyEnchant(itemstack)) {
			applyRandomEnchantments(itemstack, rand, false, false);
			return true;
		}
		return false;
	}

	public static class WeightedRandomEnchant extends WeightedRandom.Item {
		private final TESEnchantment theEnchant;

		public WeightedRandomEnchant(TESEnchantment e, int weight) {
			super(weight);
			theEnchant = e;
		}

		public TESEnchantment getTheEnchant() {
			return theEnchant;
		}
	}
}